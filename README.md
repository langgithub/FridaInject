# FridaInject

## Gerneal
frida-injiect 摆脱usb链接。和Xposed 远程调用功能类似

### 定时脚本修改
修改位置： com.lang.fridainject.AlarmReceiver

### 原理
1. 提供frida脚本（本项目是jingdong_frida_rpc_http.js）
    * 这个脚本里启动里一个http服务，接收sekiro服务端发送过来的请求。
    * 由http服务主动调用加解密并返回给sekiro服务 （整个项目主要是用于服务于sekiro群控）
2. 定时脚本的启动
    * 启动的一个service 获取闹钟服务，启动做任务
    * 任务在com.lang.fridainject.AlarmReceiver
3. 定时任务有哪些(本项目为例)
    * 关闭某东
    * 打开某东
    * 执行赋予权限给frida-injiect
    * frida-injiect 注入命令
    

### 二次开发需要修改位置提醒
1. sekiro 地址
2. 定时脚本的具体内容修改
3. frida 脚本中hook内容
4. sekiro和http 接口参数定义

### 注意
安装上apk不需要手动操作。MainActivity中的操作按钮是用于测试的
    