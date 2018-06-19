package com.example.affine.beaconscan;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

/**
 * Created by VikashPatel on 29/11/17.
 */

public class PermissionManager {
    private CoordinatorLayout m_coordinatorLayout;
    private Activity m_context;
    private Snackbar m_permissionSnackBar;

    public PermissionManager(Activity context, CoordinatorLayout coordinatorLayout) {
        m_context = context;
        m_coordinatorLayout = coordinatorLayout;
    }


    public boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(m_context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(int permissionCode, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (checkPermission(permission)) {
            return;
        }

        if (m_context.shouldShowRequestPermissionRationale(permission)) {
            notifyUser(Formatter.formatPermission(permission));
        } else {
            String[] permissions = {permission};
            ActivityCompat.requestPermissions(m_context, permissions, permissionCode);
        }
    }

    public void notifyUser(String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            m_permissionSnackBar = Snackbar.make(m_coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE);
            m_permissionSnackBar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAppSetting(m_context);
                }
            });
            m_permissionSnackBar.setText(message);
            m_permissionSnackBar.getView().setBackgroundColor(m_context.getResources().getColor(R.color.errorBackground));
            m_permissionSnackBar.setActionTextColor(Color.WHITE);
            m_permissionSnackBar.show();
        }
    }

    private void openAppSetting(final Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }


}

