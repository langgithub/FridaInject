package com.lang.fridainject.util;

import android.util.Log;

public class LogUtils {

        private LogUtils() {

        }

        public static boolean isEnable = true;
        private static String TAG = "Frida-inject";
        /*
         *自定义
         */
        public static void  changeLog(String tag, boolean isEnable) {
            TAG = tag;
            isEnable = isEnable;
        }

        /*
         * 打印information日志
         */
        public static void i(String msg) {
            if (isEnable)
                Log.i(TAG, msg);
        }

        public static void i(String tag, String msg) {
            if (isEnable)
                Log.i(tag, msg);
        }

        /*
         * 打印verbose日志
         */
        public static void v(String msg) {
            if (isEnable)
                Log.v(TAG, msg);
        }

        public static void v(String tag, String msg) {
            if (isEnable)
                Log.v(tag, msg);
        }


        /*
         * 打印debug信息
         */
        public static void d(String msg) {
            if (isEnable)
                Log.d(TAG, msg);
        }

        public static void d(String tag, String msg) {
            if (isEnable)
                Log.d(tag, msg);
        }

        /*
         * 打印warn日志
         */
        public static void w(String msg) {
            if (isEnable)
                Log.w(TAG, msg);
        }

        public static void w(String tag, String msg) {
            if (isEnable)
                Log.w(tag, msg);
        }

        /*
         * 打印error日志
         */
        public static void e(String msg) {
            if (isEnable)
                Log.e(TAG, msg);
        }

        public static void e(String tag, String msg) {
            if (isEnable)
                Log.e(tag, msg);
        }



}
