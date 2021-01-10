package devs.mrp.gullproject.Controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.service.ConsultaService;

@Controller
@RequestMapping(path = "/consultas")
public class ConsultaController {

	ConsultaService consultaService;
	
	@Autowired
	public ConsultaController(ConsultaService consultaService) {
		this.consultaService = consultaService;
	}
	
	@GetMapping("/nuevo")
	public String crearAtributo(Model model) {
		
		model.addAttribute(new Consulta());
		
		return "createConsulta";
	}
	
	@PostMapping("/nuevo")
	public String procesaNuevoAtributo(@Valid Consulta consulta, BindingResult bindingResult, Model model) {
		// TODO test
		if (bindingResult.hasErrors()) {
			model.addAttribute("consulta", consulta);
			return "createConsulta";
		}
		
		consultaService.save(consulta).subscribe();
		
		return "redirect:/consultas/nuevo?add=1";
	}
	
}
