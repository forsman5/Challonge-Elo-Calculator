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
			
			//not sure why this fixes many issues...
			//does rs.next() load the next result in?
			if (rs.next()) {
				out = rs.getDate(1);
			}
		} catch (SQLException e) {
			//TODO
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
			//TODO
			e.printStackTrace();
		}
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
			//TODO
		}
		
		return conn;
	}

	/*
	 * Gets the player name associated with this ID
	 */
	public String getPlayerName(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Inserts the given player into the database.
	 * 
	 * REQUIRES:
	 * 	player ID must be unique in the database.
	 */
	public void savePlayer(Player toAdd) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Inserts the given alias into the database.
	 * 
	 */
	public void addAlias(String name, String alias) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Method to get the id attached to this master name.
	 * 
	 * If no id is attached to this name, returns -1;
	 */
	public int getPlayerId(String name) {
		//TODO
		
		int toReturn = 0;
		
		//select player id where name == name from players
		
		//if returned == null
		//	toReturned = -1;
		
		return toReturn;
	}

	/*
	 * Create a new player record in the database, using this name.
	 * 
	 * Allow the player id to be autofilled by the database
	 */
	public void savePlayerByName(String name) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Given a name, check all the names and aliases to find the true player ID for this record.
	 * 
	 * Returns -1 if this ID is never found
	 */
	public int getIDFromAlias(String name) {
		int id = getPlayerId(name);
		
		if (id == -1) {
			String trueName = ""; //query the alias database for the name attached to this
			id = getPlayerId(trueName);
		}
		
		//else do nothing, return the original id
		
		// TODO Auto-generated method stub
		return id;
	}
}
