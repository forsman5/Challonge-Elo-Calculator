import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.*;
import org.json.*;

/**
 * This is the main driver file for the webservice.
 * 
 * More details to follow. TODO
 * 
 * @author Joe Forsman
 *
 */
public class Driver {
	public static void main (String [] args) {
		//load last saved date
		ArrayList<Tournament> tournies = null;
		
		//if an error occured
		boolean flag = false;
		
		tournies = getTournaments(getLastCheckedDate());
		
		if (tournies == null) {
			//setting it to an empty list
			//this means that no execution will continue later, as the list is empty.
			tournies = new ArrayList<>();
			flag = true;
		}
		
		for (Tournament t : tournies) {
			//get and process players
			String url = "https://api.challonge.com/v1/tournaments/"+t.id+"/participants.json?api_key="+Constants.API_KEY;
			ArrayList<JSONObject> json = executeRequest(url);
			
			System.out.println(t.name);
			//get and process matches 
		}
		
		//update last checked
		if (!flag) {
			//only if success... if no success, try again
			saveNewTimeChecked(System.currentTimeMillis());
		}
	}
	

	/*
	 * Load the last date everything was checked out of database memory
	 * 
	 * Return it as a string formatted as YYYY-MM-DD
	 */
	private static String getLastCheckedDate() {
		// TODO
		// Temporary implementation
		//long fromSql = ...
		
		Calendar cal = Calendar.getInstance();
		DateFormat df = DateFormat.getDateInstance();
		try {
			cal.setTime(df.parse("January 1, 1970"));// this string should be taken from sql
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			//long saved in a bad format in sql
		}
		return getDateAsString(cal);
	}
	
	/*
	 * Save the toSave parameter to sql, as the time the challonge api was last checked
	 * 
	 * toSave represents the number of milliseconds since Jan 1 1970 (the unix epoch)
	 */
	private static void saveNewTimeChecked(long toSave) {
		//TODO
	}

	/*
	 * Request all the tournaments for the account set as the API_KEY in constants.java.
	 * 
	 * Performs all related subdomains requests as well, and then filters the tournaments.
	 * 
	 * Returns an ArrayList of Tournament objects. This list contains every relevant tournament object.
	 * 
	 * if an error occurs, returns null. Else, will return an arraylist of at least size 0.
	 */
	public static ArrayList<Tournament> getTournaments(String createdAfter) {
		//for most tournaments
		String reqUrl = "https://api.challonge.com/v1/tournaments.json?state=ended&api_key=" + Constants.API_KEY + "&created_after="+createdAfter;
		
		ArrayList<JSONObject> json = executeRequest(reqUrl);
		
		//for any tournaments hosted in subdomains
		for (String s : Constants.SUBDOMAIN_NAME) {
			String subUrl = reqUrl + "&subdomain=" + s;
			
			ArrayList<JSONObject> subJson = executeRequest(subUrl);
			
			/*
			 * Adding all json objects from the subdomain response into the main
			 */
			for (JSONObject j : subJson) {
				json.add(j);
			}
			
		}
		
		//remove all tournaments that don't match a set of criteria
		filter(json);
		
		//arrayList to return, full of tournament objects
		ArrayList<Tournament> toReturn = new ArrayList<>();
		
		for (JSONObject j : json) {
			//convert the new tournaments into tournament objects
			
			Tournament toAdd = new Tournament();
			
			toAdd.name = j.getString("name");
			toAdd.dateStarted = j.getString("started_at");
			toAdd.link = j.getString("full_challonge_url");
			toAdd.id = j.getInt("id");
			
			toReturn.add(toAdd);
		}
		
		//return a set full of tournament objects
		return toReturn;
	}

	/*
	 * Constructs a new HTTP request based on the given url
	 */
	private static Request getNewRequest(String reqUrl) {
		Request request = new Request.Builder()
				  .url(reqUrl)
				  .get()
				  .addHeader("cache-control", "no-cache")
				  .build();
		return request;
	}
	

	/*
	 * gets the date to pass to the HTTP request
	 * 
	 * Uses the data stored in the calendar parameter
	 * 
	 * returns in the format YYYY-MM-DD
	 */
	private static String getDateAsString(Calendar lastChecked) {
		String year = "" + lastChecked.get(Calendar.YEAR);
		String mon = null;
		String day = null;
		
		int month = lastChecked.get(Calendar.MONTH);
		int d = lastChecked.get(Calendar.DAY_OF_MONTH);
		
		//0 - 11, we want 1 - 12
		month ++;
		
		//0 - 29, we want 1 - 30
		d ++;
		
		//ensuring both digits exist
		if (month < 10) {
			mon = "0" + month;
		} else {
			mon = "" + month;
		}
		
		if (d < 10) {
			day = "0" + month;
		} else {
			day = "" + month;
		}

		return year + "-" + mon + "-" + day;
	}

	/*
	 * Turns a response from challonge to an API call for all tournaments into an arrayList of JSONObjects
	 * 
	 * This is done because challonge returns a set of tournaments, not just one json object
	 */
	private static ArrayList<JSONObject> getJson(Response response) {
		String body = null;
		
		try {
			body = response.body().string();
			body= body.substring(1, body.length() - 1); //cut out first and final []
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<JSONObject> toReturn = new ArrayList<>();
		
		//find opening bracket
		while (!body.isEmpty()) {
			if (body.charAt(0) == '{') {
				//discard first found opening brace
				body = body.substring(1);
				
				//discard name of object
				while (body.charAt(0) != '{') {
					body = body.substring(1);
				}
				
				//now at beginning.. construct new string
				String temp = "";
				
				//fill to end
				while (body.charAt(0) != '}') {
					temp = temp + body.charAt(0);
					body = body.substring(1);
				}

				//adding final closing brace
				temp = temp +body.charAt(0);
				body = body.substring(1);
				
				//new object has been filled
				toReturn.add(new JSONObject(temp));
			} else { 
				//discard till find new beginning of a json object
				body = body.substring(1);
				
				//this should discard any extra closing braces
			}
		}
		
		return toReturn;
	}
	
	/*
	 * Ensures that an HTTP response works. If not, returns false. If 200 OK, returns true
	 * 
	 * If returns false, will also log the error
	 */
	private static boolean ensureSuccess(Response rsp) {
		//TODO
		//check to make sure both returned with 200 OK, if not, do something -- exception, logging, send an email...
		
		boolean toReturn = true;
		
		//if bad,
		//TODO
		if (false) {
			//handleBadResponse(rsp);
			//log the response here
			toReturn = false;
		}
		
		return toReturn;
	}

	/*
	 * Removes any objects in the array list that do not match a set of criteria
	 * 
	 * This criteria is taken from Constants.java
	 */
	private static void filter(ArrayList<JSONObject> arr) {
		ArrayList<JSONObject> toRemove = new ArrayList<>();
		
		//cannot remove while looping based on size
		for (JSONObject j : arr) {
			//cannot use get String here
			//throws an exception if an integer or empty string is encountered... not sure why
			String gameIdString = j.get("game_id").toString();
			
			if (isNull(gameIdString) || Integer.parseInt(gameIdString) != Constants.GAME_ID) {
				toRemove.add(j);
			}
		}
		
		//removing all undesirable records
		for (JSONObject i : toRemove) {
			arr.remove(i);
		}
	}
	
	/*
	 * Given a string, create a request, execute the request, ensure the response is valid, and then return the set of JSONObjects
	 * returned by the HTTP request.
	 * 
	 * If an invalid response is returned, this will return null.
	 */
	private static ArrayList<JSONObject> executeRequest(String req) {
		OkHttpClient client = new OkHttpClient();
		Request request = getNewRequest(req);
		
		//toReturn
		ArrayList<JSONObject> json = null;
		
		//execute new requests
		
		Response response = null;
		
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			//bad request
			e.printStackTrace();
		}
		
		//check for failure
		if (ensureSuccess(response)) {
			json = getJson(response);
		} // else, return null
		
		return json;
	}
	
	/*
	 * checks if a string is essentially null
	 */
	private static boolean isNull(String in) {
		boolean toReturn = false;
		
		if (in == null || in.isEmpty() || in.trim().isEmpty() || in.equals("null")) 
			toReturn = true;
		
		return toReturn;
	}
}
