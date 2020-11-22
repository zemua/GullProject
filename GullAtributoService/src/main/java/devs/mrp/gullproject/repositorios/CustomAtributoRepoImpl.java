package devs.mrp.gullproject.repositorios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import devs.mrp.gullproject.domains.Atributo.dataFormat;
import reactor.core.publisher.Mono;

public class CustomAtributoRepoImpl implements CustomAtributoRepo {

	private final ReactiveMongoTemplate mongoTemplate;
	
	@Autowired
	public CustomAtributoRepoImpl(ReactiveMongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@Override
	public void pushAtributo(String id, String name, dataFormat dataFormat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pushAtributo(Mono<String> id, Mono<String> name, Mono<dataFormat> dataFormat) {
		// TODO Auto-generated method stub
		
	}

}
