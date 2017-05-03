package com.csuft.ppx.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by zf on 2017/5/3.
 * 全局数据
 */

public class ApplicationData extends Application {
    public static Context globalContext;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = this;
    }
}
