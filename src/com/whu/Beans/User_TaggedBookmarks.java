package com.whu.Beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="User_TaggedBookmarks")
public class User_TaggedBookmarks {
	private Long utb_ID;
	private String user_ID;
	private String bookmark_ID;
	private String tag_ID;
	
	@Id
	@GeneratedValue
	public Long getUtb_ID() {
		return utb_ID;
	}
	public void setUtb_ID(Long utb_ID) {
		this.utb_ID = utb_ID;
	}
	public String getUser_ID() {
		return user_ID;
	}
	public void setUser_ID(String user_ID) {
		this.user_ID = user_ID;
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
}
