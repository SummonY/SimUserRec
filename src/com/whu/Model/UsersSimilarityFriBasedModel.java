package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.UsersSimilarityFriends;
import com.whu.Manager.SessionFactoryManager;

public class UsersSimilarityFriBasedModel {
	private volatile static UsersSimilarityFriBasedModel sharedModel = null;
	
	private UsersSimilarityFriBasedModel() {
		
	}
	
	public static UsersSimilarityFriBasedModel getModel() {
		if (sharedModel == null) {
			synchronized (UsersSimilarityFriBasedModel.class) {
				if (sharedModel == null) {
					sharedModel = new UsersSimilarityFriBasedModel();
				}
			}
		}
		return sharedModel;
	}
	
	
	/*
	 * 保存用户到数据库
	 */
	public void saveUsersSimilarityFriBased(String user_FromID, String user_ToID, Double similarity) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			UsersSimilarityFriends usf = new UsersSimilarityFriends();
			usf.setUser_FromID(user_FromID);
			usf.setUser_ToID(user_ToID);
			usf.setSimilarity(similarity);
			
			session.save(usf);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
}
