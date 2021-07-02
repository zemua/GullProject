package devs.mrp.gullproject.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.dto.propuesta.AtributoForFormDto;
import lombok.Data;
import reactor.core.publisher.Flux;

@Data
@Service
public class AtributoUtilities {

	AtributoServiceProxyWebClient atributoService;
	ModelMapper modelMapper;
	
	@Autowired
	public AtributoUtilities(AtributoServiceProxyWebClient atributoService, ModelMapper modelMapper) {
		this.atributoService = atributoService;
		this.modelMapper = modelMapper;
	}
	
	public Flux<AtributoForFormDto> getAttributesAsSelectable() {
		return atributoService.getAllAtributos()
				.map(rAttProp -> modelMapper.map(rAttProp, AtributoForFormDto.class));
	}
	
}
