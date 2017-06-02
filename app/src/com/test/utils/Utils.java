package com.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

/**
 * @Copyright © 2017 sanbo Inc. All rights reserved.
 * @Description: TODO
 * @Version: 1.0
 * @Create: 2017-6-1 下午7:17:09
 * @Author: sanbo
 */
public class Utils {

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
                // get instrumentation activity list
                List<Instrumentation.ActivityMonitor> mActivityMonitors;
                f = Instrumentation.class.getDeclaredField("mActivityMonitors");
                f.setAccessible(true);
                mActivityMonitors = (List<Instrumentation.ActivityMonitor>) f.get(base);
                for (int i = 0; i < mActivityMonitors.size(); i++) {
                    Instrumentation.ActivityMonitor activityMonitor = mActivityMonitors.get(i);
                    // 获取Activity名称
                    result = activityMonitor.getLastActivity().getClass().getName();
                    Log.d("sanbo", "AppUtils getNowActivity " + result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Object getActivityThread(Context context) {
        try {
            Class activityThread = Class.forName("android.app.ActivityThread");
            // ActivityThread.currentActivityThread()
            Method m = activityThread.getMethod("currentActivityThread", new Class[0]);
            m.setAccessible(true);
            Object thread = m.invoke(null, new Object[0]);
            if (thread != null)
                return thread;

            // context.@mLoadedApk.@mActivityThread
            Field mLoadedApk = context.getClass().getField("mLoadedApk");
            mLoadedApk.setAccessible(true);
            Object apk = mLoadedApk.get(context);
            Field mActivityThreadField = apk.getClass().getDeclaredField("mActivityThread");
            mActivityThreadField.setAccessible(true);
            return mActivityThreadField.get(apk);
        } catch (Throwable ignore) {
            throw new RuntimeException("Failed to get mActivityThread from context: " + context);
        }
    }

    /**
     * 判断当前应用是否具有指定的权限
     * 
     * @param context
     * @param permission
     *            权限信息的完整名称 如：<code>android.permission.INTERNET</code>
     * @return 当前仅当宿主应用含有 参数 permission 对应的权限 返回true 否则返回 false
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Throwable e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Method to reflectively invoke the SystemProperties.get command - which is
     * the equivalent to the adb shell getProp command.
     * 
     * @param context
     *            A {@link Context} object used to get the proper ClassLoader
     *            (just needs to be Application Context object)
     * @param property
     *            A {@code String} object for the property to retrieve.
     * @return {@code String} value of the property requested.
     */
    public static String getProp(Context context, String property) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> systemProperties = classLoader.loadClass("android.os.SystemProperties");

            Method get = systemProperties.getMethod("get", String.class);

            Object[] params = new Object[1];
            params[0] = new String(property);

            return (String) get.invoke(systemProperties, params);
        } catch (Exception iAE) {
            return null;
        }
    }

    /**
     * 通过/system/build.prop获取对应对
     * 
     * @param propertyKey
     * @return
     */
    public static String getBuildProp(String propertyKey) {
        Properties pts = getBuildProp();
        if (pts.size() > 0) {
            return pts.getProperty(propertyKey);
        } else {
            return null;
        }
    }

    private static Properties getBuildProp() {
        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            prop.load(fis);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Throwable e) {
                }
            }
        }
        return prop;
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        // In theory, if the package installer does not throw an exception,
        // package exists
        try {
            packageManager.getInstallerPackageName(packageName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Check if the normal method of "isUserAMonkey" returns a quick win of who
     * the user is.
     * 
     * @return {@code true} if the user is a monkey or {@code false} if not.
     */
    public static boolean isUserAMonkey() {
        return ActivityManager.isUserAMonkey();
    }

    /**
     * 
     * Believe it or not, there are packers that use this...
     */
    public static boolean isBeingDebugged() {
        return Debug.isDebuggerConnected();
    }

}
