package com.example.jakub.arapp.page.bluetoothScannerPage.bleDeviceList;

import com.example.jakub.arapp.model.device.BleDeviceWrapper;

public class ListViewBleDeviceItem {

    private boolean checked ;

    private BleDeviceWrapper device;

    public ListViewBleDeviceItem(BleDeviceWrapper device){
        this.checked=false;
        this.device=device;
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getItemAddressText() {
        return device.getAddress();
    }

    public String getItemNameText(){
        return device.getName();
    }

}