Xatkit Alexa Platform
=====

[![License Badge](https://img.shields.io/badge/license-EPL%202.0-brightgreen.svg)](https://opensource.org/licenses/EPL-2.0)
[![Build Status](https://travis-ci.com/xatkit-bot-platform/xatkit-alexa-platform.svg?branch=master)](https://travis-ci.com/xatkit-bot-platform/xatkit-alexa-platform)
[![Wiki Badge](https://img.shields.io/badge/doc-wiki-blue)](https://github.com/xatkit-bot-platform/xatkit-releases/wiki/Xatkit-Alexa-Platform)

Create an Alexa Skill server and respond to the user using the Xatkit defined intent library.

## Providers

| Provider                   | Type  | Context Parameters | Description                                                  |
| -------------------------- | ----- | ------------------ | ------------------------------------------------------------ |
| AlexaIntentProvider | Intent | - `alexa.request_id`: the unique identifier of the Alexa single request<br/> - `alexa.username`: the full name (*empty* if not available from permissions) of the Amazon user that sent the request<br/> - `alexa.user_id`: the Alexa unique identifier of the user that sent the request<br/> - `alexa.session_id`: the Alexa unique identifier of the ongoing and active conversation for the user<br/> - `<<sessionId>>`: A generated unique context containing all the replies per conversation. Used by the Reply action mechanism| Receive messages from Alexa requests and translates them into Xatkit-compatible intents.|


## Actions

| Action  | Parameters | Return                                  | Return Type | Description                                     |
| ------- | ---------- | --------------------------------------- | ----------- | ----------------------------------------------- |
| Reply | - `message` (**String**): the content of the PlainText that has to be spoken by Alexa devices | `null` | Integer | Creates a session variable storing the reply message to be sent back to Alexa as a PlainText |

## Options

The Alexa platform supports the following configuration options

| Key                  | Values | Description                                                  | Constraint    |
| -------------------- | ------ | ------------------------------------------------------------ | ------------- |
| `xatkit.alexa.invocationMessage` | String | The message that Xatkit uses to respond back to the user once the Alexa skill it's launched. It is not possible to retrieve an intent from the `LaunchRequest`  | **Optional** |
| `xatkit.alexa.responseNotFoundMessage` | String | The message that Xatkit uses to respond back to Alexa in the case something wrong happens during the processing of the request  | **Optional** |

## Usage

Check [our tutorial here](https://github.com/xatkit-bot-platform/xatkit-alexa-platform/tree/master/examples/AlexaBasicReply#xatkit---alexa-platform) to know how to **deploy an Alexa skill** and connect it to **Xatkit** 
