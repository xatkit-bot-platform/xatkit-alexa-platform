package com.xatkit.alexa.platform;

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

	public AlexaPlatform(XatkitCore xatkitCore, Configuration configuration) {
		super(xatkitCore,configuration);
		Log.info("Alexa core service started");
		this.getXatkitCore().getXatkitServer().registerRestEndpoint("/alexa/receiver", 
		 (headers, param, content) -> {
                JsonObject contentObject = content.getAsJsonObject();
                String contentWhole = contentObject.getAsString();
                Log.info("Content: {0}", contentWhole);
                JsonObject result = new JsonObject();
                return result;
            });
	}
}
