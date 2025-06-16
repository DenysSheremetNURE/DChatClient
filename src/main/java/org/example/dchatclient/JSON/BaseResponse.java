package org.example.dchatclient.JSON;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse {
    public String type;

    public BaseResponse(){}

    public BaseResponse(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
