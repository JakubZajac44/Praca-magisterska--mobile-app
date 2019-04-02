package com.example.jakub.arapp.model.device;

import lombok.Getter;
import lombok.Setter;

public abstract class IoTDevice {
    @Getter
    @Setter
    protected String name;
    @Getter
    @Setter
    protected int status;
    @Getter
    @Setter
    String sample;


}
