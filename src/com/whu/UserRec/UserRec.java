package com.whu.UserRec;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.LDA.LDAParameters;
import com.whu.Manager.SessionFactoryManager;
import com.whu.Model.UsersSimilarityFriBasedModel;
import com.whu.Model.UsersSimilarityTopicsModel;

public class UserRec {
	
	/*
	 * 基于用户兴趣，度量用户相似度
	 */
	public void mesureUsersSimilarity() {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		List<String> userLists = null;
//		Set<String> user_Set = new HashSet<String>();
		try {
			Query getUsers = session.createQuery("select u.user_ID from Users as u");
			userLists = getUsers.list();
			
//			for (String user : user_Set) {
//				if (!user_Set.contains(user)) {
//					user_Set.add(user);
//				}
//			}

			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
		
		for (String user_ID : userLists) {
			Set<String> commonTopics = new HashSet<String>();
			
			SessionFactory sf = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
			Session session2 = sf.getCurrentSession();
			Transaction tran = session2.beginTransaction();

			Double userSim = 0.0;
			Set<String> userTopics = new HashSet<String>();
			try {
				Query getUserTopics = session2.createQuery(
						"select ut.topic_Num, ut.topic_Set from UsersTopics as ut where ut.user_ID = :user_ID");
				getUserTopics.setParameter("user_ID", user_ID);

				int topic_Num = 0;
				String topic_Set = "";

				if (!getUserTopics.list().isEmpty()) {
					List<Object> userTopic = getUserTopics.list();
					for (Object object : userTopic) {
						Object[] topic = (Object[]) object;

						topic_Num = (Integer) topic[0];
						topic_Set = (String) topic[1];
					}
				}
				String[] topics = topic_Set.split(";");
				for (String topic : topics) {
					if (topic.trim().length() > 0) {
						userTopics.add(topic);
					}
				}
				userSim = Math.sqrt(((double) topic_Num / LDAParameters.topicNum));
				// System.out.println("用户 " + user_ID + "相似度：" + userSim);
				tran.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tran.rollback();
			}
			if (userSim > 0) {
				
//				SessionFactory sf2 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
//				Session session3 = sf2.getCurrentSession();
//				Transaction tran2 = session3.beginTransaction();
//
//				List<String> otherUserLists = null;
//				try {
//					Query getOtherUsers = session3.createQuery("select u.user_ID from Users as u where u.user_ID != :user_ID");
//					getOtherUsers.setParameter("user_ID", user_ID);
//					otherUserLists = getOtherUsers.list();
//					
//					tran2.commit();
//				} catch (Exception e) {
//					e.printStackTrace();
//					tran2.rollback();
//				}
				
				for (String otherUser : userLists) {
					if (otherUser == user_ID) {
						continue;
					}
					commonTopics.clear();
					commonTopics.addAll(userTopics);

					SessionFactory sf3 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
					Session session4 = sf3.getCurrentSession();
					Transaction tran3 = session4.beginTransaction();

					Double similarity = 0.0;
					try {
						Query getOtherUserTopics = session4.createQuery(
								"select ut.topic_Num, ut.topic_Set from UsersTopics as ut where ut.user_ID = :user_ID");
						getOtherUserTopics.setParameter("user_ID", otherUser);

						int ou_topic_Num = 0;
						String ou_topic_Set = "";
						Set<String> ou_userTopics = new HashSet<String>();
						if (!getOtherUserTopics.list().isEmpty()) {
							List<Object> ou_userTopic = getOtherUserTopics.list();
							for (Object object : ou_userTopic) {
								Object[] topic2 = (Object[]) object;

								ou_topic_Num = (Integer) topic2[0];
								ou_topic_Set = (String) topic2[1];
							}
						}
						String[] ou_topics = ou_topic_Set.split(";");
						for (String ou_topic : ou_topics) {
							if (ou_topic.trim().length() > 0) {
								ou_userTopics.add(ou_topic);
							}
						}
						Double ou_userSim = Math.sqrt(((double) ou_topic_Num / LDAParameters.topicNum));
						commonTopics.retainAll(ou_userTopics);
						Double commonSim = (double) ((double) commonTopics.size() / LDAParameters.topicNum);

						if (ou_userSim > 0) {
							similarity = commonSim / (userSim * ou_userSim);
						}
						tran3.commit();
					} catch (Exception e) {
						e.printStackTrace();
						tran3.rollback();
					}

					if (similarity > 0) {
						System.out.println("用户" + user_ID + "和用户" + otherUser + " 相似度：" + similarity);
						UsersSimilarityTopicsModel.getModel().saveUsersSimilarityTopics(user_ID, otherUser, similarity);
					}
				}
			}
		}
	}
	
	/*
	 * 基于用户当前好友关系状态，结合用户兴趣相似度，重新计算用户相似度
	 * 是否推荐用户U2，算法1：U1-->U6, U2-->U6
	 */
	public void masureFriBasedUsersSimilarity(Double lambda) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		List<String> userLists = null;
		try {
			Query getUsers = session.createQuery("select u.user_ID from Users as u");
			userLists = getUsers.list();
			
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
		
		for (String user_ID : userLists) {
			
			// 获取用户订阅集合
			Set<String> comUsers_Set = new HashSet<String>();
			Set<String> subUsers_Set = new HashSet<String>();
			
			SessionFactory sf2 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
			Session session2 = sf2.getCurrentSession();
			Transaction tran2 = session2.beginTransaction();
			
			try {
				Query getSubUsers = session2.createQuery(
						"select us.user_ToID from UsersSubscrib as us where us.user_FromID = :user_ID");
				getSubUsers.setParameter("user_ID", user_ID);

				if (!getSubUsers.list().isEmpty()) {
					List<String> subUsersLists = getSubUsers.list();
					for (String subUser : subUsersLists) {
						if (!subUsers_Set.contains(subUser)) {
							subUsers_Set.add(subUser);
						}
					}
				}
				
				tran2.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tran2.rollback();
			}
			
			// 获取其它所有用户
			for (String otherUser : userLists) {
				if (user_ID == otherUser) {
					continue;
				}
				
				SessionFactory sf3 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
				Session session3 = sf3.getCurrentSession();
				Transaction tran3 = session3.beginTransaction();
				
				Set<String> toUsers_Set = new HashSet<String>();
				try {
					Query getToUsers = session3.createQuery("select us.user_ToID from UsersSubscrib as us where us.user_FromID = :user_ID");
					getToUsers.setParameter("user_ID", otherUser);
					
					List<String> toUsersList = getToUsers.list();
					for (String toUser : toUsersList) {
						if (!toUsers_Set.contains(toUser)) {
							toUsers_Set.add(toUser);
						}
					}
					
					tran3.commit();
				} catch (Exception e) {
					e.printStackTrace();
					tran3.rollback();
				}
				
				comUsers_Set.clear();
				comUsers_Set.addAll(subUsers_Set);
				comUsers_Set.retainAll(toUsers_Set);
				
				int comUsers_Num = comUsers_Set.size();
				
				SessionFactory sf5 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
				Session session5 = sf5.getCurrentSession();
				Transaction tran5 = session5.beginTransaction();
				
				Double topicSim = 0.0;
				try {
					
					Query getFromSimilarity = session5.createQuery("select ust.similarity from UsersSimilarityTopics as ust "
							+ "where ust.user_FromID = :user_FromID and ust.user_ToID = :user_ToID");
					getFromSimilarity.setParameter("user_FromID", user_ID);
					getFromSimilarity.setParameter("user_ToID", otherUser);
					
					List<Double> fromSimList = getFromSimilarity.list();
					for (Double object : fromSimList) {
						topicSim = (Double) object;
					}
					
					tran5.commit();
				} catch (Exception e) {
					e.printStackTrace();
					tran5.rollback();
				}
				
				
				
				Double comSim = 0.0;
				
				for (Iterator it = comUsers_Set.iterator(); it.hasNext();) {
					String comUser = (String) it.next();
					
					SessionFactory sf4 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
					Session session4 = sf4.getCurrentSession();
					Transaction tran4 = session4.beginTransaction();
					
					Double fromSim = 0.0;
					Double toSim = 0.0;
					try {
						
						Query getFromSimilarity = session4.createQuery("select ust.similarity from UsersSimilarityTopics as ust "
								+ "where ust.user_FromID = :user_FromID and ust.user_ToID = :user_ToID");
						getFromSimilarity.setParameter("user_FromID", user_ID);
						getFromSimilarity.setParameter("user_ToID", comUser);
						
						List<Double> fromSimList = getFromSimilarity.list();
						for (Object object : fromSimList) {
							fromSim = (Double) object;
						}
						
						Query getToSimilarity = session4.createQuery("select ust.similarity from UsersSimilarityTopics as ust "
								+ "where ust.user_FromID = :user_FromID and ust.user_ToID = :user_ToID");
						getToSimilarity.setParameter("user_FromID", otherUser);
						getToSimilarity.setParameter("user_ToID", comUser);
						
						List<Double> toSimList = getToSimilarity.list();
						for (Double object : toSimList) {
							toSim = object;
						}
						
						comSim += (fromSim + toSim) / 2;
						
						tran4.commit();
					} catch (Exception e) {
						e.printStackTrace();
						tran4.rollback();
					}
				}
				
				if (comUsers_Num > 0) {
					comSim = comSim / comUsers_Num;
				}
				
				Double userSim = lambda * topicSim + (1 - lambda) * comSim;
				
				if (userSim > 0.2) {
					System.out.println("用户" + user_ID + "和用户" + otherUser + "共同用户个数：" + comUsers_Num + "相似度：" + userSim);
				}
				
				
				
			
			}
		}
	}
	
	
	/*
	 * 基于用户当前好友关系状态，结合用户兴趣相似度，重新计算用户相似度
	 * 是否推荐用户U2，算法2: U1-->U6, U6-->U2
	 */
	public void masureFriBasedUsersSimilarity2(Double lambda, Double gamma) {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		List<String> userLists = null;
		try {
			Query getUsers = session.createQuery("select u.user_ID from Users as u ");
			userLists = getUsers.list();
			
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
		
		int sum_Num = userLists.size();
		for (int u = 0; u < sum_Num; u++) {
			String user_ID = userLists.get(u);
			
			if (u <= 20) {		// 20个用户进行测试
				continue;
			}
			if (u >= 23) {
				break;
			}
			
			// 获取用户订阅集合
			Set<String> comUsers_Set = new HashSet<String>();
			Set<String> subUsers_Set = new HashSet<String>();
			
			SessionFactory sf2 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
			Session session2 = sf2.getCurrentSession();
			Transaction tran2 = session2.beginTransaction();
			
			try {
				Query getSubUsers = session2.createQuery(
						"select us.user_ToID from UsersSubscrib as us where us.user_FromID = :user_ID");
				getSubUsers.setParameter("user_ID", user_ID);

				if (!getSubUsers.list().isEmpty()) {
					List<String> subUsersLists = getSubUsers.list();
					for (String subUser : subUsersLists) {
						if (!subUsers_Set.contains(subUser)) {
							subUsers_Set.add(subUser);
						}
					}
				}
				
				tran2.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tran2.rollback();
			}
			
			int subUser_Num = subUsers_Set.size();
			
			// 获取其它所有用户
			for (String otherUser : userLists) {
				if (user_ID == otherUser) {
					continue;
				}
				
				SessionFactory sf3 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
				Session session3 = sf3.getCurrentSession();
				Transaction tran3 = session3.beginTransaction();
				
				Set<String> toUsers_Set = new HashSet<String>();
				try {
					Query getToUsers = session3.createQuery("select us.user_ToID from UsersSubscrib as us where us.user_FromID = :user_ID");
					getToUsers.setParameter("user_ID", otherUser);
					
					List<String> toUsersList = getToUsers.list();
					for (String toUser : toUsersList) {
						if (!toUsers_Set.contains(toUser)) {
							toUsers_Set.add(toUser);
						}
					}
					
					tran3.commit();
				} catch (Exception e) {
					e.printStackTrace();
					tran3.rollback();
				}
				
				comUsers_Set.clear();
				comUsers_Set.addAll(subUsers_Set);
				comUsers_Set.retainAll(toUsers_Set);
				
				int comUsers_Num = comUsers_Set.size();
				
				SessionFactory sf5 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
				Session session5 = sf5.getCurrentSession();
				Transaction tran5 = session5.beginTransaction();
				
				Double topicSim = 0.0;
				try {
					
					Query getFromSimilarity = session5.createQuery("select ust.similarity from UsersSimilarityTopics as ust "
							+ "where ust.user_FromID = :user_FromID and ust.user_ToID = :user_ToID");
					getFromSimilarity.setParameter("user_FromID", user_ID);
					getFromSimilarity.setParameter("user_ToID", otherUser);
					
					List<Double> fromSimList = getFromSimilarity.list();
					for (Double object : fromSimList) {
						topicSim = object;
					}
					
					tran5.commit();
				} catch (Exception e) {
					e.printStackTrace();
					tran5.rollback();
				}
				
				
				Double comSim = 0.0;
				
				for (Iterator it = comUsers_Set.iterator(); it.hasNext();) {
					String comUser = (String) it.next();
					
					SessionFactory sf4 = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
					Session session4 = sf4.getCurrentSession();
					Transaction tran4 = session4.beginTransaction();
					
					Double fromSim = 0.0;
					Double toSim = 0.0;
					try {
						
						Query getFromSimilarity = session4.createQuery("select ust.similarity from UsersSimilarityTopics as ust "
								+ "where ust.user_FromID = :user_FromID and ust.user_ToID = :user_ToID");
						getFromSimilarity.setParameter("user_FromID", user_ID);
						getFromSimilarity.setParameter("user_ToID", comUser);
						
						List<Double> fromSimList = getFromSimilarity.list();
						for (Double object : fromSimList) {
							fromSim = object;
						}
						
						Query getToSimilarity = session4.createQuery("select ust.similarity from UsersSimilarityTopics as ust "
								+ "where ust.user_FromID = :user_FromID and ust.user_ToID = :user_ToID");
						getToSimilarity.setParameter("user_FromID", comUser);
						getToSimilarity.setParameter("user_ToID", otherUser);
						
						List<Double> toSimList = getToSimilarity.list();
						for (Double object : toSimList) {
							toSim = object;
						}
						
						comSim += (fromSim + toSim) / 2;
						
						tran4.commit();
					} catch (Exception e) {
						e.printStackTrace();
						tran4.rollback();
					}
				}
				
				if (comUsers_Num > 0) {
					comSim = comSim / comUsers_Num;
				}
				
				Double numSim = (double)comUsers_Num / subUser_Num;
				
				Double userSim = lambda * topicSim + (1 - lambda - gamma) * comSim + gamma * numSim;
				
				if (userSim > 0.3) {
					System.out.println("用户" + user_ID + "和用户" + otherUser + "\t共同用户个数：" + comUsers_Num + "\t相似度：" + userSim);
				}
				
				UsersSimilarityFriBasedModel.getModel().saveUsersSimilarityFriBased(user_ID, otherUser, userSim);
				
			}
		}
	}
	
	/*
	 * 基于用户相似度，提取top-k相似用户
	 */
	public void extractTopKSimUsers(int k) {
		Double prevision_Rate = 0.0;		// 精准率
		Double recall_Rate = 0.0;		// 召回率
		int sum_Num = 0;
		
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		List<String> userLists = null;
		try {
			Query getUsers = session.createQuery("select distinct usf.user_FromID from UsersSimilarityFriends as usf ");
			userLists = getUsers.list();
			sum_Num = userLists.size();
			
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
		
		int user_Num = 20;
		
		
		for (int n = 0; n < user_Num; ++n) {
			String user_ID = userLists.get(n);
			
			Double precision = 0.0;		// 精准率
			Double recall = 0.0;		// 召回率
			
			SessionFactory sf = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
			Session session2 = sf.getCurrentSession();
			Transaction tran2 = session2.beginTransaction();
			
			Set<String> recResult = new HashSet<String>();
			try {
				Query getSubUsers = session2.createQuery("select us.user_ToID from UsersSubscrib as us "
						+ "where us.user_FromID = :user_ID");
				getSubUsers.setParameter("user_ID", user_ID);
				
				List<String> subLists = getSubUsers.list();
				Set<String> sub_Set = new HashSet<String>();
				String sub_Users = "";
				for (String subUser : subLists) {
//					if (!sub_Set.contains(subUser)) {
						sub_Set.add(subUser);
//					}
					sub_Users += subUser + ";";
				}
				int sub_Num = sub_Set.size();
//				System.out.println("订阅用户集合：" + sub_Users);
				
				Query getSimUsers = session2.createQuery("select usf.user_ToID from UsersSimilarityFriends as usf "
						+ "where usf.user_FromID = :from_ID order by usf.similarity desc");
				getSimUsers.setParameter("from_ID", user_ID);
				
				List<String> simLists = getSimUsers.list();
				Set<String> sim_Set = new HashSet<String>();
				
				String sim_Users = "";
				for (int i = 0; i < k && i < simLists.size(); ++i) {
//					if (sim_Set.contains(simUser)) {
						sim_Set.add(simLists.get(i));
//					}
					sim_Users += simLists.get(i) + ";";
				}
				int sim_Num = sim_Set.size();
//				System.out.println("推荐用户集合：" + sim_Users);
				
				recResult.clear();
				recResult.addAll(sub_Set);
				recResult.retainAll(sim_Set);
				
				
				int true_Num = recResult.size();
				
//				System.out.println("推荐个数：" + sim_Num + "\t订阅个数: " + sub_Num + "\t正确个数：" + true_Num);
				if (sub_Num > 0) {
					precision = (double)true_Num / sim_Num;
				}
//				System.out.println("精准度：" + precision);
				if (sim_Num > 0) {
					recall = (double)true_Num / sub_Num;
				}
//				System.out.println("召回率：" + recall);
				
				prevision_Rate += precision;
				recall_Rate += recall;
				
				tran2.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tran2.rollback();
			}
		}
		
		prevision_Rate = prevision_Rate / user_Num;
		recall_Rate = recall_Rate / user_Num;
		
		System.out.println("精准度：" + prevision_Rate);
		System.out.println("召回率：" + recall_Rate);
		
	}
}
