package com.lang.fridainject.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {
    static void extractAsset(Context context, String assetName, File dest) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream in = assetManager.open(assetName);
        OutputStream out = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();
    }

    static void writeToFile(File dest, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    new FileOutputStream(dest));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromFile(File src) {
        try {
            InputStream inputStream = new FileInputStream(src);
            return readFromFile(new FileInputStream(src));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readFromFile(InputStream is) throws IOException {
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String tContents = new String(buffer);
        return tContents;

//        InputStreamReader inputStreamReader = new InputStreamReader(is);
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//        String content;
//        StringBuilder stringBuilder = new StringBuilder();
//        while ((content = bufferedReader.readLine()) != null) {
//            stringBuilder.append(content);
//        }
//        is.close();
//        return stringBuilder.toString();
    }

    public static String sendReuqst(String url) {
        System.out.println(url);
        String body = "";
        try {
            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
            Request request = new Request.Builder()
                    .url(url)//请求接口。如果需要传参拼接到接口后面。
                    .build();//创建Request 对象
            Response response = null;
            response = client.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                body = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    public static boolean copyAssetAndWrite(Context context, String fileName){
        try {
            File cacheDir=context.getCacheDir();
            if (!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            File outFile =new File(cacheDir,fileName);
            if (!outFile.exists()){
                boolean res=outFile.createNewFile();
                if (!res){
                    return false;
                }
            }else {
                if (outFile.length()>10){//表示已经写入一次
                    return true;
                }
            }
            InputStream is=context.getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }    return false;
    }
}
