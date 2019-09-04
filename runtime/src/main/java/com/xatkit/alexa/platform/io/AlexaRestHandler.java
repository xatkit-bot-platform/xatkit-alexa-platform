package com.xatkit.alexa.platform.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xatkit.alexa.AlexaUtils;
import com.xatkit.core.XatkitException;
import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.server.JsonRestHandler;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.intent.RecognizedIntent;
import com.xatkit.plugins.chat.ChatUtils;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.http.Header;
import org.apache.http.NameValuePair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static java.util.Objects.nonNull;

public class AlexaRestHandler extends JsonRestHandler {

    private AlexaIntentProvider provider;

    public AlexaRestHandler(AlexaIntentProvider provider) {
        super();
        this.provider = provider;
    }

    @Override
    public JsonElement handleParsedContent(@Nonnull List<Header> headers, @Nonnull List<NameValuePair> params,
                                           @Nullable JsonElement jsonElement) {

        JsonObject contentObject = jsonElement.getAsJsonObject();

        // Gets request section
        JsonObject request = contentObject.getAsJsonObject("request");
        String requestId = request.get("requestId").getAsString();
        Log.info("Request ID: {0}", requestId);

        // Flags request as invocation type
        boolean isLaunchRequest = request.get("type").getAsString().equals("LaunchRequest");

        // The eventual general intent value to be parsed into intent discovery
        String generalIntent = null;

        XatkitSession session = provider.getRuntimePlatform().getXatkitCore().getOrCreateXatkitSession("alexa");

        // Continues parsing to retrieve general intent
        if (!isLaunchRequest) {
            // this is not a launch request, we need to retrieve the general intent (i.e. the input sentence)

            // Retrieves JSON branch
            JsonObject generalIntentObject = request.get("intent").getAsJsonObject()
                    .get("slots").getAsJsonObject()
                    .get("general_intent").getAsJsonObject();
            generalIntent = generalIntentObject.get("value").getAsString();
            Log.info("Found general intent: {0}", generalIntent);

            RecognizedIntent intent = IntentRecognitionHelper.getRecognizedIntent(generalIntent, session,
                    provider.getRuntimePlatform().getXatkitCore());

            /*
             * Set the session after the recognition because decrementLifespan will remove them otherwise.
             * Note that we need to set the chat-related context values because AlexaPlatform extends ChatPlatform.
             * The ChatPlatform superclass will check that these context values are set before propagating the intent
             *  to the execution engine.
             */
            // TODO: I don't know if we can find the username from the payload content?
            session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                    ChatUtils.CHAT_USERNAME_CONTEXT_KEY, "toto");
            // This should be set with an identifier from the payload content that represent the channel/session
            session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                    ChatUtils.CHAT_CHANNEL_CONTEXT_KEY, "chan");
            session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                    ChatUtils.CHAT_RAW_MESSAGE_CONTEXT_KEY, generalIntent);
            session.getRuntimeContexts().setContextValue(AlexaUtils.ALEXA_CONTEXT_KEY, 1,
                    AlexaUtils.ALEXA_REQUEST_ID_CONTEXT_KEY, requestId);

            provider.sendEventInstance(intent, session);
        }

        JsonObject result = new JsonObject();
        JsonObject response = new JsonObject();
        JsonObject outputSpeech = new JsonObject();

        // Adds basic information
        result.addProperty("version", "1.0");
        response.addProperty("shouldEndSession", false);
        outputSpeech.addProperty("type", "PlainText");

        // Check if it is an invocation request
        if (isLaunchRequest) {
            // TODO: retrieve and send welcome message from AlexaUtils and configuration
            outputSpeech.addProperty("text", this.provider.getRuntimePlatform().getInvocationMessage());
        } else {
            /*
             * Quickfix: wait here to be sure the Reply action has been executed. A better implementation would be to
             *  be notified when a new message is available for this requestId.
             */
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {

            }
            String responseMessage = this.provider.getRuntimePlatform().getMessage(requestId);
            if (nonNull(responseMessage)) {
                outputSpeech.addProperty("text", responseMessage);
            } else {
                // TODO this should not throw an exception, it is possible that the bot doesn't return anything. What
                //  should we do in this case?
                throw new XatkitException("No reply message found for Alexa request");
            }
        }

        // Adds sub-branches to final response
        response.add("outputSpeech", outputSpeech);
        result.add("response", response);

        // Logs response
        Log.info("Sent {0}", result.toString());

        return result;
    }
}
