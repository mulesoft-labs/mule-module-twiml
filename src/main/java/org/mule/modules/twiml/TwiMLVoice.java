package org.mule.modules.twiml;

/**
 * Voice gender
 */
public enum TwiMLVoice {
    MAN("man"),
    WOMAN("woman");

    private String code;

    private TwiMLVoice(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
