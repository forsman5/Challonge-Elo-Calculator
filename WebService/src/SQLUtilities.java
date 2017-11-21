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
	 * Check the player table to see if there exists a 
	 */
	public int checkForPlayerId(int id) {
		// TODO Auto-generated method stub
		return -1;
	}

	public String getPlayerName(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void savePlayer(Player toAdd) {
		// TODO Auto-generated method stub
		
	}

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
		
		//select player id where name == null from players
		
		//if returned == null
		//	toReturned = -1;
		
		return toReturn;
	}

	public void savePlayerByName(String name) {
		// TODO Auto-generated method stub
		
	}
}
