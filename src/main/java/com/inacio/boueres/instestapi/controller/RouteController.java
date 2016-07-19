package com.inacio.boueres.instestapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author Inacio da Cunha Boueres Filho
 * Controller to indicate the behavior of the pages on the application
 *
 */
@Controller
public class RouteController {
	
	
	  @RequestMapping(value="/",method = RequestMethod.GET)
	      public String homepage(){
	          return "index"; 
	      }

}