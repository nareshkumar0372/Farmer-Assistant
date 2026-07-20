package com.example.farmerassistant.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.farmerassistant.database.DBHelper;

public class ProductProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.farmerassistant.provider";
    public static final String TABLE_NAME = DBHelper.TABLE_PRODUCTS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    private static final int PRODUCTS = 1;
    private static final int PRODUCT_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TABLE_NAME, PRODUCTS);
        uriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", PRODUCT_ID);
    }

    private DBHelper helper;

    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (uriMatcher.match(uri) != PRODUCTS) throw new IllegalArgumentException("Invalid URI for insert");

        long id = db.insert(TABLE_NAME, null, values);
        if (id > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new RuntimeException("Insert failed");
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                String id = uri.getLastPathSegment();
                cursor = db.query(TABLE_NAME, projection, DBHelper.COL_ID + "=?", new String[]{id}, null, null, sortOrder);
                break;
            default: throw new IllegalArgumentException("Unknown URI");
        }
        if (cursor != null) cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows;
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                rows = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                String id = uri.getLastPathSegment();
                rows = db.update(TABLE_NAME, values, DBHelper.COL_ID + "=?", new String[]{id});
                break;
            default: throw new IllegalArgumentException("Unknown URI");
        }
        if (rows > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows;
        switch (uriMatcher.match(uri)) {
            case PRODUCTS: rows = db.delete(TABLE_NAME, selection, selectionArgs); break;
            case PRODUCT_ID:
                String id = uri.getLastPathSegment();
                rows = db.delete(TABLE_NAME, DBHelper.COL_ID + "=?", new String[]{id}); break;
            default: throw new IllegalArgumentException("Unknown URI");
        }
        if (rows > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PRODUCTS: return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + TABLE_NAME;
            case PRODUCT_ID: return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + TABLE_NAME;
            default: throw new IllegalArgumentException("Unsupported URI");
        }
    }
}