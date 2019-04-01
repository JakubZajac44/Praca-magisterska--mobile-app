package com.example.jakub.arapp.utility;

public class Ints {

    public static int convertDataToInt(byte[] data)
    {
        if(data == null)return -1;
        int a = (data[0] & 0xFF)
                |  ((data[1] & 0xFF) << 8)
                |  ((data[2] & 0xFF) << 16)
                |  ((data[3] & 0xFF) << 24);
        return  (int)Float.intBitsToFloat(a);
    }
}