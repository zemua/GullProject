package devs.mrp.gullproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import devs.mrp.gullproject.service.LineaService;

@Controller
@RequestMapping(path = "/lineas")
public class LineaController {

	private LineaService lineaService;
	
	@Autowired
	public LineaController(LineaService lineaService) {
		this.lineaService = lineaService;
	}
	
	
	
}
