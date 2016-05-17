package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.UsersTopics;
import com.whu.Manager.SessionFactoryManager;

public class UsersTopicsModel {
	private volatile static UsersTopicsModel sharedModel = null;
	
	private UsersTopicsModel() {
		
	}
	
	public static UsersTopicsModel getModel() {
		if (sharedModel == null) {
			synchronized (UsersTopicsModel.class) {
				if (sharedModel == null) {
					sharedModel = new UsersTopicsModel();
				}
			}
		}
		return sharedModel;
	}
	
	
	/*
	 * 保存用户到数据库
	 */
	public void saveUsersTopics(String user_ID, int topic_Num, String topic_Set) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			UsersTopics usersTopics = new UsersTopics();
			usersTopics.setUser_ID(user_ID);
			usersTopics.setTopic_Num(topic_Num);
			usersTopics.setTopic_Set(topic_Set);
			
			session.save(usersTopics);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
}
