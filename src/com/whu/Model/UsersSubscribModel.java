package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.UsersSubscrib;
import com.whu.Manager.SessionFactoryManager;

public class UsersSubscribModel {
	private volatile static UsersSubscribModel sharedModel = null;
	
	private UsersSubscribModel() {
		
	}
	
	public static UsersSubscribModel getModel() {
		if (sharedModel == null) {
			synchronized (UsersSubscribModel.class) {
				if (sharedModel == null) {
					sharedModel = new UsersSubscribModel();
				}
			}
		}
		return sharedModel;
	}
	
	/*
	 * 保存用户订阅关系到数据库
	 */
	public void saveUsersSubscrib(String from_ID, String to_ID) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			UsersSubscrib usersSub = new UsersSubscrib();
			usersSub.setUser_FromID(from_ID);
			usersSub.setUser_ToID(to_ID);
			
			session.save(usersSub);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
	
}
