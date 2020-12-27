package com.wahaha.mihook.utils;

import android.content.Context;
import android.util.NoSuchPropertyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SystemProperty {
    private final Context mContext;

    public SystemProperty(Context mContext) {
        this.mContext = mContext;
    }

    public String getOrThrow(String key) throws NoSuchPropertyException {
        try {
            ClassLoader classLoader = mContext.getClassLoader();
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            Method methodGet = SystemProperties.getMethod("get", String.class);
            return (String) methodGet.invoke(SystemProperties, key);
        } catch (ClassNotFoundException e) {
            throw new NoSuchPropertyException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new NoSuchPropertyException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new NoSuchPropertyException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new NoSuchPropertyException(e.getMessage());
        }
    }

    public String get(String key) {
        try {
            return getOrThrow(key);
        } catch (NoSuchPropertyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
