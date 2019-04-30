package com.example.jakub.arapp.arEngine.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.example.jakub.arapp.R;
import com.example.jakub.arapp.arEngine.openGLprovider.MyRender;
import com.example.jakub.arapp.utility.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import lombok.Getter;
import lombok.Setter;

public class Frame extends ShapeSquare {
    static final int CORDS_PER_VERTEX = 3;

    private final int vertexStride = CORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int mProgram;
    private final FloatBuffer textureBuffer;

    @Getter
    @Setter
    float azimuth;
    @Setter
    @Getter
    float pitch;
    private Bitmap bitmap;
    private boolean bitmapChanged = false;
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private Context context;
    private int textureDataHandle;
    private int textureUniformHandle;
    private int textureCoordinateHandle;

    public Frame(Context context, double azimuth, double pitch) {
        this.context = context;
//        this.setColor(Constants.WHITE_COLOR);
//        this.setStatusTexture(Constants.UNKNOWN_STATUS);
        this.prepareCords(azimuth, pitch);
        Matrix.setIdentityM(mModelMatrix, 0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer texCoordinates = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
        texCoordinates.order(ByteOrder.nativeOrder());
        textureBuffer = texCoordinates.asFloatBuffer();
        textureBuffer.put(textureCoordinates);
        textureBuffer.position(0);

        textureDataHandle = this.loadTexture(context, R.drawable.device_icon_green);
        int vertexShader = MyRender.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCodee);
        int fragmentShader = MyRender.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCodee);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private int loadTexture(final Context context, int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    private float[] getCordsFromAngle(double azimuth, double pitch) {

        float R = Constants.SCENE_R;
        double scale = 3.0;
        double azimuthPlus = Math.toRadians(azimuth + scale);
        double azimuthMinus = Math.toRadians(azimuth - scale);
        double pitchPlus = Math.toRadians(pitch + scale);
        double pitchMinus = Math.toRadians(pitch - scale);

        float x1 = R * (float) Math.cos(azimuthMinus) * (float) Math.cos(pitchPlus);
        float z1 = R * (float) Math.sin(azimuthMinus) * (float) Math.cos(pitchPlus);
        float y1 = R * (float) Math.sin(pitchPlus);

        float x2 = R * (float) Math.cos(azimuthMinus) * (float) Math.cos(pitchMinus);
        float z2 = R * (float) Math.sin(azimuthMinus) * (float) Math.cos(pitchMinus);
        float y2 = R * (float) Math.sin(pitchMinus);

        float x3 = R * (float) Math.cos(azimuthPlus) * (float) Math.cos(pitchMinus);
        float z3 = R * (float) Math.sin(azimuthPlus) * (float) Math.cos(pitchMinus);
        float y3 = R * (float) Math.sin(pitchMinus);


        float x4 = R * (float) Math.cos(azimuthPlus) * (float) Math.cos(pitchPlus);
        float z4 = R * (float) Math.sin(azimuthPlus) * (float) Math.cos(pitchPlus);
        float y4 = R * (float) Math.sin(pitchPlus);

        float[] coords = {
                x1, y1, z1,   // top left
                x2, y2, z2,   // bottom left
                x3, y3, z3,   // bottom right
                x4, y4, z4    // top right
        };

        return coords;
    }

    public void setColor(float[] colors) {
        color[0] = colors[0];
        color[1] = colors[1];
        color[2] = colors[2];
        color[3] = colors[3];
    }

    public void setStatusTexture(int status) {
        bitmapChanged = true;
        int idDrawable;
        switch (status) {
            case Constants.CONNECTED_STATUS:
                idDrawable = R.drawable.device_icon_green;
                break;
            case Constants.DISCONNECTED_STATUS:
                idDrawable = R.drawable.device_icon_red;
                break;
            case Constants.UNKNOWN_STATUS:
                idDrawable = R.drawable.device_icon_black;
                break;
            default:
                idDrawable = R.drawable.device_icon_white;
                break;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        bitmap = BitmapFactory.decodeResource(context.getResources(), idDrawable, options);  //Read in the resource
    }

    public void changeBitmap() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmapChanged = false;
    }


    public void draw(float[] mvpMatrix, GL10 gl) {
        GLES20.glUseProgram(mProgram);
        if (bitmapChanged) {
            changeBitmap();
        }

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, CORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        textureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TextureCoordinates");
        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        textureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLES20.glUniform1i(textureUniformHandle, 0);

//        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
//        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);


        gl.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private void prepareCords(double azimuth, double pitch) {

        this.azimuth = (float) azimuth;
        this.pitch = (float) pitch;
        float[] squareCords = this.getCordsFromAngle(azimuth, pitch);
        cubeTextureCoordinateData = this.getCordsFromAngle(azimuth, pitch);

//        System.arraycopy(squareCords, 0, aaa, 0, squareCords.length);
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCords);
        vertexBuffer.position(0);
    }

    public void changeCords(double azimuth, double pitch) {
        this.prepareCords(azimuth, pitch);
    }
}
