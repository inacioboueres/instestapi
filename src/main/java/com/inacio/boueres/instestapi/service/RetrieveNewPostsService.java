package com.inacio.boueres.instestapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inacio.boueres.instestapi.entity.InsteFolowed;
import com.inacio.boueres.instestapi.entity.InsteReturn;
import com.inacio.boueres.instestapi.exceptions.UserNotFoundException;
/**
 * 
 * @author Inacio da Cunha Boueres Filho
 * This service is responsible to start a TimeTask to check for new updates;
 * start the download task for new users;
 * retrieve the download information from a user
 * and remove a user from memory
 */
@Service
public class RetrieveNewPostsService {

	/*
	 * Usually I would store the information on a DB, but in order to keep it simple
	 * I decided to maintain it in memory
	 * */
	private static final Map<String, InsteReturn> followed = new HashMap<String, InsteReturn>();
	
	/*
	 * Inject the Service responsible to start the download task
	 * */
	@Autowired
	AsyncImageLoaderService asyncImageLoaderService;

	/**
	 * TimeTask responsible to check if have new updates every 30s, 
	 * for every user followed it I'll try to retrieve information 
	 * until reache the newest information on memory, and than add to 
	 * the collection, as it is a TreeSet, it ensure that all insert will be
	 * sorted and unique
	 */
	@Scheduled(fixedRate = 30000)
	private void updateFolloweds() {
		for (String user : followed.keySet()) {
			InsteReturn feed = null;
			if(followed.get(user)==null){
				followed.remove(user);
			}
			Long lastPhoto = followed.get(user).getItems().first().getCreated_time();
			do {
				feed = getLasterImages(user, feed, lastPhoto);
			} while (feed.getMore_available_laster());
			followed.get(user).getItems().addAll(feed.getItems());
			
		}
	}

	/**
	 * 
	 * @param user
	 * @return user informations
	 * Check if user requested is Already in memory,
	 * if it is, return it information else, request the user information 
	 * a first time, and than start the task to download the remaining information in background
	 */
	public InsteReturn getImages(String user) {
		if (followed.containsKey(user)) {
			return followed.get(user);
		} else {
			InsteReturn feed = null;
			feed = getImages(user, feed);
			followed.put(user, feed);
			try {
				asyncImageLoaderService.getImagesAsync(user, feed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return feed;
		}
	}
	
	/**
	 * 
	 * @return list of followed users
	 * Retrieve a list of users that are been followed, with the amount of media in memory
	 */
	public Collection<InsteFolowed> listActiveFolowed(){
		TreeSet<InsteFolowed> ret = new TreeSet<InsteFolowed>();
		
		for(String user : followed.keySet()){
			InsteFolowed insteFolowed =  new InsteFolowed();
			insteFolowed.setName(user);
			insteFolowed.setPhotos(followed.get(user).getItems().size());
			ret.add(insteFolowed);
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param user
	 * Remove the user from memory and stop the download task if its active
	 * 
	 */
	public void deleteUser(String user){
		if(!followed.get(user).getMore_available()){
			followed.remove(user);
		}else{
			asyncImageLoaderService.removeUser(user);
			followed.remove(user);
		}
	}

	/**
	 * 
	 * @param user
	 * @param feed
	 * @return
	 * 
	 * Get the information from the server based on the last id updated, this routine ensures that even if
	 * some exception occur, it will restart from the last media downloaded
	 * 
	 */
	private InsteReturn getImages(String user, InsteReturn feed) {
		InsteReturn ret = new InsteReturn();
		try {
			//get the media based on last media
			String url = "https://www.instagram.com/" + user + "/media";
			if (feed != null) {
				String lastId = feed.getItems().last().getId();
				url = "https://www.instagram.com/" + user + "/media?max_id=" + lastId;
			}

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//read information from returned Json
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			try {
				ret = mapper.readValue(response.toString(), InsteReturn.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
				throw new UserNotFoundException();
			} catch (JsonMappingException e) {
				e.printStackTrace();
				throw new UserNotFoundException();
			} catch (IOException e) {
				e.printStackTrace();
				throw new UserNotFoundException();
			} catch (NumberFormatException e) {  
				e.printStackTrace();
				throw new UserNotFoundException();
			}
			
			//Add downloaded media, as the collection is a treeSet, it will be unique and ordered 
			if (feed == null) {
				return ret;
			} else {
				feed.getItems().addAll(ret.getItems());
				feed.setMore_available(ret.getMore_available());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new UserNotFoundException();
		}

		return feed;

	}

	/**
	 * 
	 * @param user
	 * @param feed
	 * @return
	 * 
	 * Get the information from the server based on the until the newest media
	 * 
	 */
	private InsteReturn getLasterImages(String user, InsteReturn feed, Long lastPhoto) {
		InsteReturn ret = new InsteReturn();
		try {
			//get the media based on last media
			String url = "https://www.instagram.com/" + user + "/media";
			if (feed != null) {
				String lastId = feed.getItems().last().getId();
				url = "https://www.instagram.com/" + user + "/media?max_id=" + lastId;
			}

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");


			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			//read information from returned Json
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			try {
				ret = mapper.readValue(response.toString(), InsteReturn.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {  
				e.printStackTrace();
			}
			
			
			/*
			 * check if the date of the newest download is lesser than the newest photo on memory,
			 * it will perform the method until reach this condicion 
			 */
		
			if (feed == null) {
				if(ret.getItems().last().getCreated_time()<lastPhoto){
					ret.setMore_available_laster(false);
				}else{
					ret.setMore_available_laster(true);
				}
				return ret;
			} else {
				feed.getItems().addAll(ret.getItems());
				if(ret.getItems().last().getCreated_time()<lastPhoto){
					feed.setMore_available_laster(false);
				}else{
					feed.setMore_available_laster(true);
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return feed;

	}

}
