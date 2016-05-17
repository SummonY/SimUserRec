package com.whu.LDA;

public class LDAParameters {
	public static Double alpha = 0.5;		// alpha参数, 选择为 50/K, 其中K是topic数
	
	public static Double beta = 0.01;		// beta参数
	
	public static int topicNum = 100;		// 主题数目
	
	public static int wordNumOfTopic = 20;	// 主题中单词按概率排序后，从每个主题中选择的单词的个数
	
	public static int iterationNum = 501;	// 迭代次数
	
	public static int beginSaveIter = 200;	// 开始保存
	
	public static int saveIterStep = 100;	// 每隔100步保存一次
	
	public static int recommendNum = 5;		// 推荐用户个数
}
