package com.whu.File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.whu.Model.BookmarkTagsModel;
import com.whu.Model.BookmarksModel;
import com.whu.Model.TagsModel;
import com.whu.Model.UserTaggedBookmarkModel;
import com.whu.Model.UsersModel;
import com.whu.Model.UsersSubscribModel;

public class FileManager {
	
	private volatile static FileManager sharedManager = null;
	
	private FileManager() {
		
	}
	
	public static FileManager getManager() {
		if (sharedManager == null) {
			synchronized (FileManager.class) {
				if (sharedManager == null) {
					sharedManager = new FileManager();
				}
			}
		}
		return sharedManager;
	}
	
	/*
	 * 提取用户订阅关系，保存到数据库
	 */
	public void extractUsersSubscrib() {
		System.out.println("提取用户订阅关系");
		
		File file = new File(FileConfig.ORIGINALPATH + "user_contacts.dat");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String rowString = null, from_ID = null, to_ID = null;
			int line = 1;
			String[] row = null;
			rowString = reader.readLine();		// 去掉首行
			
			while ((rowString = reader.readLine()) != null) {
				row = rowString.split("	");
				from_ID = row[0];
				to_ID = row[1];
				
				UsersSubscribModel.getModel().saveUsersSubscrib(from_ID, to_ID);
				
				++line;
			}
			System.out.println("用户订阅关系数：" + line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 提取所有用户
	 */
	public void extractUsers() {
		System.out.println("提取用户");
		
		File file = new File(FileConfig.ORIGINALPATH + "user_contacts.dat");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String rowString = null, from_ID = null, to_ID = null;
			String[] row = null;
			rowString = reader.readLine();		// 去掉首行
			
			while ((rowString = reader.readLine()) != null) {
				row = rowString.split("	");
				from_ID = row[0];
				to_ID = row[1];
				
				UsersModel.getModel().saveUsers(from_ID);
				UsersModel.getModel().saveUsers(to_ID);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 提取书签
	 */
	public void extractBookmarks() {
		System.out.println("提取书签");
		
		File file = new File(FileConfig.ORIGINALPATH + "bookmarks.dat");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String rowString = null, id = null, title = null, url = null, urlPrincipal = null;
			int line = 1;
			String[] row = null;
			rowString = reader.readLine();		// 去掉首行
			
			while ((rowString = reader.readLine()) != null) {
//				System.out.println(rowString);
				row = rowString.split("	");
				id = row[0];
				title = row[2];
				url = row[3];
				urlPrincipal = row[5];
//				System.out.println(id + " : " + title + " : " + url + " : " + urlPrincipal );
				
				BookmarksModel.getModel().saveBookmarks(id, title, url, urlPrincipal);
				
				++line;
			}
			System.out.println("书签个数：" + line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 提取标签
	 */
	public void extractTags() {
		System.out.println("提取书签");
		
		File file = new File(FileConfig.ORIGINALPATH + "tags.dat");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String rowString = null, id = null, value = null;
			int line = 1;
			String[] row = null;
			rowString = reader.readLine();		// 去掉首行
			
			while ((rowString = reader.readLine()) != null) {
//				System.out.println(rowString);
				row = rowString.split("	");
				id = row[0];
				value = row[1];
				TagsModel.getModel().saveTags(id, value);
				
				++line;
			}
			System.out.println("标签个数：" + line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 提取书签的标签标记，及标记次数
	 */
	public void extractBookmarkTags() {
		System.out.println("提取书签的标签标记，即标记次数");
		
		File file = new File(FileConfig.ORIGINALPATH + "bookmark_tags.dat");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String rowString = null, bm_id = null, tag_id = null, tag_w = null;
			int line = 1;
			String[] row = null;
			rowString = reader.readLine();		// 去掉首行
			
			while ((rowString = reader.readLine()) != null) {
				row = rowString.split("	");
				bm_id = row[0];
				tag_id = row[1];
				tag_w = row[2];
//				System.out.println("书签标记 " + bm_id + " " + tag_id + " " + tag_w);
				
				BookmarkTagsModel.getModel().saveBookmarkTags(bm_id, tag_id, tag_w);
				++line;
			}
			System.out.println("标签个数：" + line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 提取用户采用标签标记书签，保存到数据库
	 */
	public void extractUserTaggedBookmarks() {
		System.out.println("提取用户采用标签标记书签");
		
		File file = new File(FileConfig.ORIGINALPATH + "user_taggedbookmarks.dat");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String rowString = null, user_ID = null, bm_ID = null, tag_ID = null;
			int line = 1;
			String[] row = null;
			rowString = reader.readLine();		// 去掉首行
			
			while ((rowString = reader.readLine()) != null) {
//				System.out.println(rowString);
				row = rowString.split("	");
				user_ID = row[0];
				bm_ID = row[1];
				tag_ID = row[2];
				
//				System.out.println("用户标签标记书签： " + user_ID + " " + bm_ID + " " + tag_ID);
				UserTaggedBookmarkModel.getModel().saveUserTaggedBookmarks(user_ID, bm_ID, tag_ID);
				
				++line;
			}
			System.out.println("标签个数：" + line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
