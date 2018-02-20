package com.newsapp.newsapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.newsapp.newsapplication.config.Config;

public class SplashActivity extends Activity {
    public final int SPLASH_DISPLAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Hide title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this.getBaseContext(), R.color.colorPrimary)); // Set background color
        this.setContentView(R.layout.activity_splash);

        this.displayAppName(); // Print the application name

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainActivity);
                SplashActivity.this.finish();
            }
        }, this.SPLASH_DISPLAY_TIME);
    }

    private void displayAppName() {
        TextView t = this.findViewById(R.id.SplashTitle);
        Typeface font = Typeface.createFromAsset(getAssets(), Config.getAppNameFont());
        final String appName = Config.getAppName().toUpperCase();

        t.setText(appName);
        t.setTypeface(font);
    }
}
