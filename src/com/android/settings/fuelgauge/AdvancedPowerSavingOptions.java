/*
 *  Copyright (C) 2020 ZenX-OS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.android.settings.fuelgauge;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import androidx.preference.*;
import androidx.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import com.android.settings.R;
import android.content.Intent;

import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settings.development.OverlayCategoryPreferenceController;
import com.android.settings.dashboard.DashboardFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;

import com.android.settings.SettingsPreferenceFragment;
import com.zenx.support.preferences.SystemSettingMasterSwitchPreference;

public class AdvancedPowerSavingOptions extends DashboardFragment implements
        OnPreferenceChangeListener {

    private static final String SENSOR_BLOCK = "sensor_block";
    private static final String SCREEN_STATE_TOGGLES_ENABLE = "screen_state_toggles_enable_key";

    private SystemSettingMasterSwitchPreference mSensorBlock;
    private SystemSettingMasterSwitchPreference mEnableScreenStateToggles;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
         final ContentResolver resolver = getActivity().getContentResolver();

        mSensorBlock = (SystemSettingMasterSwitchPreference) findPreference(SENSOR_BLOCK);
        int sensorBlock = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.SENSOR_BLOCK, 0, UserHandle.USER_CURRENT);
        mSensorBlock.setChecked(sensorBlock != 0);
        mSensorBlock.setOnPreferenceChangeListener(this);

        mEnableScreenStateToggles = (SystemSettingMasterSwitchPreference) findPreference(SCREEN_STATE_TOGGLES_ENABLE);
        int enabled = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.START_SCREEN_STATE_SERVICE, 0, UserHandle.USER_CURRENT);
        mEnableScreenStateToggles.setChecked(enabled != 0);
        mEnableScreenStateToggles.setOnPreferenceChangeListener(this);


    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.advanced_powersaving_options;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        return controllers;
    }

    @Override
    protected String getLogTag() {
        return "AdvancedPowerSavingOptions";
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSensorBlock) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
		            SENSOR_BLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mEnableScreenStateToggles) {
            boolean value = (Boolean) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.START_SCREEN_STATE_SERVICE, value ? 1 : 0, UserHandle.USER_CURRENT);
            Intent service = (new Intent())
                .setClassName("com.android.systemui", "com.android.systemui.screenstate.ScreenStateService");
            if (value) {
                getActivity().stopService(service);
                getActivity().startService(service);
            } else {
                getActivity().stopService(service);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ZENX_SETTINGS;
    }
}
