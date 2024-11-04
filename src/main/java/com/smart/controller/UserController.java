package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    private String imageUrl;
	private String loggedInUserName;

    
	// controller for checking login details and displaying user_dashboard page
    @PostMapping("/index")
    public String login_control(@RequestParam("email") String email, @RequestParam("password") String password,
            Model model) {
    	
        List<User> user = this.userRepository.findByEmailAndPassword(email, password);
        if (user.isEmpty()) {
            model.addAttribute("login_error", "Wrong Email OR Password, please try again");
            return "login";
        }
        Optional<User> users = (userRepository.findById(user.get(0).getId()));
        loggedInUserName = users.get().getName();
        imageUrl = users.get().getImageUrl();
        model.addAttribute("image_url",imageUrl);
        model.addAttribute("name", loggedInUserName);
        return "users/user_dashboard";
    }
    
    // controller for checking if user is logged in or not and then showing user_dashboard page
    @GetMapping("/index")
    public String homepage(Model m)
    {
    	if(loggedInUserName == null)
    		return "redirect:/";
    	
        m.addAttribute("image_url",imageUrl);
    	m.addAttribute("name",loggedInUserName);
    	return "users/user_dashboard";
    }
    
    
    // controller for checking if user is logged in or not and then showing add-contact page
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
    	if(loggedInUserName == null)
    		return "redirect:/";

        model.addAttribute("image_url",imageUrl);
    	model.addAttribute("name",loggedInUserName);
    	model.addAttribute("title", "Add Contact- Smart Contact Manager");
		model.addAttribute("user", new Contact());

    	return "users/add_contact_form";
    }
    
    // controller for adding the given contact details in database and then redirecting to the same add-contact page 
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,Model model,@RequestParam("contactImage") MultipartFile file) {
    	try {
    	model.addAttribute("image_url",imageUrl);	
    	model.addAttribute("name",loggedInUserName);
    
    	//processing and uploading image file
    	if(file.isEmpty())
    		contact.setImage("contact.jpg");
    	else {
    	contact.setImage(file.getOriginalFilename());
    	
    	File saveFile = new ClassPathResource("static/img").getFile();
    	Path path =Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
    	Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
    	
    	User user = this.userRepository.findByName(loggedInUserName);
    	user.getContacts().add(contact);
    	contact.setUser(user);
    	
    	this.userRepository.save(user);
    	model.addAttribute("message","Contact Added Successfully");
    		 }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		model.addAttribute("message","Something went Wrong, please try again");
    	}
    	return "users/add_contact_form";
    }
    
    
    // controller for showing all contacts of a user in the view-contacts page
    //show contacts handler
    // per page = 3 records
    // current page = 0 [page]  -- url
    @RequestMapping("/view-contact/{page}")
    public String viewContacts(@PathVariable("page") Integer page, Model model) {
    	if(loggedInUserName == null)
    		return "redirect:/";
    	
    model.addAttribute("image_url",imageUrl);	
    model.addAttribute("name",loggedInUserName);
	model.addAttribute("title", "Show Contact- Smart Contact Manager");
	
	User user = this.userRepository.findByName(loggedInUserName);
	int userId = user.getId();
	
	Pageable pageable = PageRequest.of(page, 2);
	Page<Contact> contacts= this.contactRepository.findContactByUserId(userId,pageable);
	
	model.addAttribute("contacts",contacts);
	model.addAttribute("currentPage",page);
	model.addAttribute("totalPages",contacts.getTotalPages());
	
    return "users/view_contacts";
    }
        
    
    // controller for logging out the user and redirecting to the home page
    @RequestMapping("/logoutController")
    public String logoutController(Model m) {
    	loggedInUserName = null;
    	m.addAttribute("name",loggedInUserName);
		return "redirect:/";
    }
    
    
    // controller for showing individual contact details in the contact-detail page
    @RequestMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Integer cId,Model model)
    {
    	if(loggedInUserName == null)
    		return "redirect:/";
    	
    model.addAttribute("image_url",imageUrl);	
    model.addAttribute("name",loggedInUserName);
	model.addAttribute("title", "Contact Detail - Smart Contact Manager");
	
	Optional<Contact> Optionalcontact = this.contactRepository.findById(cId);
	Contact contact = Optionalcontact.get();
	
	User user = this.userRepository.findByName(loggedInUserName);
	if(user.getId() == contact.getUser().getId())
	model.addAttribute("contact",contact);

    	return "users/contact_detail";
    }
}
