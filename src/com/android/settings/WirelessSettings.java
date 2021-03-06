/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.android.settings;

import com.android.settings.bluetooth.BluetoothEnabler;
import com.android.settings.wifi.WifiEnabler;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;

public class WirelessSettings extends PreferenceActivity {

    private static final String KEY_TOGGLE_AIRPLANE = "toggle_airplane";
    private static final String KEY_TOGGLE_BLUETOOTH = "toggle_bluetooth";
    private static final String KEY_TOGGLE_WIFI = "toggle_wifi";
    private static final String KEY_TOGGLE_TETHERING = "toggle_tethering";
    
    private static final String KEY_WIFI_SETTINGS = "wifi_settings";
    private static final String KEY_VPN_SETTINGS = "vpn_settings";

    private WifiEnabler mWifiEnabler;
    private AirplaneModeEnabler mAirplaneModeEnabler;
    private BluetoothEnabler mBtEnabler;
    private TetheringEnabler mTetheringEnabler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.wireless_settings);

        initToggles();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        mWifiEnabler.resume();
        mAirplaneModeEnabler.resume();
        mBtEnabler.resume();
        mTetheringEnabler.resume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        mWifiEnabler.pause();
        mAirplaneModeEnabler.pause();
        mBtEnabler.pause();
        mTetheringEnabler.pause();
    }
    
    private void initToggles() {
        
        Preference airplanePreference = findPreference(KEY_TOGGLE_AIRPLANE);
        Preference wifiPreference = findPreference(KEY_TOGGLE_WIFI);
        Preference btPreference = findPreference(KEY_TOGGLE_BLUETOOTH);
        Preference wifiSettings = findPreference(KEY_WIFI_SETTINGS);
        Preference vpnSettings = findPreference(KEY_VPN_SETTINGS);
        Preference tetheringPreference = findPreference(KEY_TOGGLE_TETHERING);

        mWifiEnabler = new WifiEnabler(
                this, (WifiManager) getSystemService(WIFI_SERVICE),
                (CheckBoxPreference) wifiPreference);
        mAirplaneModeEnabler = new AirplaneModeEnabler(
                this, (CheckBoxPreference) airplanePreference);
        
        mBtEnabler = new BluetoothEnabler(
                this, (CheckBoxPreference) btPreference);
        
        mTetheringEnabler = new TetheringEnabler(
                this, (CheckBoxPreference) tetheringPreference);

        // manually set up dependencies for Wifi if its radio is not toggleable in airplane mode
        String toggleableRadios = Settings.System.getString(getContentResolver(),
                Settings.System.AIRPLANE_MODE_TOGGLEABLE_RADIOS);
        if (toggleableRadios == null || !toggleableRadios.contains(Settings.System.RADIO_WIFI)) {
            wifiPreference.setDependency(airplanePreference.getKey());
            wifiSettings.setDependency(airplanePreference.getKey());
            vpnSettings.setDependency(airplanePreference.getKey());
            tetheringPreference.setDependency(airplanePreference.getKey());
        }
    }

}
