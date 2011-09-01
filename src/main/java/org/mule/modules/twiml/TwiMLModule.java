package org.mule.modules.twiml;

import org.mule.api.annotations.Module;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.callback.HttpCallback;
import org.mule.api.annotations.callback.ProcessorCallback;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;

@Module(name = "twiml",
        namespace = "http://repository.mulesoft.org/releases/org/mule/modules/twiml/mule-module-twiml",
        schemaLocation = "http://repository.mulesoft.org/releases/org/mule/modules/twiml/mule-module-twiml/1.0/mule-twiml.xsd")
public class TwiMLModule {

    /**
     * The Say verb converts text to speech that is read back to the caller. <Say> is useful for development or saying dynamic text that is difficult to pre-record.
     *
     * @param lang  The 'language' attribute allows you pick a voice with a specific language's accent and pronunciations. Twilio currently supports languages English, Spanish, French and German. The default is 'English'.
     * @param voice The 'voice' attribute allows you to choose a male or female voice to read text back. The default value is 'man'.
     * @param loop  The 'loop' attribute specifies how many times you'd like the text repeated. The default is once. Specifying '0' will cause the the Say verb to loop until the call is hung up.
     * @return
     */
    @Processor
    public String say(@Optional TwiMLLanguage lang,
                      @Optional TwiMLVoice voice,
                      @Optional @Default("1") int loop,
                      @Optional ProcessorCallback innerProcessor) throws Exception {
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

        builder.append(innerProcessor.process().toString());
        builder.append("</Say>");

        return builder.toString();
    }

    /**
     * The Play verb plays an audio file back to the caller. Twilio retrieves the file from a URL that you provide.
     *
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
     *
     * @param loop The 'loop' attribute specifies how many times the audio file is played. The default behavior is to play the audio once. Specifying '0' will cause the the <Play> verb to loop until the call is hung up.
     * @param file Audio file to play
     * @return
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
     *
     * If no input is received before timeout, <Gather> falls through to the next verb in the TwiML document.
     *
     * You may optionally nest <Say> and <Play> verbs within a <Gather> verb while waiting for input. This allows
     * you to read menu options to the caller while letting her enter a menu selection at any time. After the first
     * digit is received the audio will stop playing.
     *
     * @param action When the caller has finished entering digits Twilio will make a GET request to this flow including
     * a Digits variable which represent the digits the caller pressed, excluding the finishOnKey digit if used.
     * @param timeout Sets the limit in seconds that Twilio will wait for the caller to press another digit before
     *  moving on and making a request to the 'action' flow. For example, if 'timeout' is
     * '10', Twilio will wait ten seconds for the caller to press another key before submitting the previously entered
     * digits to the 'action' flow. Twilio waits until completing the execution of all nested verbs before beginning
     * the timeout period.
     * @param finishOnKey The 'finishOnKey' attribute lets you choose one value that submits the received data when
     * entered. For example, if you set 'finishOnKey' to '#' and the user enters '1234#', Twilio will immediately
     * stop waiting for more input when the '#' is received and will submit "Digits=1234" to the 'action' flow.
     * @param numDigits The 'numDigits' attribute lets you set the number of digits you are expecting, and submits
     * the data to the 'action' flow once the caller enters that number of digits. For example, one might set
     * 'numDigits' to '5' and ask the caller to enter a 5 digit zip code. When the caller enters the fifth digit
     * of '94117', Twilio will immediately submit the data to the 'action' flow.
     * @param innerProcessor
     * @return
     */
    @Processor
    public String gather(HttpCallback action,
                         @Optional @Default("5") int timeout,
                         @Optional @Default("#") String finishOnKey,
                         @Optional Integer numDigits,
                         @Optional ProcessorCallback innerProcessor) throws Exception {

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

        builder.append(innerProcessor.process().toString());
        builder.append("</Say>");

        return builder.toString();
    }

}
