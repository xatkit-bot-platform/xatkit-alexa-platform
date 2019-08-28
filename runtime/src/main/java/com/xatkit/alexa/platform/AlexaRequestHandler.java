package com.xatkit.alexa.platform;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xatkit.alexa.AlexaUtils;

import fr.inria.atlanmod.commons.log.Log;

/**
 * A class that processes Alexa requests body coming from {@link AlexaPlatform} REST endpoint
 * This class is part of xatkit's core platforms, and can be used in an execution model by importing the
 * <i>AlexaPlatform</i> package.
 */
public class AlexaRequestHandler {

	private JsonElement content;	//Variable used to store the entire request coming from Alexa
	private AlexaUtils alexaUtils;	//Alexa utils keychain to retrieve configuration variables
	
	//Parsed variables
	private boolean launchRequest;	//Flags the Alexa request as a "LaunchRequest" via invocation name
	private String generalIntent;	//Gets the eventual general intent value to be parsed into intent discovery
	
	public AlexaRequestHandler(JsonElement content) {
		this.content = content;
		
		//Parses content values to retrieve Xatkit key values
        JsonObject contentObject = this.content.getAsJsonObject();
        
        //Gets request section
        JsonObject request = contentObject.getAsJsonObject("request");
        
        //Flags request as invocation type
        this.launchRequest = request.get("type").getAsString().equals("LaunchRequest");
        
        //Continues parsing to retrieve general intent
        if(!this.isLaunchRequest()) {
        	//Retrieves JSON branch
        	JsonObject generalIntent = request.get("intent").getAsJsonObject()
        			                          .get("slots").getAsJsonObject()
        									  .get("general_intent").getAsJsonObject();
        	//Retrieves intent value
        	this.generalIntent = generalIntent.get("value").getAsString();        	
        }
	}

	//Tells if the request is a invocation launch request
	private boolean isLaunchRequest() {
		return this.launchRequest;
	}

	//Creates a basic response based on the type of request sent by alexa
	public JsonObject respondPlainText() {
		
		//Creates resulting JSONObject and sub-branches
		JsonObject result = new JsonObject();
        JsonObject response = new JsonObject();
        JsonObject outputSpeech = new JsonObject();
        
        //Adds basic informations
        result.addProperty("version", "1.0");
        response.addProperty("shouldEndSession", false);
        outputSpeech.addProperty("type", "PlainText");
        
        //Checks if it is an invocation request 
        if(this.isLaunchRequest())
        	//TODO: Retrieve and send Welcome message from AlexaUtils and configuration
        	outputSpeech.addProperty("text", "Hello! welcome to xatkit! What can I do for you?");
        else
        	//TODO: Retrieve intent and check presence for Alexa.Reply??
        	outputSpeech.addProperty("text", this.generalIntent);
        	
        //Adds sub-branches to final response
        response.add("outputSpeech", outputSpeech);
        result.add("response", response);
        
        //Logs response
        Log.info("sent {0}",result.toString());
        
		return result;
	}
	
	

}
