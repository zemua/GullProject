package devs.mrp.gullproject.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import devs.mrp.gullproject.service.CampoService;

@Controller
@RequestMapping(path = "/campos")
public class CampoController {
	
	// TODO
	
	CampoService campoService;
	
	@Autowired
	public CampoController(CampoService campoService) {
		this.campoService = campoService;
	}
	
	

}
