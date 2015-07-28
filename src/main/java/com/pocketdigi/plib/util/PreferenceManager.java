package com.pocketdigi.plib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.pocketdigi.plib.core.PApplication;
import com.pocketdigi.plib.core.PLog;

import java.util.WeakHashMap;

/**
 * 配置管理,如果是写入，必须在写完后手动commit
 * 多个写入，建议串联操作
 * Created by fhp on 14-8-22.
 */
public class PreferenceManager {
    private static WeakHashMap<String, PreferenceManager> preferenceManagerWeakHashMap;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private static final String defaultFile="preference";
    private PreferenceManager(String fileName) {
        settings = PApplication.getInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = settings.edit();
    }
    public static PreferenceManager getDefaultManager()
    {
        return PreferenceManager.getManagerWithFileName(defaultFile);
    }
    /**
     * 返回针对指定文件名的PreferenceManager
     *
     * @param fileName 文件名
     * @return
     */
    public static PreferenceManager getManagerWithFileName(String fileName) {
        if (preferenceManagerWeakHashMap == null) {
            preferenceManagerWeakHashMap = new WeakHashMap<>();
        }
        if ((!preferenceManagerWeakHashMap.containsKey(fileName))||preferenceManagerWeakHashMap.get(fileName)==null) {
            PreferenceManager manager = new PreferenceManager(fileName);
            preferenceManagerWeakHashMap.put(fileName, manager);
        }
        return preferenceManagerWeakHashMap.get(fileName);
    }

    /**
     * 写入字符串，所有写入操作执行完后，必须commit
     *
     * @param key
     * @param value
     * @return
     */
    public PreferenceManager putString(String key, String value) {
        if(!TextUtils.isEmpty(value)) {
            editor.putString(key, value);
        }else{
            editor.remove(key);
        }
        return this;
    }

    /**
     * 写入boolean，所有写入操作执行完后，必须commit
     *
     * @param key
     * @param value
     * @return
     */
    public PreferenceManager putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        PLog.d(this, "set " + key + " = " + value);
        return this;
    }

    /**
     * 写入float，所有写入操作执行完后，必须commit
     *
     * @param key
     * @param value
     * @return
     */
    public PreferenceManager putFloat(String key, float value) {
        editor.putFloat(key, value);
        return this;
    }

    /**
     * 写入int，所有写入操作执行完后，必须commit
     *
     * @param key
     * @param value
     * @return
     */
    public PreferenceManager putInt(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    /**
     * 写入long，所有写入操作执行完后，必须commit
     *
     * @param key
     * @param value
     * @return
     */
    public PreferenceManager putLong(String key, long value) {
        editor.putLong(key, value);
        return this;
    }

    public PreferenceManager remove(String key) {
        editor.remove(key);
        return this;
    }

    /**
     * 清除所有配置
     */
    public void clear()
    {
        editor.clear();
        editor.commit();
    }
    /**
     * 保存写入
     */
    public void commit() {
        editor.commit();
    }

    public String getString(String key, String defVal) {
        return settings.getString(key, defVal);
    }

    public boolean getBoolean(String key, boolean defVal) {
        return settings.getBoolean(key, defVal);
    }

    public int getInt(String key, int defVal) {
        return settings.getInt(key, defVal);
    }

    public long getLong(String key, long defVal) {
        return settings.getLong(key, defVal);
    }

    public float getFloat(String key, float defVal) {
        return settings.getFloat(key, defVal);
    }

    /**
     * 写入Object，所有写入操作执行完后，必须commit
     * 原理:使用gson将Object序列化成字符串
     * @param key
     * @param value
     * @return
     */
    public PreferenceManager putObject(String key, Object value) {
        Gson gson=new Gson();
        if(value!=null) {
            String str = gson.toJson(value);
            editor.putString(key, str);
        }else{
            editor.remove(key);
        }
        return this;
    }

    /**
     * 读取Object
     * @param key 保存的key
     * @param classOfT 对象类型
     * @param <T>
     * @return
     */
    public <T> T getObject(String key,Class<T> classOfT)
    {
        String str=settings.getString(key,null);
        if(TextUtils.isEmpty(str))
            return null;
        try {
            Gson gson = new Gson();
            T t = gson.fromJson(str, classOfT);
            return t;
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}