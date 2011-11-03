/**
 * Mule TwiML Module
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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
