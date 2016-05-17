package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.Bookmark_Tags;
import com.whu.Manager.SessionFactoryManager;

public class BookmarkTagsModel {
	private volatile static BookmarkTagsModel sharedModel = null;
	
	private BookmarkTagsModel() {
		
	}
	
	public static BookmarkTagsModel getModel() {
		if (sharedModel == null) {
			synchronized (BookmarkTagsModel.class) {
				if (sharedModel == null) {
					sharedModel = new BookmarkTagsModel();
				}
			}
		}
		return sharedModel;
	}
	
	/*
	 * 保存用户订阅关系到数据库
	 */
	public void saveBookmarkTags(String bm_ID, String tag_id, String tag_weight) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			Bookmark_Tags bm_Tags = new Bookmark_Tags();
			bm_Tags.setBookmark_ID(bm_ID);
			bm_Tags.setTag_ID(tag_id);
			bm_Tags.setTag_Weight(Long.parseLong(tag_weight));
			
			session.save(bm_Tags);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
}
