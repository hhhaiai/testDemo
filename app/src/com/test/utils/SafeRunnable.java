package com.test.utils;

public abstract class SafeRunnable implements Runnable {

    @Override
    public void run () {
        try {
            safeRun ();
        } catch (Throwable th) {
            if(th != null)
                th.printStackTrace ();
        }
    }

    public abstract void safeRun ();
}
