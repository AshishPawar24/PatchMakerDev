package com.patchmaker.coreservice.dto.groq;

import java.util.List;

public record GroqResponse(List<GroqChoice> choices) {

    public record GroqChoice(GroqMessage message) {
    }
}