package com.example.developer.mycamera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by developer on 11/27/2017.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraView(Context context, Camera camera) {
        super(context);

        mCamera = camera;
        //get the holder and set this class as the callback, so we can get camera data here
        mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed
        mHolder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //set camera preview to correct size
        final Camera.Parameters params = mCamera.getParameters();
        final List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        final int screenWidth = ((View) getParent()).getWidth();
        int minDiff = Integer.MAX_VALUE;
        Camera.Size bestSize = null;
        int previewWidth;
        int previewHeight;

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // Find the camera preview width that best matches the
            // width of the surface.
            for (Camera.Size size : sizes) {
                Log.d("size", "width : " + size.width + " height : " + size.height);
                final int diff = Math.abs(size.width - screenWidth);
                if (diff < minDiff) {
                    minDiff = diff;
                    bestSize = size;
                }

            }
            params.setRotation(0);
            previewWidth = bestSize.width;
            previewHeight = bestSize.height;
        } else {
            // Find the camera preview HEIGHT that best matches the
            // width of the surface, since the camera preview is rotated.
            mCamera.setDisplayOrientation(90);
            for (Camera.Size size : sizes) {
                final int diff = Math.abs(size.height - screenWidth);
                if (Math.abs(size.height - screenWidth) < minDiff) {
                    minDiff = diff;
                    bestSize = size;
                }
            }
            previewWidth = bestSize.height;
            previewHeight = bestSize.width;
        }


        Log.d("size", "width : " + bestSize.width + " height : " + bestSize.height);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = previewHeight;
        layoutParams.width = previewWidth;
        setLayoutParams(layoutParams);

        params.setPreviewFormat(ImageFormat.NV21);
        mCamera.setParameters(params);

        //when the surface is created, we can set the camera to draw images in this surfaceholder
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e){
            Log.d("Error","Camera error on surfaceCreated " + e.getMessage());
        }

    }



    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //before changing the application orientation, you need to stop the preview, rotate and then start it again
        if (mHolder.getSurface()==null) return;//check if the surface is ready to receive camera data

        try{
            mCamera.stopPreview();
        } catch (Exception e){
            //this will happen when you are trying the camera if it's not running
            Log.d("Error","Camera error on surfaceChanged " + e.getMessage());
        }

        //now, recreate the camera preview
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }

        mCamera.setPreviewCallback(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //our app has only one screen, so we'll destroy the camera in the surface
        //if you are using more screens, please move this code your activity
        mCamera.stopPreview();
        mCamera.release();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Log.d("bytes", "size ; "+ bytes.length);

    }
}
