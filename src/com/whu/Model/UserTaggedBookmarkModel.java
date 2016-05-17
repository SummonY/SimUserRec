package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.User_TaggedBookmarks;
import com.whu.Manager.SessionFactoryManager;

public class UserTaggedBookmarkModel {
	private volatile static UserTaggedBookmarkModel sharedModel = null;
	
	private UserTaggedBookmarkModel() {
		
	}
	
	public static UserTaggedBookmarkModel getModel() {
		if (sharedModel == null) {
			synchronized (UserTaggedBookmarkModel.class) {
				if (sharedModel == null) {
					sharedModel = new UserTaggedBookmarkModel();
				}
			}
		}
		return sharedModel;
	}
	
	public void saveUserTaggedBookmarks(String user_ID, String bookmark_ID, String tag_ID) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			User_TaggedBookmarks user_TagedBm = new User_TaggedBookmarks();
			user_TagedBm.setUser_ID(user_ID);
			user_TagedBm.setBookmark_ID(bookmark_ID);
			user_TagedBm.setTag_ID(tag_ID);
			
			session.save(user_TagedBm);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
}
