package com.example.socket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
/*

这个项目是使用Socket在两进程间进行通讯，android开发艺术探索有点错误，已修改可以正常实现
* 客户端
* */
public class TCPClientActivity extends AppCompatActivity {

    private static final String TAG = "TCPClientActivity";

    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;//接受到消息的型号量
    private static final int MESSAGE_SOCKET_CONNECTED = 2;//

    private Button mSendButton;//点击发送消息
    private TextView mMessageTextView;//显示接受到的消息（加上接受时间）
    private EditText mMessageEditText;//输入要发送的消息

    private Socket mClientSocket;
    private PrintWriter mPrintWriter;//输出
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG:
                    mMessageTextView.setText(mMessageTextView.getText() + (String) msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    mSendButton.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMessageTextView = (TextView) findViewById(R.id.textView);
        mMessageEditText = (EditText) findViewById(R.id.editText);
        mSendButton = (Button) findViewById(R.id.button);
        startService(new Intent(this, TCPServerService.class));
        new Thread() {
            @Override
            public void run() {
                super.run();
                connectTCPServer();//网络请求必须在子线程中进行
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
    public void click(View v){
        final String msg = mMessageEditText.getText().toString();
        Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
            new Thread(){
                @Override
                public void run() {
                    mPrintWriter.println(msg);//网络请求必须在子线程中
                    super.run();
                }
            }.start();
            mMessageEditText.setText("");
//            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
//            String time=df.format(new Date());
//            mMessageTextView.setText(mMessageTextView.getText() + time);

        }
    }

    private void connectTCPServer() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.d(TAG, "connect server success");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                Log.e(TAG, "connect tcp server failed,retry..");
            }
        }

        //接受服务器消息
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //活动被关闭则不再监听
            while (!TCPClientActivity.this.isFinishing()) {
                String msg = br.readLine();
                Log.d(TAG, "receive:" + msg);
                if (msg != null) {
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                    String time=df.format(new Date());
                    final String showedMsg = "server" + time + ":" + msg + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG,showedMsg).sendToTarget();
                }
            }
            Log.d(TAG, "quit..");
            mPrintWriter.close();
            br.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
