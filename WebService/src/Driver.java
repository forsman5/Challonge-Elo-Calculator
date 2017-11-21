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
		//process any new aliases before running the new tournaments
		Alias.run();
		
		//load last saved date
		ArrayList<Tournament> tournies = null;
		
		//sql drive
		SQLUtilities util = new SQLUtilities();
		
		tournies = getTournaments(util.getLastCheckedDate());
		
		if (tournies == null) {
			//setting it to an empty list
			//this means that no execution will continue later, as the list is empty.
			tournies = new ArrayList<>();
		}
		
		for (Tournament t : tournies) {
			//save every new tournament
			util.insertTournament(t);
			
			//get and process players
			ArrayList<Player> players = getPlayers(t.id);
			
			//get placings
			for (Player p : players) {
				//if (p.currId != -1) {
				//savePlacing (p.currId, t)
				//else savePlacing (p.id, t);
			}
			
			//debugging
			System.out.println(t.name);
			
			//get and process matches 
		}
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
		filterTournaments(json);
		
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
	 * Request all the players for the provided tournament id. After retreiving the data,
	 * then filters it for a series of noninteresting players. Returns a set of player objects.
	 * 
	 * if an error occurs, returns null. Else, will return an arraylist of at least size 0.
	 * 
	 * For players that do not already exist in the database, this method will save the newly created players to the database.
	 */
	public static ArrayList<Player> getPlayers(int tId) {
		//sql driver for finding the true player_id (matching records)
		SQLUtilities sql = new SQLUtilities();
		
		String url = "https://api.challonge.com/v1/tournaments/"+tId+"/participants.json?api_key="+Constants.API_KEY;
		ArrayList<JSONObject> json = executeRequest(url);
		
		filterPlayers(json);
		
		ArrayList<Player> toReturn = new ArrayList<>();
		
		for (JSONObject j : json) {
			Player toAdd = new Player();
			
			//fill the player object out
			int id = j.getInt("id");
			String name = j.getString("name");
			
			int returned = sql.getIDFromAlias(name);
			if (returned == -1) {
				//create new record
				toAdd.player_id = id;
				toAdd.name = name;
				
				//adding new player to the database
				sql.insertPlayer(toAdd);
			} else {
				toAdd.player_id = returned;
				toAdd.curr_id = id;
				
				//getting from database
				toAdd.name = sql.getPlayerName(id);
			}
			
			toReturn.add(toAdd);
		}
		
		return toReturn;
	}

	private static void filterPlayers(ArrayList<JSONObject> json) {
		ArrayList<JSONObject> toRemove = new ArrayList<>();

		for (JSONObject j : json) {
			String name = j.getString("name");
			
			if (!Constants.ALLOW_SLASHES) {
				//checking if it contains
				CharSequence x = "\\";
				CharSequence y = "/";
				
				if (name.contains(x) || name.contains(y)) {
					toRemove.add(j);
				}
			}
		}
		
		//removing all undesirable records
		for (JSONObject i : toRemove) {
			json.remove(i);
		}
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
	private static void filterTournaments(ArrayList<JSONObject> arr) {
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
