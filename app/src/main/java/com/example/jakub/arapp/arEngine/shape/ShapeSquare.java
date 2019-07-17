package com.example.jakub.arapp.arEngine.shape;

public abstract class ShapeSquare {

    protected final String vertexShaderCodee =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    " v_TextureCoordinates = a_TextureCoordinates;" +
                    "}";
    protected final String fragmentShaderCodee =
            "precision mediump float;" +
                    "uniform sampler2D u_TextureUnit;"+
                    "varying vec2 v_TextureCoordinates;" +
                    "void main() {" +
                    "   gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";

    protected short drawOrder[] = {0, 1, 2, 0, 2, 3};
    protected float[] mModelMatrix = new float[16];
    protected float[] cubeTextureCoordinateData;
    protected float textureCoordinates[] = {0.0f, 0.0f, //
            0.0f, 1.0f, //
            1.0f, 1.0f, //
            1.0f, 0.0f, //
    };

    float color[] = new float[4];

    public float[] getmModelMatrix() {
        return mModelMatrix;
    }
}
