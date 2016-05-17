package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.UsersSimilarityTopics;
import com.whu.Manager.SessionFactoryManager;

public class UsersSimilarityTopicsModel {
	private volatile static UsersSimilarityTopicsModel sharedModel = null;
	
	private UsersSimilarityTopicsModel() {
		
	}
	
	public static UsersSimilarityTopicsModel getModel() {
		if (sharedModel == null) {
			synchronized (UsersSimilarityTopicsModel.class) {
				if (sharedModel == null) {
					sharedModel = new UsersSimilarityTopicsModel();
				}
			}
		}
		return sharedModel;
	}
	
	
	/*
	 * 保存用户到数据库
	 */
	public void saveUsersSimilarityTopics(String user_FromID, String user_ToID, Double similarity) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			UsersSimilarityTopics ust = new UsersSimilarityTopics();
			ust.setUser_FromID(user_FromID);
			ust.setUser_ToID(user_ToID);
			ust.setSimilarity(similarity);
			
			session.save(ust);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
}
