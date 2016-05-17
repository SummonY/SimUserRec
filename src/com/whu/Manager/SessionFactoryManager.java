package com.whu.Manager;


import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class SessionFactoryManager {
	private static SessionFactory sessionFactory = null;
	private volatile static SessionFactoryManager sharedManager = null;
	
	private SessionFactoryManager() {
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}
	
	public static SessionFactoryManager sharedSessionFactory() {
		if (sharedManager == null) {
			synchronized (SessionFactoryManager.class) {
				if (sharedManager == null) {
					sharedManager = new SessionFactoryManager();
				}
			}
		}
		return sharedManager;
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
