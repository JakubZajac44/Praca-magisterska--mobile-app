package com.example.jakub.arapp.page.bluetoothDeviceListPage.bleDeviceList;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ListViewBleDeviceHolder extends  RecyclerView.ViewHolder {

    private CheckBox itemCheckbox;

    private TextView itemAddressTextView;
    private TextView itemNameTextView;

    public ListViewBleDeviceHolder(View itemView) {
        super(itemView);
    }

    public CheckBox getItemCheckbox() {
        return itemCheckbox;
    }

    public void setItemCheckbox(CheckBox itemCheckbox) {
        this.itemCheckbox = itemCheckbox;
    }

    public TextView getItemAddressTextView() {
        return itemAddressTextView;
    }

    public TextView getItemNameTextView() {
        return itemNameTextView;
    }

    public void setItemAddressTextView(TextView itemAddressTextView) {
        this.itemAddressTextView = itemAddressTextView;
    }

    public void setItemNameTextView(TextView itemNameTextView){
        this.itemNameTextView = itemNameTextView;
    }


}
