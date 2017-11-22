import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SQLUtilities {
	
	private Connection conn = null;
	
	/*
	 * Constructor. Initializes this as the driver for the JDBC of mysql
	 */
	public SQLUtilities() {

        // This will load the MySQL driver, each DB has its own driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			//should not occur...
			e.printStackTrace();
		}
		
        conn = getConnection();
	}
	
	/*
	 * Gets the standard connection object used to connect to the database.
	 */
	private static Connection getConnection() {
		String connectionString = "jdbc:mysql://localhost/" + Constants.DATABASE_NAME + "?"
                + "user=" + Constants.DATABASE_USERNAME + "&password=" + Constants.DATABASE_PASSWORD+"&useSSL=false";

		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
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
	 * Return it as a string formatted as YYYY-MM-DD
	 */
	public String getLastCheckedDate() {
		Date out = null;
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetLatestTourneyDate(?)}");
			cs.registerOutParameter(1, Types.DATE);
			cs.setDate(1, out);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				out = rs.getDate(1);
			} else {
				//return the epoch, as no data currently exists in the database
				out = Date.valueOf("1970-01-01");
			}
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
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
		try {
			CallableStatement cs = conn.prepareCall("{call InsertTournament(?, ?, ?, ?)}");
			
			//set parameters
			cs.setInt(1, x.id);
			cs.setDate(2, java.sql.Date.valueOf(x.dateStarted));
			cs.setString(3, x.name);
			cs.setString(4, x.link);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Gets the player name associated with this ID
	 */
	public String getPlayerName(int id) {
		String toReturn = "null";
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetPlayerID(?, ?)}");
			cs.setInt(1, id);
			cs.registerOutParameter(2, Types.VARCHAR);
			cs.setString(2, toReturn);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getString(1);
			}
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
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
		try {
			CallableStatement cs = conn.prepareCall("{call InsertPlayer(?, ?, ?)}");
			
			//set parameters
			cs.setInt(1, toAdd.player_id);
			cs.setString(2, toAdd.name);
			cs.setInt(3, toAdd.elo);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * Inserts the given alias into the database.
	 */
	public void insertAlias(String name, String alias) {
		try {
			CallableStatement cs = conn.prepareCall("{call InsertAlias(?, ?)}");
			
			//set parameters
			cs.setString(1, name);
			cs.setString(2, alias);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Method to get the id attached to this master name.
	 * 
	 * If no id is attached to this name, returns -1;
	 */
	public int getPlayerID(String name) {
		int toReturn = -1;
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetPlayerID(?, ?)}");
			cs.setString(1, name);
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(2, toReturn);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getInt(1);
			}
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
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
		
		Player toInsert = new Player();
		toInsert.name = name;
		
		try {
			CallableStatement cs = conn.prepareCall("{call InsertPlayerName(?, ?)}");
			
			//set parameters
			cs.setString(1, toInsert.name);
			cs.setInt(2, toInsert.elo);
			
			cs.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Given a name, check all the names and aliases to find the true player ID for this record.
	 * 
	 * Returns -1 if this ID is never found
	 */
	public int getIDFromAlias(String name) {
		int id = getPlayerID(name);
		
		if (id == -1) {
			String trueName = getNameFromAlias(name); //query the alias database for the name attached to this
			
			if (!Constants.isNull(trueName)) {
				id = getPlayerID(trueName);
			}
			
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
		String toReturn = "null";
		
		try {
			CallableStatement cs = conn.prepareCall("{call GetNameFromAlias(?, ?)}");
			cs.setString(1, alias);
			cs.registerOutParameter(2, Types.VARCHAR);
			cs.setString(2, toReturn);
			
			ResultSet rs = cs.executeQuery();
			
			if (rs.next()) {
				toReturn = rs.getString(1);
			}
			
			//else, toReturn already initialized to -1
		} catch (SQLException e) {
			//should be caught by next
			e.printStackTrace();
		}
		
		return toReturn;
	}
}
