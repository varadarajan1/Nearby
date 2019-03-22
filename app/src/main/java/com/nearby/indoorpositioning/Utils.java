package com.nearby.indoorpositioning;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Utils {
    static final String KEY_CACHED_MESSAGES = "cached-messages";

    /**
     * Fetches message strings stored in {@link SharedPreferences}.
     *
     * @param context The context.
     * @return  A list (possibly empty) containing message strings.
     */
    static List<String> getCachedMessages(Context context) {
        SharedPreferences sharedPrefs = getSharedPreferences(context);
        String cachedMessagesJson = sharedPrefs.getString(KEY_CACHED_MESSAGES, "");
        if (TextUtils.isEmpty(cachedMessagesJson)) {
            return Collections.emptyList();
        } else {
            Type type = new TypeToken<List<String>>() {}.getType();
            return new Gson().fromJson(cachedMessagesJson, type);
        }
    }

    /**
     * Saves a message string to {@link SharedPreferences}.
     *
     * @param context The context.
     * @param message The Message whose payload (as string) is saved to SharedPreferences.
     */
    static void saveFoundMessage(Context context, Message message){
        ArrayList<String> cachedMessages = new ArrayList<>(getCachedMessages(context));
        Set<String> cachedMessagesSet = new HashSet<>(cachedMessages);
        String messageString = new String(message.getContent());
        if (!cachedMessagesSet.contains(messageString)) {
            cachedMessages.add(0, new String(message.getContent()));
            getSharedPreferences(context)
                    .edit()
                    .putString(KEY_CACHED_MESSAGES, new Gson().toJson(cachedMessages))
                    .apply();
        }
    }

    static void saveFoundMessage(Context context, Message message, BleSignal signal){
        ArrayList<String> cachedMessages = new ArrayList<>(getCachedMessages(context));
        final Gson gsonParser = new Gson();
        BeaconDataWithSignalStrength existingMessage = null;
        if(cachedMessages.stream().count()>0)
         existingMessage = gsonParser.fromJson(cachedMessages.get(0), BeaconDataWithSignalStrength.class);
        BeaconData newMessage = gsonParser.fromJson(new String(message.getContent()), BeaconData.class);
        if(isNewMessageBLENearer(signal, existingMessage, newMessage)){
            cachedMessages.add(0, gsonParser.toJson(newMessage));
            getSharedPreferences(context)
                    .edit()
                    .putString(KEY_CACHED_MESSAGES, gsonParser.toJson(cachedMessages))
                    .apply();
        }
    }

    private static boolean isNewMessageBLENearer(BleSignal signal, BeaconDataWithSignalStrength existingMessage, BeaconData newMessage) {
        return existingMessage==null || signal.getRssi()>existingMessage.getRssi();
    }

    /**
     * Removes a message string from {@link SharedPreferences}.
     * @param context The context.
     * @param message The Message whose payload (as string) is removed from SharedPreferences.
     */
    static void removeLostMessage(Context context, Message message) {
        ArrayList<String> cachedMessages = new ArrayList<>(getCachedMessages(context));
        cachedMessages.remove(new String(message.getContent()));
        getSharedPreferences(context)
                .edit()
                .putString(KEY_CACHED_MESSAGES, new Gson().toJson(cachedMessages))
                .apply();
    }

    /**
     * Gets the SharedPReferences object that is used for persisting data in this application.
     *
     * @param context The context.
     * @return The single {@link SharedPreferences} instance that can be used to retrieve and modify
     *         values.
     */
    static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                context.getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE);
    }

    static void clearCachedMessages(Context context){
        ArrayList<String> cachedMessages = new ArrayList<>(getCachedMessages(context));
        cachedMessages.clear();
        getSharedPreferences(context)
                .edit()
                .putString(KEY_CACHED_MESSAGES, new Gson().toJson(cachedMessages))
                .apply();
    }
}
