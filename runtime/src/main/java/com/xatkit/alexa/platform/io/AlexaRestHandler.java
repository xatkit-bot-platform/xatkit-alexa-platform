package com.xatkit.alexa.platform.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.server.JsonRestHandler;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.intent.RecognizedIntent;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.http.Header;
import org.apache.http.NameValuePair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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

            // set the session after the recognition because of decrementLifespan
            session.getRuntimeContexts().setContextValue("chat", 1, "username", "toto");
            session.getRuntimeContexts().setContextValue("chat", 1, "channel", "chan");
            session.getRuntimeContexts().setContextValue("chat", 1, "rawMessage", generalIntent);


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
            outputSpeech.addProperty("text", "Hello! Welcome to Xatkit! What can I do for you?");
        } else {
            outputSpeech.addProperty("text", generalIntent);
        }

        // Adds sub-branches to final response
        response.add("outputSpeech", outputSpeech);
        result.add("response", response);

        // Logs response
        Log.info("Sent {0}", result.toString());

        return result;
    }
}
