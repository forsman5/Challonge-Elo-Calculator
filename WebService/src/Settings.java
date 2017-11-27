import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
 * Class that contains all of the settings set by the player, as well as hardcoded into the program
 * (set by developer).
 * 
 * Use by calling the no-args constructor, and then passing the name of the setting to the appropiate getter method.
 */
public class Settings {
	private final String FILE_LOCATION = "dat/settings.cfg";
	private final int MAX_ARRAY_SIZE = 255;
	
	private Map<String, Object> dict = new HashMap<String, Object>();
	
	/*
	 * Call this to initialize this object.
	 */
	public Settings() {
		Scanner inFile = null;
		
		//open the file for reading
		try {
			inFile = new Scanner(new File(FILE_LOCATION));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//while the file is not empty
		while (inFile.hasNextLine()) {
			//load strings out of memory
			String line = inFile.nextLine();
			
			String filtered = "";
			
			//handle comments
			while (line.length() > 0 && line.charAt(0) != '#') {
				filtered += line.charAt(0);
				
				//if can still move forward
				if (line.length() > 1) {
					line = line.substring(1);	
				} else {
					line = "";
				}
			}
			
			if (filtered.indexOf(':') != -1) {
				//cut line into pieces
				String key = filtered.substring(0, filtered.indexOf(':')).trim();
				String value = filtered.substring(filtered.indexOf(':') + 1).trim();
				
				dict.put(key, value);
			}
			
			//else could have been a line with only a comment
		}
		
		//close the file
		inFile.close();
		
	}
	
	/*
	 * Below are a few getter methods. These will return the proper type of value 
	 * for each requested setting.
	 * 
	 * for the basic get, if the setting does not exist, returns a null object.
	 */
	public Object get(String key) {
		return dict.get(key);
	}
	
	/*
	 * If this setting does not exist, returns -1.
	 */
	public int getInt(String key) {
		Object temp = dict.get(key);
		
		if (temp != null) {
			return Integer.parseInt(temp.toString());
		} else {
			return -1;
		}
	}
	
	/*
	 * If this setting does not exist, returns "null"
	 */
	public String getString(String key) {
		Object temp = dict.get(key);
		
		String toReturn;
		
		if (temp != null) {
			toReturn = temp.toString();
		} else {
			toReturn = "null";
		}
		
		return toReturn;
	}
	
	/*
	 * if this setting does not exist, returns false by default.
	 */
	public boolean getBool(String key) {
		Object result = dict.get(key);

		boolean toReturn = false;
		
		if (result != null) {
			String temp = result.toString().toLowerCase().trim();
			
			if (temp.equals("true")) {
				toReturn = true;
			} //else false
		}
		
		return toReturn;
	}
	
	/*
	 * If this setting does not exist, returns an empty array
	 */
	public String[] getStringArr(String key) {
		String[] toReturn = new String[MAX_ARRAY_SIZE];
		
		Object result = dict.get(key);
		
		if (result != null) {
			String s = result.toString();
			
			ArrayList<String> temp = new ArrayList<String>();
			
			while (s.length() > 0 ) {
				String toAdd = "";
				
				while (s.length() > 0 && s.charAt(0) != '\"') {
					//moving to opening of word
					s = s.substring(1);
				}
				
				//removing the opening quote
				if (s.length() > 0) {
					s = s.substring(1);
				}
						
				//reading word
				while (s.length() > 0 && s.charAt(0) != '\"') {
					toAdd += s.charAt(0);
					s = s.substring(1);
				}
				
				//removing the ending quote
				if (s.length() > 0) {
					s = s.substring(1);
				}
				
				if (toAdd.length() > 0) {
					temp.add(toAdd);
				}
			}
			
			toReturn = temp.toArray(new String[temp.size()]);
		}
		
		return toReturn;
	}
}
