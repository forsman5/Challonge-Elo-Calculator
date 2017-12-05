package com.challonge_elo_calculator.calculator;
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
	private static class AliasRecord {
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
	public static void run(String source, String store, SQLUtilities sql) {
		//read new aliases to process
		ArrayList<AliasRecord> aliases = getAliases(source);
		
		//open file for writing (appending)
		FileWriter fw = null;
		BufferedWriter writer = null;
		
		try {
			fw = new FileWriter(store, true);
			writer = new BufferedWriter(fw);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for (AliasRecord a : aliases) {
			//ensure this does not exist already
			if (!sql.getNameFromAlias(a.alias).equals(a.name)) {
				//find if both players exist
				//first player must exist, if doesnt, is created by getAliases
				int secondId = sql.getIDFromAlias(a.alias);
				
				if (secondId != -1) {
					//other record exists -- must be reconciled
					int firstId = sql.getPlayerID(a.name);
					
					int firstElo = sql.getElo(firstId);
					int secondElo = sql.getElo(secondId);
					
					int matchesOne = sql.getMatches(firstId).length;
					int matchesTwo = sql.getMatches(secondId).length;
					
					sql.updatePlayerId(secondId, firstId);
					sql.updateAliasReference(a.alias, a.name);
					
					double ratioOne = matchesOne / (matchesOne + matchesTwo);
					double ratioTwo = 1 - ratioOne;
					
					//new elo for unified player is the average of both of the old elos weighted by number of matches played.
					//cannot be truly fixed without a recalc, which should be exposed to a sys adm console on the webiste
					int newElo = (int) Math.round(firstElo * ratioOne + secondElo * ratioTwo);
					
					sql.setElo(firstId, newElo);
	
				}
				
				//perform the rest of the code in both cases
				
				sql.insertAlias(a.name, a.alias);
				
				//append the data to the old file
				try {
					writer.write(a.name + ":" + a.alias + "\n");
				} catch (IOException e) {
					//should not occur...
					e.printStackTrace();
				}
			}
			
			//else, do nothing with this record
		}
		
		//prevent memory leaks
		try {
			writer.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
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
	private static ArrayList<AliasRecord> getAliases(String source) {
		SQLUtilities sql = new SQLUtilities();
		
		ArrayList<AliasRecord> toReturn = new ArrayList<>();
		
		Scanner inFile = null;
		
		//open the file for reading
		try {
			inFile = new Scanner(new File(source));
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
			FileWriter writer = new FileWriter(source);
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
