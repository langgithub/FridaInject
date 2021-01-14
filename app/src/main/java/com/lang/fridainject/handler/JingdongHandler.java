package com.lang.fridainject.handler;

import com.lang.fridainject.util.Utils;
import com.lang.sekiro.api.SekiroRequest;
import com.lang.sekiro.api.SekiroRequestHandler;
import com.lang.sekiro.api.SekiroResponse;

import java.net.URLEncoder;


/**
 * http://XXX:11001/asyncInvoke?group=fridainject&action=jingdong&method=encode&phone=17621972154%2C15775691981
 */
public class JingdongHandler  implements SekiroRequestHandler {

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        // 全局锁，使程序同一时间只有一个任务
        //解析参数
        // http://172.20.22.137:8888/jingdong?action=encode&phone=17621972154%2C15775691981
        String method = sekiroRequest.getString("method");
        String url = "";
        String body = "";
        switch (method){
            case "encode":
                // http://XXX:11001/asyncInvoke?group=fridainject&action=jingdong&method=encode&phone=17621972154%2C15775691981
                String phone = sekiroRequest.getString("phone");
                url = "http://127.0.0.1:8888/jingdong?action=encode&phone="+ URLEncoder.encode(phone);
                break;
            case "decode":
                // http://XXX:11001/asyncInvoke?group=fridainject&action=jingdong&method=decode&data=qDFk%2bVD%2f7u6oOL1V%2fXpPIPMYwRTVtytQ&enc=2
                String data = sekiroRequest.getString("data");
                String enc = sekiroRequest.getString("enc");
                url = "http://127.0.0.1:8888/jingdong?action=decode&data="+ URLEncoder.encode(data)+"&enc="+enc;
                break;
            case "getSign":
                // http://XXX:11001/asyncInvoke?group=fridainject&action=jingdong&method=getSign&functionId=fetchFriendsByMobileNums&body=%7B%22enc%22%3A2%2C%22phoneNums%22%3A%22qDFk%2BVD%2F7u6oOL1V%2FXpPIPMYwRTVtytQ%22%2C%22plugin_version%22%3A90300%7D&uuid=867686021141286-009acdb84517&client=android&clientVersion=9.3.0
                String functionId = sekiroRequest.getString("functionId");
                String _body = sekiroRequest.getString("body");
                String uuid = sekiroRequest.getString("uuid");
                String client = sekiroRequest.getString("client");
                String clientVersion = sekiroRequest.getString("clientVersion");
                url = "http://127.0.0.1:8888/jingdong?action=getSign&functionId="+functionId+"&body="+URLEncoder.encode(_body)+"&uuid="+uuid+"&client="+client+"&clientVersion="+clientVersion;
                break;
        }
        body = Utils.sendReuqst(url);
        sekiroResponse.success(body);
    }

}