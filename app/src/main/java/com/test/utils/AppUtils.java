package com.test.utils;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Copyright © 2017 Umeng Inc. All rights reserved.
 * Description: TODO
 * Version: 1.0
 * Create: 17/2/14 11:14
 * Author: sanbo
 */
public class AppUtils {

    public static String getNowActivity(Application app) {
        Field f;
        Object thread = null;
        Instrumentation base;
        String result = "";
        // Replace instrumentation
        try {
            thread = getActivityThread(app);
            f = thread.getClass().getDeclaredField("mInstrumentation");
            f.setAccessible(true);
            base = (Instrumentation) f.get(thread);
            if (base != null) {
                //get instrumentation activity list
                List<Instrumentation.ActivityMonitor> mActivityMonitors;
                f = Instrumentation.class.getDeclaredField("mActivityMonitors");
                f.setAccessible(true);
                mActivityMonitors = (List<Instrumentation.ActivityMonitor>) f.get(base);
                for (int i = 0; i < mActivityMonitors.size(); i++) {
                    Instrumentation.ActivityMonitor activityMonitor = mActivityMonitors.get(i);
                    //获取Activity名称
                    result = activityMonitor.getLastActivity().getClass().getName();
                    Log.d("sanbo", "AppUtils getNowActivity " + result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Object getActivityThread (Context context) {
        try {
            Class activityThread = Class.forName ("android.app.ActivityThread");
            // ActivityThread.currentActivityThread()
            Method m = activityThread.getMethod ("currentActivityThread", new Class[0]);
            m.setAccessible (true);
            Object thread = m.invoke (null, new Object[0]);
            if(thread != null) return thread;

            // context.@mLoadedApk.@mActivityThread
            Field mLoadedApk = context.getClass ().getField ("mLoadedApk");
            mLoadedApk.setAccessible (true);
            Object apk = mLoadedApk.get (context);
            Field mActivityThreadField = apk.getClass ().getDeclaredField ("mActivityThread");
            mActivityThreadField.setAccessible (true);
            return mActivityThreadField.get (apk);
        } catch (Throwable ignore) {
            throw new RuntimeException ("Failed to get mActivityThread from context: " + context);
        }
    }
}
