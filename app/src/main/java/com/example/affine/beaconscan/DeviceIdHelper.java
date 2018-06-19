package com.example.affine.beaconscan;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by lenovo on 20/11/17.
 */

public class DeviceIdHelper {
    private final Context m_context;

    public DeviceIdHelper(Context context) {
        m_context = context;
    }

    public String getDeviceId() {
        String androidId = Settings.Secure.getString(m_context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }
}

