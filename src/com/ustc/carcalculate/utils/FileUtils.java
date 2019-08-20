package com.ustc.carcalculate.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class FileUtils {
	
	private static final String TAG = "FileUtils";
	public static final String CACHE_DIR = "/MultiPlayer/ImageCaches";
	
	public static final String DOC_CACHE_DIR = "/MultiPlayer/DocCaches";
	

	
	public static File getCacheFile(String fileUrl,String saveDir){
		
		File cacheFile = null;
		
		try{
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				File sdCardDir = Environment.getExternalStorageDirectory();
				String fileName = getFileName(fileUrl);
				File dir = new File(sdCardDir.getCanonicalPath() + saveDir);
				if(!dir.exists()){
					dir.mkdirs();
				}
				
				cacheFile = new File(dir,fileName);
				
				Log.i(TAG, "cacheFile exists:" + cacheFile.exists() + ",dir:" + dir + ",file:" + fileName);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, "getCacheFileError:" + e.getMessage());
		}
		
		return cacheFile;
				
	}

	public static String cacheDocument(String docPath){
		try {

	        File cacheFile = getCacheFile(docPath, DOC_CACHE_DIR);
	        
	        if( !cacheFile.exists() ){
				URL url = new URL(docPath);
				 
		        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
		        //设置超时间为3秒  
		        conn.setConnectTimeout(3*1000);  
		        //防止屏蔽程序抓取而返回403错误  
		        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
		        //得到输入流  
		        InputStream inputStream = conn.getInputStream();    
		        
		        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
		        String fileLocalPath = cacheFile.getCanonicalPath();
				Log.i(TAG, "write document file to " + fileLocalPath);
				
				byte[] buf = new byte[2048];
				int len = 0;
				//将网络上的文件存储到本地
				while((len = inputStream.read(buf)) > 0){
					bos.write(buf, 0, len);
				}
				inputStream.close();
				bos.close();
				
				return fileLocalPath;
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		}   
		return null;
	}
	
	private static String getFileName(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(index+1);
	}
}