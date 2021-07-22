package devs.mrp.gullproject.rest;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.domains.Atributo;
import devs.mrp.gullproject.domains.DataFormat;
import devs.mrp.gullproject.domains.StringWrapper;
import devs.mrp.gullproject.domains.DTO.AtributoDTO;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModel;
import devs.mrp.gullproject.domains.representationmodels.AtributoRepresentationModelAssembler;
import devs.mrp.gullproject.domains.representationmodels.AtributoRespresentationModelMapper;
import devs.mrp.gullproject.service.AtributoService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(value = "/api/atributos", produces = "application/json")
public class AtributoRestController {

	private final AtributoService atributoService;
	//private final AtributoRepresentationModelAssembler arma;
	private final ModelMapper modelMapper;
	
	@Autowired AtributoRespresentationModelMapper repMapper;
	
	@Autowired
	public AtributoRestController(AtributoService atributoService, ModelMapper modelMapper) {
		this.atributoService = atributoService;
		this.modelMapper = modelMapper;
	}
	
	@GetMapping(path = "/all")
	public Flux<EntityModel<Atributo>> getAllAtributos() {
		Flux<Atributo> atributos = atributoService.findAll();
		Flux<EntityModel<Atributo>> arm = atributos.flatMap(e -> repMapper.from(e));
		return arm;
	}
	
	@GetMapping(path = "/idforcampo/{id}")
	public Mono<AtributoDTO> getAtributoForCampoById(@PathVariable(value = "id") String id){
		Mono<Atributo> atributo = atributoService.findById(id);
		Mono<AtributoDTO> atributoDTO = atributo.map(a -> modelMapper.map(a, AtributoDTO.class));
		return atributoDTO;
	}
	
	@GetMapping(path = "/formatos")
	public Flux<StringWrapper> getTodosLosDataFormat(){
		DataFormat[] values = DataFormat.values();
		Flux<StringWrapper> flux = Flux.fromArray(values)
			.map(df -> new StringWrapper(df.toString()));
		return flux;
	}
	
	@GetMapping(path = "/typeofformat/{format}")
	public Mono<String> getTypeOfGivenFormat(@PathVariable(value = "format") String format){
		// beware of exceptions being thrown here if the String is not a valid value for the enum
		if (DataFormat.isMember(format)) {
			try {
				log.debug("going to get type of " + format);
				String c = DataFormat.valueOf(format).getTipo();
				log.debug("type is " + c);
				return Mono.just(c);
			} catch (Exception e) {
				e.printStackTrace();
				return Mono.just("");
			}
		} else {
			return Mono.just("");
		}
	}
	
	@GetMapping(path = "/arrayofcampos/byids")
	public Flux<AtributoDTO> getAtributosByArrayOfIds(@RequestParam List<String> ids) {
		Flux<Atributo> atributos = atributoService.findAtributoByIdIn(ids);
		Flux<AtributoDTO> dtos = atributos.map(a -> modelMapper.map(a, AtributoDTO.class));
		return dtos;
	}
	
}
