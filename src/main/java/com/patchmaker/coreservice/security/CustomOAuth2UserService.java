package com.patchmaker.coreservice.security;

import com.patchmaker.coreservice.entity.AuthProvider;
import com.patchmaker.coreservice.entity.Role;
import com.patchmaker.coreservice.entity.User;
import com.patchmaker.coreservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        if (email == null) {
            email = fetchPrimaryEmail(userRequest.getAccessToken().getTokenValue());
        }
        if (email == null) {
            throw new OAuth2AuthenticationException("Could not retrieve email from GitHub. Make email public or grant email access.");
        }

        String name = (String) attributes.get("name");
        String login = (String) attributes.get("login");
        String avatarUrl = (String) attributes.get("avatar_url");
        String githubUrl = (String) attributes.get("html_url");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = User.builder()
                    .name(name != null ? name : login)
                    .email(email)
                    .password(null)
                    .role(Role.DEVELOPER) // default role for GitHub sign-ups
                    .githubUrl(githubUrl)
                    .profileImage(avatarUrl)
                    .authProvider(AuthProvider.GITHUB)
                    .build();
        } else {
            user.setGithubUrl(githubUrl);
            user.setProfileImage(avatarUrl);
        }

        user = userRepository.save(user);
        return new CustomOAuth2User(user, attributes);
    }

    private String fetchPrimaryEmail(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> emails = response.getBody();
        if (emails == null) return null;

        return emails.stream()
                .filter(e -> Boolean.TRUE.equals(e.get("primary")))
                .map(e -> (String) e.get("email"))
                .findFirst()
                .orElse(null);
    }
}