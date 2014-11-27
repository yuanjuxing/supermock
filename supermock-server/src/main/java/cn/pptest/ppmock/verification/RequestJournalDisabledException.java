package cn.pptest.ppmock.verification;

import cn.pptest.ppmock.ConfigurationException;


public class RequestJournalDisabledException extends ConfigurationException {

    public RequestJournalDisabledException() {
        super("The request journal is disabled, so no verification or request searching operations are available");
    }
}
