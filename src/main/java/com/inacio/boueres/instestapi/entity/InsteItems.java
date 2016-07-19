package com.inacio.boueres.instestapi.entity;

import java.io.Serializable;

public class InsteItems implements Serializable, Comparable<InsteItems>{
	
	private static final long serialVersionUID = -7499936216173367723L;
	
	private String id;
	private Long created_time;
	private InsteImages images;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public InsteImages getImages() {
		return images;
	}
	public void setImages(InsteImages images) {
		this.images = images;
	}
	public Long getCreated_time() {
		return created_time;
	}
	public void setCreated_time(Long created_time) {
		this.created_time = created_time;
	}
	@Override
	public int compareTo(InsteItems o) {
		return (this.getCreated_time().compareTo(o.getCreated_time())*-1);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		InsteItems other = (InsteItems) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	

}
