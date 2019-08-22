package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static String key="0";
    static final String KEY_FIELD = "key";
    static final String VALUE_FIELD = "value";
    static final int SERVER_PORT = 10000;

    /*
    The code is entirely taken from PA1 and modified accordingly
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket:"+e.getMessage());
        }
        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));

        final Button button4 = (Button) findViewById(R.id.button4);
        final EditText EditText = (EditText) findViewById(R.id.editText1);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                String msg = EditText.getText().toString() + "\n";
                EditText.setText("");
                TextView localTextView = (TextView) findViewById(R.id.textView1);
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);

            }
        });
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
    }

    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            try
            {
                while (true) {
                    Socket socket = serverSocket.accept();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = (String) br.readLine();
                    Log.e(TAG, "Server task: message received");
                    publishProgress(msg);
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.write("gotmessage");
                    out.flush();
                    Log.e(TAG, "Servertask asking client to close");
                    socket.close();
                }
            }

            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String...strings) {

            String strReceived = strings[0].trim();
            TextView remoteTextView = (TextView) findViewById(R.id.textView1);
            remoteTextView.append(strReceived + "\t\n");

            /*
            code is implemented referring to code in OnPTestClickListener.java
             */
            Uri uri_forinsert;
            uri_forinsert = buildUri_forinsert("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");

            ContentValues keyValuetoInsert = new ContentValues();
            keyValuetoInsert.put(KEY_FIELD,key);
            keyValuetoInsert.put(VALUE_FIELD,strReceived);
            Log.e(TAG, "Key value inserting into contentresolver"+key);
            Uri newUri=getContentResolver().insert(uri_forinsert,keyValuetoInsert);
            key = String.valueOf(Integer.parseInt(key) + 1);
            return;
        }
        private Uri buildUri_forinsert(String scheme, String authority) {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority(authority);
            uriBuilder.scheme(scheme);
            return uriBuilder.build();
        }
    }

    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {
            try {
               String[] remoteport=new String[5];
                    remoteport = new String[]{"11108","11112", "11116","11120","11124"};

                Log.e(TAG, "ClientTask port values assigned:");
                for(int i=0;i<remoteport.length;i++) {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remoteport[i].toString()));
                    Log.e(TAG, "creating socket:");

                    String msgToSend = msgs[0];

                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.write(msgToSend);
                    Log.e(TAG, "ClientTask msgsent");
                    out.flush();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = (String) br.readLine();
                    if (msg == "gotmessage")
                        socket.close();

                }

            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException:"+ e.getMessage());

            }

            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
