import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Class to handle all alias data.
 */
public class Alias {
	
	/*
	 * Private wrapper class to save aliases to the database.
	 */
	private static class AliasRecord { // TODO is static going to work here ?? 
		public String alias;
		public String name;
		
		//no args constructor - none else needed, all fields are public (in a private class)
		public AliasRecord() {
			alias = null;
			name = null;
		}
	}

	/*
	 * Processes all new alias data.
	 * 
	 * Reads from a constant in the constant class, saves to another constant.
	 */
	public static void run() {
		//for saving to the database
		SQLUtilities sql = new SQLUtilities();
		
		//read new aliases to process
		ArrayList<AliasRecord> aliases = getAliases();
		
		//open file for writing (appending)
		
		for (AliasRecord a : aliases) {
			sql.insertAlias(a.name, a.alias);
			
			//append the data to the old file
			try {
				//open to append
				FileWriter fw = new FileWriter(Constants.ALIAS_OLD, true);
				BufferedWriter writer = new BufferedWriter(fw);

				writer.write(a.name + ":" + a.alias + "\n");

				writer.close();
				fw.close();
			} catch (IOException e) {
				//should not occur...
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Gets every alias from the file saved in Constants.Alias.
	 * 
	 * The file will be one string per line. Each string will be a player's master name, a colon, and the alias to add to their record.
	 * 
	 * Ex: Name:Alias
	 * 	   Name2:Alias2
	 * 
	 * Clears the Constants.Alias file after reading.
	 */
	private static ArrayList<AliasRecord> getAliases() {
		SQLUtilities sql = new SQLUtilities();
		
		ArrayList<AliasRecord> toReturn = new ArrayList<>();
		
		Scanner inFile = null;
		
		//open the file for reading
		try {
			inFile = new Scanner(new File(Constants.ALIAS_FILE));
		} catch (FileNotFoundException e) {
			//no new aliases to add
			//end execution
			return toReturn;
		}
		
		//while the file is not empty
		while (inFile.hasNextLine()) {
			//load strings out of memory
			String line = inFile.nextLine();
			
			//cut line into pieces
			String name = line.substring(0, line.indexOf(':'));
			String alias = line.substring(line.indexOf(':') + 1);
			
			//get the id for the name from the database.
			int pId = sql.getPlayerID(name);
			
			//if this player exists
			if (pId == -1) {
				//create new player record, and save it to the database.
				
				sql.insertPlayerByName(name);
			}
			
			//create an object 
			AliasRecord x = new AliasRecord();
			
			x.alias = alias;
			x.name = name;
			
			//add to list
			toReturn.add(x);
			
		}
		
		//close the file
		inFile.close();
		
		//clear the contents of this file
		try {
			FileWriter writer = new FileWriter(Constants.ALIAS_FILE);
			writer.write("");
			writer.close();
		} catch (IOException e) {
			/*
			 * This will never happen.
			 * 
			 * If this exception would have happened, it would have been thrown before,
			 * and already would have returned out of this method
			 */
		}
		
		return toReturn;
	}
}
