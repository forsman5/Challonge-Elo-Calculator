/*
 * Class to model a tournament object stored in the database.
 */
public class Tournament {
	public String name;
	public String link;
	public String dateStarted;
	public int id;
	
	/*
	 * no args constructor.
	 * 
	 * This is not needed, as all members are public
	 */
	public Tournament() {
		name = "";
		link = "";
		dateStarted = "";
		id = -1;
	}
}
