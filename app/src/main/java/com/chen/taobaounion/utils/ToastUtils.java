package com.chen.taobaounion.utils;

import android.widget.Toast;

import com.chen.taobaounion.base.BaseApplication;

public class ToastUtils {

    private static Toast sToast;

    public static void showToast(String msg) {
        if (sToast == null) {
            sToast = Toast.makeText(BaseApplication.getAppContext(), msg, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(msg);
        }
        sToast.show();
    }
}
