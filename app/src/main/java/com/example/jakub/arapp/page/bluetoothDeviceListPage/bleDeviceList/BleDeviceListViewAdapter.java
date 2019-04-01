package com.example.jakub.arapp.page.bluetoothDeviceListPage.bleDeviceList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.jakub.arapp.R;

import java.util.List;

public class BleDeviceListViewAdapter extends BaseAdapter{
    private List<ListViewBleDeviceItem> listViewItemDtoListBleDevice = null;

    private Context ctx = null;

    public BleDeviceListViewAdapter(Context ctx, List<ListViewBleDeviceItem> listViewItemDtoListBleDevice) {
        this.ctx = ctx;
        this.listViewItemDtoListBleDevice = listViewItemDtoListBleDevice;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if(listViewItemDtoListBleDevice !=null)
        {
            ret = listViewItemDtoListBleDevice.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int itemIndex) {
        Object ret = null;
        if(listViewItemDtoListBleDevice !=null) {
            ret = listViewItemDtoListBleDevice.get(itemIndex);
        }
        return ret;
    }

    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    @Override
    public View getView(int itemIndex, View convertView, ViewGroup viewGroup) {

        ListViewBleDeviceHolder viewHolder = null;

        if(convertView!=null)
        {
            viewHolder = (ListViewBleDeviceHolder) convertView.getTag();
        }else
        {
            convertView = View.inflate(ctx, R.layout.ble_device_item, null);

            CheckBox listItemCheckbox =  convertView.findViewById(R.id.bleDeviceItemCheckBox);

            TextView listItemAddressText = convertView.findViewById(R.id.bleDeviceItemAddress);

            TextView listItemNameText = convertView.findViewById(R.id.bleDeviceItemName);

            viewHolder = new ListViewBleDeviceHolder(convertView);

            viewHolder.setItemCheckbox(listItemCheckbox);

            viewHolder.setItemAddressTextView(listItemAddressText);

            viewHolder.setItemNameTextView(listItemNameText);

            convertView.setTag(viewHolder);
        }

        ListViewBleDeviceItem listViewBleDeviceItem = listViewItemDtoListBleDevice.get(itemIndex);
        viewHolder.getItemCheckbox().setChecked(listViewBleDeviceItem.isChecked());
        viewHolder.getItemAddressTextView().setText(listViewBleDeviceItem.getItemAddressText());
        viewHolder.getItemNameTextView().setText(listViewBleDeviceItem.getItemNameText());

        return convertView;
    }
}
