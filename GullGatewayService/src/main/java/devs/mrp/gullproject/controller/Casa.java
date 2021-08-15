package devs.mrp.gullproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//@Controller
public class Casa {

	@GetMapping("/")
	public String casa() {
		return "redirect:/consultas/all/";
	}
	
}
