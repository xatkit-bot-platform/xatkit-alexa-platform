package com.xatkit.alexa.platform.io;

import com.xatkit.alexa.platform.AlexaPlatform;
import com.xatkit.plugins.chat.platform.io.WebhookChatIntentProvider;
import org.apache.commons.configuration2.Configuration;

public class AlexaIntentProvider extends WebhookChatIntentProvider<AlexaPlatform, AlexaRestHandler> {

	private final static String ENDPOINT_URI = "/alexa/receiver";

	public AlexaIntentProvider(AlexaPlatform runtimePlatform, Configuration configuration) {
		super(runtimePlatform, configuration);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getEndpointURI() {
		return ENDPOINT_URI;
	}

	@Override
	protected AlexaRestHandler createRestHandler() {
		return new AlexaRestHandler(this);
	}
}
