package edu.buffalo.cse.cse486586.groupmessenger2;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
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
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
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
    static String myPortNumber = "";
    static String key="0";
    static final String KEY_FIELD = "key";
    static final String VALUE_FIELD = "value";
    static final int SERVER_PORT = 10000;
    List<Double> arrayList = new ArrayList<Double>();
    int sequencer = 0;
    String msgToSend;
    String portNum;
    double agreedseq;
    String flagval;
    String failedavd="0";
    /*
    priority queue comparator logic was referred from below site
    https://stackoverflow.com/questions/683041/how-do-i-use-a-priorityqueue
     */
    PriorityQueue<message> queue=new PriorityQueue<message>(300, new Comparator<message>() {
        @Override
        public int compare(message lhs, message rhs) {
            if(lhs.seqnum<rhs.seqnum)
                return -1;
            if(lhs.seqnum>rhs.seqnum)
                return 1;
            return 0;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        myPortNumber = myPort;
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
    private class message {
        String portno;
        Double seqnum;
        String value;
        String flag;
        String failavd;
        String forfailure;
        String clientport;
        message(String portnumber, double seqnum, String value, String flag, String favd, String forfailure, String clientport) {
            this.portno = portnumber;
            this.seqnum = seqnum;
            this.value = value;
            this.flag = flag;
            this.failavd = favd;
            this.forfailure = forfailure;
            this.clientport = clientport;
        }
    }
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            Socket socket = null;
            try
            {
                while (true) {
                    socket = serverSocket.accept();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = (String) br.readLine();
                    Log.e("Server message received",msg);
                    String[] arrofString = msg.split(":");
                    String flagfromarray = arrofString[2];
                    Log.e(TAG,"flag value"+flagfromarray);
                    String prtfromarray = arrofString[4];
                    String seqfromarray = arrofString[3];
                    String msgfromarray = arrofString[5];
                    String failfromarray = arrofString[1];
                    String clientportval = arrofString[0];
                    Log.e(TAG,"Failed avd:"+failfromarray);
                    String forfailurecheck = prtfromarray;
                    prtfromarray="0."+prtfromarray;
                    double prtfromarray_double=Double.parseDouble(prtfromarray);
                    Log.e(TAG,"DOUBLE VAL"+prtfromarray_double);
                    double seqqq=Double.parseDouble(seqfromarray);
                    Log.e(TAG,"DECIMAL SEQ"+seqqq);
                    boolean b=Boolean.parseBoolean(flagfromarray);
                    if(b) {
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        sequencer += 1;
                        String valtosend=sequencer+":"+prtfromarray;
                        out.write(String.valueOf(valtosend));
                        out.flush();
                        Log.e(TAG, "Server: sent sequence value"+valtosend);
                        message mesobj = new message(prtfromarray, seqqq, msgfromarray, "false", failfromarray, forfailurecheck, clientportval);
                        queue.add(mesobj);
                        Log.e(TAG, "Server task obj added: "+mesobj.value);

                    }
                    if(!b) {

                        PrintWriter out1 = new PrintWriter(socket.getOutputStream());
                        out1.write("ack");
                        out1.flush();

                        for (message items : queue) {

                            if (msgfromarray.equals(items.value)) {
                                queue.remove(items);
                                items.flag = "true";
                                items.seqnum = seqqq;
                                Log.e(TAG, "SEQQQ:" + seqqq);
                                int somevar = (int) Math.round(seqqq);
                                Log.e(TAG, "SOMEVAR:" + somevar);
                                sequencer = Math.max(sequencer, Math.round(somevar));
                                Log.e(TAG, "SEQUENCER:" + sequencer);
                                sequencer = sequencer + 1;
                                queue.add(items);
                                Log.e(TAG, "Updated queue");
                            }
                        }

                    }



                    for(message items:queue) {

                        Log.e(TAG, "Trying to Remove--->" + items.clientport + ":  " + failfromarray);
                        if (items.clientport.trim().equals(failfromarray.trim()))
                        {
                            queue.remove(items);
                        }
                    }

                    for (message it : queue) {
                        Log.e(TAG, "Queue val--->" + it.value);
                        Log.e(TAG, "Queue flag--->" + it.flag);
                        Log.e(TAG, "Queue seq--->" + it.seqnum);
                        Log.e(TAG, "Queue port--->" + it.portno);
                        Log.e(TAG, "Queue clientport--->"+it.clientport);
                        Log.e(TAG, "Queue for failure--->"+it.forfailure);
                    }

                    while((queue.peek()!=null && queue.peek().flag.equals("true") )|| (queue.peek()!=null && queue.peek().forfailure==failfromarray && queue.peek().flag.equals("false"))) {
                        Log.e(TAG, "entering while:" + queue.peek().flag);
                        if(queue.peek().flag.equals("true")) {
                            Log.e(TAG, "entering while to publish");
                            message peeking = queue.peek();
                            publishProgress(peeking.value);
                            queue.poll();
                            Log.e(TAG, "Published and removed");
                        }
                        else {
                            Log.e(TAG, "Inside else:");

                            if (queue.peek().forfailure==failfromarray && queue.peek().flag.equals("false"))
                            {
                                Log.e(TAG, "Removing ");
                                queue.poll();
                            }
                        }
                    }


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
            uri_forinsert = buildUri_forinsert("content", "edu.buffalo.cse.cse486586.groupmessenger2.provider");
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
            String[] remoteport=new String[5];
            remoteport = new String[]{"11108","11112", "11116","11120","11124"};
            Socket socket=null;
            String msgRecieved=null;
            Log.e(TAG, "C: ClientTask port values assigned:");
            for(int i=0;i<remoteport.length;i++) {
                //try {
                if ((Integer.parseInt(remoteport[i])) != (Integer.parseInt(failedavd))) {
                    try {
                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(remoteport[i].toString()));
                        socket.setSoTimeout(2000);
                        Log.e(TAG, "C: creating socket:1 ");
                        msgToSend = msgs[0];
                        portNum = remoteport[i];
                        flagval = "true";
                        String msgToServer = myPortNumber+":"+failedavd + ":" + flagval + ":" + agreedseq + ":" + portNum + ":" + msgToSend;
                        Log.e(TAG, "Final Message in client 1 " + msgToServer);
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        out.write(msgToServer);
                        Log.e(TAG, "C: ClientTask msgsent " + msgToServer);
                        out.flush();
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        msgRecieved = (String) br.readLine();
                        Log.e(TAG, "in client from server" + msgRecieved);
                        String[] readingProposal = msgRecieved.split(":");
                        Double readingportvalue = Double.parseDouble(readingProposal[1]);
                        Double readingproposalvalue = Double.parseDouble(readingProposal[0]);
                        arrayList.add(readingportvalue + readingproposalvalue);
                        for (int ka = 0; ka < arrayList.size(); ka++) {
                            Log.e(TAG, "arrayList--->" + String.valueOf(arrayList.get(ka)));
                        }
                        socket.close();
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Caught null pointer exception 2:" + e.getMessage());
                        failedavd = portNum;
                    } catch (SocketTimeoutException se) {
                        Log.e(TAG, "IO Exception 1:" + se);
                        failedavd = portNum;
                    } catch (StreamCorruptedException e) {
                        Log.e(TAG, "IO Exception 1:" + e);
                        failedavd = portNum;
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "IO Exception 1:" + e);
                        failedavd = portNum;
                    }catch (EOFException e) {
                        Log.e(TAG, "IO Exception 1:" + e);
                        failedavd = portNum;
                    }catch (IOException e) {
                        Log.e(TAG, "IO Exception 1:" + e);
                        failedavd = portNum;
                    }catch (Exception e) {
                        Log.e(TAG, " Exception 1:" + e);
                        failedavd = portNum;
                    }

                } //if closing
            } //for closing
            //try {
            agreedseq = Collections.max(arrayList);
            Log.i(TAG, "AgreedSequence " + agreedseq);
            Log.e(TAG, "ClientTask port values assigned:");
            for (int i = 0; i < remoteport.length; i++) {

                Log.e(TAG, "creating socket: Port: " + remoteport[i]);
                try {
                    if ((Integer.parseInt(remoteport[i])) != (Integer.parseInt(failedavd))) {
                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(remoteport[i].toString()));
                        socket.setSoTimeout(2000);
                        Log.e(TAG, "creating socket:2 " + remoteport[i] );
                        portNum = remoteport[i];
                        //String pp = remoteport[i] + 1;
                        flagval = "false";
                        String msgToServer2 = myPortNumber+":"+failedavd + ":" + flagval + ":" + agreedseq + ":"+portNum+ ":" + msgToSend;
                        Log.e(TAG, "Final Message in client 2" + msgToServer2);
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        out.write(msgToServer2);
                        Log.e(TAG, "ClientTask msgsent 2:" + msgToServer2);
                        out.flush();
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String msgRecieved1 = "";
                        Log.e(TAG, "ClientTask waiting");
                        while((msgRecieved1 = br.readLine()) != null){
                            Log.e(TAG, "ClientTask message received:" + msgRecieved1);
                            if(msgRecieved1=="ack"){
                                break;
                            }
                        }
                        socket.close();

                    }
                } catch (SocketTimeoutException se) {
                    Log.e(TAG, "IO Exception 1:" + se);
                    failedavd = portNum;
                } catch (StreamCorruptedException e) {
                    Log.e(TAG, "IO Exception 1:" + e);
                    failedavd = portNum;
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "IO Exception 1:" + e);
                    failedavd = portNum;
                }catch (EOFException e) {
                    Log.e(TAG, "IO Exception 1:" + e);
                    failedavd = portNum;
                }catch (UnknownHostException e) {
                    Log.e(TAG, "ClientTask UnknownHostException");
                } catch (IOException e) {
                    Log.e(TAG, "ClientTask socket IOException:" + e.getMessage());
                    failedavd = portNum;
                }catch (Exception e) {
                    Log.e(TAG, " Exception 1:" + e);
                    failedavd = portNum;
                }
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
