package com.run.stepway;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        

       
       new Handler().postDelayed(new Runnable(){

           @Override
           public void run() {
               Intent intent = new Intent(SplashActivity.this,MainActivity.class);
               startActivity(intent);
               SplashActivity.this.finish();
           }
                       
       }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_splash, menu);
        return true;
    }
}
