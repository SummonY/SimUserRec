package com.whu.LDA;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.whu.File.FileUtil;
import com.whu.File.NoiseWords;

public class Documents {
	ArrayList<Document> docs; 				// 文档集合, {0 ~ M-1}
	Map<String, Integer> termToIndexMap;	// 单词到索引，<单词，索引>
	ArrayList<String> indexToTermList;		// 词索引，<单词>
	Map<String,Integer> termCountMap;		// 词出现次数，<单词，计数>
	
	public Documents() {
		docs = new ArrayList<Document>();
		termToIndexMap = new HashMap<String, Integer>();
		indexToTermList = new ArrayList<String>();
		termCountMap = new HashMap<String, Integer>();
	}
	
	
	// 读取文档结合
	public void readDocs(String docsPath) {
		for(File doc_File : new File(docsPath).listFiles()) { 
			Document doc = new Document(doc_File.getAbsolutePath(), termToIndexMap, indexToTermList, termCountMap);
			docs.add(doc);
		}
	}
	
	// 定义文档
	public static class Document {
		private String doc_Name;			// 文档名字
		Integer[] doc_Words;				// 文档中单词向量
		
		public Document(String doc_Name, Map<String, Integer> termToIndexMap, ArrayList<String> indexToTermList, Map<String, Integer> termCountMap) {
			doc_Name = doc_Name;
			
			// 读取文件，并初始化单词索引
			ArrayList<String> doc_Lines = new ArrayList<String>();
			ArrayList<String> words = new ArrayList<String>();
			
			FileUtil.readLines(doc_Name, doc_Lines);		// 按行读取文件
			for (String line : doc_Lines) {
				FileUtil.tokenizeAndLowerCase(line, words);
			}
			
			// 删除噪音数据
			for (int w = 0; w < words.size(); ++w) {
				if (NoiseWords.isNoiseWord(words.get(w))) {
					words.remove(w);
					--w;
				}
			}
			
			// 为单词建立索引
			this.doc_Words = new Integer[words.size()];
			for (int w = 0; w < words.size(); ++w) {
				String word = words.get(w);
				if (!termToIndexMap.containsKey(word)) {			// 不存在该单词
					int newIndex = termToIndexMap.size();	// 当前单词索引位置
					termToIndexMap.put(word, newIndex);		// 保存单词索引位置
					indexToTermList.add(word);				// 添加单词到词索引列表
					termCountMap.put(word, new Integer(1)); // 单词第一次出现
					doc_Words[w] = newIndex;				// 记录第w个单词，所在的索引
				} else {											// 包含该单词
					doc_Words[w] = termToIndexMap.get(word);				// 初始化文档单词向量
					termCountMap.put(word, termCountMap.get(word) + 1);		// 单词计数
				}
			}
		}
	}
	
	
	
}
