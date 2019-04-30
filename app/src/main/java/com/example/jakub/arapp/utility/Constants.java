package com.example.jakub.arapp.utility;

public class Constants {

    public static final String START_FOREGROUND_ACTION = "Start foreground service";
    public static final String STOP_FOREGROUND_ACTION = "Stop foreground service";
    public static final String CONNECTION_STATE_BLE_DEVICE_ACTION = "Change ble device status";

    public static final String CONNECTED = "CONNECTED";
    public static final String DISCONNECTED = "DISCONNECTED";
    public static final boolean CONNECTED_STATE = true;
    public static final boolean DISCONNECTED_STATE = false;

    public static final int CONNECTED_STATUS = 0;
    public static final int DISCONNECTED_STATUS = 1;
    public static final int UNKNOWN_STATUS = 2;
    public static final int ERROR_STATUS = -1;

    public static final int NON_RATIO = -1;
    public static final int INIT_RATIO = 20;

    public static final double ERATH_R = 6378.14;
    public static final float SCENE_R = 7.0f;

    public static final float[] BLACK_COLOR = {0f,0f,0f,1f};
    public static final float[] WHITE_COLOR = {1f,1f,1f,1f};
    public static final float[] BLUE_COLOR = {0f,0f,1f,1f};
    public static final float[] RED_COLOR = {1f,0f,0f,1f};
    public static final float[] GREEN_COLOR = {0f,1f,0f,1f};
    public static final float[] INVISIBLE_COLOR = {0f,0f,0f,0f};

    public static final String ACCOUNT_REGISTRATION_STATUS_KEY = "ACCOUNT_REGISTRATION_STATUS_KEY";
    public static final String ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY";
    public static final String ACCOUNT_PASSWORD_KEY = "ACCOUNT_PASSWORD_KEY";
    public static final String ACCOUNT_NAME_IV_KEY = "ACCOUNT_NAME_IV_KEY";
    public static final String ACCOUNT_PASSWORD_IV_KEY = "ACCOUNT_PASSWORD_IV_KEY";
    public static final String RUNNING_WITHOUT_LOGIN = "RUNNING_WITHOUT_LOGIN";
    public static final String FINGERPRINT_USER_CONFIRMATION = "FINGERPRINT_USER_CONFIRMATION";

    public static final String ACCOUNT_PIN_KEY = "ACCOUNT_PIN_KEY";
    public static final String ACCOUNT_PIN_IV_KEY = "ACCOUNT_PIN_IV_KEY";

    public static final String ACCOUNT_TOKEN_KEY = "ACCOUNT_TOKEN_KEY";

    public static final int USER_ALREADY_EXIST = 400;
    public static final int USER_NOT_EXIST = 401;

    public static final String USER_CREDENTIALS_KEY = "USER_CREDENTIALS";

}
