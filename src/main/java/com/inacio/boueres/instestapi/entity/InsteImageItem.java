package com.inacio.boueres.instestapi.entity;

import java.io.Serializable;

public class InsteImageItem implements Serializable{
	
	private static final long serialVersionUID = 2650283505135708941L;
	
	private String url;
	private Integer width;
	private Integer height;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	
	

}
