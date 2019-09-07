package com.xatkit.alexa.platform;

import com.xatkit.alexa.AlexaUtils;
import com.xatkit.core.XatkitCore;
import com.xatkit.plugins.chat.platform.ChatPlatform;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link ChatPlatform} class that interacts with Alexa Voice API
 * This class is part of xatkit's core platforms, and can be used in an execution model by importing the
 * <i>AlexaPlatform</i> package.
 */
public class AlexaPlatform extends ChatPlatform {

    private Map<String, String> storedMessages;

    private String invocationMessage;
    private String responseNotFoundMessage;

    public AlexaPlatform(XatkitCore xatkitCore, Configuration configuration) {
        super(xatkitCore, configuration);
        Log.info("{0} started", this.getClass().getSimpleName());
        this.storedMessages = new ConcurrentHashMap<>();
        this.invocationMessage = configuration.getString(AlexaUtils.ALEXA_INVOCATION_MESSAGE_KEY,
                AlexaUtils.DEFAULT_ALEXA_INVOCATION_MESSAGE);
        this.responseNotFoundMessage = configuration.getString(AlexaUtils.ALEXA_RESPONSE_NOT_FOUND_MESSAGE_KEY,
                AlexaUtils.DEFAULT_ALEXA_RESPONSE_NOT_FOUND_MESSAGE);
    }

    public void storeMessage(String requestId, String message) {
        if (this.storedMessages.containsKey(requestId)) {
            Log.warn("There is already a message stored for the ID {0} ({1}), erasing it", requestId,
                    this.storedMessages.get(requestId));
        }
        this.storedMessages.put(requestId, message);
    }

    public String getMessage(String requestId) {
        String message = this.storedMessages.get(requestId);
        this.storedMessages.remove(requestId);
        return message;
    }

    public String getInvocationMessage() {
        return this.invocationMessage;
    }

    public String getResponseNotFoundMessage() {
        return this.responseNotFoundMessage;
    }
}
