package com.whu.Beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Tags")
public class Tags {
	private String tag_ID;
	private String tag_Name;
	
	@Id
	@GeneratedValue
	public String getTag_ID() {
		return tag_ID;
	}
	public void setTag_ID(String tag_ID) {
		this.tag_ID = tag_ID;
	}
	public String getTag_Name() {
		return tag_Name;
	}
	public void setTag_Name(String tag_Name) {
		this.tag_Name = tag_Name;
	}
}
