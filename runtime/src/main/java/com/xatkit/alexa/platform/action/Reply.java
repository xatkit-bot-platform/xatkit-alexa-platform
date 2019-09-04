package com.xatkit.alexa.platform.action;

import com.xatkit.alexa.platform.AlexaPlatform;
import com.xatkit.core.platform.action.RuntimeAction;
import com.xatkit.core.platform.action.RuntimeMessageAction;
import com.xatkit.core.session.XatkitSession;;

public class Reply extends RuntimeMessageAction<AlexaPlatform> {

	public Reply(AlexaPlatform runtimePlatform, XatkitSession session, String message) {
		super(runtimePlatform, session, message);
	}

	@Override
	protected Object compute() throws Exception {
		String requestId = (String) this.session.getRuntimeContexts().getContextValue("alexa", "requestId");
		this.runtimePlatform.storeMessage(requestId, message);
		return null;
	}

	@Override
	protected XatkitSession getClientSession() {
		// TODO this should be initialized with the payload content
		return this.runtimePlatform.getXatkitCore().getOrCreateXatkitSession("alexa");
	}
}
