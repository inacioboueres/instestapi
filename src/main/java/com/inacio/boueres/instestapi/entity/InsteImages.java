package com.inacio.boueres.instestapi.entity;

import java.io.Serializable;

public class InsteImages implements Serializable {
	
	private static final long serialVersionUID = 3543524668150404986L;
	
	private InsteImageItem low_resolution;
	private InsteImageItem standard_resolution;
	private InsteImageItem thumbnail;
	public InsteImageItem getLow_resolution() {
		return low_resolution;
	}
	public void setLow_resolution(InsteImageItem low_resolution) {
		this.low_resolution = low_resolution;
	}
	public InsteImageItem getStandard_resolution() {
		return standard_resolution;
	}
	public void setStandard_resolution(InsteImageItem standard_resolution) {
		this.standard_resolution = standard_resolution;
	}
	public InsteImageItem getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(InsteImageItem thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	

}
