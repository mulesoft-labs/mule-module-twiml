package org.mule.modules.twiml;

/**
 * Voice language with a specific language's accent and pronunciations
 */
public enum TwiMLLanguage {
    ENGLISH("en"), SPANSIH("es"), FRENCH("fr"), GERMAN("de");

    private String code;

    private TwiMLLanguage(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
