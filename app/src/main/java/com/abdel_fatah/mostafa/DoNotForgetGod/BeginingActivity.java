package com.abdel_fatah.mostafa.DoNotForgetGod;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.abdel_fatah.mostafa.remembergod.R;


public class BeginingActivity extends AppCompatActivity {

    private int time=5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 0);
        setContentView(R.layout.activity_begining);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent page = new Intent();
                page.setClass(getApplicationContext(), MainActivity.class);
                /*
                 to remove activity from stack you can use
                 1- finish();
                 2-startActivity(page);
                */
                startActivity(page);
                finish();
            }
        }, 5000);
    }
}
