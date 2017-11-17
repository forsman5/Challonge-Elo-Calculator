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
		getTournaments(getLastCheckedDate());
		
		//update last checked
		saveNewTimeChecked(System.currentTimeMillis());
	}
	
	/*
	 * Load the last date everything was checked out of database memory
	 */
	private static Calendar getLastCheckedDate() {
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
		}
		return cal;
	}
	
	/*
	 * Save the toSave parameter to sql, as the time the challonge api was last checked
	 * 
	 * toSave represents the number of milliseconds since Jan 1 1970 (the unix epoch)
	 */
	private static void saveNewTimeChecked(long toSave) {
		//TODO
	}

	// methods may be placed in here, to be moved later depending on future architecting
	public static void getTournaments(Calendar lastChecked) {
		
		//get created after string
		String createdAfter = getDateAsString(lastChecked);
		
		OkHttpClient client = new OkHttpClient();

		//for most tournaments
		String reqUrl = "https://api.challonge.com/v1/tournaments.json?state=ended&api_key=" + Constants.API_KEY + "&created_after="+createdAfter;
		
		//for any tournaments hosted in subdomains
		//TODO idea: make subdomain constant an array, for many subdomains. Then, have this handle multiple different subdomains
		String subUrl = reqUrl + "&subdomain=" + Constants.SUBDOMAIN_NAME;
		
		Request request = getNewRequest(reqUrl);
		Request subdomain = getNewRequest(subUrl);
		
		//execute new requests
		
		Response response = null;
		Response subResponse = null;
		
		try {
			response = client.newCall(request).execute();
			subResponse = client.newCall(subdomain).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//check for failures
		ensureSuccess(response);
		ensureSuccess(subResponse);
		
		ArrayList<JSONObject> json = getJson(response);
		ArrayList<JSONObject> subJson = getJson(subResponse);
		
		/*
		 * Adding all json objects from the subdomain response into the main
		 */
		for (JSONObject j : subJson) {
			json.add(j);
		}
		
		//debugging TODO
		for (JSONObject j : json) {
			System.out.println(j.get("name"));
		}
		
		
		
		//void for now...
		//return a set of tournaments?
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
	
	private static void ensureSuccess(Response rsp) {
		//TODO
		//check to make sure both returned with 200 OK, if not, do something -- exception, logging, send an email...
	}
}
