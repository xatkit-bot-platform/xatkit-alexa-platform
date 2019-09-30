package com.xatkit.alexa.platform.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.chat.platform.ChatPlatform;

import fr.inria.atlanmod.commons.log.Log;

/**
 * A class that sends request to the alexa api Endpoint 
 */
public class AlexaAPIClient{
	
	private String apiEndpoint;
	private String apiAccessToken;
	private XatkitSession session;
	
	private static String NAME_RESOURCE_URL = "/v2/accounts/~current/settings/Profile.name";	//Resource to interview to get user name  
	
	private String response = "";
	
	public AlexaAPIClient(String apiEndpoint, String apiAccessToken, XatkitSession session) {
		this.apiEndpoint = apiEndpoint;
		this.apiAccessToken = apiAccessToken;
		this.session = session;
		
		Log.info("Sending Alexa API request to retrieve username");
		try {	
			//Request creation and header setting
			URL url = new URL(this.apiEndpoint + AlexaAPIClient.NAME_RESOURCE_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Authorization", "Bearer " + this.apiAccessToken);			
			
			//Error in connection
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed to retrieve user name from Alexa API: HTTP error code : "
						  					+ conn.getResponseCode());
			}
			
			//Read Alexa API Server response
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			Log.info("Output from Alexa API Server .... \n");
			/*while ((*/this.response += br.readLine();/*) != null) {}*/
			Log.info("{0}",this.response);
						
			//Controls the presence of '{' chars to indicate missing authorizations or server errors
			if(this.response.contains("{")) {
				Log.error("Authorization failure detected when retrieving Full Name");
				this.response = "";
			}
			
			//Strips bad chars
			this.response = this.response.replaceAll("\"", "");
			
			conn.disconnect();			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getResponse() {
		return response;
	}
}
