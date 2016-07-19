package com.inacio.boueres.instestapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inacio.boueres.instestapi.entity.InsteReturn;

/**
 * 
 * @author Inacio da Cunha Boueres Filho
 * This service is used to start and stop the task that will retrieve the 
 */
@Service
public class AsyncImageLoaderService {

	//List of user on queue to be removed from task
	private List<String> removeUser = new ArrayList<String>();
	
	/**
	 * 
	 * @param user
	 * @return
	 * Indicate that a user must have his updating task stoped
	 */
	@Async
	public Future<String>  removeUser(String user){
		removeUser.add(user);
		return new AsyncResult<String>(user);
	}
	
	/**
	 * 
	 * @param user
	 * @param feed
	 * @return
	 * @throws InterruptedException
	 * Task that will try to retrieve the media from user passed as parameter and fill the feed with the return
	 * as the API changed and don't give access to public media without an explicit permission from instagram 
	 * I was forced to use "https://www.instagram.com/user/media" to retrieve the user media information
	 * but it return only 20 posts on a roll, so until it returns the field More_available=false, I'll need to request
	 * the next 20 over and over
	 */
	@Async
	public Future<InsteReturn>  getImagesAsync(String user, InsteReturn feed) throws InterruptedException {
		while (feed.getMore_available()) {
			feed = getImages(user, feed);
			//if the user is on remove queue, stop the task
			if(removeUser.contains(user)){
				removeUser.remove(user);
				break;
			}
		}
		return  new AsyncResult<InsteReturn>(feed);
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
	public InsteReturn getImages(String user, InsteReturn feed) {
		InsteReturn ret = new InsteReturn();
		//get the media based on last media
		try {
			String url = "https://www.instagram.com/" + user + "/media";
			if (feed != null) {
				String lastId = feed.getItems().last().getId();
				url = "https://www.instagram.com/" + user + "/media?max_id=" + lastId;
			}

			//perform get
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
		}

		return feed;

	}
	
}
