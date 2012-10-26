package com.run.stepway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.run.stepway.MainActivity.SWMainHandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends Activity {

	EditText editTextContent = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        editTextContent = (EditText)findViewById(R.id.editTextContent);
        
        float burn = SWMap.GetInstance().getBurn();
		
		float distance = SWMap.GetInstance().getDistance();

		Time time = SWMap.GetInstance().getRunTime();
		int minute = time.minute+time.hour*60;

		float speed = SWMap.GetInstance().getAverageSpeed();
		
        String content = String.format("我刚使用#步途#完成跑步运动%.2f公里" +
        		",用时%d分钟,平均速度%.2f公里/小时,燃烧%.1f大卡。快来和我一起运动吧！", 
        		distance,minute,speed,burn);
        
        editTextContent.setText(content);
		editTextContent.setSelection(editTextContent.length());
		
		editTextContent.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if(arg1 == KeyEvent.KEYCODE_ENTER){
		    		if(SWWeibo.GetInstance().isAuthorize()){
		    			share();
		    		}
		    		SWWeibo.GetInstance().authorize();
				}
				return false;
			}
			
		});
		
		
        Button buttonShare = (Button)findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	    		if(SWWeibo.GetInstance().isAuthorize()){
	    			share();
	    		}
	    		SWWeibo.GetInstance().authorize();
			}
        	
        });
        
        SWWeibo.GetInstance().setActivity(this);
        SWWeibo.GetInstance().setHandler(new SWShareHandler());
    }

    public void share(){	
    	String content = editTextContent.getText().toString();
    	SWMap.GetInstance().viewTrack();
		Bitmap bmTrack = SWMap.GetInstance().getBitmap();
		try {
			saveBitmap(bmTrack,"/sdcard/swtrack.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	SWWeibo.GetInstance().Share(content, "/sdcard/swtrack.png", "", "");
    }
    
	public void saveBitmap(Bitmap bitmap, String bitName) throws IOException {
		File f = new File(bitName);
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
		fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
		fOut.flush();
		} catch (IOException e) {
		e.printStackTrace();
		}
		try {
		fOut.close();
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_share, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SWWeibo.GetInstance().onActivityResult(requestCode, resultCode, data);
    }
    
    public void weiboAurhorizeSuccess(){	
    	Toast.makeText(this, "验证成功！", Toast.LENGTH_SHORT)
		.show();
    }
    public void weiboAurhorizeError(){	
    	Toast.makeText(this, "验证失败！", Toast.LENGTH_SHORT)
		.show();
    }
    public void weiboShareSuccess(){	
    	Toast.makeText(this, "分享成功！", Toast.LENGTH_SHORT)
		.show();
    }
    public void weiboShareError(){	
    	Toast.makeText(this, "分享失败！", Toast.LENGTH_SHORT)
		.show();
    }
    
    class SWShareHandler extends Handler{

    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		try {
    			switch (msg.what) {		
    			case SWWeibo.AURHORIZE_SUCCESS:
    				weiboAurhorizeSuccess();
    				share();
    				break;
    			case SWWeibo.AURHORIZE_ERROR:
    				weiboAurhorizeError();
    				break;
    			case SWWeibo.SHARE_SUCCESS:
    				weiboShareSuccess();
    				break;
    			case SWWeibo.SHARE_ERROR:
    				weiboShareError();
    				break;
    			default:
    				break;
    			}
    		} catch (Exception ex) {

    		}
    	}
    }
}
