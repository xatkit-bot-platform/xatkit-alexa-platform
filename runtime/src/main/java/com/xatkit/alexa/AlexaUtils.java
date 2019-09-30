package com.xatkit.alexa;

import com.xatkit.plugins.chat.ChatUtils;

public interface AlexaUtils extends ChatUtils {
	
	String ALEXA_API_KEY = "xatkit.alexa.api.key";
	
	String ALEXA_CONTEXT_KEY = "alexa";

	String ALEXA_REQUEST_ID_CONTEXT_KEY = "request_id";
	
	String ALEXA_SESSION_ID_CONTEXT_KEY = "session_id";
	
	String ALEXA_USERID_CONTEXT_KEY = "user_id";

	String ALEXA_INVOCATION_MESSAGE_KEY = "xatkit.alexa.invocationMessage"; //Key to retrieve default invocation welcome message

	String DEFAULT_ALEXA_INVOCATION_MESSAGE = "Hello! Welcome to Xatkit! What can I do for you?";

	String ALEXA_RESPONSE_NOT_FOUND_MESSAGE_KEY = "xatkit.alexa.responseNotFoundMessage"; //Key to retrieve default message to send to Alexa
	
	String DEFAULT_ALEXA_RESPONSE_NOT_FOUND_MESSAGE = "Xatkit could not respond to your request, can you please try again?";
}
