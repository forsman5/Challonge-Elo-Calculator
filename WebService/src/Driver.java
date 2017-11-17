import java.io.IOException;

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
	/*
	 * tester
	 */
	public static void main (String [] args) {
		getTournaments();
	}
	
	// methods may be placed in here, to be moved later depending on future architecting
	public static void getTournaments() {
		
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		  .url("https://api.challonge.com/v1/tournaments.json?state=ended&api_key=jp1XS5QmgVgYe6HZqGpHoOBneFY2RtCVkRdSIhKm")
		  .get()
		  .addHeader("cache-control", "no-cache")
		  .build();
		
		Response response = null;
		String  body = null;
		
		try {
			response = client.newCall(request).execute();
				
			body = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * it is a json object of json objects...???
		 */
		//JSONObject json = new JSONObject(body.substring(1, body.length() - 1));
		
		//System.out.println(json.get("name"));
		
		//void for now...
		//return a set of tournaments?
	}
}
