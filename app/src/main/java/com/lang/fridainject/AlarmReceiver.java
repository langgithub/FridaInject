package com.lang.fridainject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lang.fridainject.handler.JingdongHandler;
import com.lang.fridainject.util.Command;
import com.lang.fridainject.util.Utils;

/**
 * 唯一需要修改的脚本的地方
 */
public class AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// do something
		// frida-inject 挂掉了，需要重写启动相关程序
		new Thread(new Runnable() {
			@Override
			public void run() {
				String body = Utils.sendReuqst("http://127.0.0.1:8888/heart");
				if(!"error 500".equals(body)) {
					// 脚本
					Command.execRootCmd("am force-stop com.jingdong.app.mall");
					Command.execRootCmd("am start -n com.jingdong.app.mall/com.jd.lib.jdfriend.view.activity.FriendListActivity");
					String agentJsPath = context.getCacheDir() + "/jingdong_frida_rpc_http.js";
					String fridaInjectPath = context.getCacheDir() + "/frida-inject-12.8.2-android-arm64";
					Command.execRootCmd("chmod +x " + fridaInjectPath);
					Command.execRootCmdNotExit(fridaInjectPath + " -n com.jingdong.app.mall -s " + agentJsPath + " --runtime=v8 -e");
				}
			}
		}).start();
	}

}
