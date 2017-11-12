package com.example.processcommunication;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by HeTingwei on 2017/11/12.
 * 这个是服务端代码
 * 说明：
 * 使用Messenger，是使用客户端服务器间通讯实现进程间通讯
 * 在androidManifest中将service开在了新的进程中,所以log也在另一个进程里
 */

public class MessengerService extends Service {

    private static final String TAG = "MessengerService";
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyContents.MSG_FROM_CLIENT:
                    //下面一句接受来自客户端的消息
                    Log.d(TAG, "handleMessage: receive message from client:\n"+msg.getData().getString("msg"));

                    //下面代码是向客户端发送消息
                    Messenger client = msg.replyTo;
                    Message replyMessage = Message.obtain(null, MyContents.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply","Ok,I have received your message.");
                    replyMessage.setData(bundle);
                    try {
                        client.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
