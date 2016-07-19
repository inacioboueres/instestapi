package com.inacio.boueres.instestapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
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

@Service
public class RetrieveNewPostsService {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private static final Map<String, InsteReturn> followed = new HashMap<String, InsteReturn>();
	
	@Autowired
	AsyncImageLoaderService asyncImageLoaderService;

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
	
	public void deleteUser(String user){
		if(!followed.get(user).getMore_available()){
			followed.remove(user);
		}else{
			asyncImageLoaderService.removeUser(user);
			followed.remove(user);
		}
	}

	private InsteReturn getImages(String user, InsteReturn feed) {
		InsteReturn ret = new InsteReturn();
		try {
			String url = "https://www.instagram.com/" + user + "/media";
			if (feed != null) {
//				String lastId = feed.getItems().get(feed.getItems().size() - 1).getId();
				String lastId = feed.getItems().last().getId();
				url = "https://www.instagram.com/" + user + "/media?max_id=" + lastId;
			}

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

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
			} catch (NumberFormatException e) { // Can try to pass something
												// else to rules.id that expect
												// a BigDecimal
				e.printStackTrace();
				throw new UserNotFoundException();
			}
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

	private InsteReturn getLasterImages(String user, InsteReturn feed, Long lastPhoto) {
		InsteReturn ret = new InsteReturn();
		try {
//			String url = "https://www.instagram.com/" + user + "/media";
//			if (feed != null) {
//				String lastId = feed.getItems().get(0).getId();
//				url = "https://www.instagram.com/" + user + "/media?min_id=" + lastId;
//			}
			String url = "https://www.instagram.com/" + user + "/media";
			if (feed != null) {
//				String lastId = feed.getItems().get(feed.getItems().size() - 1).getId();
				String lastId = feed.getItems().last().getId();
				url = "https://www.instagram.com/" + user + "/media?max_id=" + lastId;
			}

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			// con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

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
			} catch (NumberFormatException e) { // Can try to pass something
												// else to rules.id that expect
												// a BigDecimal
				e.printStackTrace();
			}
			if (feed == null) {
				if(ret.getItems().last().getCreated_time()<lastPhoto){
					ret.setMore_available_laster(false);
				}else{
					ret.setMore_available_laster(true);
				}
				return ret;
			} else {
				feed.getItems().addAll(ret.getItems());
//				if(ret.getItems().get(ret.getItems().size()-1).getCreated_time()<lastPhoto){
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
