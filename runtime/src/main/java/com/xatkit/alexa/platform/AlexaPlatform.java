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
                
                //LOGGING
                Log.info("Content: {0}", contentObject.getAsString());
                
                //Builds a 
                JsonObject result = new JsonObject();
                return result;
            });
	}
}
