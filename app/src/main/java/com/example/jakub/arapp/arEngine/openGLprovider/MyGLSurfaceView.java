package com.example.jakub.arapp.arEngine.openGLprovider;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyGLSurfaceView {

    private GLSurfaceView mGLView;
    private MyRender myRender;

    public  MyGLSurfaceView(GLSurfaceView mGLView, MyRender myRender) {
        this.mGLView = mGLView;
        this.myRender = myRender;

        this.mGLView.setEGLContextClientVersion(2);
        this.mGLView.setPreserveEGLContextOnPause(true);
        this.mGLView.setZOrderOnTop(true);
        this.mGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.mGLView.getHolder().setFormat(PixelFormat.RGBA_8888);
        this.mGLView.setRenderer(this.myRender);
        this.mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLView.setOnTouchListener((view, motionEvent) -> {
            myRender.showCords( motionEvent.getX(),motionEvent.getY());
            return false;
        });
    }

    public void updateGlView(){
        mGLView.requestRender();
    }

}
