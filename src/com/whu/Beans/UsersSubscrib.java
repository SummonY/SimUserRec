package com.whu.Beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="UsersSubscrib")
public class UsersSubscrib {
	private Long sub_ID;
	private String user_FromID;
	private String user_ToID;
	
	@Id
	@GeneratedValue
	public Long getSub_ID() {
		return sub_ID;
	}
	public void setSub_ID(Long sub_ID) {
		this.sub_ID = sub_ID;
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
}