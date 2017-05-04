package com.csuft.ppx.application;

import android.app.Application;
import android.content.Context;

import com.csuft.ppx.MainActivity;

/**
 * Created by zf on 2017/5/3.
 * 全局数据
 */

public class ApplicationData extends Application {
    public static Context globalContext;
    public static Context mainActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = this;
    }

    public static void setMainActivity(Context context) {
        mainActivity = context;
    }
}
