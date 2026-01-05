package com.platform.dto;

import jakarta.validation.constraints.NotBlank;

public class EventRequestDTO {

    @NotBlank
    private String type;

    @NotBlank
    private String payload;

   

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}