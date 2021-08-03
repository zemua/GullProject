package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(path = "/users")
public class UserController {

	@GetMapping("/add")
	public String addUser(Model model) {
		model.addAttribute("myUser", myUserFactory.create());
		return "addUser";
	}
	
}
