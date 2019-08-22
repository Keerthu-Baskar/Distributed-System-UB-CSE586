package edu.buffalo.cse.cse486586.simpledht;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.concurrent.ExecutionException;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
public class SimpleDhtProvider extends ContentProvider {
    static final String TAG = SimpleDhtProvider.class.getSimpleName();
    static String myPortNumber = "";
    static String myport="";
    String predecessor="";
    String successor="";
    String predecessororiginal="";
    String successororiginal="";
    static final int SERVER_PORT = 10000;
    String[] remoteport = new String[]{"11108", "11112", "11116", "11120", "11124"};
    String entirearraylist="";
    static ArrayList<nodeID> listofnodes=new ArrayList<nodeID>();
    ArrayList<nodeID> localarraylist=new ArrayList<nodeID>();
    InputStreamReader inputStreamReader=null;
    BufferedReader bufferedReader=null;
    public class nodeID {
        String pred="null";
        String succ="null";
        String hashnodeval="null";
        String portnumber="null";
        String predoriginal="null";
        String succoriginal="null";
        nodeID(String predecessor, String successor, String hashportnum, String portnum, String predoriginal, String succoriginal){
            this.pred=predecessor;
            this.succ=successor;
            this.hashnodeval=hashportnum;
            this.portnumber=portnum;
            this.predoriginal=predoriginal;
            this.succoriginal=succoriginal;
        }
        public void pred(String portnumber) {
            this.pred=portnumber;
        }
        public void succ(String portnumber) {
            this.succ=portnumber;
        }

        public void predoriginal(String portnumber) {
            this.predoriginal=portnumber;
        }

        public void succoriginal(String portnumber) {
            this.succoriginal=portnumber;
        }
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        if(selection.equals("@"))
        {
            String[] filelist=getContext().fileList();
            Log.e(TAG,"delete 3:"+filelist.length);
            for(String file:filelist)
                getContext().deleteFile(file);
        }
        if((!selection.equals("*"))&&(!selection.equals("@")))
        {
            Socket socket = null;
            try {
                socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(myPortNumber.toString()));
                DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                String deletefile=selection+":"+"deletefile";
                out.writeUTF(deletefile);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selection.equals("*"))
        {
            String delto="delete";
            Socket socket = null;
            String avd0="11108";
            String todel="todel";
            String deleteall="deleteall";
            //AsyncTask<String, Void, String> for_delete=new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, delto, myPortNumber);
            try {
                socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(avd0.toString()));
                DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                out.writeUTF(todel);
                out.flush();
                Log.e(TAG,"socket connected success:"+todel);
                DataInputStream br=new DataInputStream(socket.getInputStream());
                String msg=br.readUTF();
                Log.e(TAG,"data read:"+msg);
                String[] spl=msg.split(":");
                for(int i=0;i<spl.length;i++)
                {
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(spl[i].toString()));
                    DataOutputStream out1=new DataOutputStream(socket.getOutputStream());
                    out1.writeUTF(deleteall);
                    out1.flush();
                    Log.e(TAG,"socket connected success 222:"+deleteall);
                }
            }  catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        FileOutputStream outputStream;
        try {
            String keyformcontentval=(String)values.get("key");
            String valfromcontentval=(String)values.get("value");
            String hashedkey=genHash(keyformcontentval);

            if((!predecessor.equals(""))&&(!successor.equals(""))) {
                Log.e(TAG,"pred and succ at insert:"+predecessor+":"+successor);
                int myavd=Integer.parseInt(myPortNumber);
                myavd=myavd/2;
                String mavd=Integer.toString(myavd);
                String myavdhash=genHash(mavd);
                if(((genHash(keyformcontentval).compareTo(predecessor))>0)&&(myavdhash.compareTo(genHash(keyformcontentval)))>=0)
                {
                    Log.e(TAG, "opening file name at insert at local:" + keyformcontentval);
                    outputStream = getContext().openFileOutput(keyformcontentval, Context.MODE_PRIVATE);
                    outputStream.write(valfromcontentval.getBytes());
                    outputStream.close();
                }
                else {
                    //String po = "11108";
                    String params = keyformcontentval + ":pleasecheck:" + valfromcontentval;
                    AsyncTask<String, Void, String> keyinsert = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, params, myPortNumber);
                }
                //AsyncTask<String, Void, String> keyinsert = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "broadcast", "11108");
               /* for (int i = 0; i < listofnodes.size(); i++) {
                    Log.e(TAG,"portvalnum:"+myPortNumber);
                    Log.e(TAG, "port val checking:"+listofnodes.get(0).portnumber);
                    if (((listofnodes.get(i).hashnodeval.compareTo(hashedkey)) > 0) &&((hashedkey.compareTo(listofnodes.get(i).pred))>0)) {
                        String concatval = toinsert + ":" + listofnodes.get(i).portnumber + ":" + hashedkey + ":" + keyformcontentval + ":" + valfromcontentval;
                        Log.e(TAG,"Concat val:"+concatval);
                        AsyncTask<String, Void, String> keyinsert = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, concatval, listofnodes.get(i).portnumber);
                        break;
                    }
                }*/
            }
            Log.e(TAG,"*********");

            Log.e(TAG,"local array list pred succ val:"+predecessor+":"+successor+"***");
            if((predecessor=="")&&(successor=="")) {

                Log.e(TAG, "opening file name at insert if null:" + keyformcontentval);
                outputStream = getContext().openFileOutput(keyformcontentval, Context.MODE_PRIVATE);
                outputStream.write(valfromcontentval.getBytes());
                outputStream.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "File write failed at insert");
        }

        Log.v("insert", values.toString());
        return uri;
    }
    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        TelephonyManager tel = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        String emulator0=portStr;
        myport = String.valueOf((Integer.parseInt(portStr) * 2));
        myPortNumber = myport;
        String hashedportval= null;
        try {
            hashedportval = genHash(emulator0);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if(emulator0.equals("5554"))
        {
            Log.e(TAG,"hashed port val of avd0:"+hashedportval);
            nodeID nodeobj=new nodeID(null,null, hashedportval, myPortNumber, null, null);
            listofnodes.add(nodeobj);
            for(int i=0;i<listofnodes.size();i++)
            {
                Log.e(TAG,"Adding to array--->"+listofnodes.get(i).portnumber);
            }
        }
        else
        {
            AsyncTask<String, Void, String> joinreq=new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, myPortNumber, myPortNumber);
            try {
                Log.e(TAG,"join reply:"+joinreq.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket:"+e.getMessage());
        }
        return false;
    }
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            Socket socket = null;
            try {
                while (true) {
                    socket = serverSocket.accept();
                    DataInputStream br=new DataInputStream(socket.getInputStream());
                    String msg=br.readUTF();
                    String temp="";
                    String nameoffile="";
                    Boolean flag=true;
                    boolean flagvar=true;

                    if(msg.contains("deletefile"))
                    {
                        String[] filelist=getContext().fileList();
                        Log.e(TAG,"delete 1:"+filelist.length);
                        for(String file:filelist) {
                            getContext().deleteFile(file);
                        }
                    }
                    if(msg.contains("deleteall"))
                    {
                        String[] filelist=getContext().fileList();
                        Log.e(TAG,"delete 2:"+filelist.length);
                        for(String file:filelist) {
                            getContext().deleteFile(file);
                        }
                    }
                    if(msg.contains("fromavd0"))
                    {
                        String returnval="";
                        for(int i=0;i<listofnodes.size();i++)
                        {
                            if(flagvar==true) {
                                returnval = listofnodes.get(i).portnumber;
                                Log.e(TAG,"return val flag val:"+returnval);
                                flagvar=false;
                            }
                            else {
                                returnval = returnval +":"+ listofnodes.get(i).portnumber;
                                Log.e(TAG,"return val flag val 2:"+returnval);
                            }
                        }

                        DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(returnval);
                        out.flush();
                        Log.e(TAG, "S: List of ports:"+returnval);
                    }

                    if(msg.contains("todel"))
                    {
                        String returnval="";
                        for(int i=0;i<listofnodes.size();i++)
                        {
                            if(flagvar==true) {
                                returnval = listofnodes.get(i).portnumber;
                                Log.e(TAG,"return val flag val 444:"+returnval);
                                flagvar=false;
                            }
                            else {
                                returnval = returnval +":"+ listofnodes.get(i).portnumber;
                                Log.e(TAG,"return val flag val 555:"+returnval);
                            }
                        }

                        DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(returnval);
                        out.flush();
                        Log.e(TAG, "S: List of ports rttt:"+returnval);
                    }

                    if(msg.equals("*"))
                    {
                        Log.e(TAG, "S: GOT FIRST SERVER SIDE MESSAGE ");
                        String[] filelist=getContext().fileList();
                        int fff=filelist.length;
                        Log.e(TAG,"NUMBER of files: "+fff);
                        for(String file:filelist)
                        {
                            try {
                                InputStream inputStream = getContext().openFileInput(file);
                                if (inputStream != null) {
                                    Log.e(TAG,"COUNT1");
                                    inputStreamReader = new InputStreamReader(inputStream);
                                    bufferedReader = new BufferedReader(inputStreamReader);
                                    String receiveString = "";
                                    if ((receiveString = bufferedReader.readLine()) != null) {
                                        Log.e(TAG,"Received String ---> "+receiveString);
                                        if(flag==true)
                                        {
                                            temp=receiveString;
                                            nameoffile=file;
                                            flag=false;
                                        }
                                        else
                                        {
                                            temp=temp+":"+receiveString;
                                            nameoffile=nameoffile+":"+file;
                                        }
                                        Log.e(TAG,"INside Loop ---> "+temp+" file name ---> "+nameoffile);
                                    }
                                    inputStream.close();
                                }

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Log.e(TAG,"Exception 3:"+e.getMessage());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG,"Exception 4:"+e.getMessage());
                            }
                        }

                        if(fff==0)
                        {
                                Log.e(TAG,"coming inside this atleast:");
                                if(flag==true)
                                {
                                    temp="algebra";
                                    nameoffile="algebra";
                                    flag=false;
                                    Log.e(TAG,"assigned algebra:");
                                }
                                else {
                                    temp=temp+":"+"algebra";
                                    nameoffile=nameoffile+":"+"algebra";
                                    Log.e(TAG,"assigned algebra 2:");
                                }
                        }
                        //temp=temp+"\n";
                        Log.e(TAG,"val sending: name of file:--->"+nameoffile);
                        Log.e(TAG,"val sending: temp:--->"+temp);
                        String valsending=temp+"keerthana"+nameoffile;
                        DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(valsending);
                        out.flush();
                        Log.e(TAG, "S: Cursor Value:"+valsending);
                    }
                    if(msg.equals("delete"))
                    {
                        String[] filelist=getContext().fileList();
                        for(String file:filelist)
                            getContext().deleteFile(file);
                        Log.e(TAG,"DELETED");
                    }
                    if((msg.equals("11112")) || (msg.equals("11116")) || (msg.equals("11120")) || (msg.equals("11124")))
                    {
                        int tohash=Integer.parseInt(msg)/2;
                        String hashedportval = genHash(Integer.toString(tohash));
                        nodeID nodeobj=new nodeID(null,null, hashedportval, msg, null, null);
                        listofnodes.add(nodeobj);
                        Log.e(TAG,"added object in list:"+nodeobj.portnumber+" size:"+ listofnodes.size()+ " hash val:"+nodeobj.hashnodeval);
                        int size=listofnodes.size();
                        Log.e("TAG","SIZE of arraylist immediately after adding:"+size);
                        for(int i=0;i<size;i++) {
                            for (int j = i + 1; j < size; j++) {
                                Log.e(TAG, "1--->" + listofnodes.get(i).hashnodeval + "---" + listofnodes.get(i).portnumber);
                                Log.e(TAG, "1--->" + listofnodes.get(j).hashnodeval + "---" + listofnodes.get(j).portnumber);
                                if ((listofnodes.get(i).hashnodeval.compareTo(listofnodes.get(j).hashnodeval)) > 0) {
                                    nodeID swap = listofnodes.get(i);
                                    listofnodes.set(i, listofnodes.get(j));
                                    listofnodes.set(j, swap);
                                    Log.e(TAG, "2--->" + listofnodes.get(i).hashnodeval + "---" + listofnodes.get(i).portnumber);
                                    Log.e(TAG, "2--->" + listofnodes.get(j).hashnodeval + "---" + listofnodes.get(j).portnumber);
                                }
                            }
                        }
                        Log.e(TAG,"size of list before setting pred succ:"+size);
                        for(int j=0;j<size;j++)
                        {
                            if(j==0)
                            {
                                listofnodes.get(j).pred(listofnodes.get(size-1).hashnodeval);
                                listofnodes.get(j).predoriginal(listofnodes.get(size-1).portnumber);
                            }
                            else
                            {
                                listofnodes.get(j).pred(listofnodes.get(j-1).hashnodeval);
                                listofnodes.get(j).predoriginal(listofnodes.get(j-1).portnumber);
                            }
                            if(j==size-1)
                            {
                                listofnodes.get(j).succ(listofnodes.get(0).hashnodeval);
                                listofnodes.get(j).succoriginal(listofnodes.get(0).portnumber);
                            }
                            else
                            {
                                listofnodes.get(j).succ(listofnodes.get(j+1).hashnodeval);
                                listofnodes.get(j).succoriginal(listofnodes.get(j+1).portnumber);
                            }
                            Log.e(TAG,"port val:"+listofnodes.get(j).portnumber+" Pred val:"+listofnodes.get(j).predoriginal+" Succ val:"+listofnodes.get(j).succoriginal +" Hash val:"+listofnodes.get(j).hashnodeval);
                        }
                        for(int i=0;i<size;i++)
                        {
                            Log.e(TAG,"After sorting order--->"+listofnodes.get(i).portnumber +" Size:"+listofnodes.size());

                        }

                        broadcast();

                    }


                    if(msg.contains("toinsert:"))
                    {
                        try {
                            FileOutputStream outputStream;
                            Log.e(TAG,"got the big message:"+msg);
                            String[] splitval = msg.split(":");
                            Log.e(TAG, "opening file name" + splitval[2]);
                            outputStream = getContext().openFileOutput(splitval[2], Context.MODE_PRIVATE);
                            outputStream.write(splitval[4].getBytes());
                            outputStream.close();
                        }
                        catch (Exception e)
                        {
                            Log.e(TAG,"Exception file writing:"+e.getMessage());
                        }

                    }

                    /*
                    if(msg.contains("entirearraylist"))
                    {
                        String[] sp=msg.split("@");
                        String[] four=sp[1].split("/");
                        Log.e(TAG,"length of ..."+four.length);
                        for(int i=0;i<four.length;i++)
                        {
                            Log.e(TAG,"localarraylist contents:"+four[i]);
                            String[] collon=four[i].split(":");
                            nodeID objcreation=new nodeID(collon[0],collon[1],collon[2],collon[3]);
                            localarraylist.add(objcreation);
                            Log.e(TAG,"localarraylist:"+collon[0]+":"+collon[1]+":"+collon[2]+":"+collon[3]);


                        }

                    }*/

                    if(msg.contains("entirearraylist"))
                    {
                        String[] sp=msg.split(":");
                        predecessor=sp[0];
                        successor=sp[2];
                        predecessororiginal=sp[3];
                        successororiginal=sp[4];
                        Log.e(TAG,"localarraylist:"+sp[3]+":"+sp[4]);



                    }


                    if(msg.contains(":otheravdrequest:"))
                    {
                        FileOutputStream outputStream;
                        String[] msgfromavd=msg.split(":");
                        Log.e(TAG, "opening file name at insert by avd0:" + msgfromavd[0]);
                        outputStream = getContext().openFileOutput(msgfromavd[0], Context.MODE_PRIVATE);
                        outputStream.write(msgfromavd[2].getBytes());
                        outputStream.close();
                    }

                    if(msg.contains("pleasecheck")) {
                        Log.e(TAG, "FINALLYYY:" + msg);
                        Log.e(TAG, "FINAL:" + listofnodes.size());

                        String[] splittingfinal = msg.split(":");
                        String hashofkeyval = genHash(splittingfinal[0]);
                        for (int i = 0; i < listofnodes.size(); i++) {
                            if (((listofnodes.get(i).hashnodeval.compareTo(hashofkeyval)) >= 0) && ((hashofkeyval.compareTo(listofnodes.get(i).pred)) > 0)) {
                                Log.e(TAG, "it will go into this port:" + listofnodes.get(i).portnumber);
                                try {
                                    String sendtothis=splittingfinal[0]+":"+listofnodes.get(i).portnumber+":"+splittingfinal[2]+":insertfile";
                                    sendtothisport(sendtothis);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        int a=listofnodes.size();
                        if(((hashofkeyval.compareTo(listofnodes.get(a-1).hashnodeval))>0))//||(("c25ddd596aa7c81fa12378fa725f706d54325d12".compareTo(hashofkeyval))>0))
                        {
                            Log.e(TAG,"11124 hashval:"+listofnodes.get(a-1).hashnodeval);
                            String finalport=listofnodes.get(0).portnumber;
                            Log.e(TAG, "it will go into this port:"+finalport);
                            try {
                                String sendtothis=splittingfinal[0]+":"+finalport+":"+splittingfinal[2]+":insertfile";
                                sendtothisport(sendtothis);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if((listofnodes.get(0).hashnodeval.compareTo(hashofkeyval))>0)
                        {
                            String firstport=listofnodes.get(0).portnumber;
                            Log.e(TAG,"zero to condition will go to:"+ firstport);
                            try {
                                String sendtofirst=splittingfinal[0]+":"+firstport+":"+splittingfinal[2]+":insertfile";
                                sendtothisport(sendtofirst);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if(msg.contains("insertfile"))
                    {
                        FileOutputStream outputStream;
                        String[] split=msg.split(":");
                        Log.e(TAG, "opening file name at insert by server:" + split[0]);
                        outputStream = getContext().openFileOutput(split[0], Context.MODE_PRIVATE);
                        outputStream.write(split[2].getBytes());
                        outputStream.close();
                    }

                    if(msg.contains("fromquerydump"))
                    {
                        Log.e(TAG, "QUERYYYY:" + msg);
                        Log.e(TAG, "QUERY TIME:" + listofnodes.size());

                        String[] splittingfinal = msg.split(":");
                        String selectionhash = genHash(splittingfinal[0]);
                        for (int i = 0; i < listofnodes.size(); i++) {
                            if (((listofnodes.get(i).hashnodeval.compareTo(selectionhash)) >= 0) && ((selectionhash.compareTo(listofnodes.get(i).pred)) > 0)) {
                                String insideport=listofnodes.get(i).portnumber;
                                Log.e(TAG, "it is inside this port:" + insideport);
                                try {

                                    DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                                    out.writeUTF(insideport);
                                    out.flush();
                                    Log.e(TAG,"CLOUD:"+insideport);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        int a=listofnodes.size();
                        if(((selectionhash.compareTo(listofnodes.get(a-1).hashnodeval))>0))//||(("c25ddd596aa7c81fa12378fa725f706d54325d12".compareTo(hashofkeyval))>0))
                        {
                            Log.e(TAG,"11124 hashval:"+listofnodes.get(a-1).hashnodeval);
                            String finalport=listofnodes.get(0).portnumber;
                            Log.e(TAG, "it is inside this port:"+finalport);
                            try {

                                DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                                out.writeUTF(finalport);
                                out.flush();
                                Log.e(TAG,"CLOUD:"+finalport);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if((listofnodes.get(0).hashnodeval.compareTo(selectionhash))>0)
                        {
                            String firstport=listofnodes.get(0).portnumber;
                            Log.e(TAG,"it is inside this port:"+ firstport);
                            try {

                                DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                                out.writeUTF(firstport);
                                out.flush();
                                Log.e(TAG,"CLOUD:"+firstport);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if(msg.contains("dummy"))
                    {
                        try {
                            String ret="";
                            String[] sp = msg.split(":");
                            InputStream inputStream = getContext().openFileInput(sp[0]);
                            if (inputStream != null) {
                                inputStreamReader = new InputStreamReader(inputStream);
                                bufferedReader = new BufferedReader(inputStreamReader);
                                String receiveString = "";
                                StringBuilder stringBuilder = new StringBuilder();
                                while ((receiveString = bufferedReader.readLine()) != null) {
                                    stringBuilder.append(receiveString);
                                }
                                inputStream.close();
                                ret = stringBuilder.toString();
                                Log.e(TAG,"string builder:"+ret);
                            }

                            DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                            out.writeUTF(ret);
                            out.flush();
                            Log.e(TAG,"string builder return:"+ret);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    //socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"Exception 5:"+e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.e(TAG,"Exception 6:"+e.getMessage());
            }
            return null;
        }
    }

    private void sendtothisport(String sendtothis) {

        String[] ss=sendtothis.split(":");
        Log.e(TAG,"Sending to port:"+ss[1]);
        AsyncTask<String, Void, String> fileinsert = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, sendtothis, ss[1]);
    }


    private void broadcast() {

        Log.e(TAG,"Broadcast method:");
        String po="11108";
        AsyncTask<String, Void, String> keyinsert = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, "broadcast", po);
    }

    private class ClientTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... msgs) {
            Socket socket = null;
            String[] columns = new String[]{"key", "value"};
            String msggot;
            String insteadofarray = "";
            boolean flag = true;
            boolean flag2=true;
            Log.e(TAG, "C: ClientTask port values assigned:");
            if (msgs[0].equals("forquery")) {
                //for (int i = 0; i < remoteport.length; i++) {
                for(int i=0;i<listofnodes.size();i++){
                    try {
                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(listofnodes.get(i).portnumber.toString()));
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("*");
                        Log.e(TAG, "C: ClientTask msgsent for query");
                        out.flush();
                        DataInputStream br = new DataInputStream(socket.getInputStream());
                        Log.e(TAG, "SOCKET STATUS " + socket.isConnected());
                        msggot = br.readUTF();
                        Log.e(TAG, "msggot:" + msggot);
                        if (flag == true) {
                            insteadofarray = msggot;
                            flag = false;
                        } else {
                            insteadofarray = insteadofarray + "@" + msggot;
                        }
                        socket.close();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Exception 7:"+e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Exception 8:"+e.getMessage());
                    }
                }
            }
            if(msgs[0].equals("delete"))
            {
                for (int i = 0; i < remoteport.length; i++) {
                    try {
                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(remoteport[i].toString()));
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("delete");
                        Log.e(TAG, "C: ClientTask msgsent for delete");
                        out.flush();
                        insteadofarray="deleted successfully";
                        socket.close();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Exception 9:"+e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Exception 10:"+e.getMessage());
                    }
                }
            }
            if((msgs[0].equals("11112")) || (msgs[0].equals("11116")) || (msgs[0].equals("11120")) || (msgs[0].equals("11124")))
            {
                try {
                    String avd0="11108";
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(avd0.toString()));
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(msgs[0]);
                    Log.e(TAG, "C: Join request sent from client:"+msgs[0]);
                    out.flush();
                    insteadofarray=msgs[0];
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception 11:"+e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception 12:"+e.getMessage());
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.e(TAG,"this should print:"+listofnodes.size());

            Log.e(TAG,"this didnt print");
            //if(listofnodes.size()>1) {
            if(msgs[0].equals("broadcast")){



            /*
                for (int i = 0; i < listofnodes.size(); i++) {
                    if (flag2 == true) {
                        entirearraylist = "entirearraylist@" + listofnodes.get(i).pred + ":" + listofnodes.get(i).succ + ":" + listofnodes.get(i).hashnodeval + ":" + listofnodes.get(i).portnumber;
                        flag2 = false;
                    } else {
                        entirearraylist = entirearraylist + "/" + listofnodes.get(i).pred + ":" + listofnodes.get(i).succ + ":" + listofnodes.get(i).hashnodeval + ":" + listofnodes.get(i).portnumber;
                    }
                }*/


                Log.e(TAG, "PLEASE PRINT:" + entirearraylist);

                for (int i = 0; i < listofnodes.size(); i++) {
                    try {
                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(listofnodes.get(i).portnumber.toString()));
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        entirearraylist=listofnodes.get(i).pred+":entirearraylist:"+listofnodes.get(i).succ+":"+listofnodes.get(i).predoriginal+":"+listofnodes.get(i).succoriginal;
                        out.writeUTF(entirearraylist);
                        Log.e(TAG, "C: entirearraylist sent to:"+listofnodes.get(i).portnumber);
                        Log.e(TAG, "C: entirearraylist pred succ val:"+listofnodes.get(i).predoriginal+":"+listofnodes.get(i).succoriginal);
                        out.flush();
                        insteadofarray=entirearraylist;
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Exception 13:"+e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Exception 14:"+e.getMessage());
                    }
                }
            }


            if(msgs[0].contains("toinsert:"))
            {
                try {
                    String[] splitting=msgs[0].split(":");
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(splitting[1].toString()));
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(msgs[0]);
                    Log.e(TAG, "C: key insertion request:"+msgs[0]);
                    out.flush();
                    insteadofarray=msgs[0];
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception 15:"+e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception 16:"+e.getMessage());
                }
            }

            if(msgs[0].contains(":otheravdrequest:"))
            {
                Log.e(TAG,"it came here:"+msgs[0]+":"+msgs[1]+":"+listofnodes.size());
                String[] otheravdsval=msgs[0].split(":");
                String hashofkey= null;
                try {
                    hashofkey = genHash(otheravdsval[0]);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception 55:"+e.getMessage());
                }
                for (int i = 0; i < listofnodes.size(); i++) {
                    Log.e(TAG,"it came here also:");
                    if (((listofnodes.get(i).hashnodeval.compareTo(hashofkey)) >= 0) &&((hashofkey.compareTo(listofnodes.get(i).pred))>0)) {
                        Log.e(TAG,"it will go into this port:"+listofnodes.get(i).portnumber);
                        try {
                            socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                    Integer.parseInt(listofnodes.get(i).portnumber.toString()));
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            out.writeUTF(otheravdsval[0]+":otheravdrequest:"+otheravdsval[2]);
                            Log.e(TAG, "C: key insertion request:"+msgs[0]);
                            out.flush();

                        }  catch (UnknownHostException e) {
                            e.printStackTrace();
                            Log.e(TAG,"Exception 26:"+e.getMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG,"Exception 27:"+e.getMessage());
                        }

                    }
                }



            }

            if(msgs[0].contains("pleasecheck"))
            {
                String por="11108";
                Log.e(TAG,"it came here 2:"+msgs[0]+":"+msgs[1]+":"+listofnodes.size());
                try {
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(por.toString()));
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    String tosend=msgs[0];
                    out.writeUTF(tosend);
                    Log.e(TAG, "C: key insertion request:"+msgs[0]);
                    out.flush();

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(msgs[0].contains("insertfile"))
            {
                FileOutputStream outputStream;
                String[] split=msgs[0].split(":");
                Log.e(TAG, "got file name at clientside:" + split[1]);
                try {
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(split[1].toString()));
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    String tosend=msgs[0];
                    out.writeUTF(tosend);
                    Log.e(TAG, "C: key insertion request 2:"+msgs[0]);
                    out.flush();

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(msgs[0].contains("fromquerydump"))
            {
                try {
                    insteadofarray = "";
                    String avd0="11108";
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(avd0.toString()));
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    String tosend=msgs[0];
                    out.writeUTF(tosend);
                    Log.e(TAG, "C: for query to avd0:"+msgs[0]);
                    out.flush();

                    DataInputStream br = new DataInputStream(socket.getInputStream());
                    String msgrec=br.readUTF();
                    Log.e(TAG, "GOT CLOUD: " + msgrec);
                    //insteadofarray=msgrec;
                    Log.e(TAG,"INSTEAD of array:"+insteadofarray);
                    socket.close();
                    return msgrec;

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(msgs[0].contains("dummy")){
                try {
                    String[] sp=msgs[0].split(":");
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(sp[1].toString()));
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    String tosend = msgs[0];
                    out.writeUTF(tosend);
                    Log.e(TAG, "dummy client came:" + msgs[0]);
                    out.flush();


                    DataInputStream br = new DataInputStream(socket.getInputStream());
                    String ms = br.readUTF();
                    Log.e(TAG, "STRING BUILDER READ" + ms);

                    socket.close();
                    return ms;

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return insteadofarray;
        }


    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // TODO Auto-generated method stub
        String ret="";
        String[] columns=new String[]{"key","value"};
        MatrixCursor cursor=new MatrixCursor(columns);
        if(selection.equals("@"))
        {
            String[] filelist=getContext().fileList();
            for(String file:filelist)
            {
                try {
                    InputStream inputStream = getContext().openFileInput(file);
                    if (inputStream != null) {
                        inputStreamReader = new InputStreamReader(inputStream);
                        bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((receiveString = bufferedReader.readLine()) != null) {
                            stringBuilder.append(receiveString);
                        }
                        inputStream.close();
                        ret = stringBuilder.toString();
                        //matrix building
                        cursor.newRow().add("key", file).add("value", ret);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception 17:"+e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception 18:"+e.getMessage());
                }
            }
            return cursor;
        }
        if(selection.equals("*"))
        {
            if ((predecessor.equals("")) && (successor.equals(""))){
                String[] filelist=getContext().fileList();
                for(String file:filelist)
                {
                    try {
                        InputStream inputStream = getContext().openFileInput(file);
                        if (inputStream != null) {
                            inputStreamReader = new InputStreamReader(inputStream);
                            bufferedReader = new BufferedReader(inputStreamReader);
                            String receiveString = "";
                            StringBuilder stringBuilder = new StringBuilder();
                            while ((receiveString = bufferedReader.readLine()) != null) {
                                stringBuilder.append(receiveString);
                            }
                            inputStream.close();
                            ret = stringBuilder.toString();
                            //matrix building
                            cursor.newRow().add("key", file).add("value", ret);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Exception 17:"+e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG,"Exception 18:"+e.getMessage());
                    }
                }
                return cursor;

            }

            else{
                Socket socket = null;
                String wholestring="";
                String toquery="fromavd0";
                String avd0="11108";
                String starval="*";
                Boolean flagvalue=true;
                String togrand="";
                try {
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(avd0.toString()));
                    DataOutputStream out=new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(toquery);
                    out.flush();
                    Log.e(TAG,"socket connected success:"+toquery);

                    DataInputStream br=new DataInputStream(socket.getInputStream());
                    String msg=br.readUTF();
                    Log.e(TAG,"data read:"+msg);
                    String[] spl=msg.split(":");
                    for(int i=0;i<spl.length;i++)
                    {
                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(spl[i].toString()));
                        DataOutputStream out1=new DataOutputStream(socket.getOutputStream());
                        out1.writeUTF(starval);
                        out1.flush();
                        Log.e(TAG,"socket connected success 222:"+starval);

                        DataInputStream br1=new DataInputStream(socket.getInputStream());
                        String msg1=br1.readUTF();
                        Log.e(TAG,"data read 1:"+msg1);
                        if(flagvalue==true){
                            togrand=msg1;
                            flagvalue=false;
                        }
                        else {
                            togrand=togrand+"@"+msg1;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                // AsyncTask<String, Void, String> trial=new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, toquery, avd0);
                try {
                    Log.e(TAG,"GRAND SUCCESS--->"+togrand);
                    wholestring=togrand;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"Exception 19:"+e.getMessage());
                }
                String[] readingconcat = wholestring.split("@");
                Log.e(TAG,"LENGTH1:"+readingconcat.length);
                for(int x=0;x<readingconcat.length;x++)
                {
                    String[] readingconcathalf=readingconcat[x].split("keerthana");
                    Log.e(TAG,"LENGTH:"+readingconcathalf.length);
                    String allvalues=readingconcathalf[0];
                    String allkeys=readingconcathalf[1];
                    String[] allvaluesarray=allvalues.split(":");
                    String[] allkeysarray=allkeys.split(":");
                    for(int i=0;i<allvaluesarray.length;i++)
                    {
                        if(((!allkeysarray[i].equals(""))&&(!allvaluesarray[i].equals("")))&&((!allkeysarray[i].equals("algebra"))&&(!allvaluesarray[i].equals("algebra")))) {
                            cursor.newRow().add("key", allkeysarray[i]).add("value", allvaluesarray[i]);
                            Log.e(TAG, "KEY VAL:" + allkeysarray[i] + " VALUE VAL:" + allvaluesarray[i]);
                        }
                    }
                }
                return cursor;
            }


            /*
            String wholestring="";
            String toquery="forquery";
            AsyncTask<String, Void, String> trial=new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, toquery, myPortNumber);
            try {
                Log.e(TAG,"GRAND SUCCESS--->"+trial.get());
                wholestring=trial.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG,"Exception 19:"+e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.e(TAG,"Exception 20:"+e.getMessage());
            }
            String[] readingconcat = wholestring.split("@");
            Log.e(TAG,"LENGTH1:"+readingconcat.length);
            for(int x=0;x<readingconcat.length;x++)
            {
                String[] readingconcathalf=readingconcat[x].split("/");
                Log.e(TAG,"LENGTH:"+readingconcathalf.length);
                String allvalues=readingconcathalf[0];
                String allkeys=readingconcathalf[1];
                String[] allvaluesarray=allvalues.split(":");
                String[] allkeysarray=allkeys.split(":");
                for(int i=0;i<allvaluesarray.length;i++)
                {
                    cursor.newRow().add("key", allkeysarray[i]).add("value", allvaluesarray[i]);
                    Log.e(TAG,"KEY VAL:"+allkeysarray[i]+" VALUE VAL:"+allvaluesarray[i]);
                }
            }
            return cursor;*/
        }
        if(!(selection.equals("@"))&&(!(selection.equals("*")))) {
            try {
                if ((predecessor.equals("")) && (successor.equals("")))
                {
                    InputStream inputStream = getContext().openFileInput(selection);
                    if (inputStream != null) {
                        inputStreamReader = new InputStreamReader(inputStream);
                        bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((receiveString = bufferedReader.readLine()) != null) {
                            stringBuilder.append(receiveString);
                        }
                        inputStream.close();
                        ret = stringBuilder.toString();
                        //matrix building
                        cursor.newRow().add("key", selection).add("value", ret);
                        return cursor;
                    }
                }
                else
                {
                    String avd0="11108";
                    String selectionwithdummy=selection+":"+"fromquerydump";
                    AsyncTask<String, Void, String> for_query_notnull=new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, selectionwithdummy, avd0);
                    String gotquery=for_query_notnull.get();
                    Log.e(TAG,"CLOUD AT QUERY:"+gotquery);

                    String dummyvariable=selection+":"+gotquery+":dummy";
                    AsyncTask<String, Void, String> requesting=new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, dummyvariable, gotquery);
                    String gotreplyrequest=requesting.get();
                    Log.e(TAG,"GOT REPLY REQ:"+gotreplyrequest);

                    cursor.newRow().add("key", selection).add("value", gotreplyrequest);
                    return cursor;
/*
                    InputStream inputStream = getContext().openFileInput(selection);
                    if (inputStream != null) {
                        inputStreamReader = new InputStreamReader(inputStream);
                        bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((receiveString = bufferedReader.readLine()) != null) {
                            stringBuilder.append(receiveString);
                        }
                        inputStream.close();
                        ret = stringBuilder.toString();
                        //matrix building
                        cursor.newRow().add("key", selection).add("value", ret);
                        return cursor;
                    }*/
                }
            }catch (FileNotFoundException e) {
                Log.e(TAG, "File could not be opened");
            } catch (IOException e) {
                Log.e(TAG, "cannot read file");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Log.v("query (not done)", selection);
        return null;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }
    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}