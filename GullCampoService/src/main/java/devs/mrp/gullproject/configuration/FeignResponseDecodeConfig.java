package devs.mrp.gullproject.configuration;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

@Configuration
public class FeignResponseDecodeConfig {
	
	/**
	 * Solamente un workaround
	 * sacado de github
	 * https://github.com/spring-cloud/spring-cloud-openfeign/issues/235
	 */

	private ObjectFactory<HttpMessageConverters> messageConverters = HttpMessageConverters::new;

    @Bean
    Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    Decoder feignFormDecoder() {
        return new SpringDecoder(messageConverters);
    }
	
}
