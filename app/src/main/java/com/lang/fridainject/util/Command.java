package com.lang.fridainject.util;

import android.content.Context;
import android.widget.Toast;

import com.lang.fridainject.MainActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by yuanlang on 2019/3/12.
 */

public class Command {

    /**
     * 执行linux指令
     *
     * @param paramString
     */
    public static void execRootCmd(String paramString) {
        LogUtils.i("execRootCmd: "+paramString);
        MainActivity.sendmsg("execRootCmd: "+paramString);
        try
        {
            final Process process = Runtime.getRuntime().exec("su");
            //处理InputStream的线程
            new Thread()
            {
                @Override
                public void run()
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;

                    try
                    {
                        while((line = in.readLine()) != null)
                        {
                            System.out.println("output: " + line);
                            MainActivity.sendmsg("output: " + line);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            new Thread()
            {
                @Override
                public void run()
                {
                    BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line = null;

                    try
                    {
                        while((line = err.readLine()) != null)
                        {
                            System.out.println("err: " + line);
                            MainActivity.sendmsg("err: " + line);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            err.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            Object localObject = process.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
            String command = paramString + "\n";
            localDataOutputStream.writeBytes(command);
            localDataOutputStream.flush();

            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            process.waitFor();
            System.out.println("finish run cmd= " + paramString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

//        try {
//            Process localProcess = Runtime.getRuntime().exec("su");
//            Object localObject = localProcess.getOutputStream();
//            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
//            String command = paramString + "\n";
//            localDataOutputStream.writeBytes(command);
//            localDataOutputStream.flush();
//
//            localDataOutputStream.writeBytes("exit\n");
//            localDataOutputStream.flush();
//
//            localProcess.waitFor();
//            localObject = localProcess.exitValue();
//        } catch (Exception localException) {
//            localException.printStackTrace();
//        }
    }


    public static void execRootCmdNotExit(String paramString) {
        LogUtils.i("execRootCmd: "+paramString);
        MainActivity.sendmsg("execRootCmd: "+paramString);
        try
        {
            final Process process = Runtime.getRuntime().exec("su");
            //处理InputStream的线程
            new Thread()
            {
                @Override
                public void run()
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;

                    try
                    {
                        while((line = in.readLine()) != null)
                        {
                            System.out.println("output: " + line);
                            MainActivity.sendmsg("output: " + line);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            new Thread()
            {
                @Override
                public void run()
                {
                    BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line = null;

                    try
                    {
                        while((line = err.readLine()) != null)
                        {
                            System.out.println("err: " + line);
                            MainActivity.sendmsg("err: " + line);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            err.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            Object localObject = process.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
            String command = paramString + "\n";
            localDataOutputStream.writeBytes(command);
            localDataOutputStream.flush();
            process.waitFor();
            System.out.println("finish run cmd= " + paramString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
