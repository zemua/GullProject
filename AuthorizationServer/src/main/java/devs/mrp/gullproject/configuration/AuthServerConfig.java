package devs.mrp.gullproject.configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthServerConfig {
	
	@Value("${gull.gateway.port}")
	private String gatewayPort;
	@Value("${server.port}")
	private String currentPort;
	
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
          .clientId("gull-client")
          .clientSecret("secret")
          .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
          .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
          .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
          .redirectUri("http://localhost:" + gatewayPort + "/login/oauth2/code/gull-client")
          .redirectUri("http://localhost:" + gatewayPort + "/authorized")
          .scope(OidcScopes.OPENID)
          .scope("articles.read")
          .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }
	
	// SETUP OF THE SIGNING KEY
	@Bean
	public JWKSource<SecurityContext> jwkSource() {
	    RSAKey rsaKey;
		try {
			rsaKey = generateRsa();
			JWKSet jwkSet = new JWKSet(rsaKey);
		    return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static RSAKey generateRsa() throws NoSuchAlgorithmException {
	    KeyPair keyPair = generateRsaKey();
	    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
	    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
	    return new RSAKey.Builder(publicKey)
	      .privateKey(privateKey)
	      .keyID(UUID.randomUUID().toString())
	      .build();
	}

	private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
	    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	    keyPairGenerator.initialize(2048);
	    return keyPairGenerator.generateKeyPair();
	}
	
	// SETTING UNIQUE ISSUER URL
	@Bean
	public ProviderSettings providerSettings() {
	    return new ProviderSettings().issuer("http://127.0.0.1:" + currentPort);
	}
	
}
