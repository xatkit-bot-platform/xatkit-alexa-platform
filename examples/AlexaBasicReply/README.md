# Xatkit - Alexa Platform
Welcome to the Alexa Platform for Xatkit wiki!

This platform aims to integrate **Alexa voice recognition** inside a Xatkit application, using the intent library defined in the bot to let the flow of the conversation follow its path. Deploying an Alexa skill will now be simple and fast and will be compatible with your previously developed Xatkit bots.

## Create your Xatkit bot with Alexa Platform

### Deploy the Alexa intent provider

Adding **Alexa intent provider** to your Xatkit applications is really simple. If you haven't already, we strongly suggest you to follow the [main tutorial](https://github.com/xatkit-bot-platform/xatkit-runtime/wiki) so that you will get a good overview on how to handle the various aspects of the framework.
> Note: This tutorial will use the `AlexaBasicReply` [example](https://github.com/xatkit-bot-platform/xatkit-alexa-platform/tree/master/examples/AlexaBasicReply) to illustrate the intent provider and Reply action usage.

In your `*.execution` file you will need to add the import for the `AlexaPlatform` with the consequent declaration of the usage of the `AlexaIntentProvider`, as shown below:

```xtext
import library "AlexaBasicReply/AlexaBasicReply-Lib.xmi" as AlexaBasicReplyLib
import library "CoreLibrary"
import platform "AlexaPlatform"

use provider AlexaPlatform.AlexaIntentProvider
```
This will create a _REST Endpoint_ in Xatkit runtime on `alexa/receiver/` when you will launch it in the terminal:
```bash
[INFO]  18:42:05,358 - Registering com.xatkit.alexa.platform.io.AlexaIntentProvider@1672d2c4 in the XatkitServer
[INFO]  18:42:05,358 - Starting RuntimeEventProvider AlexaIntentProvider
```
### Use ReplyAction

We currently have only one simple action you can perform to respond to Alexa requests, which takes as an argument a `message` string. You can use it in your execution file as follows:
```xtext
on intent Basic do 
	action AlexaPlatform.Reply(message: "Hi! How are you?")
```
Note that a Reply action should always be included inside an intent action covered by the Alexa dialog flow. Otherwise, since Alexa expects a result for each request that has been sent, the `xatkit.alexa.responseNotFoundMessage` defined in the `.properties` file will be use.

# Deploy your Alexa skill
Now that you have a working and running Xatkit environment ready to talk with Alexa, you need to create an **Amazon Developer account** to start deploying Alexa skills. If you don't have one already, you can create it [HERE](https://developer.amazon.com).

## Create a new Skill

To create Skills, go to your [Alexa Developer Console](https://developer.amazon.com/alexa/console/ask) and [Create new Skill](https://developer.amazon.com/alexa/console/ask/create-new-skill). Enter your new Skill name and choose **Custom** as model and later **Start from scratch** as template.

For each skill you create, you must use the `xatkit-skill.json` you can find in this repository [HERE](https://github.com/xatkit-bot-platform/xatkit-alexa-platform/tree/master/json-skill). A Skill developed to interface Xatkit has to handle each user input as a _XATKITGeneral custom slot value_ and pass it through a _GeneralIntent_. To do so, copy and paste the `xatkit-skill.json` file content in the **JSON Editor** menu you can find in the Alexa developer console.

Then, click on **Save Model** and **Build Model**. Your skill is now ready to work with Xatkit!
> Remember that you can change the **Invocation name** as you prefer. Also, for each change you do on the Skill, you must save and build the model back.

### Change permissions

If you want to ask the user for the Permissions to use its **Full Name** and store it under the `username` context value, you need to go to the **PERMISSIONS** section on the console menu and select **Full Name** under **Customer Name**.

## Add the Endpoint
To have a Skill working you need a public REST Endpoint that works over HTTPS. If you have one already, you can add it to the **Endpoint** section of the console menu, under the **HTTPS** choice, on the region(s) you prefer. Remember to save and build the model.

Otherwise, you can follow [this tutorial](https://github.com/xatkit-bot-platform/xatkit-alexa-platform/tree/master/examples/AlexaBasicReply#deploy-ngrok) to know how to create your hosted endpoint using **Ngrok**

## Test your skill
Head over to the **Test** section from the Alexa developer console **topbar**. If it is the first time you open that up, you will need to change the Test permission from Off to **Development**. Now you can start talking to Xatkit through this skill, using the **Alexa simulator**!

-------------------------------

## Deploy Ngrok

If you're here, it's beacause you need a fast (and free) way to obtain a public reachable Endpoint for your deployed Alexa skill that has to work with Xatkit. You can accomplish this task using the **Ngrok** service.
To use the HTTPS Tunneling you need, first go to [their website](https://ngrok.com/) and **Sign up**. Then, head to the [download area](https://ngrok.com/download) and follow their **4-steps instructions** until the last one, where you have to put port **5000** instead of 80 in the command line to have Xatkit server exposed publically:

```bash
./ngrok http 5000
```

You will notice two **Forwarding** voices on the console, like this:

```bash
Forwarding                    http://xxxxxxxx.ngrok.io -> http://localhost:5000
Forwarding                    https://xxxxxxxx.ngrok.io -> http://localhost:5000
```
Copy the **https** one (_https://xxxxxxxx.ngrok.io_), go to the **Endpoint** section of your skill in the Alexa developer console, select **HTTPS** mode and paste it in the region field you desire. Under that, select the option that says
```
My development endpoint is a sub-domain of a domain that has a wildcard certificate from a certificate authority
```
Save and Build the model. Your Alexa skill is now ready to talk with Xatkit, [go out and test it](https://github.com/xatkit-bot-platform/xatkit-alexa-platform/tree/master/examples/AlexaBasicReply#test-your-skill)!
> Note: Ngrok service remains open until you close the terminal or restart your computer. There's need to restart it every time you restart your Xatkit bot, but every time you reload the service you need to change the endpoint on your skill.
