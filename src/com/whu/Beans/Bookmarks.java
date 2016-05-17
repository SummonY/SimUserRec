package com.whu.Beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Bookmarks")
public class Bookmarks {
	private String bm_ID;
	private String bm_Title;
	private String bm_URL;
	private String bm_urlPrincipal;
	
	@Id
	public String getBm_ID() {
		return bm_ID;
	}
	public void setBm_ID(String bm_ID) {
		this.bm_ID = bm_ID;
	}
	public String getBm_Title() {
		return bm_Title;
	}
	public void setBm_Title(String bm_Title) {
		this.bm_Title = bm_Title;
	}
	public String getBm_URL() {
		return bm_URL;
	}
	public void setBm_URL(String bm_URL) {
		this.bm_URL = bm_URL;
	}
	public String getBm_urlPrincipal() {
		return bm_urlPrincipal;
	}
	public void setBm_urlPrincipal(String bm_urlPrincipal) {
		this.bm_urlPrincipal = bm_urlPrincipal;
	}
}
