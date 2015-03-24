package com.example.simplegpstracker;

import java.io.File;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

public class RequestQueueFactory {
	
	 // Default maximum disk usage in bytes
    private static final int DEFAULT_DISK_USAGE_BYTES = 25 * 1024 * 1024;

    // Default cache folder name
    private static final String DEFAULT_CACHE_DIR = "examples";
    private static final String DIR_REQUESTS = "requests";
	
	 public RequestQueueFactory() {
	 }
	 
	 public static RequestQueue createSingleRequestQueue(Context context, CookieStore cookieStore) {
	        File cacheDir = getRequestsCacheDir(context);

	        Network network = new BasicNetwork(createStack(context, cookieStore));

	        int threadPoolSize = 1; // number of network dispatcher threads to create
	        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, threadPoolSize);
	        queue.start();

	        return queue;
	    }
		
		private static HttpStack createDefaultStack(Context context) {
	        if (Build.VERSION.SDK_INT >= 9) {
	            return new HurlStack();
	        } else {
	            String userAgent = "volley/0";
	            try {
	                String packageName = context.getPackageName();
	                PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
	                userAgent = packageName + "/" + info.versionCode;
	            } catch (PackageManager.NameNotFoundException e) {
	            }

	            // http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	            return new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
	        }
	    }

		
		 private static HttpStack createCookieStack(CookieStore cookieStore) {
		        DefaultHttpClient httpClient = new DefaultHttpClient();
		        httpClient.setCookieStore(cookieStore);
		        return new HttpClientStack( httpClient );
		    }
		
		private static HttpStack createStack(Context context, CookieStore cookieStore) {
	        return cookieStore != null ? createCookieStack(cookieStore) : createDefaultStack(context);
	    }

	    private static File getRequestsCacheDir(Context context) {
	        File cacheDir = new File(getRootCacheDir(context), DIR_REQUESTS);
	        //noinspection ResultOfMethodCallIgnored
	        cacheDir.mkdirs();
	        return cacheDir;
	    }

	    private static File getRootCacheDir(Context context) {
	        File rootCache = context.getExternalCacheDir();
	        if (rootCache == null) {
	            rootCache = context.getCacheDir();
	        }

	        File cacheDir = new File(rootCache, DEFAULT_CACHE_DIR);
	        //noinspection ResultOfMethodCallIgnored
	        cacheDir.mkdirs();
	        return cacheDir;
	    }

}
