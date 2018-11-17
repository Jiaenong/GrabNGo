package com.example.user.grabngo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    static final String PREF_USER_ID = "id";
    static final String PREF_CHECK_LOGIN = "logged";
    static final String PREF_CARD_NUMBER = "cardNumber";
    static final String PREF_CARD_NAME = "cardName";
    static final String PREF_EXP_DATE = "expDate";
    static final String PREF_SAVE_DATE = "saveDate";
    static final String PREF_CHECK_SAVE = "saved";
    static final String USER_TYPE = "user";
    static final String LOW_STOCK_ALERT = "false";
    static final String PREF_CARD_TYPE = "card";
    static final String PREF_SAVE_CART = "cart";

    static SharedPreferences getSharedPreference(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getUserType(Context context){
        return getSharedPreference(context).getString(USER_TYPE,"");
    }

    public static void setID(Context context, String id, String user)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(PREF_USER_ID,id);
        editor.putString(USER_TYPE,user);
        editor.commit();
    }

    public static String getID(Context context)
    {
        return getSharedPreference(context).getString(PREF_USER_ID,"");
    }

    public static void setCardNumber(Context context, String cardNumber)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(PREF_CARD_NUMBER, cardNumber);
        editor.commit();
    }

    public static int getCartNumber(Context context)
    {
        return getSharedPreference(context).getInt(PREF_SAVE_CART,0);
    }

    public static void setCartNumber(Context context, int cartNumber)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(PREF_SAVE_CART,cartNumber);
        editor.apply();
    }

    public static String getCardNumber(Context context)
    {
        return getSharedPreference(context).getString(PREF_CARD_NUMBER,"");
    }

    public static void setCardType(Context context, int cardType)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(PREF_CARD_TYPE, cardType);
        editor.commit();
    }

    public static int getCardType(Context context)
    {
        return getSharedPreference(context).getInt(PREF_CARD_TYPE,0);
    }

    public static void setCardName(Context context, String cardName)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(PREF_CARD_NAME, cardName);
        editor.commit();
    }
    public static String getCardName(Context context)
    {
        return getSharedPreference(context).getString(PREF_CARD_NAME,"");
    }

    public static void setSaveDate(Context context, String saveDate)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(PREF_SAVE_DATE, saveDate);
        editor.commit();
    }

    public static String getSaveDate(Context context)
    {
        return getSharedPreference(context).getString(PREF_SAVE_DATE,"");
    }

    public static void setExpDate(Context context, String expDate)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(PREF_EXP_DATE, expDate);
        editor.commit();
    }
    public static String getExpDate(Context context)
    {
        return getSharedPreference(context).getString(PREF_EXP_DATE,"");
    }

    public static void setCheckLogin(Context context, Boolean login)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(PREF_CHECK_LOGIN,login);
        editor.commit();
    }

    public static Boolean getCheckLogin(Context context)
    {
        return getSharedPreference(context).getBoolean(PREF_CHECK_LOGIN,false);
    }

    public static void setCheckSave(Context context, Boolean save)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(PREF_CHECK_SAVE,save);
        editor.commit();
    }

    public static Boolean getCheckSave(Context context)
    {
        return getSharedPreference(context).getBoolean(PREF_CHECK_SAVE,false);
    }

    public static void setCheckAlert(Context context, Boolean save)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(LOW_STOCK_ALERT,save);
        editor.commit();
    }

    public static Boolean getCheckAlert(Context context)
    {
        return getSharedPreference(context).getBoolean(LOW_STOCK_ALERT,false);
    }

    public static void clearData(Context context)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(PREF_CARD_NUMBER);
        editor.remove(PREF_CARD_NAME);
        editor.remove(PREF_EXP_DATE);
        editor.remove(PREF_CHECK_SAVE);
        editor.remove(PREF_SAVE_DATE);
        editor.remove(PREF_CARD_TYPE);
        editor.commit();
    }

    public static void clearUser(Context context)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.clear();
        editor.commit();
    }

    public static void clearCart(Context context)
    {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(PREF_SAVE_CART);
        editor.apply();
    }
}

