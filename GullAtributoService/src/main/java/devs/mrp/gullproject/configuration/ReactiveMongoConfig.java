package devs.mrp.gullproject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@EnableReactiveMongoRepositories
public class ReactiveMongoConfig extends AbstractReactiveMongoConfiguration {

	// solamente necesario cuando se utiliza una instalación de MongoDB, no la embedded
	
		@Bean
	    public MongoClient mongoClient() {
	        return MongoClients.create();
	    }
	 
	    @Override
	    protected String getDatabaseName() {
	        return "reactive";
	    }
	
}
