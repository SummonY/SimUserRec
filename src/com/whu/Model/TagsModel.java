package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.Tags;
import com.whu.Manager.SessionFactoryManager;

public class TagsModel {
	private volatile static TagsModel sharedModel = null;
	
	private TagsModel() {
		
	}
	
	public static TagsModel getModel() {
		if (sharedModel == null) {
			synchronized (TagsModel.class) {
				if (sharedModel == null) {
					sharedModel = new TagsModel();
				}
			}
		}
		return sharedModel;
	}
	
	/*
	 * 保存用户订阅关系到数据库
	 */
	public void saveTags(String id, String value) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			Tags tag = new Tags();
			tag.setTag_ID(id);
			tag.setTag_Name(value);
			
			session.save(tag);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
}
