package com.challonge_elo_calculator.calculator;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.time.StopWatch;

/*
 * Class to mock the jdbc driver, and to connect to the mysql implementation.
 * 
 * All sql interactions must come through here.
 */
public class SQLUtilities {
	/*
	 * magic numbers that are inserted into the database.
	 * 
	 * FAILURE_TIME_CODE is saved in the time elapsed field of logs when an error occurs
	 * BEGIN_TIME_CODE is saved in the time elapsed field of the logs when an action is begun
	 */
	private final int FAILURE_TIME_CODE = -1;
	private final int BEGIN_TIME_CODE = -2;
	
	private Connection conn = null;
	
	private String ERROR_ALERT_DESTINATION;
	private String[] ERROR_ALERT_ORIGINATION;
	
	//stopwatch used to count number of seconds between each action
	//used for logging
	private Stack<StopWatch> watches;
	
	/*
	 * Constructor. Initializes this as the driver for the JDBC of mysql
	 */
	public SQLUtilities() {

		Settings settings = new Settings();
		
		ERROR_ALERT_DESTINATION = settings.getString("ERROR_ALERT_DESTINATION");
		ERROR_ALERT_ORIGINATION = settings.getStringArr("ERROR_ALERT_ORIGINATION");
		
        // This will load the MySQL driver, each DB has its own driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			//should not occur...
			e.printStackTrace();
		}
		
        conn = getConnection(settings.getString("DATABASE_NAME"), settings.getString("DATABASE_USERNAME"), settings.getString("DATABASE_PASSWORD"));
        
        watches = new Stack<StopWatch>();
	}
	
	/*
	 * Constructor. Initializes this as the driver for the JDBC of mysql
	 * 
	 * providing an already initialized settings object gives a slight performance boost
	 */
	public SQLUtilities(Settings settings) {
		ERROR_ALERT_DESTINATION = settings.getString("ERROR_ALERT_DESTINATION");
		ERROR_ALERT_ORIGINATION = settings.getStringArr("ERROR_ALERT_ORIGINATION");
		
        // This will load the MySQL driver, each DB has its own driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			//should not occur...
			e.printStackTrace();
		}
		
        conn = getConnection(settings.getString("DATABASE_NAME"), settings.getString("DATABASE_USERNAME"), settings.getString("DATABASE_PASSWORD"));
        
        watches = new Stack<StopWatch>();
	}	
	
	/*
	 * Gets the standard connection object used to connect to the database.
	 */
	private Connection getConnection(String db, String user, String pass) {
		String connectionString = "jdbc:mysql://localhost/" + db + "?"
                + "user=" + user + "&password=" + pass +"&useSSL=false";

		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			String message = Utility.getBody("getConnection", e, "Bad connection string.");
			String subject = "Error occured in Challonge Elo Parser application!";
			
			Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, message);
			
			//remove for production
			e.printStackTrace();
		}
		
		return conn;
	}
	
	/*
	 * Load the date of the latest tournament stored in the database.
	 * 
	 * This should be used such that no searching or processing is done for any tournaments before
	 * this date.
	 * 
	 * Adds one day to the date, so only checks after the latest tournament, not on that day.
	 * 
	 * Return it as a string formatted as YYYY-MM-DD
	 */
	public String getLastCheckedDate() {
		Date out = null;
		
		startLog("GetLastCheckedDate", "nil", "nil");
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetLatestTourneyDate()}");
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				out = rs.getDate(1);
				
				//add a day
				Calendar c = Calendar.getInstance();
				c.setTime(out);
				c.add(Calendar.DATE, 1);
				out = new java.sql.Date(c.getTimeInMillis());
			} else {
				//return the epoch, as no data currently exists in the database
				out = Date.valueOf("1970-01-01");
			}
			
			stopLog("GetLastCheckedDate", "nil", out.toString(), "nil");
		} catch (SQLException e) {
			errorLog("GetLastCheckedDate", "nil", e);
		}
		
		return out.toString();
	}
	
	/*
	 * Inserts the given tournament into the database.
	 * 
	 * REQUIRES:
	 * 	Tournament ID must be unique in the database.
	 */
	public void insertTournament(Tournament x) {
		startLog("InsertTournament", "" + x.id, "nil");
		
		try {
			CallableStatement cs = conn.prepareCall("{call InsertTournament(?, ?, ?, ?)}");
			
			SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			//set parameters
			cs.setInt(1, x.id);
			
			try {
				cs.setDate(2, new java.sql.Date(textFormat.parse(x.dateStarted).getTime()));
			} catch (java.text.ParseException e) {
				errorLog("InsertTournament", "" + x.id, "Tournament contained an invalid date. Tournament: " + x.name + ", date: " + x.dateStarted, e);
			}
					
			cs.setString(3, x.name);
			cs.setString(4, x.link);
			
			cs.executeQuery();
			
			stopLog("InsertTournament", "" + x.id, "nil", "null");
		} catch (SQLException e) {
			errorLog("InsertTournament", ""+x.id, e);
		}
	}

	/*
	 * Gets the player name associated with this ID
	 */
	public String getPlayerName(int id) {
		String toReturn = "null";

		startLog("GetPlayerName", "" + id, "nil");
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetPlayerName(?)}");
			cs.setInt(1, id);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getString(1);
			}
			
			//else, toReturn already initialized to -1
			
			stopLog("GetPlayerName", "" + id, toReturn, "nil");
		} catch (SQLException e) {
			errorLog("GetPlayerName", "" + id, e);
		}
		
		return toReturn;
	}

	/*
	 * Inserts the given player into the database.
	 * 
	 * REQUIRES:
	 * 	player ID must be unique in the database.
	 */
	public void insertPlayer(Player toAdd) {
		startLog("InsertPlayer", "" + toAdd.player_id, "Inserting player with id " + toAdd.player_id + " and more attached info..");
		
		try {
			CallableStatement cs = conn.prepareCall("{call InsertPlayer(?, ?, ?)}");
			
			//set parameters
			cs.setInt(1, toAdd.player_id);
			cs.setString(2, toAdd.name);
			cs.setInt(3, toAdd.elo);
			
			cs.executeQuery();
			
			stopLog("InsertPlayer", "" + toAdd.player_id, "nil", "nil");
		} catch (SQLException e) {
			errorLog("InsertPlayer", "" + toAdd.player_id, e);
		}
	}

	/*
	 * Inserts the given alias into the database.
	 */
	public void insertAlias(String name, String alias) {
		startLog("InsertAlias", alias, "Alias: " + alias + " Name: " + name);
		
		try {
			CallableStatement cs = conn.prepareCall("{call InsertAlias(?, ?)}");
			
			//set parameters
			cs.setString(1, name);
			cs.setString(2, alias);
			
			cs.executeQuery();
			
			stopLog("InsertAlias", alias, "nil", "Alias: " + alias + " Name: " + name);
			
		} catch (SQLException e) {
			errorLog("InsertAlias", alias, "Alias: " + alias + " Name: " + name, e);
		}
	}
	
	/*
	 * Method to get the id attached to this master name.
	 * 
	 * If no id is attached to this name, returns -1;
	 */
	public int getPlayerID(String name) {
		int toReturn = -1;
		startLog("GetPlayerID", name, "nil");
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetPlayerID(?)}");
			cs.setString(1, name);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getInt(1);
			}
			
			stopLog("GetPlayerID", name, "" + toReturn, "nil");
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			errorLog("GetPlayerID", name, e);
		}
		
		return toReturn;
	}

	/*
	 * Create a new player record in the database, using this name.
	 * 
	 * Allow the player id to be autofilled by the database
	 */
	public void insertPlayerByName(String name) {
		//create a player to invoke the constructor
		
		startLog("InsertPlayerByName", name, "nil");
		
		Player toInsert = new Player();
		toInsert.name = name;
		
		try {
			CallableStatement cs = conn.prepareCall("{call InsertPlayerName(?, ?)}");
			
			//set parameters
			cs.setString(1, toInsert.name);
			cs.setInt(2, toInsert.elo);
			
			cs.executeQuery();
			
			stopLog("InsertPlayerByName", name, "nil", "nil");
			
		} catch (SQLException e) {
			errorLog("InsertPlayerByName", name, e);
		}
	}

	/*
	 * Given a name, check all the names and aliases to find the true player ID for this record.
	 * 
	 * Returns -1 if this ID is never found
	 */
	public int getIDFromAlias(String name) {
		startLog("GetIDFromAlias", name, "nil");
		
		int id = getPlayerID(name);
		
		if (id == -1) {
			String trueName = getNameFromAlias(name); //query the alias database for the name attached to this
			
			if (!Utility.isNull(trueName)) {
				id = getPlayerID(trueName);
			}
			
			stopLog("GetIDFromAlias", name, "" + id, "nil");
			
			//else do nothing, id remains -1
		}
		
		//else do nothing, return the original id
		
		return id;
	}

	/*
	 * Get the first name associated with the given alias in the database.
	 * 
	 * Returns "null" if nothing found.
	 */
	public String getNameFromAlias(String alias) {
		startLog("GetNameFromAlias", alias, "nil");
		
		String toReturn = "null";
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetNameFromAlias(?)}");
			cs.setString(1, alias);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getString(1);
			}
			
			stopLog("GetNameFromAlias", alias, toReturn, "nil");
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			errorLog("GetNameFromAlias", alias, e);
		}
		
		return toReturn;
	}
	
	/*
	 * Wipes and recreates all tables.
	 * 
	 * WARNING - ALL DATA WILL BE LOST
	 */
	public void wipeTables() {
		// no startlog -- tables will be wiped immediately after starting
		StopWatch tempWatch = new StopWatch();
		tempWatch.start();
		watches.push(tempWatch);
		
		try {
			CallableStatement cs = conn.prepareCall("{call CreateTables()}");
			
			cs.executeQuery();
		} catch (SQLException e) {
			errorLog("WipeTables", "nil", e);
		}
		
		stopLog("WipeTables", "nil", "nil", "nil");
	}
	
	/*
	 * Create a new placing record in the database.
	 */
	public void insertPlacing(int player_id, int t_id, int final_placing) {
		startLog("InsertPlacing", "" + player_id, "Inserting placing for player_id " + player_id + " and tourney_id " + t_id);
		
		try {
			CallableStatement cs = conn.prepareCall("{call InsertPlacing(?, ?, ?)}");
			
			//set parameters
			cs.setInt(1, player_id);
			cs.setInt(2, t_id);
			cs.setInt(3, final_placing);
			
			cs.executeQuery();
			
			stopLog("InsertPlacing", "" + player_id, "nil", "Inserted placing for player_id " + player_id + " and tourney_id " + t_id);
			
		} catch (SQLException e) {
			errorLog("InsertPlacing", "" + player_id, "Failed inserting placing for player_id " + player_id + " and tourney_id " + t_id + "...", e);
		}
	}

	/*
	 * Create a new match record in the database.
	 */
	public void insertMatch(Match m) {
		startLog("InsertMatch", "" + m.match_id, "nil");
		
		try {
			CallableStatement cs = conn.prepareCall("{call InsertMatch(?, ?, ?, ?, ?, ?)}");
			
			//set parameters
			cs.setInt(1, m.match_id);
			cs.setInt(2, m.winner_id);
			cs.setInt(3, m.loser_id);
			cs.setInt(4, m.winner_score);
			cs.setInt(5, m.loser_score);
			cs.setInt(6, m.tourney_id);
			
			cs.executeQuery();
			
			stopLog("InsertMatch", "" + m.match_id, "nil", "nil");
			
		} catch (SQLException e) {
			errorLog("InsertMatch", "" + m.match_id, e);
		}
	}

	/*
	 * Record the elo value associated with this player_id
	 */
	public int getElo(int id) {
		int toReturn = -1;
		
		startLog("GetElo", "" + id, "nil");
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetElo(?)}");
			cs.setInt(1, id);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getInt(1);
			}
			
			stopLog("GetElo", "" + id, "" + toReturn, "nil");
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			errorLog("GetElo", "" + id, e);
		}
		
		return toReturn;
	}
	
	/*
	 * Record the elo value associated with this player_id
	 */
	public void setElo(int id, int newElo) {
		startLog("SetElo", "" + id, "Set the new elo for player " + id + " to be " + newElo);
		
		try {
			CallableStatement cs = conn.prepareCall("{call SetElo(?, ?)}");
			cs.setInt(1, id);
			cs.setInt(2, newElo);
			
			cs.executeQuery();
			
			stopLog("SetElo", "" + id, "nil", "Set the new elo for player " + id + " to be " + newElo);
		} catch (SQLException e) {
			errorLog("SetElo", "" + id, "Set the new elo for player " + id + " to be " + newElo, e);
		}
	}

	/*
	 * Gets an array of Player objects who have no match records associated with
	 * their relevant player_id in the match table of the database.
	 */
	public Player[] getEmptyPlayers() {
		startLog("GetEmptyPlayers", "nil", "nil");
		
		Player[] toReturn = new Player[] {};
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetEmptyPlayers()}");
			
			ResultSet rs = cs.executeQuery();
			
			ArrayList<Player> tempList = new ArrayList<>();
			
			while (rs.next()) {
				Player p = new Player();
				
				p.player_id = rs.getInt(1);
				p.elo = rs.getInt(2);
				p.name = rs.getString(3);
				
				tempList.add(p);
			}
			
			toReturn = tempList.toArray(toReturn);
			
			String temp = "Empty array";
			if (toReturn.length > 0)  {
				temp = "Array, first element is player with player_id " + toReturn[0].player_id;
			}
			
			stopLog("GetEmptyPlayers", "nil", temp, "nil");
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			errorLog("GetEmptyPlayers", "nil", e);
		}
		
		return toReturn;
	}
	
	/*
	 * Get an array of every alias attached to the given name present in the aliases datatable.
	 */
	public String[] getAliases(String name) {
		startLog("GetAliases", name, "nil");
		
		String[] toReturn = new String[] {};
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetAliases(?)}");
			cs.setString(1, name);
			
			ResultSet rs = cs.executeQuery();
			
			ArrayList<String> tempList = new ArrayList<>();
			
			while (rs.next()) {
				tempList.add(rs.getString(1));
			}
			
			toReturn = tempList.toArray(toReturn);
			
			String temp = "Empty array";
			if (toReturn.length > 0)  {
				temp = "Array, first element is " + toReturn[0];
			}
			
			stopLog("GetAliases", name, temp, "nil");
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			errorLog("GetAliases", name, e);
		}
		
		return toReturn;
	}

	/*
	 * Removes a player from the player database.
	 * 
	 * This will also remove any associate match records, as well as any placing records.
	 * 
	 * Removes any trace of a player ever playing -- except elo. Elo is not updated, and cannot be updated, short of a total
	 * recalculation.
	 */
	public void deletePlayer(int player_id) {
		startLog("DeletePlayer", "" + player_id, "nil");
		
		try {
			//these are not exposed individually
			//if they were, possible that theres leftover match records with no players attached..
			CallableStatement cs1 = conn.prepareCall("{call DeletePlacings(?)}");
			CallableStatement cs2 = conn.prepareCall("{call DeleteMatches(?)}");
			CallableStatement cs = conn.prepareCall("{call DeletePlayer(?)}");
			
			cs.setInt(1, player_id);
			cs1.setInt(1, player_id);
			cs2.setInt(1, player_id);
			
			//order here is important, parent rows deleted last
			cs1.executeQuery();
			cs2.executeQuery();
			cs.executeQuery();
			
			stopLog("DeletePlayer", "" + player_id, "nil", "nil");
		} catch (SQLException e) {
			errorLog("DeletePlayer", "" + player_id, e);
		}
	}

	/*
	 * Return every match, in an array, with the given playerId
	 */
	public Match[] getMatches(int playerId) {
		startLog("GetMatches", "" + playerId, "nil");
		
		Match[] toReturn = new Match[] {};
		
		try {
			CallableStatement cs = conn.prepareCall("{call getMatches(?)}");
			cs.setInt(1, playerId);
			
			ResultSet rs = cs.executeQuery();
			
			ArrayList<Match> tempList = new ArrayList<>();
			
			while (rs.next()) {
				Match m = new Match();
				
				//creating match from results
				m.match_id = rs.getInt(1);
				m.winner_id = rs.getInt(2);
				m.loser_id = rs.getInt(3);
				m.winner_score = rs.getInt(4);
				m.loser_score = rs.getInt(5);
				m.tourney_id = rs.getInt(6);
				
				tempList.add(m);
			}
			
			toReturn = tempList.toArray(toReturn);
			
			String temp = "Empty array";
			if (toReturn.length > 0)  {
				temp = "Array, first element is match with match_id " + toReturn[0].match_id;
			}
			
			stopLog("GetMatches", "" + playerId, temp, "nil");
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//if something breaks, empty array is still returned
			errorLog("GetMatches", "" + playerId, e);
		}
		
		return toReturn;
	}

	/*
	 * Switch every instance of the oldId to the playerId provided.
	 * 
	 * This will also delete the player record of the oldId once everything has been switched.
	 * 
	 * Sort of merges all records from oldId into playerID
	 */
	public void updatePlayerId(int oldId, int playerId) {
		startLog("UpdatePlayerId", "" + oldId, "Updating player_id " + oldId + " to be merged with " + playerId);
		
		try {
			CallableStatement cs = conn.prepareCall("{call UpdatePlayerId(?, ?)}");
			cs.setInt(1, oldId);
			cs.setInt(2, playerId);
			
			cs.executeQuery();
			
			//once all associated records are fixed, remove the old player
			//player cannot be fixed with rest of updates, will result in duplicate id
			deletePlayer(oldId);

			stopLog("UpdatePlayerId", "" + oldId, "nil", "Updated player_id " + oldId + " to be merged with " + playerId);
		} catch (SQLException e) {
			errorLog("UpdatePlayerId", "" + oldId, "Updated player_id " + oldId + " to be merged with " + playerId, e);
		}
	}

	/*
	 * Force every alias record that referred to oldName before, now to refer to newName
	 */
	public void updateAliasReference(String oldName, String newName) {
		startLog("UpdateAliasReferences", oldName, "Set all aliases previously referring to " + oldName + " to now refer to " + newName);
		
		try {
			CallableStatement cs = conn.prepareCall("{call UpdateAliasReferences(?, ?)}");
			cs.setString(1, oldName);
			cs.setString(2, newName);
			
			cs.executeQuery();
			
			stopLog("UpdateAliasReferences", oldName, "nil", "Set all aliases previously referring to " + oldName + " to now refer to " + newName);
		} catch (SQLException e) {
			errorLog("UpdateAliasReferences", oldName, "Set all aliases previously referring to " + oldName + " to now refer to " + newName, e);
		}
	}
	
	/*
	 * Insert a new event log at the current time.
	 * 
	 * in: an important in parameter to be recorded. Can be null
	 * out: result, or description of the returned value
	 * elapsed: number of milliseconds to complete the call. 
	 * 		-2 means this is the start of the call
	 * 		-1 means never completed
	 * message: any additional information to add
	 * method: name of method calling
	 */
	private void insertEventLog(String method, String in, String out, int elapsed, String message) {
		java.util.Date temp = new java.util.Date(); //current time
		Timestamp date = new Timestamp(temp.getTime());
		
		try {
			CallableStatement cs = conn.prepareCall("{call InsertEventLog(?, ?, ?, ?, ?, ?)}");
			cs.setTimestamp(1, date);
			cs.setString(2, method);
			cs.setString(3, in);
			cs.setString(4, out);
			cs.setInt(5, elapsed);
			cs.setString(6, message);
			
			cs.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Insert a log for the starting of a method.
	 * 
	 * Requires the watch be reset (by calling an ending log entry (error or otherwise)).
	 */
	public void startLog(String method, String in, String message) {
		StopWatch watch;
		
		/*
		 * One watch is always to be left on the stack
		 * 
		 * This is done so that that watch is reusable.
		 * 
		 * This is done to avoid creating a new watch for every single call to startLog
		 * (read: every call to SQLUtilities!!)
		 * 
		 * I'm afraid that, since a watch runs on its own thread (presumably), allocating the 
		 * memory and creating / allocating that thread for every single method call to SQLUtilities
		 * that doesn't call any other SQLUtilities methods (most of them)
		 * is too slow and requires too much memory, while repeatedly reusing the top watch on the 
		 * watches stack is much more efficient.
		 */
		if (watches.size() == 1 && !watches.peek().isStarted()) {
			watch = watches.peek();
		} else {
			watch = new StopWatch();
			watches.push(watch);
		}
		
		watch.start();
		
		insertEventLog(method, in, "nil", BEGIN_TIME_CODE, message);
	}
	
	/*
	 * Log the successful completion of a method.
	 */
	public void stopLog(String method, String in, String out, String message) {
		//See documentation in startLog as to why this is a peek
		StopWatch watch = watches.peek();
		
		watch.stop();
		
		insertEventLog(method, in, out, (int) watch.getTime(), message);
		
		watch.reset();
		
		//other watches are in use
		if (watches.size() > 1) {
			watches.pop();
		}
	}
	
	/*
	 * Log an error. Appends the exception message onto the message given.
	 * 
	 * Will also send an email to the standard address, detailling the error
	 */
	public void errorLog(String method, String in, String message, Exception e) {
		//do nothing with the watch, just clean the stack
		StopWatch watch = watches.peek();
		
		watch.stop();
		
		watch.reset();
		
		if (watches.size() > 1) {
			watches.pop();
		}
		
		String body = "";

		//check input
		if (Utility.isNull(message)) {
			message = "";
			body = Utility.getBody(method, e);
		} else {
			body = Utility.getBody(method, e, message);
		}
		
		String subject = "Error occured in Challonge Elo Parser application!";
		
		Utility.sendEmail(ERROR_ALERT_DESTINATION, ERROR_ALERT_ORIGINATION, subject, body);

		insertEventLog(method, in, "nil", FAILURE_TIME_CODE, message + "Error occured in " + method + ": " + e.getMessage());
	}
	
	/*
	 * Overload for no custom message
	 */
	public void errorLog(String method, String in, Exception e) {
		errorLog(method, in, "", e);
	}

	/*
	 * Get a count of number of calls to every distinct method name in the database for today's date.
	 */
	public Map<String, Integer> getDailyMethodCounts() {
		// get unique method names
		startLog("GetDailyMethodCounts", "nil", "nil");
		
		Map<String, Integer> toReturn = new HashMap<String, Integer>();
		
		ArrayList<String> methods = new ArrayList<String>();
		
		try {
			CallableStatement cs = conn.prepareCall("SELECT DISTINCT method FROM event_log;");
			
			ResultSet rs = cs.executeQuery();
			
			while (rs.next()) {
				methods.add(rs.getString(1));
			}

			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			e.printStackTrace();
			errorLog("GetDailyMethodCounts", "nil", "Failed getting distinct method names.", e);
		}
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetCountOfMethod(?)}");
			
			for (String method : methods) {
				//done here to avoid repeatedly calling prepareCall.
				//according to jdbc documentation, that is a very expensive call
				cs.setString(1, method);
				
				ResultSet rs = cs.executeQuery();
				
				while (rs.next()) {
					toReturn.put(method, rs.getInt(1));
				}
			}
		} catch (SQLException e) {
			errorLog("GetDailyMethodCounts", "nil", "Failed getting counts for method names.", e);
			
			e.printStackTrace();
		}
		
		//prevent accessing first element if empty set
		String setDesc = (toReturn.size() == 0 ? "Empty Set" : "Set with first key " + toReturn.keySet().toArray()[0]);
		
		stopLog("GetDailyMethodCounts", "nil", "Map, with key set " + setDesc, "nil");
		
		return toReturn;
	}
}
