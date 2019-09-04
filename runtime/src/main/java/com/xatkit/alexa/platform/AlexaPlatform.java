package com.xatkit.alexa.platform;

import com.xatkit.core.XatkitCore;
import com.xatkit.plugins.chat.platform.ChatPlatform;
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
		Log.info("{0} started", this.getClass().getSimpleName());
	}
}
