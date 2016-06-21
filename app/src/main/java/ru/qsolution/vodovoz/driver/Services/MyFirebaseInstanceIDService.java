package ru.qsolution.vodovoz.driver.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ru.qsolution.vodovoz.driver.AsyncTasks.AsyncTaskResult;
import ru.qsolution.vodovoz.driver.AsyncTasks.EnablePushNotificationsTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.IAsyncTaskListener;
import ru.qsolution.vodovoz.driver.R;

/**
 * Created by Andrei Vinogradov on 21.06.16.
 * (c) Quality Solution Ltd.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String TOKEN_KEY = "firebase_token";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        saveRegistrationToken(refreshedToken);
        sendTokenToServer(refreshedToken);
    }

    private void saveRegistrationToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (sharedPref.contains(TOKEN_KEY))
            editor.remove(TOKEN_KEY);
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    private void sendTokenToServer(String token) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        EnablePushNotificationsTask task = new EnablePushNotificationsTask();
        task.execute(sharedPref.getString("Authkey", ""), token);
    }
}