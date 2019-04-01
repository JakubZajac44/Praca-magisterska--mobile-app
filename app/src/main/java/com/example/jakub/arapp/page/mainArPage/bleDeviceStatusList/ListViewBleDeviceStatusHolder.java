package com.example.jakub.arapp.page.mainArPage.bleDeviceStatusList;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import lombok.Getter;
import lombok.Setter;

public class ListViewBleDeviceStatusHolder extends RecyclerView.ViewHolder {

    @Setter
    @Getter
    private TextView itemSubTextView;
    @Setter
    @Getter
    private TextView itemNameTextView;
    @Setter
    @Getter
    private ImageView itemStatusImageView;

    public ListViewBleDeviceStatusHolder(@NonNull View itemView) {
        super(itemView);
    }
}
