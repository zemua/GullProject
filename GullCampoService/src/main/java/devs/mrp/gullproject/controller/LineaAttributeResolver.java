package devs.mrp.gullproject.controller;

import org.modelmapper.ModelMapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import devs.mrp.gullproject.ainterfaces.MyFactoryNew;
import devs.mrp.gullproject.domains.linea.Linea;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class LineaAttributeResolver implements HandlerMethodArgumentResolver {

	private MyFactoryNew<Linea> factory;
	private ModelMapper mapper;
	
	public LineaAttributeResolver(MyFactoryNew<Linea> factory, ModelMapper mapper) {
		this.factory = factory;
		this.mapper = mapper;
	}
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(Linea.class);
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext,
			ServerWebExchange exchange) {
		return exchange.getFormData()
				.map(data -> {
					Linea linea = factory.create();
					linea = mapper.map(data, linea.getClass()); // is mapping fields as arrays, may have to remap manually
					log.debug("data: " + data.toString());
					log.debug("mapped to linea: " + linea.toString());
					return linea;
					/**
					 * Flux HandlerMethodArgumentResolver method resolveArgument doesn't have ModelAndViewContainer mavContainer as the regular MVC does
					 * So we cannot implement the following to get the bindingResult in the controller:
					 * 
					 * String name = ModelFactory.getNameForParameter(parameter);
					 * var binder = bindingContext.createDataBinder(exchange, parameter, name);
					 * Map<String, Object> bindingResultModel = binder.getBindingResult().getModel();
					 * mavContainer.removeAttributes(bindingResultModel);
					 * mavContainer.addAllAttributes(bindingResultModel);
					 * 
					 * Reference: https://github.com/spring-projects/spring-framework/issues/20067
					 * RequestParams has been removed from WebFlux because it is a blocking operation
					 */
				});
	}

}
