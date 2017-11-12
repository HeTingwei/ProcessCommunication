package com.example.aidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.List;
/*客户端实现：
* 绑定服务端的服务，将返回的binder转换为AIDL接口
* 通过这个接口去调用远程服务的方法
*
* */
public class BookManagerActivity extends AppCompatActivity {
    private static final String TAG = "BookManagerActivity";
    IBookManager bookManager;//通过bookManager的getBookList()或addBook方法查询或添加书籍
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
             bookManager = IBookManager.Stub.asInterface(service);
            try{
                List<Book> list = bookManager.getBookList();
                Log.d(TAG, "query book list,list type:" + list.getClass().getCanonicalName());
                Log.d(TAG, "query book 2: "+list.get(1).getBookName());
                Book book = new Book(3, "Android开发艺术");
                bookManager.addBook(book);
                List<Book> newList = bookManager.getBookList();
                Log.d(TAG, "new book is"+newList.get(newList.size()-1).getBookName());
            }catch (RemoteException e){
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
        bindService(new Intent(this, BookManagerService.class), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
    public void click(View v){
        try {
            bookManager.addBook(new Book(4,"添加"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
