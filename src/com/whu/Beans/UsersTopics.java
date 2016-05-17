package com.whu.Beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Users_Topics")
public class UsersTopics {
	private Long ut_ID;
	private String user_ID;
	private int topic_Num;
	private String topic_Set;
	
	@Id
	@GeneratedValue
	public Long getUt_ID() {
		return ut_ID;
	}
	public void setUt_ID(Long ut_ID) {
		this.ut_ID = ut_ID;
	}
	public String getUser_ID() {
		return user_ID;
	}
	public void setUser_ID(String user_ID) {
		this.user_ID = user_ID;
	}
	public int getTopic_Num() {
		return topic_Num;
	}
	public void setTopic_Num(int topic_Num) {
		this.topic_Num = topic_Num;
	}
	
	@Column(length=1000)
	public String getTopic_Set() {
		return topic_Set;
	}
	public void setTopic_Set(String topic_Set) {
		this.topic_Set = topic_Set;
	}
}
