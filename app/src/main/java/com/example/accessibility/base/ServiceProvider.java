package com.example.accessibility.base;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.accessibility.BuildConfig;
import com.example.accessibility.MyApp;

/**
 * 跨进程提供binder service,对于本工程来说，ServiceProvider需要和Lib库中的MyAccessibilityService
 * 运行在同一个进程，已方便来测试跨进程调用。
 *
 * 注意manifest中 android:process=":accessibility" 属性
 */
public class ServiceProvider extends ContentProvider {

    public static final String AUTHORITIES = BuildConfig.APPLICATION_ID + ".provider.Service";
    protected static final Uri GET_SERVICE = Uri.parse("content://" + ServiceProvider.AUTHORITIES + "/service");
    public static final String KEY_SERVICE = "key_service";

    public ServiceProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Nullable
    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (TextUtils.isEmpty(arg)) {
            return null;
        }
        if (MyServiceManger.ACC_SERVICE.equals(arg)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Bundle result = new Bundle();
                result.putBinder(KEY_SERVICE, MyApp.sService);
                return result;
            }
        } else {
            // TODO
        }
        return null;
    }
}
