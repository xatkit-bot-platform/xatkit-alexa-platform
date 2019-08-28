package com.xatkit.alexa.platform;

import com.xatkit.alexa.AlexaUtils;
import com.xatkit.core.XatkitCore;

import com.xatkit.plugins.chat.platform.ChatPlatform;

import com.google.gson.JsonObject;


import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;

/**
 * A {@link ChatPlatform} class that interacts with Alexa Voice API
 * This class is part of xatkit's core platforms, and can be used in an execution model by importing the
 * <i>AlexaPlatform</i> package.
 */
public class AlexaPlatform extends ChatPlatform {	

	//Alexa keychain
	private AlexaUtils alexaUtils;
	
	public AlexaPlatform(XatkitCore xatkitCore, Configuration configuration) {
		super(xatkitCore,configuration);
		
		//LOGGING
		Log.info("Alexa core service started");
		
		//Registers a REST Endpoint to respond to alexa requests
		this.getXatkitCore().getXatkitServer().registerRestEndpoint("/alexa/receiver", 
		 (headers, param, content) -> {
			 	
			 	//Retrieves request body content
                JsonObject contentObject = content.getAsJsonObject();
                
                //Gets request section
                JsonObject request = contentObject.getAsJsonObject("request");                
                
                //Builds a response object
                JsonObject result = new JsonObject();
                
                JsonObject response = new JsonObject();
                JsonObject outputSpeech = new JsonObject();
                
                result.addProperty("version", "1.0");
                
                outputSpeech.addProperty("type", "PlainText");
                
                //Checks if invocation
                if(request.get("type").getAsString().equals("LaunchRequest"))
                	outputSpeech.addProperty("text", "Hello! welcome to xatkit! What can I do for you?");
                else {
                	JsonObject intent = request.get("intent").getAsJsonObject();
                	JsonObject slots = intent.get("slots").getAsJsonObject();
                	JsonObject generalIntent = slots.get("general_intent").getAsJsonObject();
                	
                	outputSpeech.addProperty("text", generalIntent.get("value").getAsString());
                }
                	
                
                response.add("outputSpeech", outputSpeech);
                
                response.addProperty("shouldEndSession", false);
                
                result.add("response", response);
                
                Log.info("sent {0}",result.toString());
                
                return result;
            });
	}
}
