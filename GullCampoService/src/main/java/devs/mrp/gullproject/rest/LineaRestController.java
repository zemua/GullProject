package devs.mrp.gullproject.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devs.mrp.gullproject.service.LineaService;

@RestController
@RequestMapping(value = "/api/lineas", produces = MediaType.APPLICATION_JSON_VALUE)
public class LineaRestController {
// TODO test
	
	private final LineaService lineaService;
	
	@Autowired
	public LineaRestController(LineaService lineaService) {
		this.lineaService = lineaService;
	}
	
	
	
	// TODO compeltar
	
}
