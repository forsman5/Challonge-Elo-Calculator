import java.io.IOException;
import java.util.ArrayList;

import okhttp3.*;
import org.json.*;

//TODO
//WHY IS CALEB F SO LOW

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
			//get and process players
			ArrayList<Player> players = getPlayers(t.id);
			
			for (Player p : players) {
				util.insertPlacing(p.player_id, t.id, p.final_placing);
			}
			
			//get and process matches 
			ArrayList<Match> matches = getMatches(t.id);
			
			processMatches(matches, players, util);
			
			//saving any not filtered out
			for (Match m : matches) {
				util.insertMatch(m);
			}
		}
	}	

	/*
	 * Returns an array of all matches played in the provided tourney id.
	 * 
	 * Disqualifications are filtered out (as are matches scored 0-0).
	 * 
	 * Nothing is saved - this must be done with the array of players.
	 */
	public static ArrayList<Match> getMatches(int id) {
		//sql driver for finding the true player_id (matching records)
		
		String url = "https://api.challonge.com/v1/tournaments/"+id+"/matches.json?api_key="+Constants.API_KEY;
		ArrayList<JSONObject> json = executeRequest(url);
		
		filterMatches(json);
		
		ArrayList<Match> toReturn = new ArrayList<>();
		
		for (JSONObject j : json) {
			Match toAdd = new Match();
			
			//fill the match object out
			toAdd.match_id = j.getInt("id");
			toAdd.tourney_id = id;
			
			int scores[] = getScores(j.getString("scores_csv"));
			
			toAdd.winner_id = j.getInt("winner_id");
			toAdd.loser_id = j.getInt("loser_id");
			
			if (scores[0] > scores[1]) {
				toAdd.winner_score = scores[0];
				toAdd.loser_score = scores[1];
			} else {
				//ties are handled here...
				toAdd.loser_score = scores[0];
				toAdd.winner_score = scores[1];
			}
					
			toReturn.add(toAdd);
		}
		
		return toReturn;
	}

	/*
	 * Request all the tournaments for the account set as the API_KEY in constants.java.
	 * 
	 * Performs all related subdomains requests as well, and then filters the tournaments.
	 * 
	 * Returns an ArrayList of Tournament objects. This list contains every relevant tournament object.
	 * 
	 * if an error occurs, returns null. Else, will return an arraylist of at least size 0.
	 * 
	 * All new tournaments that are returned are automatically saved to the database.
	 */
	public static ArrayList<Tournament> getTournaments(String createdAfter) {
		//for inserting the new tournaments
		SQLUtilities sql = new SQLUtilities();
		
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

			//save every new tournament
			sql.insertTournament(toAdd);
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
			
			//getting group id
			JSONArray group_player_ids = j.getJSONArray("group_player_ids");
			if (group_player_ids.length() != 0) {
				toAdd.group_id = (Integer) group_player_ids.get(0);
			} else {
				toAdd.group_id = -1;
			}
			
			name = sanitizeName(name);
			
			int returned = sql.getIDFromAlias(name);
			if (returned == -1) {
				//create new record
				toAdd.player_id = id;
				toAdd.name = name;
				
				//adding new player to the database
				sql.insertPlayer(toAdd);
			} else {
				toAdd.player_id = returned;
				
				//getting from database
				toAdd.name = sql.getPlayerName(returned);
				toAdd.elo = sql.getElo(returned);
			}
			
			toAdd.curr_id = id;
			Object temp = j.get("final_rank");
			
			if (Constants.isNull(temp.toString())) {
				//did not advance out of the group stage
				/*
				 * Get seed, as this is seed going into the final round (or result out of the group stage)
				 * 
				 * What if it is a seeding tourney - a pools tourney?
				 * Do we want to display final placing for the pools stage of a tourney that then opens up to 
				 * amateur / pro?
				 */
				toAdd.final_placing = j.getInt("seed");
			} else {
				toAdd.final_placing = j.getInt("final_rank");
			}
					
			toReturn.add(toAdd);
		}
		
		return toReturn;
	}

	/*
	 * Process all matches and their results.
	 * 
	 * Carries out all elo calculations and filters the given match set to ensure that only viable matches are saved.
	 * 
	 * the given set of matches and players should be every match and player (after filtering) from a given tournament.
	 * 
	 * sql param is needed to save elo updates to the db.
	 */
	private static void processMatches(ArrayList<Match> matches, ArrayList<Player> players, SQLUtilities sql) {
		//must reconcile all matches using a curr_id to use the proper player_id
		//all matches with at least one id that does not exist in the player_id table 
		//   (ie - a spacer) must be filtered out
		ArrayList<Match> toRemove = new ArrayList<Match>();
		
		for (Match m : matches) {
			//for optimization..
			boolean winnerFound = false;
			boolean loserFound = false;
			
			int index = 0;
			while ((!winnerFound || !loserFound) && index < players.size()) {
				Player p = players.get(index);
				
				//compare to the matches id's
				if (!loserFound && (m.loser_id == p.curr_id || m.loser_id == p.group_id)) {
					m.loser_id = p.player_id;
					loserFound = true;
				}
				
				if (!winnerFound && (m.winner_id == p.curr_id || m.winner_id == p.group_id)) {
					m.winner_id = p.player_id;
					winnerFound = true;
				}
				
				index++;
			}
			
			//ensure that both participants exist in the database
			if (!winnerFound || !loserFound) {
				toRemove.add(m);
			} else {
				//valid match - perform elo calc
				int eloW = sql.getElo(m.winner_id);
				int eloL = sql.getElo(m.loser_id);
				
				int elos[] = calcNewElo(eloW, eloL, m.winner_score, m.loser_score);
				
				sql.setElo(m.winner_id, elos[0]);
				sql.setElo(m.loser_id, elos[1]);
			}
		}
		
		//removing bad matches
		for (Match r : toRemove) {
			matches.remove(r);
		}
		
	}
	
	/*
	 * Given the elo and score of the winner and loser of a match,
	 * calculates the new elo value of both players, and returns them in an arry.
	 * 
	 * Element 0 in returned array is the updated elo score of the winner, and Element 0 is the updated elo score of the loser.
	 */
	private static int[] calcNewElo(int eloW, int eloL, int wScore, int lScore) {
		//transform the ratings
		double onePrime = Math.pow(10, eloW/400);
		double twoPrime = Math.pow(10, eloL/400);
		
		double expected1 = onePrime / (onePrime + twoPrime);
		double expected2 = twoPrime / (onePrime + twoPrime);
		
		double simpleScore1;
		double simpleScore2;
		
		//TODO
		//add accounting for the margin of the win??
		if (wScore > lScore) {
			simpleScore1 = 1;
			simpleScore2 = 0;
		} else if (wScore == lScore) {
			simpleScore1 = 0;
			simpleScore2 = 1;
		} else {
			//draw
			simpleScore1 = .5;
			simpleScore2 = .5;
		}
		
		int[] toReturn = new int[] {0,0};
		
		toReturn[0] = (int) Math.round(eloW + Constants.KFACTOR * (simpleScore1 - expected1));
		toReturn[1] = (int) Math.round(eloL + Constants.KFACTOR * (simpleScore2 - expected2));
		
		return toReturn;
	}

	/*
	 * Convert the given name to fit requirements.
	 * 
	 * For example, all '+' are changed to 'and'
	 * 
	 * Removes all sponsor tags.
	 */
	private static String sanitizeName(String name) {
		if (name.indexOf('|') != -1) {
			//remove sponsor tag
			name = name.substring(name.indexOf('|') + 1);
		}
		
		if (name.indexOf('+') != -1) {
			name = name.substring(0, name.indexOf('+')).trim() + " and " + name.substring(name.indexOf('+') + 1).trim();
		}
		
		//remove nicknames
		int firstOccurence = name.indexOf('\"');
		if (firstOccurence != -1) {
			//plus 2 - eat space after end of second quote
			name = name.substring(0, firstOccurence) + name.substring(name.indexOf('\"', firstOccurence + 1) + 2);
		}
		
		return name.trim();
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
			
			for (String flag : Constants.DISCARD_FLAGS) {
				CharSequence f = flag;
				
				if (name.contains(f)) {
					// if this name contains one of the flags, remove it
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
			
			if (Constants.isNull(gameIdString) || Integer.parseInt(gameIdString) != Constants.GAME_ID) {
				toRemove.add(j);
			}
		}
		
		//removing all undesirable records
		for (JSONObject i : toRemove) {
			arr.remove(i);
		}
	}
	
	/*
	 * Removes any unwanted matches.
	 * 
	 * Removes any matches score 0-0, or disqualifications (one player scored -1);
	 */
	private static void filterMatches(ArrayList<JSONObject> arr) {
		// if either of the players do not exist in the database, do not log this entry
		// this means that one of players was filtered out
		
		ArrayList<JSONObject> toRemove = new ArrayList<JSONObject>();
		
		for (JSONObject j : arr) {
			String scoreString = j.getString("scores_csv");

			int scores[] = getScores(scoreString);
			
			if (scores[0] == 0 && scores[1] == 0) {
				toRemove.add(j);
			} else if (scores[0] < 0 || scores[1] < 0) {
				//disqualification
				toRemove.add(j);
			}
		}
		
		for (JSONObject j : toRemove) {
			arr.remove(j);
		}
	}
	
	private static int[] getScores(String scores) {
		int[] toReturn = new int[] {0, 0};
		
		int commaIndex;
		
		do {
			commaIndex = scores.indexOf(',');
			
			//start searching after the first value -- if the first index is  a '-', it just means the first number is a negative
			toReturn[0] += Integer.parseInt(scores.substring(0, scores.indexOf('-', 1)));
			toReturn[1] += Integer.parseInt(scores.substring(scores.indexOf('-', 1) + 1, (commaIndex == -1 ? scores.length() : commaIndex)));
			
			if (commaIndex != -1) {
				//move scores string forward
				scores = scores.substring(commaIndex + 1);
			}
		} while (commaIndex != -1);
		
		return toReturn;
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
		if (response.isSuccessful()) {
			json = getJson(response);
		} // else, return null
		
		return json;
	}
}
