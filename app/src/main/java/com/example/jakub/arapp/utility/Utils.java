package com.example.jakub.arapp.utility;

import java.io.IOException;

public class Utils {
    public static CharSequence getCharSequence(String text) {
        return (CharSequence)text;
    }

    public static byte[] getByteArryFromString(String text){
        String[] split = text.substring(1, text.length()-1).split(", ");
        byte[] array = new byte[split.length];
        for (int i = 0; i < split.length; i++) {
            array[i] = Byte.parseByte(split[i]);
        }
        return array;
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
