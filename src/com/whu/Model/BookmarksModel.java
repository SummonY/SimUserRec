package com.whu.Model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.Beans.Bookmarks;
import com.whu.Manager.SessionFactoryManager;

public class BookmarksModel {
	private volatile static BookmarksModel sharedModel = null;
	
	private BookmarksModel() {
		
	}
	
	public static BookmarksModel getModel() {
		if (sharedModel == null) {
			synchronized (BookmarksModel.class) {
				if (sharedModel == null) {
					sharedModel = new BookmarksModel();
				}
			}
		}
		return sharedModel;
	}
	
	/*
	 * 保存书签信息到数据库
	 */
	public void saveBookmarks(String id, String title, String url, String urlPrincipal) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			Bookmarks bookmark = new Bookmarks();
			bookmark.setBm_ID(id);
			bookmark.setBm_Title(title);
			bookmark.setBm_URL(url);
			bookmark.setBm_urlPrincipal(urlPrincipal);
			
			session.save(bookmark);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
	}
}
