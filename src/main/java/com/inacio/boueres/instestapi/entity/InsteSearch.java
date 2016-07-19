package com.inacio.boueres.instestapi.entity;

import java.io.Serializable;

public class InsteSearch implements Serializable{

	private static final long serialVersionUID = 7697399536520161446L;
	
	private String user;
	private Integer page;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	
	

}
