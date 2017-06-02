package com.test.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

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

    private Map<String, String> mMap = new HashMap<String, String>();
    {
        mMap.put("wifi.interface", "wlan0");// wifi=wlan0 有线=eth0 wifi则继续看
                                            // wlan.driver.status(wifi状态)
        mMap.put("qemu.sf.fake_camera", null); // 真机没有模拟器有。
        mMap.put("ro.product.device", "generic"); // 模拟器可能为android
        mMap.put("ro.serialno", null); // 区别shell/getprop/SystemProperties.get
        mMap.put("ro.boot.serialno", null); // 区别shell/getprop/SystemProperties.get
        mMap.put("ro.product.cpu.abi", "x86"); // 一般模拟器都是x86，防止误伤建议和ro.product.cpu.abi2对比
        mMap.put("ro.product.brand", "Android"); // 肯定不能是android
        mMap.put("ro.product.board", "Android"); // 肯定不能是android
        mMap.put("ro.product.manufacturer", "unknow"); // 厂商不能为unknow
                                                       // 并且和产品应该符合一致
        mMap.put("ro.build.tags", "test-keys"); // test-keys是模拟器 .
                                                // 正式发版则是release-keys
        mMap.put("ro.build.fingerprint", "test-keys"); // 包含test-keys为模拟器.userdebug为是有root权限的机器
        mMap.put("ro.build.display.id", "test-keys"); // 包含test-keys为模拟器
        mMap.put("ro.build.description", "test-keys"); // 包含test-keys为模拟器
        /**
         * 有这个说明是有线的 dhcp.eth1.dns2 dhcp.eth1.dns3
         * dhcp.eth1.dns4有值都是说明是有线设备，如果wlan0连接，则说明为假设备
         */
        mMap.put("dhcp.eth1.dns1", "");
    }
    private static String[] mCheckList = new String[] { "wifi.interface", "wlan.driver.status", "qemu.sf.fake_camera",
            "ro.product.device", "ro.product.device", "ro.serialno", "ro.boot.serialno", "ro.product.cpu.abi",
            "ro.product.cpu.abi2", "ro.product.brand", "ro.product.board", "ro.product.manufacturer", "ro.build.tags",
            "ro.build.fingerprint", "ro.build.display.id", "ro.build.description" };

    public static String hasQEmuProps(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t").append(mCheckList[0]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[0])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[0])).append("\n")

                .append("\t").append(mCheckList[1]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[1])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[1])).append("\n")

                .append("\t").append(mCheckList[2]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[2])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[2])).append("\n")

                .append("\t").append(mCheckList[3]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[3])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[3])).append("\n")

                .append("\t").append(mCheckList[4]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[4])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[4])).append("\n")

                .append("\t").append(mCheckList[5]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[5])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[5])).append("\n")

                .append("\t").append(mCheckList[6]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[6])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[6])).append("\n")

                .append("\t").append(mCheckList[7]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[7])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[7])).append("\n")

                .append("\t").append(mCheckList[8]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[8])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[8])).append("\n")

                .append("\t").append(mCheckList[9]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[9])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[9])).append("\n")

                .append("\t").append(mCheckList[10]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[10])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[10])).append("\n")

                .append("\t").append(mCheckList[11]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[11])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[11])).append("\n")

                .append("\t").append(mCheckList[12]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[12])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[12])).append("\n")

                .append("\t").append(mCheckList[13]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[13])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[13])).append("\n")

                .append("\t").append(mCheckList[14]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[14])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[14])).append("\n")

                .append("\t").append(mCheckList[15]).append(": ").append("\n").append("\t\tFile==>")
                .append(Utils.getBuildProp(mCheckList[15])).append("\n").append("\t\tSystemProperties==>")
                .append(Utils.getProp(context, mCheckList[15])).append("\n")

        ;
        return sb.toString();
    }

}
