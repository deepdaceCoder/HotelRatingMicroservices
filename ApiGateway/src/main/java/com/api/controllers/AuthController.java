package com.api.controllers;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.model.AuthResponse;

@RestController
@RequestMapping(name = "/auth")
public class AuthController {

	private Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@SuppressWarnings("unchecked")
	@GetMapping("/login")
	public ResponseEntity<AuthResponse> login(@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,
													@AuthenticationPrincipal OidcUser user,
													Model model) {
		
		logger.info("user email id : {} ", user.getEmail());
		
		AuthResponse response = AuthResponse.builder()
											.userId(user.getEmail())
											.accessToken(client.getAccessToken().getTokenValue())
											.refreshToken(client.getRefreshToken().getTokenValue())
											.expireAt(client.getAccessToken().getExpiresAt().getEpochSecond())
											.authoritites((Collection<String>) user.getAuthorities().stream()
													          .map(grantedAuthority -> grantedAuthority.getAuthority()))
											.build();
		
		return ResponseEntity.ok(response);
		
	}
	
}
