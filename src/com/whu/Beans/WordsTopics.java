package com.whu.Beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WordsTopics")
public class WordsTopics {
	private Long wt_ID;
	private String word;
	private String topic_ID;
	
	@Id
	@GeneratedValue
	public Long getWt_ID() {
		return wt_ID;
	}
	public void setWt_ID(Long wt_ID) {
		this.wt_ID = wt_ID;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getTopic_ID() {
		return topic_ID;
	}
	public void setTopic_ID(String topic_ID) {
		this.topic_ID = topic_ID;
	}
}
