TwiML Module
===========

    !!!!THIS MODULE IS UNDER DEVELOPMENT!!!!

A Mule module for generating Twilios Markup Language. Twilio can handle instructions for calls and SMS messages in real
time from iON applications. When an SMS or incoming call is received, Twilio looks up the iON app associated with the
phone number called and makes a request to it. iON will respond to the request and that response will decides how the
call should proceed by returning a Twilio Markup XML (TwiML) document telling Twilio to say text to the caller, send
an SMS message, play audio files, get input from the keypad, record audio, connect the call to another phone and more.

TwiML is similar to HTML. Just as HTML is rendered in a browser to display a webpage, TwiML is 'rendered' by Twilio
to the caller. Only one TwiML document is rendered to the caller at once but many documents can be linked together
to build complex interactive voice applications.

Outgoing calls are controlled in the same manner as incoming calls using TwiML. The initial flow for the call is
provided as a parameter to the Twilio Cloud Connector.

Installation
------------

The module can either be installed for all applications running within the Mule instance or can be setup to be used
for a single application.

*All Applications*

Download the module from the link above and place the resulting jar file in
/lib/user directory of the Mule installation folder.

*Single Application*

To make the module available only to single application then place it in the
lib directory of the application otherwise if using Maven to compile and deploy
your application the following can be done:

Add the connector's maven repo to your pom.xml:

    <repositories>
        <repository>
            <id>mulesoft-snapshots</id>
            <name>MuleForge Snapshot Repository</name>
            <url>https://repository.mulesoft.org/snapshots/</url>
            <layout>default</layout>
        </repsitory>
    </repositories>

Add the connector as a dependency to your project. This can be done by adding
the following under the dependencies element in the pom.xml file of the
application:

    <dependency>
        <groupId>org.mule.modules</groupId>
        <artifactId>mule-module-twiml</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>