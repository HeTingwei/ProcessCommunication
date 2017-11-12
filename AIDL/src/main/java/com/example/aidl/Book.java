package com.example.aidl;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by HeTingwei on 2017/11/12.
 */

public class Book implements Parcelable {
    String bookName;
    int bookId;

    public Book( int bookId,String bookName) {
        this.bookName = bookName;
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public int getBookId() {
        return bookId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(bookId);
        parcel.writeString(bookName);

    }

    public static final Creator<Book>CREATOR=new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel parcel) {
            return new Book(parcel);
        }

        @Override
        public Book[] newArray(int i) {
            return new Book[i];
        }
    };
    private Book(Parcel in){
        bookId = in.readInt();
        bookName = in.readString();
    }
}
