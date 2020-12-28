package com.wahaha.mihook;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;

import com.wahaha.mihook.utils.MiUIInfo;
import com.wahaha.mihook.utils.RuntimeParam;

import java.util.Objects;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XC_MethodReplacement.returnConstant;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            final Class<?> clazz = XposedHelpers.findClass(RuntimeParam.class.getName(), lpparam.classLoader);
            XposedHelpers.setStaticBooleanField(clazz, "XposedIsActive", true);
        }
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context context = (Context) param.args[0];
                miuiHook(context, lpparam);
            }
        });
    }

    private void miuiHook(Context context, XC_LoadPackage.LoadPackageParam lpparam) {
        String miuiVersion = MiUIInfo.getMiUIVersion(context);
        if (!lpparam.packageName.equals("com.miui.securitycenter")) return;
        switch (Objects.requireNonNull(miuiVersion)) {
            case "":
                XposedBridge.log("当前系统非MIUI系统");
                break;
            case "V11":
                miui11UsbInstall(lpparam.classLoader);
                break;
            case "V12":
                miui12UsbInstall(lpparam.classLoader);
                break;
        }
    }

    private void miui11UsbInstall(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.miui.permcenter.install.AdbInstallVerifyActivity", classLoader, "onCreate", android.os.Bundle.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.callMethod(param.thisObject, "setEnabled", true);
                return null;
            }
        });
    }

    private void miui12UsbInstall(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.miui.permcenter.install.AdbInstallVerifyActivity", classLoader, "onCreate", android.os.Bundle.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.callMethod(param.thisObject, "a", true);
                return null;
            }
        });
        XposedHelpers.findAndHookMethod("com.miui.permcenter.install.AdbInstallActivity", classLoader, "onCreate", android.os.Bundle.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.callMethod(param.thisObject, "onCreate", param.args[0]);
                XposedHelpers.callMethod(param.thisObject, "onClick", new Class[]{DialogInterface.class, int.class}, null, -2);
                XposedHelpers.callMethod(param.thisObject, "finish");
                return null;
            }
        });
    }
}
