/*
 * simple constants file.
 * 
 * Used to increase scalability -- update these constants and the application should work for any challonge account
 * 
 * These are also used as a setting file
 */
final class Constants {
	/*
	 * only include games played with this game id.
	 */
	public final static int GAME_ID = 394;
	
	/*
	 * api key used for authenication
	 */
	public final static String API_KEY = "jp1XS5QmgVgYe6HZqGpHoOBneFY2RtCVkRdSIhKm";
	
	/*
	 * an array of subdomains to include tournaments from.
	 */
	public final static String[] SUBDOMAIN_NAME = {"powellsmash"};
		
	
	/*
	 * Location new aliases are placed into.
	 */
	public final static String ALIAS_FILE = "alias.dat";
	
	/*
	 * Location old aliases are moved to after being read.
	 */
	public final static String ALIAS_OLD = "old_alias.dat";
	
	/*
	 * This is a setting to turn on or off the alias suggestion feature.
	 * 
	 * If turned on, it will compare a name which has no records to all the aliases in the database.
	 * 
	 * If it is similar to at one of the aliases, it will be suggested as a possible alias for that player.
	 * 
	 * Similarity is determined if the number of characters in the string that are shared is greater than the 
	 * number of different characters. TODO - REVISE?
	 * 
	 * If it is suggested, then TODO
	 * send email?
	 * save to a new db?
	 */
	public final static boolean SUGGEST_ALIAS = false;
	
	/*
	 * Name of the database to connect to.
	 */
	public final static String DATABASE_NAME = "powellsmashdb";
	
	/*
	 * Username of the user to connect as.
	 * 
	 * (Can only guarantee no errors if this user is an admin)
	 */
	public final static String DATABASE_USERNAME = "root";
	
	/*
	 * Password for the given username.
	 */
	public final static String DATABASE_PASSWORD = "admin";
	
	/*
	 * Port the database was created using.
	 */
	public final static int DATABASE_PORT = 3306;
  
  /*
	 * Determines if the / or \ character is allowed in player names.
	 * 
	 * This is set because, often, someone would be subbed in, mid bracket. These results are being discarded.
	 */
	public final static boolean ALLOW_SLASHES = false;
}
