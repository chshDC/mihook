package com.wahaha.mihook.utils;

import android.content.Context;
import android.util.Log;

public class MiUIInfo {
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    //手机品牌
    private static final String KEY_SYSTEM_BUILD_BRAND = "ro.product.system.brand";
    //手机型号
    private static final String KEY_SYSTEM_BUILD_MODEL = "ro.product.system.model";
    private static final String tag = "MiUIInfo";

    public static boolean isMiUI(Context context) {
        String brand = getCode(context, KEY_SYSTEM_BUILD_BRAND);
        Log.e(tag, brand);
        return brand != null && brand.length() > 0 && brand.equals("Xiaomi");
    }

    public static String getMiUIVersion(Context context) {
        String miUIVersion = getCode(context, KEY_MIUI_VERSION_NAME);
        if (miUIVersion == null || miUIVersion.length() <= 0) {
            return null;
        } else {
            return miUIVersion;
        }

    }

    private static String getCode(Context context, String key) {
        SystemProperty systemProperty = new SystemProperty(context);
        return systemProperty.get(key);
    }

}
