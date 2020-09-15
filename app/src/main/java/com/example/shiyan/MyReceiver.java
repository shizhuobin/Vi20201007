package com.example.shiyan;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import static android.os.SystemClock.sleep;

public class MyReceiver extends BroadcastReceiver {

    private final String apiEndpoint = "https://vi-power.cognitiveservices.azure.cn/face/v1.0/";
    private final String subscriptionKey = "a2f847cb765f44f592077fa31803f36d";
    private final FaceServiceClient faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);
    String name;
    int httptime;
    String path;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("myService ", "executed at " + new Date().toString());

        Log.d("myService ", "executed at " + intent.getStringExtra("name"));
        //context.stopService(intent);
        name=intent.getStringExtra("name");
        path=intent.getStringExtra("path");
        //context.startService(i);
        httptime=intent.getIntExtra("time",2);
        //sleep(httptime);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String file1=path+"/"+name;
                String jpgname=name;
                String jpgpath=path;
                /*
                try {
                    Thread.sleep(httptime*2*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */

                FileInputStream fs = null;
                ByteArrayInputStream inputStream=null;
                Bitmap bitmap=null;
                ByteArrayOutputStream outputStream=null;
                try {
                    fs = new FileInputStream(new File(file1));
                    bitmap  = BitmapFactory.decodeStream(fs);
                    outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                    inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (fs != null) {
                    System.out.println(11111111);

                    Face[] result = new Face[0];
                    try {
                        synchronized (faceServiceClient) {
                            result = faceServiceClient.detect(
                                    inputStream,
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    //null          // returnFaceAttributes:
                                    new FaceServiceClient.FaceAttributeType[]{
                                            FaceServiceClient.FaceAttributeType.Age,
                                            FaceServiceClient.FaceAttributeType.Gender,
                                            FaceServiceClient.FaceAttributeType.Emotion,
                                            FaceServiceClient.FaceAttributeType.Smile,
                                            FaceServiceClient.FaceAttributeType.Makeup}

                            );
                        }
                        inputStream.close();
                        fs.close();

                    } catch (ClientException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(result.length==0)
                    {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(),  bitmap.getHeight(), matrix, true);
                        ByteArrayOutputStream outputStream2= new ByteArrayOutputStream();
                        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, outputStream2);

                        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(outputStream2.toByteArray());
                        result = new Face[0];
                        try {
                            synchronized (faceServiceClient) {
                                result = faceServiceClient.detect(
                                        inputStream2,
                                        true,         // returnFaceId
                                        false,        // returnFaceLandmarks
                                        //null          // returnFaceAttributes:
                                        new FaceServiceClient.FaceAttributeType[]{
                                                FaceServiceClient.FaceAttributeType.Age,
                                                FaceServiceClient.FaceAttributeType.Gender,
                                                FaceServiceClient.FaceAttributeType.Emotion,
                                                FaceServiceClient.FaceAttributeType.Smile,
                                                FaceServiceClient.FaceAttributeType.Makeup}

                                );
                            }
                            inputStream2.close();


                        } catch (ClientException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }



                    System.out.println(result.length);
                    if(result.length!=0) {
                        Emotion emotion = result[0].faceAttributes.emotion;
                        String s=jpgname
                                +" anger:"+emotion.anger
                                +" contempt:"+emotion.contempt
                                +" disgust:"+emotion.disgust
                                +" fear:"+emotion.fear
                                +" happiness:"+emotion.happiness
                                +" neutral:"+emotion.neutral
                                +" sadness:"+emotion.sadness
                                +" surprise:"+emotion.surprise
                                +"\n";
                        System.out.println(s);


                        FileWriter fileWritter = null;
                        try {
                            fileWritter = new FileWriter(jpgpath+".txt",true);
                            fileWritter.write(s);
                            fileWritter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }




/*
                        //File file = new File((file1).replaceAll("jpg","txt"));
                        File file = new File(jpgpath+".txt");
                        FileOutputStream outStream = null;
                        try {
                            outStream = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {

                            outStream.write(s.getBytes());
                            outStream.flush();
                            outStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

 */
                    }
                    else
                    {
                        FileWriter fileWritter = null;
                        try {
                            fileWritter = new FileWriter(jpgpath+".txt",true);
                            fileWritter.write(jpgname+" not find face\n");
                            fileWritter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }


            }
        }).start();


    }
}
