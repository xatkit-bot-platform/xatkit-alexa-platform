# alexa-platform
Receive inputs and reply through Alexa. 

Requires a basic alexa skill deployed on Amazon Developer Console and ngrok running on the same machine of Xatkit server to allow the comunication via REST Endpoint.

Basic "Xatkit" Alexa skill JSON implementation can be found under /json-skill/xatkit-skill.json. Invocation name can be changed at own pleasure. The general_intent definition will be read by Xatkit server.

When defining the configuration file for Xatkit application, one should define a ALEXA_INVOCATION_MESSAGE that will be replied back to Alexa automatically when invocating the skill.