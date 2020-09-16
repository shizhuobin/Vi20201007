package com.example.shiyan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.graphics.SurfaceTexture;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import android.content.Intent;

import static java.lang.Thread.sleep;

public class MyWindow extends LinearLayout implements SurfaceTextureListener {

    private final String apiEndpoint = "https://vi-power.cognitiveservices.azure.cn/face/v1.0/";
    private final String subscriptionKey = "a2f847cb765f44f592077fa31803f36d";
    private final FaceServiceClient faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    private int delaytime=1500;

    private TextureView textureView;
    /**
     * 相机类
     */
    public Camera myCamera;
    private Context context;
    private WindowManager mWindowManager;
    private Bitmap bitmap_get = null;
    int stoptime=0;

    static int httptime=1;

    public MyWindow(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.window, this);
        this.context = context;

        initView();
    }

    private void initView() {
        textureView = (TextureView) findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
        mWindowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (myCamera == null) {
            // 创建Camera实例
            //myCamera = Camera.open(1);

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        Log.d("Demo", "tryToOpenCamera"+cameraCount+" "+camIdx);
                        myCamera = Camera.open(camIdx);



                        Camera.Parameters params = myCamera.getParameters();

                        params.setPreviewSize(MainActivity.getWeigh(), MainActivity.getHeigh());
                        myCamera.setParameters(params);


                    } catch (RuntimeException e) {
                            e.printStackTrace();
                    }
                }
            }

            try {
                // 设置预览在textureView上
                myCamera.setPreviewTexture(surface);
                myCamera.setDisplayOrientation(SetDegree(MyWindow.this));

                // 开始预览
                //myCamera.startPreview();
                handler.sendEmptyMessage(BUFFERTAG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStoptime()
    {
        stoptime=1;
        myCamera.stopPreview();
    }

    int tag;
    private void getPreViewImage() {
        tag=0;
        if (myCamera != null){
            myCamera.setPreviewCallback(new Camera.PreviewCallback(){

                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Camera.Size size = camera.getParameters().getPreviewSize();
                   // Log.d("size", "hhh Chosen resolution: "+size.width+" "+size.height);

                    myCamera.stopPreview();

                    try{
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                        if(image!=null&&tag==0){
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, stream);



                            bitmap_get = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                            //Log.d("预览", "147");
                            //**********************
                            //因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上
                            bitmap_get = rotateMyBitmap(bitmap_get);






                            //detectAndFrame(stream);
                            //**********************************
                            stream.close();
                            tag=1;
                        }
                    }catch(Exception ex){
                        Log.e("预览","Error:"+ex.getMessage());
                    }
                }
            });
        }
    }


    public Bitmap rotateMyBitmap(Bitmap mybmp){
        //*****旋转一下
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap nbmp2 = Bitmap.createBitmap(mybmp, 0,0, mybmp.getWidth(),  mybmp.getHeight(), matrix, true);
        mybmp=null;
        saveImage(nbmp2);
        return nbmp2;
    };

    static void setHttptime()
    {
        httptime=1;
    }



    File file;
    Bitmap bmp_save;
    String name;
    String path;
    public void saveImage(Bitmap bmp) {


       path =context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+MainActivity.getWeigh()+"x"+MainActivity.getHeigh()+" "+MainActivity.getHz()+"s 1次"; //所创建文件目录
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs(); //创建目录
        }

        //file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");




        Calendar Cld = Calendar.getInstance();
        int YY = Cld.get(Calendar.YEAR) ;
        int MM = Cld.get(Calendar.MONTH)+1;
        int DD = Cld.get(Calendar.DATE);
        int HH = Cld.get(Calendar.HOUR_OF_DAY);
        int mm = Cld.get(Calendar.MINUTE);
        int SS = Cld.get(Calendar.SECOND);
        int MI = Cld.get(Calendar.MILLISECOND);//毫秒

        name=YY+"-"+MM+"-"+DD+" "+HH+":"+mm+":"+SS+"."+MI+".jpg";
        System.out.println("date:"+name);
        //name=System.currentTimeMillis() + ".jpg";
        file= new File(path,name);


        System.out.println(path+"/"+name);
        bmp_save=bmp;
        Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    FileOutputStream fos = null;

                    String jpgname=name;
                    String jpgpath=path;
                    Bitmap bitmap=bmp_save;
                    ByteArrayOutputStream outputStream=null;
                    ByteArrayInputStream inputStream=null;
                    outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                    /*
                    try {
                        fos = new FileOutputStream(file);
                        Log.d("test", "running!!!");
                        bmp_save.compress(Bitmap.CompressFormat.JPEG, 100, fos);


                        fos.flush();
                        fos.close();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    */
                    System.out.println(1111111111);
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
                    } catch (ClientException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(22222222);
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
                    System.out.println("result.length:"+result.length);
                    if(result.length!=0) {
                        Emotion emotion = result[0].faceAttributes.emotion;
                        String s = jpgname
                                + " anger:" + emotion.anger
                                + " contempt:" + emotion.contempt
                                + " disgust:" + emotion.disgust
                                + " fear:" + emotion.fear
                                + " happiness:" + emotion.happiness
                                + " neutral:" + emotion.neutral
                                + " sadness:" + emotion.sadness
                                + " surprise:" + emotion.surprise
                                + "\n";
                        System.out.println(s);


                        FileWriter fileWritter = null;
                        try {
                            fileWritter = new FileWriter(jpgpath + ".txt", true);
                            fileWritter.write(s);
                            fileWritter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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


/*
                    if (MainActivity.isUpload.equals("是")) {
                        Intent intent = new Intent("com.shi.zhu.bin");
                        intent.putExtra("name", name);
                        //intent.putExtra("time", t*3);
                        intent.putExtra("path", path);
                        context.sendBroadcast(intent);
                    }
*/
                    //myCamera.stopPreview();
                }
            };
        //runnable.run();
        new Thread(runnable).start();
    }

    public static final int BUFFERTAG = 100;
    public static final int BUFFERTAG1 = 101;
    private boolean isGetBuffer = true;

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch(msg.what){
                case BUFFERTAG:
                    if(isGetBuffer){
                        if(stoptime==1) {
                            handler.sendEmptyMessageDelayed(BUFFERTAG1, 1000);
                            break;
                        }
                        if (myCamera != null)
                            myCamera.startPreview();
                        getPreViewImage();
                        //Log.d("CameraService", "第1次");
                        handler.sendEmptyMessageDelayed(BUFFERTAG, MainActivity.getHz()*1000);
                    }else{
                        myCamera.setPreviewCallback(null);
                        handler.sendEmptyMessageDelayed(BUFFERTAG, MainActivity.getHz()*1000);
                    }
                    break;

                case BUFFERTAG1:
                    myCamera.setPreviewCallback(null);
                    handler.sendEmptyMessageDelayed(BUFFERTAG1, 1000);
                    Log.d("CameraService", "第2次");
                    break ;
            }
        };
    };


    private int SetDegree(MyWindow myWindow) {
        // 获得手机的方向
        int rotation = mWindowManager.getDefaultDisplay().getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface.release();

        myCamera.setPreviewCallback(null);

        myCamera.stopPreview(); //停止预览

        myCamera.lock();

        myCamera.release();     // 释放相机资源
        myCamera = null;

        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

}

