package com.dizsun.currency;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sundiz on 16/11/24.
 */

public class PrefsMgr {
    private static SharedPreferences sSharedPreferences;
    public static void setString(Context context,String local,String code){
        sSharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putString(local,code);
        editor.commit();
    }
    public static String getString(Context context,String local){
        sSharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        return sSharedPreferences.getString(local,null);
    }
}
