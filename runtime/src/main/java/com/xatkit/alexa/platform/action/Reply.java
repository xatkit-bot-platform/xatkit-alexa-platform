package com.xatkit.alexa.platform.action;

import com.xatkit.alexa.AlexaUtils;
import com.xatkit.alexa.platform.AlexaPlatform;
import com.xatkit.core.platform.action.RuntimeMessageAction;
import com.xatkit.core.session.XatkitSession;

import fr.inria.atlanmod.commons.log.Log;;

public class Reply extends RuntimeMessageAction<AlexaPlatform> {

	private String sessionID;
	
	public Reply(AlexaPlatform runtimePlatform, XatkitSession session, String message) {
		super(runtimePlatform, session, message);
	}

	@Override
	protected Object compute() throws Exception {
		// The request is retrieved from the new context created from the sessionId
		String requestId = (String) this.session.getRuntimeContexts().getContextValue(this.sessionID, AlexaUtils.ALEXA_REQUEST_ID_CONTEXT_KEY);
		Log.info("Request found: {0}", requestId);
		this.runtimePlatform.storeMessage(requestId, message);
		return null;
	}

	@Override
	protected XatkitSession getClientSession() {
		// TODO this should be initialized with the payload content
		/**
		 * COMPLETED: Now the SessionID field is used to create a new dedicated context in a non-launch request
		 * in the AlexaRestHandler. Inside it, the requestId is stored, so that all of the conversation messages
		 * are inside a separate context.
		 */
		this.sessionID = (String) this.session.getRuntimeContexts().getContextValue(AlexaUtils.ALEXA_CONTEXT_KEY, AlexaUtils.ALEXA_SESSION_ID_CONTEXT_KEY);
		Log.info("Session found: {0}", sessionID);
		return this.runtimePlatform.getXatkitCore().getOrCreateXatkitSession(this.sessionID);
	}
}
