package com.inacio.boueres.instestapi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inacio.boueres.instestapi.entity.InsteFolowed;
import com.inacio.boueres.instestapi.entity.InsteItems;
import com.inacio.boueres.instestapi.entity.InsteReturn;
import com.inacio.boueres.instestapi.entity.InsteSearch;
import com.inacio.boueres.instestapi.exceptions.MalformedJsonException;
import com.inacio.boueres.instestapi.exceptions.UserNotFoundException;
import com.inacio.boueres.instestapi.service.RetrieveNewPostsService;
/**
 * 
 * @author Inacio da Cunha Boueres Filho
 *
 * InstagramController is the controller responsible to define the REST API of the system
 * It have the methods to control the request and retrieve of profile photos 
 *
 */
@RestController
public class InstagramController {

	/**
	 * 
	 * @param retrieveNewPosts
	 * Service that will start the tasks to retrieve user information from web
	 * 
	 */
	private final RetrieveNewPostsService retrieveNewPosts;

	/**
	 * 
	 * @param retrieveNewPosts
	 * 
	 * Inject the service and start to load Justin Bieber medias 
	 * 
	 */
	@Inject
	public InstagramController(final RetrieveNewPostsService retrieveNewPosts) {
		this.retrieveNewPosts = retrieveNewPosts;
		addFollowed("justinbieber");
	}

	/**
	 * 
	 * @param acontName
	 * @return Images from the requested user
	 * Start the tasks to retrieve informations about the user passed as parameter
	 * it return the first medias as well
	 */
	@RequestMapping(value = "/addFollowed", method = RequestMethod.POST, consumes = { "text/plain" })
	public Collection<InsteItems> addFollowed(@RequestBody String acontName) {
		InsteReturn feed = null;
		if(acontName==null || acontName.trim().equals("")){
			return new ArrayList<InsteItems>();
		}
			
		feed = retrieveNewPosts.getImages(acontName);
		return feed.getItems();
	}

	/**
	 * 
	 * @param searchParam
	 * @return  Images from the requested user form some page 20 per page
	 * Get the information loaded in memory from the download tasks
	 */
	@RequestMapping(value = "/getInstaInfo/{searchParam}", method = RequestMethod.GET)
	public Collection<InsteItems> instaInfo(@PathVariable String searchParam) {

		/*
		 * Get parameters from Json
		 * */
		InsteSearch is = new InsteSearch();
		ObjectMapper mapper = new ObjectMapper();
		try {
			is = mapper.readValue(searchParam, InsteSearch.class);
		} catch (JsonParseException e) {
			throw new MalformedJsonException();
		} catch (JsonMappingException e) {
			throw new MalformedJsonException();
		} catch (IOException e) {
			throw new MalformedJsonException();
		} catch (NumberFormatException e) { //Can try to pass something else to rules.id that expect a BigDecimal
			throw new MalformedJsonException();
		}
		/*
		 * Check if a valid user is been passed
		 * */
		InsteReturn feed = null;
		if(is.getUser()==null || is.getUser().trim().equals("")){
			return new ArrayList<InsteItems>();
		}
		/*
		 * Retrieve the information about the requested user
		 * */
		feed = retrieveNewPosts.getImages(is.getUser());
		
		//Get the 20 medias of the user based on page
		List<InsteItems> temp = new ArrayList<InsteItems>(feed.getItems());
		return temp.subList(((is.getPage()-1)*20), (is.getPage()*20));
	}

	/**
	 * 
	 * @return list of users followed
	 * Retrieve the list of users that is been followed as the amount of media loaded from it profile untill now
	 * 
	 */
	@RequestMapping(value = "/listActiveFolowed", method = RequestMethod.GET)
	public Collection<InsteFolowed> listActiveFolowed() {
		return retrieveNewPosts.listActiveFolowed();
	}
	
	/**
	 * 
	 * @param acontName
	 * Remove user from memory and from task if it's still active
	 */
	@RequestMapping(value = "/deleteFollowed/{acontName}", method = RequestMethod.DELETE)
	public void deleteCalorie(@PathVariable("acontName") String  acontName)
	{
		retrieveNewPosts.deleteUser(acontName);;
	}
	
    
    //expected erros
    @ExceptionHandler
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public String handleDefaultException(UserNotFoundException e) {
    	return "{\"error\": \""+"User requested dos not exists"+"\"}";
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public String handleDefaultException(MalformedJsonException e) {
    	return "{\"error\": \""+"The Json parameter is invalid!"+"\"}";
    }
    
    //Generic
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDefaultException(Exception e) {
    	return "{\"error\": \""+ "Exception"+"\"}";
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDefaultException(RuntimeException e) {
    	return "{\"error\": \""+ "RuntimeException"+"\"}";
    }

}
