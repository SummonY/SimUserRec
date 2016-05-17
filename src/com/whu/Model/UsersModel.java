package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.Users;
import com.whu.Manager.SessionFactoryManager;

public class UsersModel {
	private volatile static UsersModel sharedModel = null;
	
	private UsersModel() {
		
	}
	
	public static UsersModel getModel() {
		if (sharedModel == null) {
			synchronized (UsersModel.class) {
				if (sharedModel == null) {
					sharedModel = new UsersModel();
				}
			}
		}
		return sharedModel;
	}
	
	
	/*
	 * 保存用户到数据库
	 */
	public void saveUsers(String user_ID) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			Users user = new Users();
			user.setUser_ID(user_ID);
			user.setUser_Name("U" + user_ID);
			
			session.save(user);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
}
