package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

import devs.mrp.gullproject.models.MyClient;
import devs.mrp.gullproject.repository.ClientRepository;

@Component
public class MongoClientDetailsService implements ClientDetailsService {

	@Autowired ClientRepository repo;
	
	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		MyClient client = repo.findByClientName(clientId);
		if(client == null) {
			throw new ClientRegistrationException("Client not found");
		}
		var cd = new BaseClientDetails();
		cd.setClientId(client.getClientName());
		cd.setClientSecret(client.getSecret());
		cd.setScope(client.getScopes());
		cd.setAuthorizedGrantTypes(client.getAuthorities());
		return cd;
	}

}
