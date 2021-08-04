package devs.mrp.gullproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import devs.mrp.gullproject.models.MyUser;
import devs.mrp.gullproject.models.MyUserImpl;
import devs.mrp.gullproject.models.factory.UserFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(path = "/users")
public class UserController {
	
	@Autowired UserFactory userFactory;
	
	@ModelAttribute("myUserModel")
	public MyUser getMyUser(MyUserImpl user) {
		return user;
	}

	@GetMapping("/add")
	public String addUser(Model model) {
		log.debug("getting into addUser()");
		model.addAttribute("myUser", userFactory.create());
		return "adduser";
	}
	
}
