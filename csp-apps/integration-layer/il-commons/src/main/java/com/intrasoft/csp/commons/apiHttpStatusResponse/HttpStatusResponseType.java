package com.intrasoft.csp.commons.apiHttpStatusResponse;

public enum HttpStatusResponseType {

    SUCCESSFUL_OPERATION(200, "Successful operation"),
    MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE(404, "Mapping not found for given tuple."),
    MALFORMED_INTEGRATION_DATA_STRUCTURE(400, "Malformed Integration Data Structure"),
    CSP_AUTHORIZATION_FAILED(403, "CSP authorization failed. External CSP is not authorized to sent specific data type"),
    UNSUPPORTED_DATA_TYPE(415, "Unsupported data type. Local CSP does not support given data type. No " +
            "application that handles given data type is installed");


    private final int value;
    private final String reasonPhrase;

    HttpStatusResponseType(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }
}
