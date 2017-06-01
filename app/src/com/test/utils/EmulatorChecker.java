package com.test.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.util.Log;

/**
 * @Copyright © 2017 sanbo Inc. All rights reserved.
 * @Description: 模拟器识别技术
 * @Version: 1.0
 * @Create: 2017-5-31 下午8:03:08
 * @Author: sanbo
 */
public class EmulatorChecker {

    private static final String T = "sanbo";
    // 英特尔
    private static final String mIntelCPU = "Intel(R)";
    // 赛扬
    private static final String mCeleronCPU = "Celeron(R)";
    // arm(没有arm的电脑未验证)
    private static final String mARMCPU = "ARM";
    private static final String mVirvualCPU = "Virtual";
    private static final String mHardware = "Hardware";

    /**
     * <pre>
     * 通过cpuinfo检查是否为模拟器
     * 1.模拟器一般没有Hardware这行,Hardware行代表芯片厂商一行,即使有也需要效验是否为Goldfish/SDK/android/Google SDK
     * 2.模拟器的CPU一般包含宿主机器的CPU型号.Intel(R),也有包含Virtual的
     * </pre>
     * 
     * @return true模拟器 false非模拟器
     */
    public static String checkEmulatorByCpuInfo() {

        StringBuilder sb = new StringBuilder();

        File file = new File("/proc/cpuinfo");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        String res = sb.toString();

        // Log.d(T, res);
        sb = new StringBuilder();
        sb.append("下面几个是重要模拟器判断的关键字段").append("\n");
        sb.append("\t").append(mIntelCPU).append(": ");
        if (res.toLowerCase().contains(mIntelCPU.toLowerCase())) {
            sb.append("包含").append("\n");
        } else {
            sb.append("未包含").append("\n");
        }
        sb.append("\t").append(mCeleronCPU).append(": ");
        if (res.toLowerCase().contains(mCeleronCPU.toLowerCase())) {
            sb.append("包含").append("\n");
        } else {
            sb.append("未包含").append("\n");
        }
        sb.append("\t").append(mVirvualCPU).append(": ");
        if (res.toLowerCase().contains(mVirvualCPU.toLowerCase())) {
            sb.append("包含").append("\n");
        } else {
            sb.append("未包含").append("\n");
        }

        /**
         * 如果有Hareware这行,一般就认为是真的，但是需要判断这行没有关键字
         */
        if (res.toLowerCase().contains(mHardware.toLowerCase())) {
            String[] s = res.split("\n");

            String line = null;
            for (int i = 0; i < s.length; i++) {
                line = s[i];
                if (line != null) {
                    if (line.contains(mHardware)) {
                        String[] ll = line.split(":");
                        // Goldfish/SDK/android/Google SDK
                        if (ll[1].contains("Goldfish") || ll[1].contains("SDK") || ll[1].contains("android")
                                || ll[1].contains("Google SDK")) {
                            sb.append("包含Hareware,疑似模拟器:").append(ll[1].trim()).append("\n");
                        } else {
                            sb.append("包含Hareware:").append(ll[1].trim()).append("\n");
                        }
                    }
                }
            }
        }
        sb.append("\n\n").append("=======完整CPUInfo=======").append("\n").append(res);
        return sb.toString();
    }
}
