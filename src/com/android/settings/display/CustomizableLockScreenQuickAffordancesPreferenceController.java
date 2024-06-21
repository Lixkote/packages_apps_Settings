/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.utils.ThreadUtils;

/**
 * Preference for accessing an experience to customize lock screen quick affordances.
 */
public class CustomizableLockScreenQuickAffordancesPreferenceController extends
        BasePreferenceController implements PreferenceControllerMixin {

    public CustomizableLockScreenQuickAffordancesPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return CustomizableLockScreenUtils.isFeatureEnabled(mContext)
                ? AVAILABLE
                : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final Preference preference = screen.findPreference(getPreferenceKey());
        if (preference != null) {
            preference.setOnPreferenceClickListener(preference1 -> {
                final Intent intent = CustomizableLockScreenUtils.newIntent();
                final String packageName =
                        mContext.getString(R.string.config_wallpaper_picker_package);
                if (!TextUtils.isEmpty(packageName)) {
                    intent.setPackage(packageName);
                }
                intent.putExtra("destination", "quick_affordances");

                // Check if there's an activity to handle the intent
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                } else {
                    // Handle the error gracefully, e.g., show a toast
                    Toast.makeText(mContext, R.string.wallpaper_picker_not_found, Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            refreshSummary(preference);
        }
    }

    @Override
    protected void refreshSummary(Preference preference) {
        ThreadUtils.postOnBackgroundThread(() -> {
            final CharSequence summary =
                    CustomizableLockScreenUtils.getQuickAffordanceSummary(mContext);
            ThreadUtils.postOnMainThread(() -> preference.setSummary(summary));
        });
    }
}
