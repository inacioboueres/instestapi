package com.inacio.boueres.instestapi.entity;

import java.io.Serializable;

public class InsteFolowed implements Serializable, Comparable<InsteFolowed> {
	
	private static final long serialVersionUID = 4399682463944036358L;
	
	private String name;
	private Integer photos;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPhotos() {
		return photos;
	}
	public void setPhotos(Integer photos) {
		this.photos = photos;
	}
	@Override
	public int compareTo(InsteFolowed o) {
		return this.name.compareTo(o.name);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InsteFolowed other = (InsteFolowed) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

}
