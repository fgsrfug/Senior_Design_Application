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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    Button button, laserButton, cameraButton;
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
        image.getLayoutParams().height = 768;
        image.getLayoutParams().width = 1024;
        image.setImageResource(R.drawable.sampleimage);
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
                Log.d("mybug","going into main()");
                main();
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

    private void main() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String serverMessage = "";
                    int messageCounter = 1;
                    int imageSize = 0;
                    Boolean expectingImage = Boolean.FALSE;
                    Bitmap bitmapImage = null;
                    Bitmap setBitmap = null;
                    Boolean continueWhileLoop = true;
                    while (continueWhileLoop) {
                        Log.d("mybug", "Top of main while loop");
                        if (socket == null) {
                            //Create the socket and set it to only look for the raspberry pi
                            // Log.d("mybug", "creating socket");
                            socket = new Socket(ipAddressText, 9090);
                            Log.d("mybug", "socket created");
                        }

                        Log.d("mybug", "Server message is: " + serverMessage);

                        switch (stringSearch(serverMessage, messageCounter)) {
                            case 0:
                                Log.d("mybug", "No ACK");
                                sendMessage("No ACK");
                                serverMessage = recieveMessage();
                                break;
                            case 1:
                                Log.d("mybug", "This is the Client");
                                sendMessage(clientMessage);
                                messageCounter = 1;
                                serverMessage = recieveMessage();
                                break;
                            case 2:
                                Log.d("mybug", "camera exposure");
                                sendMessage("Camera exposure " + cameraExposure);
                                messageCounter = 2;
                                serverMessage = recieveMessage();
                                break;
                            case 3:
                                Log.d("mybug", "laser intensity");
                                sendMessage("Laser intensity " + laserIntensity);
                                messageCounter = 3;
                                serverMessage = recieveMessage();
                                break;
                            case 4:
                                Log.d("mybug", "image size");
                                sendMessage("Send image size");
                                messageCounter = 4;
                                serverMessage = recieveMessage();
                                Log.d("mybug", "got servermessage imagesize");
                                imageSize = findIntInString(serverMessage);
                                Log.d("mybug", "got int imagesize as " + imageSize);
                                break;
                            case 5:
                                Log.d("mybug", "Receiving image attributes");
                                sendMessage("Send image attributes");
                                serverMessage = recieveMessage();
                            case 6:
                                if(messageCounter == 4) {
                                    //Log.d("mybug", "Set expectingImage to false, calling closeSocket");
                                    Log.d("mybug", "Ready for image");
                                    sendMessage("Ready for image");
                                    expectingImage = Boolean.TRUE;
                                    Log.d("mybug", "Expecting image is set to " + expectingImage);
                                    Log.d("mybug", "imageSize variable is " + imageSize);
                                    setBitmap = recieveImage(imageSize, expectingImage, bitmapImage);
                                    expectingImage = Boolean.FALSE;
                                    continueWhileLoop = false;
                                    onPostExecute(setBitmap, serverMessage);
                                    break;
                                }
                            default:
                                closeSocket(socket.getOutputStream());
                        }
                    }
                }

                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void sendMessage(final String message) {
        Log.d("mybug", "top of sendMessage");
        //Create a new thread to handle the socket connection
            try {
                Log.d("mybug", "at top of sendMessage's run()");
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
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("mybug", "bottom of sendMessage");
    }

    //Close the socket
    private void closeSocket(OutputStream out) {
        try {
            out.close();
            out.close();
            socket.close();
            Log.d("mybug", "socket closed!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int stringSearch(final String stringToCheck, int messageCount){
        Log.d("mybug", "top of stringSearch");
        switch (stringToCheck){
            case "":
                //Sending "this is the client"
                Log.d("mybug", "empty string case");
                return 1;

            case "Message received":
                //ACK to this is the client
                Log.d("mybug", "MessageCount" + messageCount);
                Log.d("mybug", "Message received case");
                if (messageCount == 1) {
                    Log.d("mybug", "first message received");
                    return 2;
                }
                //ACK to first camera exposure data
                else if(messageCount == 2){
                    Log.d("mybug", "second message received");
                    return 3;
                }
                //ACK to laser intensity
                else if(messageCount == 3){
                    Log.d("mybug", "third message received");
                    return 4;
                }
            case "close":
                try {
                    closeSocket(socket.getOutputStream());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            default:
                Log.d("mybug", "entering default behavior");
                if (stringToCheck.contains("Image size")){
                    Log.d("mybug", "Got the image size");
                    return 5;
                }
                else if (stringToCheck.contains("The concentration")) {
                    Log.d("mybug", "Got the concentration");
                    return 6;
                }
                try {
                    closeSocket(socket.getOutputStream());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        return 0;
    }

    private String recieveMessage(){
        try {
            Log.d("mybug", "Inside recieveMessage");
            //Create a buffer to receive incoming data and read that data into a string
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d("mybug", "Created buffered reader");
            stringy = input.readLine();
            Log.d("mybug", "created input variables/reply is: " + stringy);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return stringy;
    }

    private Bitmap recieveImage(int imageSize, boolean expectingImage, Bitmap bitmapImage) {
        try {
            if (expectingImage) {
                Log.d("mybug", "inside recieveImage");
                //Bring in inputstream to get data from
                InputStream imageInputStream = socket.getInputStream();
                Log.d("mybug", "imageInputStream declared");
                //create bytes to hold incoming data and total data, respectively
                byte [] recievedImage = new byte[imageSize];
                byte [] bitmapArray = new byte[imageSize];
                //Keep track of how many bytes were read and where we are in the array
                int bytePosition = 0;
                int bytesRead = 0;
                //read from the input stream and put it in recievedImage
                while ((bytesRead = imageInputStream.read(recievedImage)) != -1) {
                    //Copy whatever we just read into bitmapArray at position bytePosition
                    System.arraycopy(recievedImage,0,bitmapArray,bytePosition,bytesRead);
                    //Update bytePosition
                    bytePosition+= bytesRead;
                    //Check if we've read the entire image
                    if (bytePosition == imageSize){
                        Log.d("mybug", "setting bitmapImage");
                        //Get our bitmap image
                        bitmapImage = BitmapFactory.decodeByteArray(bitmapArray, 0, imageSize);
                        Log.d("mybug", "bitmapImage is set");
                        if (bitmapImage != null) {
                            Log.d("mybug", "BITMAP ISN'T EMPTY");
                        }
                        //close the socket so our read returns -1
                        closeSocket(socket.getOutputStream());
                        break;
                    }
               }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return bitmapImage;
    }

    private int findIntInString(final String stringWithInt){
        Log.d("mybug", "in findIntInString");
        String tempString = stringWithInt.replaceAll("[^0-9]+", "");
        Log.d("mybug", "converted string");
        int intsFromString = Integer.parseInt(tempString);
        Log.d("mybug", "Returning...");
        return  intsFromString;

    }
/*
    private void determineMessage(OutputStream out){
        //String imageSize = recieveMessage();
        //Log.d("mybug","imageSize value" + imageSize);
        Log.d("mybug", "message has been sent, inside determineMessage");
        if (expectingImage == Boolean.TRUE) {
            Log.d("mybug", "calling recieveImage");
            recieveImage();
        }
        //Assign reply to return value of recieveMessage
        else {
            reply = recieveMessage();
            Log.d("mybug", "reply is: " + reply);
            //Close socket if server sends close
            if(reply.equals("'close'")){
                Log.d("mybug", "about to close socket");
                closeSocket(out);
            }
        }

        //Check if we're expecting an image
        if (handleMessage(expectingImage, reply, bitmapImage)){
            //If so, send
            sendMessage("Message recieved");
        }
        else{
            recieveImage();
        }

        Log.d("mybug", "stuff closed");
        Log.d("mybug", "end of try block");
    }

    //Handles data recieved and places it on the screen
    private boolean handleMessage(Boolean expectingImage, final String reply, final Bitmap bitmapImage){
        Log.d("mybug", "top of handleMessage");
        if (expectingImage == Boolean.FALSE){
            Log.d("mybug", "handling string");
            //Call UIThread to change viewText on main screen
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serverResponse.setText("Response from Pi: " + reply);
                    concentration.setText("Image Concentration: super " + reply );
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
*/

    private void onPostExecute(final Bitmap bitmapImage, final String Concentration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("mybug", "In onPostExecute");
                image.setImageBitmap(bitmapImage);
                //serverResponse.setText("Response from Pi:Image Size is " + bytesRead);
                concentration.setText(Concentration);
            }
        });
    }

}
