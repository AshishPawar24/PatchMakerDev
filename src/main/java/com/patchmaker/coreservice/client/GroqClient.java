package com.patchmaker.coreservice.client;

import com.patchmaker.coreservice.dto.groq.GroqMessage;
import com.patchmaker.coreservice.dto.groq.GroqRequest;
import com.patchmaker.coreservice.dto.groq.GroqResponse;
import com.patchmaker.coreservice.exception.AIServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class GroqClient {

    private final RestClient restClient;
    private final String model;

    public GroqClient(RestClient.Builder builder,
                      @Value("${groq.api.key}") String apiKey,
                      @Value("${groq.api.url}") String apiUrl,
                      @Value("${groq.model}") String model) {

        this.restClient = builder
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.model = model;
    }


    public String getCompletion(String prompt) {

        GroqRequest request = new GroqRequest(
                model,
                List.of(new GroqMessage("user", prompt)),
                0.3 // low temperature = more consistent, less "creative" output — important for structured JSON replies
        );

        try {
            GroqResponse response = restClient.post()
                    .body(request)
                    .retrieve()
                    .body(GroqResponse.class);

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new AIServiceException("Groq API returned an empty response");
            }

            return response.choices().get(0).message().content();

        } catch (RestClientException ex) {
            throw new AIServiceException("Failed to communicate with Groq API: " + ex.getMessage());
        }
    }
}