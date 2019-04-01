package com.example.jakub.arapp.arEngine.openGLprovider;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.jakub.arapp.arEngine.ArActivity;
import com.example.jakub.arapp.arEngine.shape.Frame;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRender implements GLSurfaceView.Renderer {

    public static final String TAG = MyRender.class.getCanonicalName();
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;   \n" +
                    "attribute vec4 vPosition;  \n" +
                    "void main(){               \n" +
                    " gl_Position = uMVPMatrix * vPosition; \n" +
                    "}  \n";
    private final String fragmentShaderCode =
            "precisin mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    " gl_FragColor = vColor;" +
                    "}";
    ArActivity activity;
    float dupa = 0.0f;
    float X = 0.0f;
    float Y = 0.0f;
    float Z = 0.1f;
    List<Frame> objectsToDraw;

    Float R = 7.0f;
    Float scale = 0.1f;

    public MyRender(ArActivity activity) {
        this.activity = activity;
        objectsToDraw = new ArrayList<>();

    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        int i = 1;

        float[] cordsss = {
                5.0f, 0.2f, 4.9f,   // top left
                5.0f, -0.2f, 4.9f,   // bottom left
                5.2f, -0.2f, 4.554f,   // bottom right
                5.2f, 0.2f, 4.554f    // top right
        };
        Frame aaaa = new Frame(cordsss, 2);
        objectsToDraw.add(aaaa);


        float[] cordssss = {
                scale * -1f, scale * 1f, 6.85f,   // top left
                scale * -1f, scale * -1f, 6.85f,   // bottom left
                scale * 1f, scale * -1f, 6.85f,   // bottom right
                scale * 1f, scale * 1f, 6.85f    // top right
        };
        Frame aaa = new Frame(cordssss, 1);
//        objectsToDraw.add(aaa);
//
        float[] cordsssss = {
                -0.5f, 0.5f, 6.5f,   // top left
                -0.5f, -0.5f, 6.5f,   // bottom left
                0.5f, -0.5f, 6.5f,   // bottom right
                0.5f, 0.5f, 6.5f    // top right
        };
        Frame aaaaa = new Frame(cordsssss, 3);
        objectsToDraw.add(aaaaa);
//
//
//        float[] cordsssss = {
//                5.2f, 0.2f, -4.554f,   // top left
//                5.2f, -0.2f, -4.554f,   // bottom left
//                5.0f, -0.2f, -4.9f,   // bottom right
//                5.0f, 0.2f, -4.9f    // top right
//        };
//        Frame aaaaaa = new Frame(cordsssss, 1);
//        objectsToDraw.add(aaaaaa);
//
//
//        float[] cordssssss = {
//                0.2f, 0.2f, -6.9f,   // top left
//                0.2f, -0.2f, -6.9f,   // bottom left
//                -0.2f, -0.2f, -6.9f,   // bottom right
//                -0.2f, 0.2f, -6.9f    // top right
//        };
//        Frame aaaaaaa = new Frame(cordssssss, 2);
//        objectsToDraw.add(aaaaaaa);
//
//
//        float[] cordsssssss = {
//                -6.99f, 0.2f, -0.2f,   // top left
//                -6.99f, -0.2f, -0.2f,   // bottom left
//                -6.99f, -0.2f, 0.2f,   // bottom right
//                -6.99f, 0.2f, 0.2f    // top right
//        };
//        Frame aaaaaaaa = new Frame(cordsssssss, 3);
//        objectsToDraw.add(aaaaaaaa);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 0.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.1f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        float[] scratch = new float[16];

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        float x = R * (float) Math.sin(X) * (float) Math.cos(Y);
        float z = R * (float) Math.cos(X) * (float) Math.cos(Y);
        float y = R * (float) Math.sin(Y);
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, x, y, z, upX, upY, upZ);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        for (Frame singleFrame : objectsToDraw) {
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, singleFrame.mModelMatrix, 0);
            singleFrame.draw(scratch);
        }

    }

    public void setX(float shift) {
        this.X = shift;
    }

    public void setY(float shift) {
        this.Y = shift;
    }

}
