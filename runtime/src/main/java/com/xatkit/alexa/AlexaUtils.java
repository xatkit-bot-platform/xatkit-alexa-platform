package com.xatkit.alexa;

import com.xatkit.plugins.chat.ChatUtils;

public interface AlexaUtils extends ChatUtils {
	
	String ALEXA_API_KEY = "xatkit.alexa.api.key";
	
	String ALEXA_CONTEXT_KEY = "alexa";

	String ALEXA_REQUEST_ID_CONTEXT_KEY = "requestId";

	String ALEXA_INVOCATION_MESSAGE_KEY = "alexa.invocation.message"; //Key to retrieve default invocation welcome message

	String DEFAULT_ALEXA_INVOCATION_MESSAGE = "Hello! Welcome to Xatkit! What can I do for you?";
}
