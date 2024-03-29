/*
 * Copyright (C) 2013 The Android Open Source Project
 * Copyright (C) 2014-2018 Advanced Card Systems Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acs.btdemo;

import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private Button btn_scan;
    private ImageView img;
    private AnimationDrawable anim;
    private ImageView img2;
    private AnimationDrawable anim2;


    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 2;
    /* Stops scanning after 10 seconds. */
    private static final long SCAN_PERIOD = 10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_content_simple);

//        getSupportActionBar().setTitle(R.string.title_devices);
        img=(ImageView) findViewById(R.id.anim_scan);
        img.setBackgroundResource(R.drawable.animation_scan);
        img.setVisibility(View.INVISIBLE);

        img2=(ImageView) findViewById(R.id.anim_scan2);
        img2.setBackgroundResource(R.drawable.animation_scan_2);
        img2.setVisibility(View.INVISIBLE);

        btn_scan=(Button) findViewById(R.id.menu_scan);

        if (!mScanning){
            btn_scan.setEnabled(true);
            img.setVisibility(View.VISIBLE);
            anim=(AnimationDrawable) img.getBackground();
            anim.start();

            img2.setVisibility(View.VISIBLE);
            anim2=(AnimationDrawable) img2.getBackground();
            anim2.start();

            btn_scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLeDeviceListAdapter.clear();
                    scanLeDevice(true);

                    img.setVisibility(View.VISIBLE);
                    anim=(AnimationDrawable) img.getBackground();
                    anim.start();

                    img2.setVisibility(View.VISIBLE);
                    anim2=(AnimationDrawable) img2.getBackground();
                    anim2.start();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            anim.stop();
                            anim2.stop();
                            img.setVisibility(View.INVISIBLE);
                            img2.setVisibility(View.INVISIBLE);
                        }
                    }, 5000);
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    anim.stop();
                    anim2.stop();
                    img.setVisibility(View.INVISIBLE);
                    img2.setVisibility(View.INVISIBLE);
                }
            }, 5000);
        }else{
            btn_scan.setBackgroundColor(Color.parseColor("#ffffff"));
            btn_scan.setEnabled(false);
            anim.stop();
            anim2.stop();
        }




        mHandler = new Handler();
        /*
         * Use this check to determine whether BLE is supported on the device.
         * Then you can selectively disable BLE-related features.
         */
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        /*
         * Initializes a Bluetooth adapter. For API level 18 and above, get a
         * reference to BluetoothAdapter through BluetoothManager.
         */
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        /* Checks if Bluetooth is supported on the device. */
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        //toolbar.inflateMenu(R.menu.main);
//        getMenuInflater().inflate(R.menu.main, menu);
//        for (int i = 0; i < menu.size(); i++) {
//            menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//            menu.findItem(R.id.menu_stop).setVisible(true);
//            menu.findItem(R.id.menu_scan).setVisible(false);
//            menu.findItem(R.id.menu_refresh).setActionView(
//                    R.layout.actionbar_indeterminate_progress);
//        }

//        if (!mScanning) {
//            menu.findItem(R.id.menu_stop).setVisible(false);
//            menu.findItem(R.id.menu_scan).setVisible(true);
//            menu.findItem(R.id.menu_refresh).setActionView(null);
//        } else {
//            menu.findItem(R.id.menu_stop).setVisible(true);
//            menu.findItem(R.id.menu_scan).setVisible(false);
//            menu.findItem(R.id.menu_refresh).setActionView(
//                    R.layout.actionbar_indeterminate_progress);
//        }
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        case R.id.menu_scan:
//            mLeDeviceListAdapter.clear();
//            scanLeDevice(true);
//            break;
//        case R.id.menu_stop:
//            scanLeDevice(false);
//            break;
//        case R.id.menu_about:
//            DialogFragment fragment = new VersionInfoDialogFragment();
//            fragment.show(getSupportFragmentManager(), "VersionInfo");
//            break;
//        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Ensures Bluetooth is enabled on the device. If Bluetooth is not
         * currently enabled, fire an intent to display a dialog asking the user
         * to grant permission to enable it.
         */
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            /* Request access coarse location permission. */
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
            }
        }

        /* Initializes list view adapter. */
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* User chose not to enable Bluetooth. */
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) {
            return;
        }
        scanLeDevice(false);
        final Intent intent = new Intent(this, ListWtrActivity.class);
        intent.putExtra(ListWtrActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(ListWtrActivity.EXTRAS_DEVICE_ADDRESS,
                device.getAddress());
        startActivity(intent);
//        final Intent intent = new Intent(this, ReaderActivity.class);
//        intent.putExtra(ReaderActivity.EXTRAS_DEVICE_NAME, device.getName());
//        intent.putExtra(ReaderActivity.EXTRAS_DEVICE_ADDRESS,
//                device.getAddress());
//        startActivity(intent);
    }

    private synchronized void scanLeDevice(final boolean enable) {
        if (enable) {
            /* Stops scanning after a pre-defined scan period. */
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mScanning) {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            invalidateOptionsMenu();
        } else if (mScanning) {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            invalidateOptionsMenu();
        }
    }

    /* Adapter for holding devices found through scanning. */
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            /* General ListView optimization code. */
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view
                        .findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view
                        .findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    /* Device scan callback. */
    private LeScanCallback mLeScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
