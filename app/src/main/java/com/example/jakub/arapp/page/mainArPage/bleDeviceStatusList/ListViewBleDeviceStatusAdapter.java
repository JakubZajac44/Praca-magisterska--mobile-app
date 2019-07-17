package com.example.jakub.arapp.page.mainArPage.bleDeviceStatusList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jakub.arapp.R;
import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.model.device.IoTDevice;
import com.example.jakub.arapp.utility.Constants;

import java.util.List;

public class ListViewBleDeviceStatusAdapter extends BaseAdapter {
    private List<IoTDevice> ioTDeviceList = null;

    private Context ctx = null;

    public ListViewBleDeviceStatusAdapter(Context ctx, List<IoTDevice> ioTDevices) {
        this.ctx = ctx;
        this.ioTDeviceList = ioTDevices;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (ioTDeviceList != null) {
            ret = ioTDeviceList.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int itemIndex) {
        Object ret = null;
        if (ioTDeviceList != null) {
            ret = ioTDeviceList.get(itemIndex);
        }
        return ret;
    }

    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    @Override
    public View getView(int itemIndex, View convertView, ViewGroup viewGroup) {

        ListViewBleDeviceStatusHolder viewHolder = null;

        if (convertView != null) {
            viewHolder = (ListViewBleDeviceStatusHolder) convertView.getTag();
        } else {
            convertView = View.inflate(ctx, R.layout.ble_device_item_status, null);

            ImageView listItemStatusText = convertView.findViewById(R.id.bleDeviceItemStatusIcon);

            TextView listItemAddressText = convertView.findViewById(R.id.bleDeviceItemAddressStatus);

            TextView listItemNameText = convertView.findViewById(R.id.bleDeviceItemNameStatus);

            viewHolder = new ListViewBleDeviceStatusHolder(convertView);

            viewHolder.setItemStatusImageView(listItemStatusText);

            viewHolder.setItemSubTextView(listItemAddressText);

            viewHolder.setItemNameTextView(listItemNameText);

            convertView.setTag(viewHolder);
        }

        IoTDevice listViewIotDeviceItem = ioTDeviceList.get(itemIndex);
        Drawable icon = null;
        switch (listViewIotDeviceItem.getStatus()) {
            case Constants.CONNECTED_STATUS:
                icon = ctx.getResources().getDrawable(R.drawable.ic_ble_device_connected_status);
                break;
            case Constants.DISCONNECTED_STATUS:
                icon = ctx.getResources().getDrawable(R.drawable.ic_ble_device_disconnected_status);
                break;
            case Constants.UNKNOWN_STATUS:
                icon = ctx.getResources().getDrawable(R.drawable.ic_ble_device_unknown_status);
                break;
        }
        viewHolder.getItemStatusImageView().setImageDrawable(icon);

        String name = "";
        String sample = ctx.getString(R.string.sample_text) + ": ";
        if (listViewIotDeviceItem instanceof BleDeviceWrapper) {
            BleDeviceWrapper bleDeviceWrapper = (BleDeviceWrapper) listViewIotDeviceItem;
            name = bleDeviceWrapper.getName();
            sample += bleDeviceWrapper.getSample();

        } else if (listViewIotDeviceItem instanceof InternetDeviceWrapper) {
            InternetDeviceWrapper internetDeviceWrapper = (InternetDeviceWrapper) listViewIotDeviceItem;
            name = internetDeviceWrapper.getName() + ", id: " + internetDeviceWrapper.getId();
            sample += internetDeviceWrapper.getSample() + ", " + ctx.getString(R.string.time_sample_text) + ": " + internetDeviceWrapper.getUpdatetime();
        }
        viewHolder.getItemSubTextView().setText(sample);
        viewHolder.getItemNameTextView().setText(name);
        return convertView;
    }
}
