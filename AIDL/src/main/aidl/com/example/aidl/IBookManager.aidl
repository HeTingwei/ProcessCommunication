// IBookManager.aidl
package com.example.aidl;

// Declare any non-default types here with import statements
import com.example.aidl.Book;
interface IBookManager {
    List<Book>getBookList();
    void addBook(in Book book);
}
