package com.example.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by HeTingwei on 2017/11/12.
 * 这里是远程服务端的service实现，注意，要先新建Book.adil和BookManager.adil同步后，再写这里的代码，（通过adil会生成IBookManager类），aidl文件在包com.example.aidl下，为自动生成包
 * 在这里实现了AIDL的接口
 */

public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private Binder mBinder=new IBookManager.Stub(){

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.d(TAG, "addBook: "+book.getBookName());
mBookList.add(book);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"Android"));
        mBookList.add(new Book(2,"IOS"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
