package org.mule.modules.twiml;

import org.mule.api.annotations.Module;
import org.mule.api.NestedProcessor;
import org.mule.api.annotations.Processor;
import org.mule.api.callback.HttpCallback;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.OutboundHeaders;

import java.util.List;
import java.util.Map;

/**
 * A Mule module for generating Twilios Markup Language. Twilio can handle instructions for calls and SMS messages in
 * real time from iON applications. When an SMS or incoming call is received, Twilio looks up the iON app associated
 * with the phone number called and makes a request to it. iON will respond to the request and that response will
 * decides how the call should proceed by returning a Twilio Markup XML (TwiML) document telling Twilio to say text
 * to the caller, send an SMS message, play audio files, get input from the keypad, record audio, connect the call
 * to another phone and more.
 * <p/>
 * TwiML is similar to HTML. Just as HTML is rendered in a browser to display a webpage, TwiML is 'rendered' by Twilio
 * to the caller. Only one TwiML document is rendered to the caller at once but many documents can be linked together
 * to build complex interactive voice applications.
 * <p/>
 * Outgoing calls are controlled in the same manner as incoming calls using TwiML. The initial flow for the call is
 * provided as a parameter to the Twilio Cloud Connector.
 *
 * @author MuleSoft, Inc.
 */
@Module(name = "twiml",
        namespace = "http://repository.mulesoft.org/releases/org/mule/modules/mule-module-twiml",
        schemaLocation = "http://repository.mulesoft.org/releases/org/mule/modules/mule-module-twiml/1.0/mule-twiml.xsd")
public class TwiMLModule {

    /**
     * The root element of Twilio's XML Markup is the <Response> element. In any TwiML response to a Twilio request,
     * all verb elements must be nested within this element. Any other structure is considered invalid.
     *
     * {@sample.xml ../../../doc/mule-module-twiml.xml.sample twiml:response}
     *
     * @return A TwiML-based markup document containing the response element.
     * @throws Exception
     */
    @Processor
    public String response(@Optional List<NestedProcessor> nestedProcessors, @OutboundHeaders Map<String, Object> map) throws Exception {

        map.put("Content-Type", "application/xml; charset=UTF-8");

        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<Response>\n");

        for( NestedProcessor nestedProcessor : nestedProcessors ) {
            builder.append(nestedProcessor.process().toString());
        }
        builder.append("\n</Response>");

        return builder.toString();
    }

    /**
     * The Say verb converts text to speech that is read back to the caller. <Say> is useful for development or saying dynamic text that is difficult to pre-record.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-twiml.xml.sample twiml:say}
     * {@sample.java ../../../doc/mule-module-twiml.java.sample twiml:say}
     *
     * @param lang  The 'language' attribute allows you pick a voice with a specific language's accent and pronunciations. Twilio currently supports languages English, Spanish, French and German. The default is 'English'.
     * @param voice The 'voice' attribute allows you to choose a male or female voice to read text back. The default value is 'man'.
     * @param loop  The 'loop' attribute specifies how many times you'd like the text repeated. The default is once. Specifying '0' will cause the the Say verb to loop until the call is hung up.
     * @return TwiML-based markup representing the Say operation
     */
    @Processor
    public String say(@Optional TwiMLLanguage lang,
                      @Optional TwiMLVoice voice,
                      @Optional @Default("1") int loop,
                      @Optional List<NestedProcessor> nestedProcessors) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("<Say");
        if (lang != null) {
            builder.append(" lang=\"");
            builder.append(lang.getCode());
            builder.append("\"");
        }
        if (voice != null) {
            builder.append(" voice=\"");
            builder.append(voice.getCode());
            builder.append("\"");
        }
        builder.append(" loop=\"");
        builder.append(Integer.toString(loop));
        builder.append("\"");
        builder.append(">");

        for( NestedProcessor nestedProcessor : nestedProcessors ) {
            builder.append(nestedProcessor.process().toString());
        }
        builder.append("</Say>");

        return builder.toString();
    }

    /**
     * The Play verb plays an audio file back to the caller. Twilio retrieves the file from a URL that you provide.
     * <p/>
     * <ul>
     * <li>Twilio will attempt to cache the audio file the first time it is played. This means the first attempt may
     * be slow to play due to the time spent downloading the file from your remote server. Twilio may play a
     * processing sound while the file is being downloaded.</li>
     * <li>Twilio obeys standard HTTP caching headers. If you change a file already cached by Twilio, make sure your
     * web server is sending the proper headers to inform us that the contents of the file have changed.</li>
     * <li>Audio played over the telephone network is transcoded to a format the telephone network understands.
     * Regardless of the quality of the file you provide us, we will transcode so it plays correctly. This may result
     * in lower quality because the telephone number does not support high bitrate audio.</li>
     * <li>High bitrate, lossy encoded files, such as 128kbps mp3 files, will take longer to transcode and potentially
     * sound worse than files that are in lossless 8kbps formats. This is due to the inevitable degradation that
     * occurs when converting from lossy compressed formats and the processing involved in converting from higher bit
     * rates to low bit rates.</li>
     * </ul>
     * <p/>
     * {@sample.xml ../../../doc/mule-module-twiml.xml.sample twiml:play}
     * {@sample.java ../../../doc/mule-module-twiml.java.sample twiml:play}
     *
     * @param loop The 'loop' attribute specifies how many times the audio file is played. The default behavior is to play the audio once. Specifying '0' will cause the the <Play> verb to loop until the call is hung up.
     * @param file Audio file to play
     * @return TwiML-based markup representing the Play operation
     */
    @Processor
    public String play(@Optional @Default("1") int loop, String file) {
        StringBuilder builder = new StringBuilder();
        builder.append("<Play");
        builder.append(" loop=\"");
        builder.append(Integer.toString(loop));
        builder.append("\"");
        builder.append(">");

        builder.append(file);
        builder.append("</Play>");

        return builder.toString();
    }

    /**
     * The <Gather> verb collects digits that a caller enters into his or her telephone keypad. When the caller is
     * done entering data, Twilio submits that data to the provided 'action' URL in an HTTP GET or POST request, just
     * like a web browser submits data from an HTML form.
     * <p/>
     * If no input is received before timeout, <Gather> falls through to the next verb in the TwiML document.
     * <p/>
     * You may optionally nest <Say> and <Play> verbs within a <Gather> verb while waiting for input. This allows
     * you to read menu options to the caller while letting her enter a menu selection at any time. After the first
     * digit is received the audio will stop playing.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-twiml.xml.sample twiml:gather}
     * {@sample.java ../../../doc/mule-module-twiml.java.sample twiml:gather}
     *
     * @param action         When the caller has finished entering digits Twilio will make a GET request to this flow including
     *                       a Digits variable which represent the digits the caller pressed, excluding the finishOnKey digit if used.
     * @param timeout        Sets the limit in seconds that Twilio will wait for the caller to press another digit before
     *                       moving on and making a request to the 'action' flow. For example, if 'timeout' is
     *                       '10', Twilio will wait ten seconds for the caller to press another key before submitting the previously entered
     *                       digits to the 'action' flow. Twilio waits until completing the execution of all nested verbs before beginning
     *                       the timeout period.
     * @param finishOnKey    The 'finishOnKey' attribute lets you choose one value that submits the received data when
     *                       entered. For example, if you set 'finishOnKey' to '#' and the user enters '1234#', Twilio will immediately
     *                       stop waiting for more input when the '#' is received and will submit "Digits=1234" to the 'action' flow.
     * @param numDigits      The 'numDigits' attribute lets you set the number of digits you are expecting, and submits
     *                       the data to the 'action' flow once the caller enters that number of digits. For example, one might set
     *                       'numDigits' to '5' and ask the caller to enter a 5 digit zip code. When the caller enters the fifth digit
     *                       of '94117', Twilio will immediately submit the data to the 'action' flow.
     * @return TwiML-based markup representing the Gather operation
     */
    @Processor
    public String gather(HttpCallback action,
                         @Optional @Default("5") int timeout,
                         @Optional @Default("#") String finishOnKey,
                         @Optional Integer numDigits,
                         @Optional List<NestedProcessor> nestedProcessors) throws Exception {

        StringBuilder builder = new StringBuilder();
        builder.append("<Gather");
        if (numDigits != null) {
            builder.append(" numDigits=\"");
            builder.append(numDigits);
            builder.append("\"");
        }
        if (finishOnKey != null) {
            builder.append(" finishOnKey=\"");
            builder.append(finishOnKey);
            builder.append("\"");
        }
        builder.append(" timeout=\"");
        builder.append(Integer.toString(timeout));
        builder.append("\"");
        builder.append(" method=\"GET\"");
        builder.append(" action=\"");
        builder.append(action.getUrl());
        builder.append("\"");
        builder.append(">");

        for( NestedProcessor nestedProcessor : nestedProcessors ) {
            builder.append(nestedProcessor.process().toString());
        }
        builder.append("</Gather>");

        return builder.toString();
    }

    /**
     * The <Record> verb records the caller's voice and returns to you the URL of a file containing the audio
     * recording. You can optionally generate text transcriptions of recorded calls by setting the 'transcribe'
     * attribute of the <Record> verb to 'true'.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-twiml.xml.sample twiml:record}
     * {@sample.java ../../../doc/mule-module-twiml.java.sample twiml:record}
     *
     * @param action           The 'action' attribute takes an absolute or relative URL as a value. When recording is finished
     *                         Twilio will make a request to this flow. After making this request, Twilio will continue the current call using
     *                         the TwiML received in your response. There is one exception: if Twilio receives an empty recording, it will
     *                         not make a request to the 'action' URL. The current call flow will continue with the next verb in the
     *                         current TwiML document.
     * @param timeout          The 'timeout' attribute tells Twilio to end the recording after a number of seconds of silence
     *                         has passed. The default is 5 seconds.
     * @param finishOnKey      The 'finishOnKey' attribute lets you choose a set of digits that end the recording when
     *                         entered. For example, if you set 'finishOnKey' to '#' and the caller presses '#', Twilio will immediately
     *                         stop recording and submit 'RecordingUrl', 'RecordingDuration', and the '#' as parameters in a request to the
     *                         'action' flow. The allowed values are the digits 0-9, '#' and '*'. The default is '1234567890*#' (i.e. any key
     *                         will end the recording). Unlike <Gather>, you may specify more than one character as a 'finishOnKey' value.
     * @param maxLength        The 'maxLength' attribute lets you set the maximum length for the recording in seconds. If
     *                         you set 'maxLength' to '30', the recording will automatically end after 30 seconds of recorded time has
     *                         elapsed. This defaults to 3600 seconds (one hour) for a normal recording and 120 seconds (two minutes) for
     *                         a transcribed recording.
     * @param shouldTranscribe The 'transcribe' attribute tells Twilio that you would like a text representation of
     *                         the audio of the recording. Twilio will pass this recording to our speech-to-text engine and attempt to
     *                         convert the audio to human readable text. The 'transcribe' option is off by default. If you do not wish to
     *                         perform transcription, simply do not include the transcribe attribute.
     * @param transcribe       The 'transcribeCallback' attribute is used in conjunction with the 'transcribe' attribute. It
     *                         allows you to specify a flow to which Twilio will make an asynchronous request when the transcription is
     *                         complete.
     * @param playBeep         The 'playBeep' attribute allows you to toggle between playing a sound before the start of a
     *                         recording. If you set the value to 'false', no beep sound will be played.
     * @return TwiML-based markup representing the Record operation
     * @throws Exception
     */
    @Processor
    public String record(HttpCallback action,
                         @Optional @Default("5") int timeout,
                         @Optional @Default("#") String finishOnKey,
                         @Optional @Default("3600") int maxLength,
                         @Optional @Default("false") boolean shouldTranscribe,
                         @Optional HttpCallback transcribe,
                         @Optional @Default("true") boolean playBeep) {
        StringBuilder builder = new StringBuilder();
        builder.append("<Record");
        if (finishOnKey != null) {
            builder.append(" finishOnKey=\"");
            builder.append(finishOnKey);
            builder.append("\"");
        }
        builder.append(" timeout=\"");
        builder.append(Integer.toString(timeout));
        builder.append("\"");
        builder.append(" maxLength=\"");
        builder.append(Integer.toString(maxLength));
        builder.append("\"");
        builder.append(" transcribe=\"");
        builder.append(Boolean.toString(shouldTranscribe));
        builder.append("\"");
        if (shouldTranscribe) {
            builder.append(" transcribeCallback=\"");
            builder.append(transcribe.getUrl());
            builder.append("\"");
        }
        builder.append(" playBeep=\"");
        builder.append(Boolean.toString(playBeep));
        builder.append(" method=\"GET\"");
        builder.append(" action=\"");
        builder.append(action.getUrl());
        builder.append("\"");

        return builder.toString();
    }

    /**
     * The <Sms> verb sends an SMS message to a phone number during a phone call.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-twiml.xml.sample twiml:sms}
     * {@sample.java ../../../doc/mule-module-twiml.java.sample twiml:sms}
     *
     * @param action The 'action' attribute takes a flow as an argument. After processing the <Sms> verb, Twilio
     *               will call this flow with the inbound headers 'SmsStatus' and 'SmsSid'. Using an 'action' flow, your application
     *               can receive synchronous notification that the message was successfully enqueued.
     * @param from   The 'from' attribute takes a valid phone number as an argument. This number must be a phone
     *               number that you've purchased from or ported to Twilio. When sending an SMS during an incoming call, 'from'
     *               defaults to the called party. When sending an SMS during an outgoing call, 'from' defaults to the calling
     *               party. This number must be an SMS-capable local phone number assigned to your account. If the phone number
     *               isn't SMS-capable, then the <Sms> verb will not send an SMS message.
     * @param to     The 'to' attribute takes a valid phone number as a value. Twilio will send an SMS message to this
     *               number. When sending an SMS during an incoming call, 'to' defaults to the caller. When sending an SMS during
     *               an outgoing call, 'to' defaults to the called party. The value of 'to' must be a valid phone number.
     *               NOTE: sending to short codes is not currently supported.
     * @return TwiML-based markup representing the Sms operation
     */
    @Processor
    public String sms(@Optional HttpCallback action,
                      @Optional String from,
                      @Optional String to,
                      @Optional HttpCallback status,
                      @Optional List<NestedProcessor> nestedProcessors) throws Exception {

        StringBuilder builder = new StringBuilder();
        builder.append("<Sms");
        if( action != null ) {
            builder.append(" action=\"");
            builder.append(action.getUrl());
            builder.append("\"");
        }
        if (from != null) {
            builder.append(" from=\"");
            builder.append(from);
            builder.append("\"");
        }
        if (to != null) {
            builder.append(" to=\"");
            builder.append(to);
            builder.append("\"");
        }
        if (status != null) {
            builder.append(" status=\"");
            builder.append(status.getUrl());
            builder.append("\"");
        }

        for( NestedProcessor nestedProcessor : nestedProcessors ) {
            builder.append(nestedProcessor.process().toString());
        }
        builder.append("</Sms>");

        return builder.toString();
    }

    /**
     * The <Dial> verb connects the current caller to an another phone. If the called party picks up, the two parties
     * are connected and can communicate until one hangs up. If the called party does not pick up, if a busy signal
     * is received, or if the number doesn't exist, the dial verb will finish.
     * <p/>
     * When the dialed call ends, Twilio makes a request to the 'action' flow if provided. Call flow will continue
     * using the TwiML received in response to that request.
     * <p/>
     * {@sample.xml ../../../doc/mule-module-twiml.xml.sample twiml:dial}
     * {@sample.java ../../../doc/mule-module-twiml.java.sample twiml:dial}
     *
     * @param action       The 'action' attribute takes a flow as an argument. When the dialed call ends, Twilio will make
     *                     a request to this flow. If you provide an 'action' flow, Twilio will continue the current call after the dialed
     *                     party has hung up, using the TwiML received in your response to the 'action' URL request. Any TwiML verbs
     *                     occuring after a <Dial> which specifies an 'action' attribute are unreachable.
     *                     <p/>
     *                     If no 'action' is provided, Dial will finish and Twilio will move on to the next TwiML verb in the document. If
     *                     there is no next verb, Twilio will end the phone call. Note that this is different from the behavior of
     *                     <Record> and <Gather>.
     * @param timeout      The 'timeout' attribute sets the limit in seconds that <Dial> waits for the called party to
     *                     answer the call. Basically, how long should Twilio let the call ring before giving up and reporting 'no-answer'
     *                     as the 'DialCallStatus'.
     * @param hangupOnStar The 'hangupOnStar' attribute lets the calling party hang up on the called party by pressing
     *                     the '*' key on his phone. When two parties are connected using <Dial>, Twilio blocks execution of further verbs
     *                     until the caller or called party hangs up. This feature allows the calling party to hang up on the called party
     *                     without having to hang up her phone and ending her TwiML processing session. When the caller presses '*' Twilio
     *                     will hang up on the called party. If an 'action' URL was provided, Twilio submits 'completed' as the
     *                     'DialCallStatus' to the URL and processes the response. If no 'action' was provided Twilio will continue on to
     *                     the next verb in the current TwiML document.
     * @param timeLimit    The 'timeLimit' attribute sets the maximum duration of the <Dial> in seconds. For example, by
     *                     setting a time limit of 120 seconds <Dial> will hang up on the called party automatically two minutes into
     *                     the phone call. By default, there is a four hour time limit set on calls.
     * @param callerId     The 'callerId' attribute lets you specify the caller ID that will appear to the called party
     *                     when Twilio calls. By default, when you put a <Dial> in your TwiML response to Twilio's inbound call request,
     *                     the caller ID that the dialed party sees is the inbound caller's caller ID.
     *                     <p/>
     *                     For example, an inbound caller to your Twilio number has the caller ID 1-415-123-4567. You tell Twilio to
     *                     execute a <Dial> verb to 1-858-987-6543 to handle the inbound call. The called party (1-858-987-6543) will
     *                     see 1-415-123-4567 as the caller ID on the incoming call.
     * @return TwiML-based markup representing the Dial operation
     */
    @Processor
    public String dial(@Optional HttpCallback action,
                       @Optional @Default("30") int timeout,
                       @Optional @Default("false") boolean hangupOnStar,
                       @Optional @Default("14400") int timeLimit,
                       @Optional String callerId,
                       List<NestedProcessor> nestedProcessors) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("<Dial");
        if (action != null) {
            builder.append(" action=\"");
            builder.append(action.getUrl());
            builder.append("\"");
        }
        builder.append(" timeout=\"");
        builder.append(Integer.toString(timeout));
        builder.append("\"");
        builder.append(" hangupOnStar=\"");
        builder.append(Boolean.toString(hangupOnStar));
        builder.append("\"");
        builder.append(" timeLimit=\"");
        builder.append(Integer.toString(timeLimit));
        builder.append("\"");
        if (callerId != null) {
            builder.append(" callerId=\"");
            builder.append(callerId);
            builder.append("\"");
        }

        for( NestedProcessor nestedProcessor : nestedProcessors ) {
            builder.append(nestedProcessor.process().toString());
        }
        builder.append("</Dial>");

        return builder.toString();
    }
}
