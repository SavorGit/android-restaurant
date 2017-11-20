package com.savor.resturant;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

import com.common.api.utils.AppUtils;
import com.common.api.utils.FileUtils;
import com.savor.resturant.core.Session;
import com.savor.resturant.service.LocalJettyService;
import com.savor.resturant.utils.ActivitiesManager;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by zhanghq on 2016/12/19.
 */

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Application mContext;
    private final Thread.UncaughtExceptionHandler mDefaultExceptionHandler;

    public ExceptionHandler(Application context) {
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        mContext = context;

    }
    /**
     * 启动jetty服务service
     */
    private void startJettyServer(Context context) {
        Intent intent = new Intent(context, LocalJettyService.class);
        context.startService(intent);
    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
//        writeBugLogToFile(e);
        // 友盟保存统计信息
        MobclickAgent.onKillProcess(mContext);


        showCrashTips();
        startJettyServer(mContext);

        // 退出并重启应用
        exitAndRestart();
    }

    private void writeBugLogToFile(Throwable e) {
        String logPath = AppUtils.getSDCardPath()+"savor"+File.separator+"log";
        File fileDir = new File(logPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        String path = logPath + File.separator + "bug.text";
        File logFile = new File(path);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        String message = e.getMessage();
        String localizedMessage = e.getLocalizedMessage();
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        sb.append(localizedMessage);
        String logLine = sb.toString();

        try {
            FileWriter fileWriter = new FileWriter(path, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(logLine);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showCrashTips() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "亲 ，程序出了点小问题即将重启哦", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();

        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }


    private void exitAndRestart() {
        ActivitiesManager.getInstance().popAllActivities();
        Process.killProcess(Process.myPid());
    }



}
