package aaacomms.aaa_app;

import android.content.Context;
import android.content.SharedPreferences;

public class AzureID {

    public Boolean getLoginStatus(Context context) {
        SharedPreferences prefs;
        prefs = context.getSharedPreferences(context.getResources().getString(R.string.loginStatus), Context.MODE_PRIVATE);
        return prefs.getBoolean(context.getResources().getString(R.string.loginStatus), false);
    }

    public void storeLoginStatus(Boolean loginStatus, Context context) {
        SharedPreferences prefs;
        prefs = context.getSharedPreferences(context.getResources().getString(R.string.loginStatus), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getResources().getString(R.string.loginStatus), loginStatus).apply();
    }

}