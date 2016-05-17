package com.whu.Beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Bookmark_Tags")
public class Bookmark_Tags {
	private Long bt_ID;
	private String bookmark_ID;
	private String tag_ID;
	private Long tag_Weight;
	
	@Id
	@GeneratedValue
	public Long getBt_ID() {
		return bt_ID;
	}
	public void setBt_ID(Long bt_ID) {
		this.bt_ID = bt_ID;
	}
	public String getBookmark_ID() {
		return bookmark_ID;
	}
	public void setBookmark_ID(String bookmark_ID) {
		this.bookmark_ID = bookmark_ID;
	}
	public String getTag_ID() {
		return tag_ID;
	}
	public void setTag_ID(String tag_ID) {
		this.tag_ID = tag_ID;
	}
	public Long getTag_Weight() {
		return tag_Weight;
	}
	public void setTag_Weight(Long tag_Weight) {
		this.tag_Weight = tag_Weight;
	}
}
