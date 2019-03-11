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
import android.text.BoringLayout;
import android.text.TextUtils;
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


public class MainActivity extends AppCompatActivity {

    //Define variables to be used in communication
    //private EditText ipAddress;
    String ipAddressText;
    public TextView serverResponse;
    public TextView concentration;
    public ImageView image;
    private String reply;
    static Boolean expectingImage = Boolean.FALSE;
    private Bitmap bitmapImage;
    byte[] recievedImage;
    Button button, laserButton, cameraButton;
    int bytesRead;
    int laserIntensity = 0;
    int cameraExposure = 0;
    String clientMessage = "This is the Client";
    Socket socket = null;
    String path = "/storage/emulated/0/Download/squiddab.png";
    //String path = "/storage/emulated/0/DCIM/Camera/IMG_20190212_141458.jpg";

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
        //Map "Begin Analysis" button
        button = (Button) findViewById(R.id.nutButton);
        laserButton = findViewById(R.id.laserButton);
        cameraButton = findViewById(R.id.cameraButton);
        serverResponse = (TextView) findViewById(R.id.serverResponsetextView);
        concentration = findViewById(R.id.concentration_value);
        //ipAddress = (EditText) findViewById(R.id.ipAddressInput);
        image = (ImageView) findViewById(R.id.mainImage);
        image.getLayoutParams().height = 640;
        image.getLayoutParams().width = 480;
        image.setImageResource(R.drawable.squiddab);
        //set the buttons to listen to clicks
        //Set this button to initiate the analysis
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pop up a toast indicating the button has been hit and that a message was sent
                //ipAddressText = ipAddress.getText().toString();
                ipAddressText = "192.168.1.178";
                Log.d("mybug","button pressed/toast coming");
                Toast.makeText(getApplicationContext(), "Analysis initiated", Toast.LENGTH_LONG).show();

                sendMessage(clientMessage);
                Log.d("mybug","sendMessage called");
            }

        });
        Log.d("mybug", "outside button's click listener");
        //Increment laser intensity by 1 on every press
        laserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                laserIntensity++;
                //Only allow 4 different intensities
                if (laserIntensity > 3){
                    laserIntensity = 0;
                }
                Log.d("mybug", "laser intensity adjusted");
                Toast.makeText(getApplicationContext(), "Adjusted laser intensity to " + Integer.toString(laserIntensity), Toast.LENGTH_LONG).show();
            }
        });
        //Adjust camera exposure by 1 on every press
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                cameraExposure++;
                //Limit to 4 different settings
                if (cameraExposure > 3){
                    cameraExposure = 0;
                }
                Log.d("mybug", "camera exposure adjusted");
                Toast.makeText(getApplicationContext(), "Adjusted camera exposure to " + Integer.toString(cameraExposure), Toast.LENGTH_LONG).show();
            }
        });

        serverResponse.setText("Press one of the buttons below to begin.");
        concentration.setText("Data will appear here once analysis is done.");
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

    private void sendMessage(final String message) {
        Log.d("mybug", "top of sendMessage");
        //Create a new thread to handle the socket connection
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("mybug", "at top of sendMessage's run()");
                    if (socket == null) {
                        //Create the socket and set it to only look for the raspberry pi
                        Log.d("mybug", "creating socket");
                        socket = new Socket(ipAddressText, 9090);
                        Log.d("mybug", "socket created");
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
                    Log.d("mybug", "about to send message");
                    //Push out the message and flush it to ensure it was sent
                    output.println(message);
                    Log.d("mybug", "gonna flush!");
                    output.flush();
                    Log.d("mybug", "message sent");
                    determineMessage();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Log.d("mybug", "bottom of sendMessage");
    }

    //Close the socket
    private void closeSocket(OutputStream out) {
        try {
            out.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void determineMessage(){
        //String imageSize = recieveMessage();
        //Log.d("mybug","imageSize value" + imageSize);

        Log.d("mybug", "message has been sent");
        if (expectingImage == Boolean.TRUE) {
            Log.d("mybug", "calling recieveImage");
            recieveImage();
        }

        //Assign reply to return value of recieveMessage
        else {
            reply = recieveMessage();
            Log.d("mybug", "reply is" + reply);
            //Close socket if server sends close
        }

        //Check if we're expecting an image
        if (handleMessage(expectingImage, reply, bitmapImage)){
            //If so, send 
            sendMessage(reply);
        }
        else{
            recieveImage();
        }

        Log.d("mybug", "stuff closed");
        Log.d("mybug", "end of try block");
    }


    private boolean handleMessage(Boolean expectingImage, final String reply, final Bitmap bitmapImage){
        Log.d("mybug", "top of handleMessage");
        if (expectingImage == Boolean.FALSE){
            Log.d("mybug", "handling string");
            //Call UIThread to change viewText on main screen
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serverResponse.setText("Response from Pi:Image Size is " + Integer.toString(bytesRead));
                    concentration.setText("Image Concentration: Like, really concentrated");
                    Log.d("mybug", "set serverResponse");
                }
            });
            expectingImage = Boolean.TRUE;
            return expectingImage;

        }

        else {
            Log.d("mybug", "handling image");
            //runOnUiThread(new Runnable() {
            //    @Override
            //    public void run() {
            //    Log.d("mybug", "about to set image");
            //    image.setImageBitmap(bitmapImage);
            onPostExecute(bitmapImage);
            expectingImage = Boolean.FALSE;
            clientMessage = "Got image";
            return expectingImage;
            //    Log.d("mybug", "image handled");
            //    }
            //});

        }

    }

    private void onPostExecute(final Bitmap bitmapImage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                image.setImageBitmap(bitmapImage);
                serverResponse.setText("Response from Pi:Image Size is " + Integer.toString((bytesRead % 4) + 1));
                concentration.setText("Image Concentration: Like, really concentrated");
            }
        });
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
            Log.d("mybug", "imageInputStream declared");

            bytesRead = imageInputStream.read(recievedImage, 0, recievedImage.length);
            Log.d("mybug", "bytes read set");
            //int z = 1;
            //int offset = 0;
            //while(((bytesRead = imageInputStream.read(recievedImage)) != -1)) {
                //if(bytesRead < 6209){
                  //  Log.d("mybug", "read bytes" + Integer.toString(bytesRead));
                //    continue;
                //}
                //else {
                bitmapImage = BitmapFactory.decodeByteArray(recievedImage, 0, bytesRead);
                Log.d("mybug", "read bytes " + Integer.toString(bytesRead));
                    //Log.d("mybug", "num of packets" + Integer.toString(z++));
                    //  offset =+bytesRead;
                //}
            //}
            Log.d("mybug", "set bitmapImage");
            if (bitmapImage != null){
                Log.d("mybug", "BITMAP ISN'T EMPTY");
            }
            else{
                recieveImage();
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
