package org.example.dchatclient.JSON;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseRequest {
    public String command;
}
