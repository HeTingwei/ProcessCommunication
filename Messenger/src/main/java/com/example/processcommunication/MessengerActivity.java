package com.example.processcommunication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/*说明：本项目：为进程间通讯：使用Messenger ：服务端客户端模式
* 这个是客户端，在service注册时让service在另一个进程中开启
* 在客户端绑定了服务端的service
* */
public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "MessengerActivity";

    private Messenger mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = new Messenger(service);
            Message msg = Message.obtain(null, MyContents.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
//            向服务端发送一句话：Hello this is client!
            data.putString("msg", "Hello this is client!");
            msg.setData(data);

            //下面以句为了接受服务器消息
            msg.replyTo=mGetPeplyMessenger;

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    //下面是为了接受客户端消息的代码

    private  Messenger mGetPeplyMessenger=new Messenger(new MessengerHandler());

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyContents.MSG_FROM_SERVICE:
                    Log.d(TAG, "the message receive from service:"+msg.getData().getString("reply"));
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);

    }
}
