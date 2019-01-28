package fgsrfug.seniordesignapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;


import static java.sql.DriverManager.println;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Define variables to be used in communication
    private EditText ipAddress;
    String ipAddressText;
    public TextView serverResponse;
    ImageView image;
    private String reply;
    Button button;
    String clientMessage = "This is the Client";
    Socket socket = null;
    String path = "/storage/emulated/0/Download/squiddab.png";
    File file = new File(path);
    private String stringy;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Function that begins upon start-up
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("mybug", "in onCreate");
        //Link up the button and textview to the XML file
        //checkPermission();
        button = (Button) findViewById(R.id.nutButton);
        serverResponse = (TextView) findViewById(R.id.serverResponsetextView);
        ipAddress = (EditText) findViewById(R.id.ipAddressInput);

        //set the button to listen to clicks
        button.setOnClickListener(this);
        serverResponse.setText("Reply from Pi:");
        if(file.exists()) {
            Log.d("mybug", "file exists");
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            image = (ImageView) findViewById(R.id.mainImage);
            image.setImageBitmap(myBitmap);
        }
        Log.d("mybug", "done with onCreate");
        //path = getBaseContext().getFilesDir().getAbsolutePath();
        //file = new File(path);
        //Uri uri = Uri.fromFile(file);
        //String fileName = file.getAbsolutePath();
        //Log.d("mybug", fileName);

        //Bitmap theImage = BitmapFactory.decodeFile(path);
        //image.setImageBitmap(theImage);

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("mybug", "in onStart()");
    }
    //Describe what is to happen when a click occurs
    @Override
    public void onClick(View v) {

            //Pop up a toast indicating the button has been hit and that a message was sent
            //ipAddressText = ipAddress.getText().toString();
            ipAddressText = "192.168.1.106";
            Log.d("mybug","button pressed/toast coming");
            Toast.makeText(getApplicationContext(), "Analysis initiated", Toast.LENGTH_LONG).show();

            sendMessage(clientMessage);
            Log.d("mybug","sendMessage called");
    }

    public static void checkPermission(Activity activity){

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

    }

    private void sendMessage(final String message){

        //Create handler to handle message and runnable objects within this class
        //Create a new thread to handle the socket connection
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("mybug","at top of sendMessage's run()");
                    if(socket == null) {
                        //Create the socket and set it to only look for the raspberry pi
                        Log.d("mybug","creating socket");
                        socket = new Socket("192.168.1.106", 9090);
                        Log.d("mybug","socket created");
                    }
                    /*
                    else {
                        Log.d("mybug", "no socket created");
                        Toast.makeText(getApplicationContext(), "No host found", Toast.LENGTH_LONG).show();
                    }
                    */
                    boolean socketConnect = socket.isConnected();
                    String stringSockConn = String.valueOf(socketConnect);
                    Log.d("mybug", stringSockConn);

                    //Assign out to be the output stream and assign output to out so as to print in
                    //a text outputstream
                    OutputStream out = socket.getOutputStream();
                    PrintWriter output = new PrintWriter(out);

                    //Push out the message and flush it to ensure it was sent
                    output.println(message);
                    output.flush();


                    Log.d("mybug","message has been sent");
                    reply = recieveMessage();
                    Log.d("mybug", "reply is" + reply);
                    //Log.d("mybug","recieveMessage() called");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverResponse.setText("Response from Pi " + reply);
                            Log.d("mybug", "set serverResponse");
                        }
                    });

                    if(reply.equals("close")) {
                        output.close();
                        out.close();
                        socket.close();
                    }
                    Log.d("mybug","stuff closed");
                    Log.d("mybug","end of try block");
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Log.d("mybug","thread started");
        //thread.stop();
        //Log.d("mybug","thread stopped");
    }

    private String recieveMessage(){
        try {
            //final Handler handler = new Handler();
            Log.d("mybug", "Inside recieveMessage");

            //Create a buffer to receive incoming data and read that data into a string
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stringy = input.readLine();

            Log.d("mybug", "created input variables");
            //Handle the response we get from the server and print it to the screen
            //handler.post(new Runnable() {
            //    @Override
            //    public void run() {
            //Log.d("mybug", "in the handler");
            Log.d("mybug", stringy);
                    //String reply = serverResponse.getText().toString();
                    //if (stringy.trim().length() != 0)
            //serverResponse.setText("\nFrom Server : " + stringy);
            Log.d("mybug", "handler done");
            //    }
            //});

        }
        catch (IOException e){
            e.printStackTrace();
        }

        return stringy;
    }
}
