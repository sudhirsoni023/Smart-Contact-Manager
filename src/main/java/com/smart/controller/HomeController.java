package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
public class HomeController {

	@Autowired
	UserRepository userRepository;

    // controller for showing the home page of the application
	@GetMapping("/")
	public String Home(Model model) {
		model.addAttribute("title", "Home- Smart Contact Manager");
		return "home";
	}

    // controller for showing the about page of the application
	@GetMapping("/about")
	public String About(Model model) {
		model.addAttribute("title", "About- Smart Contact Manager");
		return "about";
	}

    // controller for showing the signup page of the application
	@GetMapping("/signup")
	public String Signup(Model model) {
		model.addAttribute("title", "Register- Smart Contact Manager");
		model.addAttribute("page", "signup");
		model.addAttribute("user", new User());
		return "signup";
	}

	
    // controller for registering the user in the database and redirecting back to the signup page
	@PostMapping("/do_register")
	public String registerUser(@ModelAttribute("user") User user, Model model,@RequestParam("profileImage") MultipartFile file) {
		try {
		if(file.isEmpty())
    		user.setImageUrl("contact.jpg");
    	else {
    	user.setImageUrl(file.getOriginalFilename());
    	
    	//fetching pic from form and saving it in img folder
    	File saveFile = new ClassPathResource("static/img").getFile();
    	Path path =Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
    	Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
		
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		
		this.userRepository.save(user);
		model.addAttribute("user", user);
		model.addAttribute("message", "Successfully registered");
    	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
    		model.addAttribute("message","Something went Wrong, please try again");
		}
		return "signup";
	}

    // controller for showing the login page of the application
	@GetMapping("/login")
	public String Login(Model model) {
		model.addAttribute("title", "Login- Smart Contact Manager");
		model.addAttribute("page", "login");
		return "login";
	}
}
