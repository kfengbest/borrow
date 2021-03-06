package com.intalker.borrow.util;

import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.intalker.borrow.cloud.CloudAPI;
import com.intalker.borrow.data.AppData;
import com.intalker.borrow.data.BookInfo;
import com.intalker.borrow.data.UserInfo;

public class JSONUtil {
	public static JSONObject makeBookInfoListUploadData() {
		JSONObject jsonData = new JSONObject();
		try {
			JSONArray jsonBookInfoList = new JSONArray();
			ArrayList<BookInfo> bookInfoList = AppData.getInstance().getBooks();
			for (BookInfo bookInfo : bookInfoList) {
				JSONObject jsonBookInfo = new JSONObject();
				jsonBookInfo.put(CloudAPI.DB_Book_ISBN, bookInfo.getISBN());

				// Hardcode now, will improve later
				jsonBookInfo.put(CloudAPI.DB_Book_Quantity, "1");
				jsonBookInfo.put(CloudAPI.DB_Book_PublicLevel, "all");
				jsonBookInfo.put(CloudAPI.DB_Book_Status, "available");
				// This is not book's official description, it is from owner
				jsonBookInfo.put(CloudAPI.DB_Book_Description, "");
				
				jsonBookInfoList.put(jsonBookInfo);
			}
			jsonData.put(CloudAPI.API_POST_BookInfoList, jsonBookInfoList);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonData;
	}

	public static void parseBooksInfo(String strJSON) {
		try {
			JSONArray jsonBookArray = new JSONArray(strJSON);
			int length = jsonBookArray.length();
			AppData appData = AppData.getInstance();
			ArrayList<BookInfo> curBooks = appData.getBooks();
			for (int i = 0; i < length; ++i) {
				JSONObject jsonBookItem = (JSONObject) jsonBookArray.get(i);
				if (null != jsonBookItem
						&& jsonBookItem.has(CloudAPI.DB_Book_ISBN)) {
					String isbn = jsonBookItem.getString(CloudAPI.DB_Book_ISBN);
					if (null != isbn && !appData.containsBook(isbn)) {
						BookInfo bookInfo = new BookInfo(isbn);
						bookInfo.setInitialized(false);
						curBooks.add(bookInfo);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
