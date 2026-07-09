package com.patchmaker.coreservice.dto.groq;

import java.util.List;

public record GroqRequest(String model, List<GroqMessage> messages, double temperature) {
}