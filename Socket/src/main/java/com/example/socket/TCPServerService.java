package com.example.socket;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by HeTingwei on 2017/11/12.
 * 服务器端
 *
 */
public class TCPServerService extends Service {
    private static final String TAG = "TCPServerService";
    private boolean mIsServiceDestroyed = false;
    private String[] mDefonedMessages = new String[]{
            "你好",
            "你叫什么",
            "今天天气好"
    };

    @Override
    public void onCreate() {
        new Thread(new TCPServer()).start();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed = true;
        super.onDestroy();

    }

    private  class TCPServer implements   Runnable{

        @SuppressWarnings("resource")
        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                //监听本地8688端口
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "establish failed ,port 8688");
                return;
            }

            while (!mIsServiceDestroyed) {
                try {
                    //接受客户端请求
                    final Socket client = serverSocket.accept();
                    Log.d(TAG, "accept");
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        //用于接受客户端消息
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //用于向客户端发送消息
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));
        out.println("欢迎来到聊天室");
        out.flush();

        while (!mIsServiceDestroyed) {
            String str = in.readLine();
            Log.d(TAG, "服务器接受到的消息：" + str);
            if (str == null) {
                //这个时候客户端断开了
                break;
            }
            int i=new Random().nextInt(mDefonedMessages.length);
            String msg = mDefonedMessages[i];
            out.println(msg);
            out.flush();
            Log.d(TAG, "send:"+msg);

        }
        Log.d(TAG, "client quit.");
        //关闭流
        out.close();
        in.close();
        client.close();
    }
}
