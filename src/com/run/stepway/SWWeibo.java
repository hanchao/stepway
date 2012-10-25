package com.run.stepway;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;

public class SWWeibo {

	private static final String PREFERENCES_NAME = "com_weibo_sdk_android";
	Weibo mWeibo = null;
	Oauth2AccessToken accessToken = null;
	MainActivity mMainActivity = null;
	SsoHandler mSsoHandler = null;
	SWHandler mHandler = null;
	public SWWeibo(){
		mWeibo = Weibo.getInstance("1398278549", "http://hanchao0123.diandian.com/");
	}
	
	public void setMainActivity(MainActivity mainActivity){
		mMainActivity = mainActivity;
	}
	
	public void setHandler(SWHandler handler){
		mHandler = handler;
	}
	
	public boolean isAuthorize(){
		accessToken = readAccessToken();
		return accessToken.isSessionValid();
	}
	
	public void authorize(){
		accessToken = readAccessToken();
        if(!accessToken.isSessionValid()){
        	//mWeibo.authorize(mMainActivity, new AuthDialogListener());
            mSsoHandler =new SsoHandler(mMainActivity,mWeibo);
            mSsoHandler.authorize( new AuthDialogListener());
        }
	}
	
	public void keepAccessToken(Oauth2AccessToken token) {
		SharedPreferences pref = mMainActivity.getSharedPreferences(PREFERENCES_NAME, mMainActivity.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("token", token.getToken());
		editor.putLong("expiresTime", token.getExpiresTime());
		editor.commit();
	}
	
	public void clear(){
	    SharedPreferences pref = mMainActivity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}

	public Oauth2AccessToken readAccessToken(){
		Oauth2AccessToken token = new Oauth2AccessToken();
		SharedPreferences pref = mMainActivity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setToken(pref.getString("token", ""));
		token.setExpiresTime(pref.getLong("expiresTime", 0));
		return token;
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent data) {   
        if(mSsoHandler!=null){
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
    
    public void Share(String content, String lat, String lon){
    	StatusesAPI api = new StatusesAPI(accessToken);
    	api.update(content, lat, lon, new ShareRequestListener());
    }
    
    public void Share(String content, String file, String lat, String lon){
    	StatusesAPI api = new StatusesAPI(accessToken);
    	api.upload(content, file, lat, lon, new ShareRequestListener());
    }
    
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			accessToken = new Oauth2AccessToken(token, expires_in);
			if (accessToken.isSessionValid()) {
				keepAccessToken(accessToken);
				Message msg = new Message();
				msg.what = SWHandler.WEIBO_AURHORIZE_SUCCESS;
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Message msg = new Message();
			msg.what = SWHandler.WEIBO_AURHORIZE_ERROR;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onCancel() {
			//Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Message msg = new Message();
			msg.what = SWHandler.WEIBO_AURHORIZE_ERROR;
			mHandler.sendMessage(msg);
		}

	}
	
	class ShareRequestListener implements RequestListener {

		@Override
		public void onComplete(String arg0) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = SWHandler.WEIBO_SHARE_SUCCESS;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onError(WeiboException arg0) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = SWHandler.WEIBO_SHARE_ERROR;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onIOException(IOException arg0) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = SWHandler.WEIBO_SHARE_ERROR;
			mHandler.sendMessage(msg);
		}
	
	}



}
