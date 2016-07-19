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


@Service
public class AsyncImageLoaderService {

	private List<String> removeUser = new ArrayList<String>();
	
	@Async
	public Future<String>  removeUser(String user){
		removeUser.add(user);
		return new AsyncResult<String>(user);
	}
	
	@Async
	public Future<InsteReturn>  getImagesAsync(String user, InsteReturn feed) throws InterruptedException {
		while (feed.getMore_available()) {
			feed = getImages(user, feed);
			if(removeUser.contains(user)){
				removeUser.remove(user);
				break;
			}
		}
		return  new AsyncResult<InsteReturn>(feed);
	}
	
	public InsteReturn getImages(String user, InsteReturn feed) {
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
