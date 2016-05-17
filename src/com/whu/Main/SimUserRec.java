package com.whu.Main;

import java.io.IOException;

import com.whu.UserRec.UserRec;

public class SimUserRec {

	public static void main(String[] args) throws IOException {
		// 提取用户订阅关系，保存到数据库
//		FileManager.getManager().extractUsersSubscrib();
		
		// 提取所有用户
//		FileManager.getManager().extractUsers();
		
		// 提取书签，保存到数据库
//		FileManager.getManager().extractBookmarks();
		
		// 提取标签，保存到数据库
//		FileManager.getManager().extractTags();
		
		// 提取书签对应标签和标记次数，保存到数据库
//		FileManager.getManager().extractBookmarkTags();
		
		// 提取用户用标签标记书签，保存到数据库
//		FileManager.getManager().extractUserTaggedBookmarks();
		
		// 提取书签title，保存成文件，用于LDA文档主题生成
		
		
		// 初始化文档集合
//		String docsPath = FileConfig.LDADOCS;
//		Documents doc_Set = new Documents();
//		doc_Set.readDocs(docsPath);
		
		// LDA模型参数初始化
//		LDAModel ldaModel = new LDAModel();
		
		// 根据文档集合，初始化LDA模型
//		ldaModel.initLDAModel(doc_Set);
		
		// 迭代，采用Gibbs采样算法更新主题标记
//		ldaModel.gibbsSampleLDAModel(doc_Set);
		
		// 获取每个词对应的主题
//		ldaModel.fetchTopicOfWord(doc_Set);
		
		// 获取用户兴趣爱好主题
//		ldaModel.extractUsersTopics();
		
		// 用户推荐
		UserRec userRec = new UserRec();
		
		// 基于用户兴趣主题度量用户之间的相似度
//		userRec.mesureUsersSimilarity();
		
		// 结合用户好友的好友，重新计算用户相似度，初步采用
//		userRec.masureFriBasedUsersSimilarity(0.2);
		
		// 最终采用，用户相似度度量
//		userRec.masureFriBasedUsersSimilarity2(0.3, 0.4);
		
		// 基于用户相似度，提取top-k相似用户
		for (int k = 5; k < 13; ++k) {
			System.out.println("top-" + k + "情况下精准率和召回率：");
			userRec.extractTopKSimUsers(k);
		}
		
	}

}