package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {
    static final String TAG = GroupMessengerProvider.class.getSimpleName();
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        FileOutputStream outputStream;

        /*
        Referred the below site to understand how to implement contextProvider.
        https://stackoverflow.com/questions/5386509/insert-data-into-content-provider-then-display-it-in-a-listview
        code is implemented referring to code in OnPTestClickListener.java
         */

        try {
            String keyformcontentval=(String)values.get("key");
            String valfromcontentval=(String)values.get("value");
            Log.e(TAG, "opening file name"+keyformcontentval);
            /*
            I was getting error as OpenFileOutput undefined so I used getContext(). Solution is referred from below site
            https://stackoverflow.com/questions/4015773/the-method-openfileoutput-is-undefined
             */
            outputStream = getContext().openFileOutput(keyformcontentval, Context.MODE_PRIVATE);
            outputStream.write(valfromcontentval.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "File write failed");
        }

        Log.v("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
       /* reading and writing from the file code is referred from the below site
        https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android
        Matrixcursor implementation is referred from below site
        https://medium.com/@xabaras/creating-a-cursor-from-a-list-with-matrixcursor-ab71877ecf2c
        */
        String ret="";
        try {
            InputStream inputStream = getContext().openFileInput(selection);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();

                //matrix building
                String[] columns=new String[]{"key","value"};
                MatrixCursor cursor=new MatrixCursor(columns);
                cursor.newRow().add("key",selection).add("value",ret);
                return cursor;

            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "File could not be opened");
        } catch (IOException e) {
            Log.e(TAG, "cannot read file");
        }
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        Log.v("query (not done)", selection);
        return null;
    }
}
