package com.whu.Beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="UsersSimilarityTopics")
public class UsersSimilarityTopics {
	private Long ust_ID;
	private String user_FromID;
	private String user_ToID;
	private Double similarity;
	
	@Id
	@GeneratedValue
	public Long getUst_ID() {
		return ust_ID;
	}
	public void setUst_ID(Long ust_ID) {
		this.ust_ID = ust_ID;
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
