package edu.buffalo.cse.cse486586.simpledht;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class OnLDumpClickListener implements OnClickListener {

    private static final String TAG = OnTestClickListener.class.getName();
    private static final int TEST_CNT = 50;
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";

    private final TextView mTextView;
    private final ContentResolver mContentResolver;
    private final Uri mUri;
    private final ContentValues[] mContentValues;

    public OnLDumpClickListener(TextView _tv, ContentResolver _cr) {
        mTextView = _tv;
        mContentResolver = _cr;
        mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledht.provider");
        mContentValues = initTestValues();
    }

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    private ContentValues[] initTestValues() {
        ContentValues[] cv = new ContentValues[TEST_CNT];
        for (int i = 0; i < TEST_CNT; i++) {
            cv[i] = new ContentValues();
            cv[i].put(KEY_FIELD, "key" + Integer.toString(i));
            cv[i].put(VALUE_FIELD, "val" + Integer.toString(i));
        }

        return cv;
    }

    @Override
    public void onClick(View v) {
        new Task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class Task extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (testQuery()) {
                publishProgress("Query success\n");
               // deletefunction();
               // publishProgress("DELETED @ VALUES");
            } else {
                publishProgress("Query fail\n");
            }

            return null;
        }

        protected void onProgressUpdate(String...strings) {
            mTextView.append(strings[0]);

            return;
        }

        private boolean testQuery() {
            try {

                Cursor resultCursor = mContentResolver.query(mUri, null,
                        "@", null, null);

                //while condition is referred from below site
                // https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor
                while(resultCursor.moveToNext())
                {
                    // the below line of code of getcolumnindex is reffered from following site
                    // https://stackoverflow.com/questions/903343/get-the-field-value-with-a-cursor
                    publishProgress(resultCursor.getString(resultCursor.getColumnIndex("value")));
                    publishProgress("\n");
                }
                if (resultCursor == null) {
                    Log.e(TAG, "Result null");
                    throw new Exception();
                }

                resultCursor.close();

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        private void deletefunction(){
            mContentResolver.delete(mUri,"@",null);
        }
    }
}
