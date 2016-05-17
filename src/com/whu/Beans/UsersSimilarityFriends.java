package com.whu.Beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="UsersSimilarityFriends")
public class UsersSimilarityFriends {
	private Long usf_ID;
	private String user_FromID;
	private String user_ToID;
	private Double similarity;
	
	@Id
	@GeneratedValue
	public Long getUsf_ID() {
		return usf_ID;
	}
	public void setUsf_ID(Long usf_ID) {
		this.usf_ID = usf_ID;
	}
	public String getUser_FromID() {
		return user_FromID;
	}
	public void setUser_FromID(String user_FromID) {
		this.user_FromID = user_FromID;
	}
	public String getUser_ToID() {
		return user_ToID;
	}
	public void setUser_ToID(String user_ToID) {
		this.user_ToID = user_ToID;
	}
	public Double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(Double similarity) {
		this.similarity = similarity;
	}
}
