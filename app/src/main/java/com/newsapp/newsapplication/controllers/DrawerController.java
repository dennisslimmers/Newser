package com.newsapp.newsapplication.controllers;

import android.util.Log;
import com.newsapp.newsapplication.logging.Logger;

public class DrawerController implements Logger {
    public static void onDrawerItemClick(int position) {

    }

    @Override
    public void dump(String message) {
        final String TAG = Thread.currentThread()
                .getStackTrace()[2]
                .getClassName();

        Log.e(TAG, message);
    }
}
