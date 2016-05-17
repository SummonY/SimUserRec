package com.whu.LDA;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.whu.File.FileConfig;
import com.whu.File.FileUtil;
import com.whu.File.NoiseWords;
import com.whu.Manager.SessionFactoryManager;
import com.whu.Model.UsersTopicsModel;
import com.whu.Model.WordsTopicsModel;

public class LDAModel {
	private int M;				// 文档个数
	private int V;				// 单词个数
	private int K;				// 主题个数
	private int L;				// 迭代次数
	private Double alpha;		// 文档-主题，Dirichlet先验参数
	private Double beta;		// 主题-词，Dirichlet先验参数
	private int[][] nmk;			// 文档m中主题k的出现的次数
	private int[] nm;				// 文档m中所有主题总数
	private int[][] nkt;			// 主题k中词t出现的次数
	private int[] nk;				// 主题k中所有词的总数
	private int[][] z;				// 文档m，单词n，所对应的主题
	private Double[][] theta;		// 文档-主题，多项式分布
	private Double[][] phi;			// 主题-词，多项式分布
	private int[][] doc;			// 文档索引数组
	private int saveStep;			// 每隔迭代次数，保存一次
	private int beginSave;			// 开始保存迭代结果
	
	// 初始化LDA模型参数
	public LDAModel() {
		alpha = LDAParameters.alpha;
		beta = LDAParameters.beta;
		L = LDAParameters.iterationNum;
		K = LDAParameters.topicNum;
		beginSave = LDAParameters.beginSaveIter;
		saveStep = LDAParameters.saveIterStep;
	}
	
	/*
	 * 根据文档，初始化LDA模型
	 */
	public void initLDAModel(Documents doc_Set) {
		M = doc_Set.docs.size();				// 文档个数
		V = doc_Set.termToIndexMap.size();		// 所有文档中词的总数
		nmk = new int[M][K];
		nm  = new int[M];
		nkt = new int[K][V];
		nk  = new int[K];
		theta = new Double[M][K];
		phi   = new Double[K][V];
		
		// 初始化文档索引数组
		doc = new int[M][];
		for (int m = 0; m < M; ++m) {
			int N = doc_Set.docs.get(m).doc_Words.length;		// 获取第m篇文档的单词总数
			doc[m] = new int[N];
			for (int n = 0; n < N; ++n) {
				doc[m][n] = doc_Set.docs.get(m).doc_Words[n];	// 获取第m篇文档，第n个词
			}
		}
		
		// 为所有文档中每个词初始化主题标记
		z = new int[M][];
		for (int m = 0; m < M; ++m) {
			int N = doc_Set.docs.get(m).doc_Words.length;
			z[m] = new int[N];
			for (int n = 0; n < N; ++n) {
				int initTopic = (int)(Math.random() * K);		// 初始主题，取值为0 ~ K-1
				z[m][n] = initTopic;							// 初始化主题标记
				++nmk[m][initTopic];							// 文档m中主题出现次数+1
				++nm[m];										// 文档m中所有主题总次数+1
				++nkt[initTopic][doc[m][n]];					// 主题k中词出现次数+1
				++nk[initTopic];								// 主题k出现的总次数+1
			}
		}
	}
	
	/*
	 * 迭代，Gibbs采样阶段，更新主题标记
	 */
	public void gibbsSampleLDAModel(Documents doc_Set) throws IOException {
		for (int l = 0; l < L; ++l) {			// 迭代L次
			System.out.println("迭代次数: " + l);
			
			// 从beginSave开始保存，每隔saveStep保存一次
			if ((l >= beginSave) && ((l - beginSave) % saveStep == 0)) {
				System.out.println("保存迭代" + l + "次LDA模型计算结果");
				
				// 计算预估参数phi和theta
				computeEstimatedParameters();
				
				// 保存迭代结果
				saveLDAResult(l, doc_Set);
			}
			
			// 采用Gibbs采样，更新单词w{m,n}的主题
			for (int m = 0; m < M; ++m) {
				int N = doc_Set.docs.get(m).doc_Words.length;
				for (int n = 0; n < N; ++n) {
					z[m][n] = gibbsSampleTopic(m, n);
				}
			}
		}
	}
	
	/*
	 * 采用Gibbs采样算法，采样主题
	 */
	private int gibbsSampleTopic(int m, int n) {
		
		// 从原有主题中除去单词doc[m][n]
		int oldTopic = z[m][n];
		--nmk[m][oldTopic];							// 文档m中主题出现次数-1
		--nm[m];									// 文档m中所有主题总次数-1
		--nkt[oldTopic][doc[m][n]];					// 主题k中词出现次数-1
		--nk[oldTopic];								// 主题k出现的总次数-1
		
		// 计算𝑝(𝑧𝑖 = k|𝑧⃗¬𝑖, 𝓌)
		Double[] p = new Double[K];					// 主题向量
		for (int k = 0; k < K; ++k) {
			p[k] = (nkt[k][doc[m][n]] + beta) / (nk[k] + V * beta) * (nmk[m][k] + alpha) / (nm[m] + K * alpha);
		}
		
		// 采样新的主题，类似轮盘游戏
		// 计算p的累积概率
		for (int k = 1; k < K; ++k) {
			p[k] += p[k - 1];
		}
		
		double topic = Math.random() * p[K - 1];		// p[]未归一化
		int newTopic;
		for (newTopic = 0; newTopic < K; ++newTopic) {
			if (topic < p[newTopic]) {
				break;
			}
		}
		
		// 为单词w{m,n}分配新的主题
		++nmk[m][newTopic];							// 文档m中主题出现次数+1
		++nm[m];									// 文档m中所有主题总次数+1
		++nkt[newTopic][doc[m][n]];					// 主题k中词出现次数+1
		++nk[newTopic];								// 主题k出现的总次数+1
		
		return newTopic;
	}
	
	/*
	 * 计算预估参数phi和theta
	 */
	private void computeEstimatedParameters() {
		// 计算主题-词分布phi
		for(int k = 0; k < K; k++){
			for(int t = 0; t < V; t++){
				phi[k][t] = (nkt[k][t] + beta) / (nk[k] + V * beta);
			}
		}
		
		// 计算文档-主题分布theta
		for(int m = 0; m < M; m++){
			for(int k = 0; k < K; k++){
				theta[m][k] = (nmk[m][k] + alpha) / (nm[m] + K * alpha);
			}
		}
	}
	
	/*
	 * 保存迭代收敛结果
	 */
	public void saveLDAResult(int iters, Documents doc_Set) throws IOException {
		String resultPath = FileConfig.RESULTPATH;
		String modelName = "LDA_" + iters;
		
		// 保存LDA参数
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add("alpha = " + alpha);
		parameters.add("beta = " + beta);
		parameters.add("topicNum = " + K);
		parameters.add("docNum = " + M);
		parameters.add("termNum = " + V);
		parameters.add("iterNum = " + L);
		parameters.add("saveStep = " + saveStep);
		parameters.add("beginSave = " + beginSave);
		FileUtil.writeLines(resultPath + modelName + ".params", parameters);
		
		// 保存文档-主题theta分布，M x K
		BufferedWriter writer = new BufferedWriter(new FileWriter(resultPath + modelName + ".theta"));
		for (int m = 0; m < M; ++m) {
			for (int k = 0; k < K; ++k) {
				writer.write(theta[m][k] + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		
		// 保存主题-词phi分布，K x V
		writer = new BufferedWriter(new FileWriter(resultPath + modelName + ".phi"));
		for (int k = 0; k < K; ++k) {
			for (int v = 0; v < V; ++v) {
				writer.write(phi[k][v] + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		
		// 保存单词对应的主题
		writer = new BufferedWriter(new FileWriter(resultPath + modelName + ".tassign"));
		for (int m = 0; m < M; ++m) {
			for (int n = 0; n < doc[m].length; ++n) {
				writer.write("词：" + doc[m][n] + "-主题：" + z[m][n] + "\t");
			}
			writer.write("\n");
		}
		writer.close();
		
		// 保存主题，及主题中概率最大的词
		writer = new BufferedWriter(new FileWriter(resultPath + modelName + ".twords"));
		int num = LDAParameters.wordNumOfTopic;		// 每个主题下的最大概率单词数目
		for (int k = 0; k < K; ++k) {
			List<Integer> wordIndexList = new ArrayList<Integer>();
			for (int v = 0; v < V; ++v) {
				wordIndexList.add(new Integer(v));
			}
			Collections.sort(wordIndexList, new LDAModel.TopicWordCompare(phi[k]));
			
			writer.write("Topic " + k + "\t:\t");
			for (int n = 0; n < num; ++n) {
				writer.write(doc_Set.indexToTermList.get(wordIndexList.get(n)) + " " + phi[k][wordIndexList.get(n)] + "\t");
			}
			writer.write("\n");
		}
		writer.close();
	}
	
	/*
	 * 获取每个词对应的主题
	 */
	public void fetchTopicOfWord(Documents doc_Set) {
		for (int m = 0; m < M; ++m) {
			for (int n = 0; n < doc[m].length; ++n) {
				String word = doc_Set.indexToTermList.get(doc[m][n]);
				String topic = "T" + z[m][n];
				WordsTopicsModel.getModel().saveWordsTopics(word, topic);
//				System.out.println("词：" + doc_Set.indexToTermList.get(doc[m][n]) + "\t主题：" + z[m][n]);
//				System.out.println("词：" + doc[m][n] + "-主题：" + z[m][n] + "\t");
			}
//			System.out.println("\n");
		}
	}
	
	/*
	 * 主题单词排序
	 */
	private class TopicWordCompare implements Comparator<Integer>{
		private Double[] prob;
		
		public TopicWordCompare(Double[] prob) {
			this.prob = prob;
		}

		public int compare(Integer o1, Integer o2) {
			if (prob[o1] > prob[o2]) {
				return -1;
			} else if (prob[o1] < prob[o2]) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	/*
	 * 提取标签所在的主题
	 */
	public void extractBookmarkTopics() {
		
	}
	
	/*
	 * 提取用户兴趣主题
	 */
	public void extractUsersTopics() {
		SessionFactory sessionFactory = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		List<String> userLists = null;
		try {
			Query getAllUsers = session.createQuery("select u.user_ID from Users as u");
			userLists = getAllUsers.list();

			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollback();
		}
		for (String user_ID : userLists) {
			SessionFactory sf = SessionFactoryManager.sharedSessionFactory().getSessionFactory();
			Session sess = sf.getCurrentSession();
			Transaction tran = sess.beginTransaction();

			int topic_Num  = 0;
			String userTopics = "";
			try {
				Query getTitles = sess
						.createQuery("select bm.bm_Title from User_TaggedBookmarks as ut, Bookmarks as bm "
								+ "where ut.bookmark_ID = bm.bm_ID and ut.user_ID = :user_ID");
				getTitles.setParameter("user_ID", user_ID);

				ArrayList<String> words = new ArrayList<String>();
				HashSet<String> wordSet = new HashSet<String>();

				List<String> titles = getTitles.list();
				for (String title : titles) {
					FileUtil.tokenizeAndLowerCase(title, words);
				}
				// 删除噪音数据
				for (int w = 0; w < words.size(); ++w) {
					if (NoiseWords.isNoiseWord(words.get(w))) {
						words.remove(w);
						--w;
					} else {
						wordSet.add(words.get(w));
					}
				}
				// System.out.println("单词集合：" + words);

				HashSet<String> topicSet = new HashSet<String>();
				

				for (Iterator it = wordSet.iterator(); it.hasNext();) {
					String word = (String) it.next();

					Query getTopic = sess.createQuery("select topic_ID from WordsTopics as wt where wt.word = :word");
					getTopic.setParameter("word", word);
					List<String> topics = getTopic.list();

					for (String topic : topics) {
						if (!topicSet.contains(topic)) {
							topicSet.add(topic);
						}
					}
				}

				topic_Num = topicSet.size();
				for (Iterator iterator = topicSet.iterator(); iterator.hasNext();) {
					userTopics += iterator.next() + ";";
				}

				tran.commit();
			} catch (Exception e) {
				e.printStackTrace();
				tran.rollback();
			}

//			UsersTopics usersTopics = new UsersTopics();
//			usersTopics.setUser_ID(user_ID);
//			usersTopics.setTopic_Num(topic_Num);
//			usersTopics.setTopic_Set(userTopics);
//
//			session.save(usersTopics);
			 UsersTopicsModel.getModel().saveUsersTopics(user_ID, topic_Num, userTopics);

			System.out.println("用户：" + user_ID + " 兴趣主题个数：" + topic_Num + " 兴趣主题集合：" + userTopics);
		}
	}
}
