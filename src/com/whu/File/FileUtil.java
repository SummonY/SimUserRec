package com.whu.File;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FileUtil {
	
	/*
	 * 按行写入文件
	 */
	public static void writeLines(String fileName, ArrayList<String> parameters) {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(new File(fileName)));
			for (int i = 0; i < parameters.size(); i++) {
				writer.write(parameters.get(i) + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/*
	 *  按行读取文件
	 */
	public static void readLines(String fileName, ArrayList<String> fileLines) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(new File(fileName)));

			String line = null;
			while ((line = reader.readLine()) != null) {
				fileLines.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * 根据自定义分界符进行拆分字符串为某个单词向量
	 */
	public static void tokenizeAndLowerCase(String line, ArrayList<String> tokens) {
//		StringTokenizer tokenizer = new StringTokenizer(line);
		StringTokenizer tokenizer = new StringTokenizer(line, " \t\n\r\f‘“”,、?.:：;(){}【】[]|/／!@&#0123456789『』「」", false);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			tokens.add(token.toLowerCase().trim());
		}
	}
}
