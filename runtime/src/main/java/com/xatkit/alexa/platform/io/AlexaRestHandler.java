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
        // The userId value to save in session
        String userId= null;
        // The sessionId value to save in channel
        String sessionId = null;

        XatkitSession session = provider.getRuntimePlatform().getXatkitCore().getOrCreateXatkitSession("alexa");

        // Continues parsing to retrieve general intent
        // Retrieves JSON branches
    	
    	// USERID
        JsonObject userIDObject = contentObject.get("context").getAsJsonObject()
                .get("System").getAsJsonObject()
                .get("user").getAsJsonObject();
        userId = userIDObject.get("userId").getAsString();
        Log.info("Found userId: {0}", userId);

        // SESSIONID
        JsonObject sessionIDObject = contentObject.get("session").getAsJsonObject();
        sessionId = sessionIDObject.get("sessionId").getAsString();
        Log.info("Found sessionId: {0}", sessionId);
        

        if (!isLaunchRequest) {
            // this is not a launch request, we need to retrieve the general intent (i.e. the input sentence)
        	//GENERAL INTENT
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
            /* COMPLETED 
             * API request is handled during LaunchIntent and saved as a message corresponding to the userId
             */
            //Retrieves username from runtime Platform as got from API request during launchIntent
            String username = this.provider.getRuntimePlatform().getMessage(userId);
            
            session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                    ChatUtils.CHAT_USERNAME_CONTEXT_KEY, username);
            session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                    ChatUtils.CHAT_CHANNEL_CONTEXT_KEY, sessionId);
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
        	/* COMPLETED */
            outputSpeech.addProperty("text", this.provider.getRuntimePlatform().getInvocationMessage());
            //Requests username to Alexa API services
            //Checks if corresponding permissions are loaded
            JsonObject permissionsObject = contentObject.get("context").getAsJsonObject()
                    .get("System").getAsJsonObject()
                    .get("user").getAsJsonObject()
                    .get("permissions").getAsJsonObject();
            
            this.provider.getRuntimePlatform().storeMessage(userId, "");
            
            if(permissionsObject != null) {
            	String apiAccessToken = permissionsObject.get("consentToken").getAsString();            
                Log.info("Found permission access token, requesting username");
                
                //Gets api endpoint
                JsonObject endpointObject = contentObject.get("context").getAsJsonObject()
                        .get("System").getAsJsonObject();
                String apiEndpoint = endpointObject.get("apiEndpoint").getAsString();
                
                //Sends request
                AlexaAPIClient alexaAPIClient = new AlexaAPIClient(apiEndpoint, apiAccessToken, session);
                Log.info("name: {0}",alexaAPIClient.getResponse());
                
                //Stores request as userId username
                this.provider.getRuntimePlatform().storeMessage(userId, alexaAPIClient.getResponse());
            }            
            
        } else {
            /*
             * Quickfix: wait here to be sure the Reply action has been executed. A better implementation would be to
             *  be notified when a new message is available for this requestId.
             */
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
            String responseMessage = this.provider.getRuntimePlatform().getMessage(requestId);
            if (nonNull(responseMessage)) {
                outputSpeech.addProperty("text", responseMessage);
            } else {
                // TODO this should not throw an exception, it is possible that the bot doesn't return anything. What
                //  should we do in this case?
            	/* COMPLETED Added property RESPONSE_NOT_FOUND_MESSAGE to AlexaPlatform. In the remote case a response
            	 *  cannot be retrieved (that should be fixed with the Notification pattern for reply retrieval) or it
            	 *  was not defined by the bot developer (Alexa should always be able to get it's response back to 
            	 *  provide the user a feedback), a default message, customizable in .properties file can be defined.
            	 * 
            	 *  All the requests decaying because of intent not found should be managed under Default_Fallback_Intent
            	 *  defined in the .execution file for the bot with a reserved AlexaPlatform.Reply action. If that particular
            	 *  intent will not be defined, the default response will be the RESPONSE_NOT_FOUND_MESSAGE as well.
            	 */            	
                outputSpeech.addProperty("text", this.provider.getRuntimePlatform().getResponseNotFoundMessage());
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
