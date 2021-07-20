package devs.mrp.gullproject.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@Configuration
public class ReactiveMongoConfig /*extends AbstractReactiveMongoConfiguration*/ {

	// configuración cuando se utiliza una instalación de MongoDB, no la embedded
	
	/**
	 * MONGO SERVER CONFIG MOVED TO PROPERTIES
	 */
	
		/*@Bean
	    public MongoClient mongoClient() {
	        return MongoClients.create("mongodb://localhost:27017");
	    }
	 
	    @Override
	    protected String getDatabaseName() {
	        return "test";
	    }*/
	    
	    @Bean
	    public ModelMapper modelMapper() {
	    	return new ModelMapper();
	    }
	
}
