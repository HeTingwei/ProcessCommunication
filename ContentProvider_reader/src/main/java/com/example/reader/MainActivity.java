package com.example.reader;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
/*这个项目是对：使用内容提供器，对ContentProvider_provider项目的数据表，
进行增删查改*/
public class MainActivity extends AppCompatActivity {
    int name = 0;
    int age = 0;
    TextView textView;
    Uri uri;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        uri = Uri.parse("content://com.example.contentprovidertest.provider/person");
    }

    //给reader数据库中添加数据
    public void insertClick(View v) {
        ContentValues values = new ContentValues();
        values.put("name", "添加的name" + name++);
        values.put("age",   age++);
        getContentResolver().insert(uri, values);
    }

    //删除reader表中第一个数据
    public void deleteClick(View view) {
        cursor = getContentResolver().query(uri, null, null, null, null, null);
        if (!cursor.moveToFirst()) {
            return;
        }
        String name = cursor.getString(cursor.getColumnIndex("name"));
        getContentResolver().delete(uri, "name=?", new String[]{name});
    }

    //修改第一条数据
    public void updateClick(View view) {
        cursor = getContentResolver().query(uri, null, null, null, null, null);
        if (!cursor.moveToFirst()) {
            return;
        }
        String name = cursor.getString(cursor.getColumnIndex("name"));
        ContentValues values = new ContentValues();
        values.put("age",age++);
        getContentResolver().update(uri,values,"name=?",new String[]{name});
    }

    //查询reader的数据库
    public void queryClick(View view) {
        String result = "";
        cursor = getContentResolver().query(uri, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int age = cursor.getInt(cursor.getColumnIndex("age"));
                result = result + "name:" + name + " age:" + age + "\n";
            } while (cursor.moveToNext());
        }
        textView.setText(result);
    }
}
