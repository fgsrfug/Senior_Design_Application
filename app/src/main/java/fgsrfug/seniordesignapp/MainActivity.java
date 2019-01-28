package fgsrfug.seniordesignapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    public ImageView image;
    private String reply;
    private Boolean expectingImage;
    private Bitmap bitmapImage;
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
        image = (ImageView) findViewById(R.id.mainImage);
        image.setImageResource(R.drawable.crabknife);
        //set the button to listen to clicks
        button.setOnClickListener(this);
        serverResponse.setText("Reply from Pi:");
        expectingImage = Boolean.TRUE;
        //Log.d("mybug", "file exists");
        //Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        /*
        try {
            InputStream is = (InputStream) new URL("https://vignette.wikia.nocookie.net/viceroy/images/0/00/Tumblr_inline_o4xzqu4WC61t634at_540.jpg/revision/latest?cb=20160421235423").getContent();
            Drawable draw = Drawable.createFromStream(is, null);
            image.setImageDrawable(draw);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        */

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

            sendMessage(clientMessage, expectingImage);
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

    private void sendMessage(final String message, final Boolean expectingImage){
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
                    //Check that the socket is connected.
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

                    //Assign reply to return value of recieveMessage
                    Log.d("mybug","message has been sent");
                    if(expectingImage == Boolean.TRUE) {
                        Log.d("mybug", "calling recieveImage");
                        recieveImage();
                    }
                    else {
                        reply = recieveMessage();
                        Log.d("mybug", "reply is" + reply);
                    }

                    handleMessage(expectingImage, reply, bitmapImage);

                    //Close socket if server sends close
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
    }

    private void handleMessage(Boolean expectingImage, final String reply, final Bitmap bitmapImage){
        Log.d("mybug", "top of handleMessage");
        if (expectingImage == Boolean.FALSE){
            Log.d("mybug", "handling string");
            //Call UIThread to change viewText on main screen
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serverResponse.setText("Response from Pi " + reply);
                    Log.d("mybug", "set serverResponse");
                }
            });
        }

        else {
            Log.d("mybug", "handling image");
            //runOnUiThread(new Runnable() {
                //@Override
                //public void run() {
            Log.d("mybug", "about to set image");
            //image.setImageBitmap(bitmapImage);
            onPostExecute(bitmapImage);
            Log.d("mybug", "image handled");
                //}
            //});

        }

    }

    private void onPostExecute(Bitmap bitmapImage) {
        image.setImageBitmap(bitmapImage);
    }


    private String recieveMessage(){
        try {
            Log.d("mybug", "Inside recieveMessage");

            //Create a buffer to receive incoming data and read that data into a string
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stringy = input.readLine();
            Log.d("mybug", "created input variables/reply is" + stringy);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return stringy;
    }

    private void recieveImage() {
        try {
            Log.d("mybug", "inside recieveImage");
            DataInputStream imageInputStream = new DataInputStream(socket.getInputStream());
            int bytesRead;
            byte[] recievedImage = new byte[1280 * 720];
            bytesRead = imageInputStream.read(recievedImage, 0, recievedImage.length);
            bitmapImage = BitmapFactory.decodeByteArray(recievedImage, 0, bytesRead);

            if (bitmapImage != null){
                Log.d("mybug", "BITMAP ISN'T EMPTY");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
