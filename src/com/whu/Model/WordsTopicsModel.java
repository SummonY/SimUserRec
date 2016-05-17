package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.WordsTopics;
import com.whu.Manager.SessionFactoryManager;

public class WordsTopicsModel {
	private volatile static WordsTopicsModel sharedModel = null;
	
	private WordsTopicsModel() {
		
	}
	
	public static WordsTopicsModel getModel() {
		if (sharedModel == null) {
			synchronized (WordsTopicsModel.class) {
				if (sharedModel == null) {
					sharedModel = new WordsTopicsModel();
				}
			}
		}
		return sharedModel;
	}
	
	
	/*
	 * 保存用户到数据库
	 */
	public void saveWordsTopics(String word, String topic_ID) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			WordsTopics wordsTopics = new WordsTopics();
			wordsTopics.setWord(word);
			wordsTopics.setTopic_ID(topic_ID);
			
			session.save(wordsTopics);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
}
