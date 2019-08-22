package edu.buffalo.cse.cse486586.simpledynamo;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
public class SimpleDynamoProvider extends ContentProvider {
	static final String TAG = SimpleDynamoProvider.class.getSimpleName();
	static final int SERVER_PORT = 10000;
	static String myPortNumber = "";
	static String myport="";
	Boolean lastatt=false;
	String portNum="";
	String failedavd="";
	InputStreamReader inputStreamReader=null;
	BufferedReader bufferedReader=null;
	static ArrayList<nodeID> listofnodes=new ArrayList<nodeID>();
	String[] remoteport = new String[]{"11108", "11112", "11116", "11120", "11124"};
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
		if(lastatt==false) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
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

			//FIND MY 2 SUCCESSORS
			String s1="";
			String s2="";
			for(int i=0;i<listofnodes.size();i++)
			{
				if(listofnodes.get(i).portnumber.equals(myPortNumber)){
					s1=listofnodes.get(i).succoriginal;
					Log.e(TAG,"my Successor found val1 is:"+s1);
				}
			}
			for(int i=0;i<listofnodes.size();i++)
			{
				if(listofnodes.get(i).portnumber.equals(s1)){
					s2=listofnodes.get(i).succoriginal;
					Log.e(TAG,"my Successor found val2 is:"+s2);
				}
			}

			//delete my replicas from successors
			try {
				socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
						Integer.parseInt(s1));
				socket.setSoTimeout(2000);
				portNum = s1;
				DataOutputStream out=new DataOutputStream(socket.getOutputStream());
				String deletefile=selection+":"+"deletefile";
				out.writeUTF(deletefile);
				out.flush();
			} catch (NullPointerException e) {
				Log.e(TAG, "Caught null pointer exception 2:" + e.getMessage());
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			} catch (SocketTimeoutException se) {
				Log.e(TAG, "IO Exception 1:" + se);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			} catch (StreamCorruptedException e) {
				Log.e(TAG, "IO Exception 2:" + e);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "IO Exception 3:" + e);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			} catch (EOFException e) {
				Log.e(TAG, "IO Exception 4:" + e);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			}catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "exception 3:"+e.getMessage());
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			}catch (Exception e) {
				Log.e(TAG, "IO Exception 5:" + e);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			}

			try {
				socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
						Integer.parseInt(s2));
				socket.setSoTimeout(2000);
				portNum = s2;
				DataOutputStream out=new DataOutputStream(socket.getOutputStream());
				String deletefile=selection+":"+"deletefile";
				out.writeUTF(deletefile);
				out.flush();
			} catch (NullPointerException e) {
				Log.e(TAG, "Caught null pointer exception 2:" + e.getMessage());
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			} catch (SocketTimeoutException se) {
				Log.e(TAG, "IO Exception 1:" + se);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			} catch (StreamCorruptedException e) {
				Log.e(TAG, "IO Exception 2:" + e);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			} catch (FileNotFoundException e) {
				Log.e(TAG, "IO Exception 3:" + e);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			} catch (EOFException e) {
				Log.e(TAG, "IO Exception 4:" + e);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			}catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "exception 3:"+e.getMessage());
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
			}catch (Exception e) {
				Log.e(TAG, "IO Exception 5:" + e);
				failedavd = portNum;
				Log.e(TAG,"FAILED PORT NUM:"+failedavd);
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
		if(lastatt==false) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		String keyformcontentval=(String)values.get("key");
		String valfromcontentval=(String)values.get("value");
		String hashedkey= null;
		String rep="";
		try {
			hashedkey = genHash(keyformcontentval);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < listofnodes.size(); i++) {
			if (((listofnodes.get(i).hashnodeval.compareTo(hashedkey)) >= 0) && ((hashedkey.compareTo(listofnodes.get(i).pred)) > 0)) {
				Log.e(TAG, "it will go into this port:" + listofnodes.get(i).portnumber);
				try {
					String tosend=listofnodes.get(i).portnumber+":insertion:"+keyformcontentval+":"+valfromcontentval;
					AsyncTask<String, Void, String> fileinsert = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, tosend, valfromcontentval);
					rep=tosend;
					Log.e(TAG,"reply got"+fileinsert.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		int a=listofnodes.size();
		if(((hashedkey.compareTo(listofnodes.get(a-1).hashnodeval))>0))
		{
			Log.e(TAG,"11124 hashval:"+listofnodes.get(a-1).hashnodeval);
			String finalport=listofnodes.get(0).portnumber;
			Log.e(TAG, "it will go into this port:"+finalport);
			try {
				String tosend=listofnodes.get(0).portnumber+":insertion:"+keyformcontentval+":"+valfromcontentval;
				AsyncTask<String, Void, String> fileinsert = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, tosend, valfromcontentval);
				Log.e(TAG,"reply got"+fileinsert.get());
				rep=tosend;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if((listofnodes.get(0).hashnodeval.compareTo(hashedkey))>0)
		{
			String firstport=listofnodes.get(0).portnumber;
			Log.e(TAG,"it will go into this port:"+ firstport);
			try {
				String tosend=listofnodes.get(0).portnumber+":insertion:"+keyformcontentval+":"+valfromcontentval;
				AsyncTask<String, Void, String> fileinsert = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, tosend, valfromcontentval);
				Log.e(TAG,"reply got"+fileinsert.get());
				rep=tosend;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//******REPLICATION*******
		replication(rep);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		TelephonyManager tel = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		myport = String.valueOf((Integer.parseInt(portStr) * 2));
		myPortNumber = myport;
		try {
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
		} catch (IOException e) {
			Log.e(TAG, " EXCEPTION -- Can't create a ServerSocket:"+e.getMessage());
			e.printStackTrace();
		}
		//INSERTION INTO ARRAYLIST (ENTIRE PORT LIST) STARTS HERE
		for(int x=0;x<remoteport.length;x++)
		{
			String hashedportval="";
			int tohash=Integer.parseInt(remoteport[x])/2;
			try {
				hashedportval = genHash(Integer.toString(tohash));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			nodeID nodeobj=new nodeID(null,null, hashedportval, remoteport[x], null, null);
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
		}


		//check if dummy file is there
		String[] filelist=getContext().fileList();
		String yesdummy="";
		for(String file:filelist)
		{
			if(file.contains("dummy"))
			{
				yesdummy="yesdummy";
				Log.e(TAG,"file has dummy");
			}
		}

		//yes dummy file is there which means failed node
		if(yesdummy.contains("yesdummy"))
		{
			Log.e(TAG,"yes dummy!!! -- FAILED node");

			//DELETE all the files in failed node
//			String[] filelist1=getContext().fileList();
//			Log.e(TAG,"deleting:"+filelist1.length);
//			for(String file:filelist1) {
//				if(!file.contains("dummy")) {
//					getContext().deleteFile(file);
//				}
//			}

			//FIND MY 2 SUCCESSORS and 2 PREDECESSOR
			String s1=""; //succ 1
			String s2=""; //pred 1
			String s3=""; //pred 2
			String s4=""; //succ 2
			for(int i=0;i<listofnodes.size();i++)
			{
				if(listofnodes.get(i).portnumber.equals(myPortNumber)){
					s1=listofnodes.get(i).succoriginal;
					s2=listofnodes.get(i).predoriginal;
					Log.e(TAG,"my Successor1 found val d1 is:"+s1);
					Log.e(TAG,"my predecessor1 found val d1 is:"+s2);
				}
			}
			for(int i=0;i<listofnodes.size();i++)
			{
				if(listofnodes.get(i).portnumber.equals(s2)){
					s3=listofnodes.get(i).predoriginal;
					Log.e(TAG,"my predecessor2 found val d1 is:"+s3);
				}
			}
			for(int i=0;i<listofnodes.size();i++)
			{
				if(listofnodes.get(i).portnumber.equals(s1)){
					s4=listofnodes.get(i).succoriginal;
					Log.e(TAG,"my Successor2 found val d1 is:"+s4);
				}
			}


			//get the contents
			String succdata=successordata(myPortNumber,s1);

			Log.e(TAG,"SUCCDATA:"+succdata);


			//split the contents and store in file
			FileOutputStream outputStream;
			if(succdata.length()>10)
			{
				Log.e(TAG,"succdata has some value:"+succdata);
				String[] splitval = succdata.split("failed");
				String listofval=splitval[0];
				String listofkey=splitval[1];
				String[] allvaluesarray=listofval.split("@");
				String[] allkeysarray=listofkey.split("@");
				for(int i=0;i<allvaluesarray.length;i++)
				{
					Log.e(TAG, "KEY VAL:" + allkeysarray[i] + " VALUE VAL:" + allvaluesarray[i]);
					try {
						outputStream = getContext().openFileOutput(allkeysarray[i], Context.MODE_PRIVATE);
						outputStream.write(allvaluesarray[i].getBytes());
						outputStream.close();
						Log.e(TAG,"written into file");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			//#################
			String succdata4=successordata(myPortNumber,s4);

			Log.e(TAG,"SUCCDATA:"+succdata4);

			if(succdata4.length()>10)
			{
				Log.e(TAG,"succdata has some value:"+succdata4);
				String[] splitval = succdata4.split("failed");
				String listofval=splitval[0];
				String listofkey=splitval[1];
				String[] allvaluesarray=listofval.split("@");
				String[] allkeysarray=listofkey.split("@");
				for(int i=0;i<allvaluesarray.length;i++)
				{
					Log.e(TAG, "KEY VAL:" + allkeysarray[i] + " VALUE VAL:" + allvaluesarray[i]);
					try {
						outputStream = getContext().openFileOutput(allkeysarray[i], Context.MODE_PRIVATE);
						outputStream.write(allvaluesarray[i].getBytes());
						outputStream.close();
						Log.e(TAG,"written into file");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			//#################



			//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
			//get the contents
			String succdata1=successordata(s2,s2);

			Log.e(TAG,"SUCCDATA:"+succdata);


			//split the contents and store in file
			if(succdata1.length()>10)
			{
				Log.e(TAG,"succdata has some value:"+succdata1);
				String[] splitval = succdata1.split("failed");
				String listofval=splitval[0];
				String listofkey=splitval[1];
				String[] allvaluesarray=listofval.split("@");
				String[] allkeysarray=listofkey.split("@");
				for(int i=0;i<allvaluesarray.length;i++)
				{
					Log.e(TAG, "KEY VAL:" + allkeysarray[i] + " VALUE VAL:" + allvaluesarray[i]);
					try {
						outputStream = getContext().openFileOutput(allkeysarray[i], Context.MODE_PRIVATE);
						outputStream.write(allvaluesarray[i].getBytes());
						outputStream.close();
						Log.e(TAG,"written into file");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$


			//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
			//get the contents
			String succdata2=successordata(s3,s3);

			Log.e(TAG,"SUCCDATA:"+succdata);


			//split the contents and store in file
			if(succdata2.length()>10)
			{
				Log.e(TAG,"succdata has some value:"+succdata2);
				String[] splitval = succdata2.split("failed");
				String listofval=splitval[0];
				String listofkey=splitval[1];
				String[] allvaluesarray=listofval.split("@");
				String[] allkeysarray=listofkey.split("@");
				for(int i=0;i<allvaluesarray.length;i++)
				{
					Log.e(TAG, "KEY VAL:" + allkeysarray[i] + " VALUE VAL:" + allvaluesarray[i]);
					try {
						outputStream = getContext().openFileOutput(allkeysarray[i], Context.MODE_PRIVATE);
						outputStream.write(allvaluesarray[i].getBytes());
						outputStream.close();
						Log.e(TAG,"written into file");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$


		}
		else
		{
			Log.e(TAG,"file has no dummy");
		}
		lastatt=true;

		//if there is no dummy file. which means first run then insert dummy file
		if(!yesdummy.contains("yesdummy")) {
			FileOutputStream outputStream;
			Log.e(TAG, "opening dummy");
			String dummy = "dummy";
			try {
				outputStream = getContext().openFileOutput(dummy, Context.MODE_PRIVATE);
				outputStream.write(dummy.getBytes());
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}






		return false;
	}


	private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
		@Override
		protected Void doInBackground(ServerSocket... sockets) {
			ServerSocket serverSocket = sockets[0];
			Socket socket = null;
			String temp="";
			String nameoffile="";
			Boolean flag=true;
			Boolean failflag=true;
			try {
				while (true) {
					socket = serverSocket.accept();
					DataInputStream br=new DataInputStream(socket.getInputStream());
					String msg=br.readUTF();
					//msg=portnum:insertion:key:val
					Log.e(TAG,"message received-->"+msg);
					if(msg.contains("deletefile"))
					{
						String[] filelist=getContext().fileList();
						Log.e(TAG,"delete 1:"+filelist.length);
						for(String file:filelist) {
							if(!file.contains("dummy")) {
								getContext().deleteFile(file);
							}
						}
					}
					if(msg.contains("insertion"))
					{
						if(lastatt==false)
							Thread.sleep(1000);

						FileOutputStream outputStream;
						String[] split=msg.split(":");
						String genhashing=genHash(split[2]);
						String retval=checkexists(genhashing);

						if(retval.contains("yes")){
							//retval=yes:version
							String[] rt=msg.split(":");
							int version=Integer.parseInt(rt[1]);
							version=version+1;
							Log.e(TAG, "opening file name at insert by server2:" + split[2]);
							outputStream = getContext().openFileOutput(split[2], Context.MODE_PRIVATE);
							String towrite = split[3] + ":"+version;
							outputStream.write(towrite.getBytes());
							outputStream.close();
						}
						else {
							Log.e(TAG, "opening file name at insert by server:" + split[2]);
							outputStream = getContext().openFileOutput(split[2], Context.MODE_PRIVATE);
							String towrite = split[3] + ":1";
							outputStream.write(towrite.getBytes());
							outputStream.close();
						}
						Log.e(TAG," going to closing socket 1");
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						out.writeUTF("ack");
						Log.e(TAG, "ack sent");
						out.flush();
					}
					if(msg.contains("query"))
					{
						if(lastatt==false)
							Thread.sleep(1000);
						Log.e(TAG,"S: entered query at server--"+msg);
						//msg=key:portnum:query
						String[] spl=msg.split(":");
						//spl[0]=key  spl[1]=portnum
						String ret="";
						InputStream inputStream = getContext().openFileInput(spl[0]);
						if (inputStream != null) {
							inputStreamReader = new InputStreamReader(inputStream);
							bufferedReader = new BufferedReader(inputStreamReader);
							String receiveString = "";
							StringBuilder stringBuilder = new StringBuilder();
							while ((receiveString = bufferedReader.readLine()) != null) {
								Log.e(TAG,"S: value val:"+receiveString);
								stringBuilder.append(receiveString);
							}
							inputStream.close();
							ret = stringBuilder.toString();
							Log.e(TAG,"S: string builder:"+ret);
						}
						DataOutputStream out=new DataOutputStream(socket.getOutputStream());
						out.writeUTF(ret);
						out.flush();
						Log.e(TAG,"S: string builder return:"+ret);
					}
					if(msg.contains("*"))
					{
						Log.e(TAG, "S: GOT FIRST SERVER SIDE MESSAGE ");
						String[] filelist=getContext().fileList();
						int fff=filelist.length;
						Log.e(TAG,"NUMBER of files: "+fff);
						for(String file:filelist)
						{
							if(!file.contains("dummy")) {
								try {
									InputStream inputStream = getContext().openFileInput(file);
									if (inputStream != null) {
										Log.e(TAG, "COUNT1");
										inputStreamReader = new InputStreamReader(inputStream);
										bufferedReader = new BufferedReader(inputStreamReader);
										String receiveString = "";
										if ((receiveString = bufferedReader.readLine()) != null) {
											String[] splitval = receiveString.split(":");
											Log.e(TAG, "Received String ---> " + splitval[0]);
											if (flag == true) {
												temp = splitval[0];
												nameoffile = file;
												flag = false;
											} else {
												temp = temp + ":" + splitval[0];
												nameoffile = nameoffile + ":" + file;
											}
											Log.e(TAG, "INside Loop ---> " + temp + " file name ---> " + nameoffile);
										}
										inputStream.close();
									}
								} catch (FileNotFoundException e) {
									e.printStackTrace();
									Log.e(TAG, "Exception 3:" + e.getMessage());
								} catch (IOException e) {
									e.printStackTrace();
									Log.e(TAG, "Exception 4:" + e.getMessage());
								}
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

					if(msg.contains("givefile"))
					{
						String valtobesent="";
						String filetobesent="";
						//msg=portnum:s1:givefile
						Log.e(TAG,"S: give file-->"+msg);
						String[] splitval = msg.split(":");

						//find files that belong to that failed node


						//iterate over files and find where it belongs
						String[] filelist=getContext().fileList();
						for(String file:filelist)
						{
							String hashedkey=genHash(file);
							String itis="";

							for (int i = 0; i < listofnodes.size(); i++) {
								if (((listofnodes.get(i).hashnodeval.compareTo(hashedkey)) >= 0) && ((hashedkey.compareTo(listofnodes.get(i).pred)) > 0)) {
									itis=listofnodes.get(i).portnumber;
									Log.e(TAG, "it is in:" + itis);
								}
							}
							int a=listofnodes.size();
							if(((hashedkey.compareTo(listofnodes.get(a-1).hashnodeval))>0))
							{
								Log.e(TAG,"11124 hashval:"+listofnodes.get(a-1).hashnodeval);
								String finalport=listofnodes.get(0).portnumber;
								Log.e(TAG, "it is in:"+finalport);
								itis=finalport;
							}
							if((listofnodes.get(0).hashnodeval.compareTo(hashedkey))>0)
							{
								String firstport=listofnodes.get(0).portnumber;
								Log.e(TAG,"it is in:"+ firstport);
								itis=firstport;
							}

							//if it falls in failednode
							String ret="";
							String receiveString = "";
							if(itis.contains(splitval[0]))
							{
								Log.e(TAG,"it falls in failed node:"+itis);
								InputStream inputStream = getContext().openFileInput(file);
								if (inputStream != null) {
									inputStreamReader = new InputStreamReader(inputStream);
									bufferedReader = new BufferedReader(inputStreamReader);
									//StringBuilder stringBuilder = new StringBuilder();
									while ((receiveString = bufferedReader.readLine()) != null) {
										Log.e(TAG,"value val:"+receiveString);
										if(failflag==true)
										{
											valtobesent = receiveString;
											filetobesent = file;
											failflag=false;
										}
										else {
											valtobesent = valtobesent + "@" + receiveString;
											filetobesent = filetobesent + "@" + file;
										}
										//stringBuilder.append(receiveString);
										Log.e(TAG,"VALUE-->"+valtobesent+"----"+"FILE-->"+filetobesent);
									}
									inputStream.close();
									//ret = stringBuilder.toString();
									//Log.e(TAG,"string builder:"+ret);
								}

							}

							else
							{
								Log.e(TAG,"it doesnt fall in failed node-->"+itis);
							}
						}

						String finaltosend="";

						if((!(valtobesent ==""))&&(!(filetobesent ==""))) {
							finaltosend = valtobesent + "failed" + filetobesent;
							Log.e(TAG, "final val to be sent---" + finaltosend);
						}

						DataOutputStream out=new DataOutputStream(socket.getOutputStream());
						out.writeUTF(finaltosend);
						out.flush();
						Log.e(TAG, "S: give file sent--"+filetobesent);

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "exception 4:"+e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				Log.e(TAG, "exception 5:"+e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	private class ClientTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... msgs) {
			Socket socket = null;
			//msg[0]=portnum:insertion:key:val
			if(msgs[0].contains("insertion")){
				String[] split=msgs[0].split(":");
				Log.e(TAG,"split val:"+split[0]);
				try {
					socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
							Integer.parseInt(split[0]));
					socket.setSoTimeout(2000);
					portNum = split[0];
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					String tosend=msgs[0];
					out.writeUTF(tosend);
					Log.e(TAG, "C: sent for insertion:"+tosend);
					out.flush();
					DataInputStream br=new DataInputStream(socket.getInputStream());
					String ack=br.readUTF();
					if(ack.contains("ack"))
					{
						socket.close();
					}
				}  catch (NullPointerException e) {
					Log.e(TAG, "Caught null pointer exception 2:" + e.getMessage());
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM:"+failedavd);
				} catch (SocketTimeoutException se) {
					Log.e(TAG, "IO Exception 1:" + se);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM:"+failedavd);
				} catch (StreamCorruptedException e) {
					Log.e(TAG, "IO Exception 2:" + e);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM:"+failedavd);
				} catch (FileNotFoundException e) {
					Log.e(TAG, "IO Exception 3:" + e);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM:"+failedavd);
				} catch (EOFException e) {
					Log.e(TAG, "IO Exception 4:" + e);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM:"+failedavd);
				}catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "exception 3:"+e.getMessage());
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM:"+failedavd);
				}catch (Exception e) {
					Log.e(TAG, "IO Exception 5:" + e);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM:"+failedavd);
				}

			}
			if(msgs[0].contains("query"))
			{
				//msg[0]=query:key
				//msg[1]=portnum
				String[] split=msgs[0].split(":");
				Log.e(TAG,"C: splitting received:"+split[1]);
				//split[0]=query   split[1]=key
				try {
					socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
							Integer.parseInt(msgs[1]));
					socket.setSoTimeout(2000);
					portNum = msgs[1];
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					String tosend=split[1]+":"+msgs[1]+":query";
					out.writeUTF(tosend);
					Log.e(TAG, "C: sent for query:"+tosend);
					out.flush();
					DataInputStream br=new DataInputStream(socket.getInputStream());
					String ack=br.readUTF();
					Log.e(TAG, "C: got reply query:"+ack);
					socket.close();
					return ack;
				}catch (NullPointerException e) {
					Log.e(TAG, "Caught null pointer exception 12:" + e.getMessage());
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM 1:"+failedavd);
				} catch (SocketTimeoutException se) {
					Log.e(TAG, "IO Exception 11:" + se);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM 1:"+failedavd);
				} catch (StreamCorruptedException e) {
					Log.e(TAG, "IO Exception 12:" + e);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM 1:"+failedavd);
				} catch (FileNotFoundException e) {
					Log.e(TAG, "IO Exception 13:" + e);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM 1:"+failedavd);
				} catch (EOFException e) {
					Log.e(TAG, "IO Exception 14:" + e);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM 1:"+failedavd);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "exception 13:"+e.getMessage());
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM 1:"+failedavd);
				}catch (Exception e) {
					Log.e(TAG, "IO Exception 15:" + e);
					failedavd = portNum;
					Log.e(TAG,"FAILED PORT NUM 1:"+failedavd);
				}

			}

			if(msgs[0].contains("givefile"))
			{
				//msg[0]=myportnum:s1:"givefile"    msg[1]=s1
				Log.e(TAG,"client: give file-->"+msgs[0]);
				try {
					socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
							Integer.parseInt(msgs[1]));
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF(msgs[0]);
					Log.e(TAG, "C: sent for give file-->"+msgs[0]);
					out.flush();

					DataInputStream br=new DataInputStream(socket.getInputStream());
					String ack=br.readUTF();
					Log.e(TAG, "C: got in client:"+ack);
					socket.close();
					return ack;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			return null;
		}
	}
	private String checkexists(String genhashing) {
		String toret="";
		String val="";
		try {
			InputStream inputStream = getContext().openFileInput(genhashing);
			if (inputStream != null) {
				inputStreamReader = new InputStreamReader(inputStream);
				bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();
				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}
				inputStream.close();
				val = stringBuilder.toString();
				String[] spl=val.split(":");
				Log.e(TAG,"the value stored is:"+val);
				toret="yes:"+spl[1];
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG,"the value not stored:"+val);
			toret="no";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return toret;
	}
	private void replication(String s) {
		String psucc="";
		String ppsucc="";
		Socket socket = null;
		String[] split=s.split(":");
		Log.e(TAG,"Successor val0 is:"+split[0]);
		for(int i=0;i<listofnodes.size();i++)
		{
			if(listofnodes.get(i).portnumber.equals(split[0])){
				psucc=listofnodes.get(i).succoriginal;
				Log.e(TAG,"Successor val1 is:"+psucc);
				String msgtosend=psucc+":insertion:"+split[2]+":"+split[3];
				Log.e(TAG,"msg to send rep 1:"+msgtosend);
				AsyncTask<String, Void, String> rep = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msgtosend, msgtosend);
				try {
					Log.e(TAG,"replication 1 done:"+rep.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		for(int i=0;i<listofnodes.size();i++)
		{
			if(listofnodes.get(i).portnumber.equals(psucc)){
				ppsucc=listofnodes.get(i).succoriginal;
				Log.e(TAG,"Successor val2 is:"+ppsucc);
				String msgtosend=ppsucc+":insertion:"+split[2]+":"+split[3];
				Log.e(TAG,"msg to send rep 2:"+msgtosend);
				AsyncTask<String, Void, String> rep = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msgtosend, msgtosend);
				try {
					Log.e(TAG,"replication 2 done:"+rep.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String successordata(String myPortNumber1, String s1) {
		String requestingport=myPortNumber1+":"+s1+":"+"givefile";
		String thisval="";
		AsyncTask<String, Void, String> succforfile = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestingport, s1);
		Log.e(TAG,"calling from successordata method--"+requestingport);
		try {
			thisval=succforfile.get();
			Log.e(TAG,"return val in successordata method-->"+thisval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return thisval;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {

		if(lastatt==false) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		String hashedkey="";
		String rep1="";
		String rep2="";
		String returnvalfrommain="";
		String returnvalfromrep1="";
		String returnvalfromrep2="";
		String maxrepval="";
		String queryport="";
		String[] columns=new String[]{"key","value"};
		MatrixCursor cursor=new MatrixCursor(columns);
		Log.e(TAG,"entered query method");
		if(!(selection.equals("@"))&&(!(selection.equals("*")))) {
			Log.e(TAG, "entered if.. means key value passed---" + selection);
			try {
				hashedkey = genHash(selection);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < listofnodes.size(); i++) {
				if (((listofnodes.get(i).hashnodeval.compareTo(hashedkey)) >= 0) && ((hashedkey.compareTo(listofnodes.get(i).pred)) > 0)) {
					queryport = listofnodes.get(i).portnumber;
					Log.e(TAG, "key is in this port:" + queryport);
				}
			}
			int a = listofnodes.size();
			if (((hashedkey.compareTo(listofnodes.get(a - 1).hashnodeval)) > 0)) {
				String finalport = listofnodes.get(0).portnumber;
				queryport = finalport;
				Log.e(TAG, "key is in this port:" + queryport);
			}
			if ((listofnodes.get(0).hashnodeval.compareTo(hashedkey)) > 0) {
				String firstport = listofnodes.get(0).portnumber;
				queryport = firstport;
				Log.e(TAG, "key is in this port:" + queryport);
			}
			String sending = "query:" + selection;
			AsyncTask<String, Void, String> queryingmain = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, sending, queryport);
			try {
				returnvalfrommain = queryingmain.get();
				Log.e(TAG, "queried key(from main) is:" + selection);
				Log.e(TAG, "returned value(from main) is:" + returnvalfrommain);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			//query replica1
			for (int i = 0; i < listofnodes.size(); i++) {
				if (listofnodes.get(i).portnumber.equals(queryport)) {
					rep1 = listofnodes.get(i).succoriginal;
					Log.e(TAG, "succ for querying replica 1:" + rep1);
				}
			}
			AsyncTask<String, Void, String> queryingrep1 = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, sending, rep1);
			try {
				returnvalfromrep1 = queryingrep1.get();
				Log.e(TAG, "queried key(from rep1) is:" + selection);
				Log.e(TAG, "returned value(from rep1) is:" + returnvalfromrep1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			//query replica2
			for (int i = 0; i < listofnodes.size(); i++) {
				if (listofnodes.get(i).portnumber.equals(rep1)) {
					rep2 = listofnodes.get(i).succoriginal;
					Log.e(TAG, "succ for querying replica 2:" + rep2);
				}
			}
			AsyncTask<String, Void, String> queryingrep2 = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, sending, rep2);
			try {
				returnvalfromrep2 = queryingrep2.get();
				Log.e(TAG, "queried key(from rep2) is:" + selection);
				Log.e(TAG, "returned value(from rep2) is:" + returnvalfromrep2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			//finding max version
			String[] u1 = new String[500];
			String[] u2 = new String[500];
			;
			String[] u3 = new String[500];
			;
			if (returnvalfrommain != null && !returnvalfrommain.isEmpty()) {
				u1 = returnvalfrommain.split(":");
				Log.e(TAG, "came here-");
			}
			if (returnvalfromrep1 != null && !returnvalfromrep1.isEmpty()) {
				u2 = returnvalfromrep1.split(":");
				Log.e(TAG, "came here--");
			}
			if (returnvalfromrep2 != null && !returnvalfromrep2.isEmpty()) {
				u3 = returnvalfromrep2.split(":");
				Log.e(TAG, "came here---");
			}
			if ((returnvalfrommain != null && !returnvalfrommain.isEmpty()) && (returnvalfromrep1 != null && !returnvalfromrep1.isEmpty())) {
				if (Integer.parseInt(u1[1]) >= Integer.parseInt(u2[1])) {
					maxrepval = u1[0];
					Log.e(TAG, "maxrepval-- main-->" + maxrepval);
					if((returnvalfromrep2 != null && !returnvalfromrep2.isEmpty())) {
						if (Integer.parseInt(u1[1]) >= Integer.parseInt(u3[1])) {
							maxrepval = u1[0];
							Log.e(TAG, "maxrepval-- main has-->" + maxrepval);
						}
					}
				}
			}
			if ( (returnvalfromrep2 != null && !returnvalfromrep2.isEmpty())&& (returnvalfromrep1 != null && !returnvalfromrep1.isEmpty())) {
				if (Integer.parseInt(u2[1]) >= Integer.parseInt(u3[1])) {
					maxrepval = u2[0];
					Log.e(TAG, "maxrepval-- rep1 -->" + maxrepval);
					if(returnvalfrommain != null && !returnvalfrommain.isEmpty()) {
						if (Integer.parseInt(u2[1]) >= Integer.parseInt(u1[1])) {
							maxrepval = u2[0];
							Log.e(TAG, "maxrepval-- rep1 has-->" + maxrepval);
						}
					}
				}
			}
			if ( (returnvalfrommain != null && !returnvalfrommain.isEmpty())&& (returnvalfromrep2 != null && !returnvalfromrep2.isEmpty())) {
				if (Integer.parseInt(u3[1]) >= Integer.parseInt(u1[1])) {
					maxrepval = u3[0];
					Log.e(TAG, "maxrepval-- rep2-->" + maxrepval);
					if(returnvalfromrep1 != null && !returnvalfromrep1.isEmpty()) {
						if  (Integer.parseInt(u3[1]) >= Integer.parseInt(u2[1])){
							maxrepval = u3[0];
							Log.e(TAG, "maxrepval-- rep2 has-->" + maxrepval);
						}
					}
				}
			}
			Log.e(TAG,"maxrepval-- max version-->"+maxrepval);
			cursor.newRow().add("key", selection).add("value", maxrepval);
			return cursor;
		}
		if(selection.equals("@")){
			String ret="";
			String[] filelist=getContext().fileList();
			for(String file:filelist)
			{
				if(!file.contains("dummy")) {
					try {
						InputStream inputStream = getContext().openFileInput(file);
						if (inputStream != null) {
							inputStreamReader = new InputStreamReader(inputStream);
							bufferedReader = new BufferedReader(inputStreamReader);
							String receiveString = "";
							StringBuilder stringBuilder = new StringBuilder();
							while ((receiveString = bufferedReader.readLine()) != null) {
								String[] spl = receiveString.split(":");
								stringBuilder.append(spl[0]);
							}
							inputStream.close();
							ret = stringBuilder.toString();
							//matrix building
							cursor.newRow().add("key", file).add("value", ret);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						Log.e(TAG, "Exception 17:" + e.getMessage());
					} catch (IOException e) {
						e.printStackTrace();
						Log.e(TAG, "Exception 18:" + e.getMessage());
					}
				}
			}
			return cursor;
		}
		if(selection.equals("*"))
		{
			Socket socket = null;
			String starval="*";
			Boolean flagvalue=true;
			String togrand="";
			String wholestring="";
			for(int i=0;i<listofnodes.size();i++)
			{
				try {
					socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
							Integer.parseInt(listofnodes.get(i).portnumber));
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
		return null;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
					  String[] selectionArgs) {
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