/*
 * Wrapper class for the player table in the database.
 * 
 * For processing the data coming out of tournaments, there is a local variable curr_id.
 * 
 * This does not get saved to the database!
 * 
 * This is used, such that, once the player_id is set from the aliases table, then the id
 * from the current tournament processed is saved, in case it is needed for working with
 * the matches or places later.
 */
public class Player {
	public int player_id;
	public int elo;
	public String name;
	
	//THIS DOES NOT GET SAVED
	public int curr_id;
	
	//elo starts at 1000
	public Player() {
		elo = 1000;
		
		//can be used to check if never set
		curr_id = -1;
	}
}
