package org.mule.modules.twiml;

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
