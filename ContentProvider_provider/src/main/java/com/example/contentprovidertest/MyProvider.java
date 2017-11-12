package com.example.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by HeTingwei on 2017/11/6.
 */

public class MyProvider extends ContentProvider {

    public static final int PERSON_DIR = 0;
    public static final int PERSON_ITEM = 1;

    public static final String AUTHORITY = "com.example.contentprovidertest.provider";
    private static UriMatcher uriMatcher;
    private MyDatabaseHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "person", PERSON_DIR);
        uriMatcher.addURI(AUTHORITY, "person/#", PERSON_ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MyDatabaseHelper(getContext(), "persons.db", null, 1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // 查询数据
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case PERSON_DIR:
                cursor = db.query("person", projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PERSON_ITEM:
                String personId = uri.getPathSegments().get(1);
                cursor = db.query("person", projection, "id = ?", new String[]
                        {personId}, null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PERSON_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.contentprovidertest.provider.person";
            case PERSON_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.contentprovidertest.provider.person";
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // 添加数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)) {
            case PERSON_DIR:
            case PERSON_ITEM:
                long newPersonId = db.insert("person", null, values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/person/" +
                        newPersonId);
                break;
            default:
                break;
        }
        return uriReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // 删除数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case PERSON_DIR:
                deletedRows = db.delete("person", selection, selectionArgs);
                break;
            case PERSON_ITEM:
                String personId = uri.getPathSegments().get(1);
                deletedRows = db.delete("person", "id = ?", new String[]{personId});
                break;
            default:
                break;
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // 更新数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (uriMatcher.match(uri)) {
            case PERSON_DIR:
                updatedRows = db.update("person", values, selection, selectionArgs);
                break;
            case PERSON_ITEM:
                String personId = uri.getPathSegments().get(1);
                updatedRows = db.update("person", values, "id = ?", new String[]
                        { personId });
                break;
            default:
                break;
        }
        return updatedRows;
    }
}
