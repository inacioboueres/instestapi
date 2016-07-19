package com.inacio.boueres.instestapi.entity;

import java.io.Serializable;
import java.util.List;
import java.util.TreeSet;

public class InsteReturn implements Serializable {
	
	private static final long serialVersionUID = 5497036774253905911L;
	
	private String status;
	private TreeSet<InsteItems> items;
	private Boolean more_available;
	private Boolean more_available_laster;
	private Boolean remove = false;

	public TreeSet<InsteItems> getItems() {
		return items;
	}

	public void setItems(TreeSet<InsteItems> items) {
		this.items = items;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getMore_available() {
		return more_available;
	}

	public void setMore_available(Boolean more_available) {
		this.more_available = more_available;
	}

	public Boolean getMore_available_laster() {
		return more_available_laster;
	}

	public void setMore_available_laster(Boolean more_available_laster) {
		this.more_available_laster = more_available_laster;
	}

	public Boolean getRemove() {
		return remove;
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}
	
	

}
