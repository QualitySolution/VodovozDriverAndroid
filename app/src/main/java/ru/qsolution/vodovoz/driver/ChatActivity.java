package ru.qsolution.vodovoz.driver;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.qsolution.vodovoz.driver.ArrayAdapters.MessagesListAdapter;
import ru.qsolution.vodovoz.driver.AsyncTasks.AsyncTaskResult;
import ru.qsolution.vodovoz.driver.AsyncTasks.GetChatMessagesTask;
import ru.qsolution.vodovoz.driver.AsyncTasks.IAsyncTaskListener;
import ru.qsolution.vodovoz.driver.AsyncTasks.SendMessageTask;
import ru.qsolution.vodovoz.driver.DTO.Message;
import ru.qsolution.vodovoz.driver.Services.INotificationObserver;
import ru.qsolution.vodovoz.driver.Services.MyFirebaseMessagingService;

/**
 * Created by Andrei Vinogradov on 22.06.16.
 * (c) Quality Solution Ltd.
 */

public class ChatActivity extends AppCompatActivity implements IAsyncTaskListener<AsyncTaskResult<Boolean>>, INotificationObserver {
    private EditText inputMsg;

    // Chat messages list adapter
    private MessagesListAdapter adapter;
    private List<Message> listMessages;
    private GetMessagesListener getMessagesListener;
    private SharedPreferences sharedPref;
    private boolean isActive = false;

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    protected void onDestroy() {
        MyFirebaseMessagingService.RemoveObserver(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Button btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        ListView listViewMessages = (ListView) findViewById(R.id.list_view_messages);
        sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.auth_file_key), Context.MODE_PRIVATE);
        getMessagesListener = new GetMessagesListener(this.getApplicationContext());

        refreshMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SendMessageTask task = new SendMessageTask();
                task.addListener(ChatActivity.this);
                task.execute(sharedPref.getString("Authkey", ""), inputMsg.getText().toString());
                Message msg = new Message("", inputMsg.getText().toString(), new Date());
                appendMessage(msg);
                inputMsg.setText("");
            }
        });

        listMessages = new ArrayList<>();

        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);
        MyFirebaseMessagingService.AddObserver(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MyFirebaseMessagingService.MESSAGE_NOTIFICATION);
    }

    @Override
    public void AsyncTaskCompleted(AsyncTaskResult<Boolean> result) {
        //TODO confirm message delivery
    }

    private void refreshMessages() {
        GetChatMessagesTask getMessages = new GetChatMessagesTask(this);
        getMessages.addListener(getMessagesListener);
        getMessages.execute(sharedPref.getString("Authkey", ""), "1");
    }

    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listMessages.add(m);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void playBeep() {
        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void HandleNotification() {
        refreshMessages();
        playBeep();
    }

    @Override
    public Boolean IsActive() {
        return isActive;
    }

    @Override
    public String NotificationType() {
        return "message";
    }

    private class GetMessagesListener implements IAsyncTaskListener<AsyncTaskResult<ArrayList<Message>>> {
        private final Context context;

        public GetMessagesListener(Context context) {
            this.context = context;
        }

        @Override
        public void AsyncTaskCompleted(AsyncTaskResult<ArrayList<Message>> result) {
            try {
                //If attempt was unsuccessful
                if (result.getException() == null && result.getResult() == null) {
                    Toast toast = Toast.makeText(context, R.string.unable_to_get_messages, Toast.LENGTH_LONG);
                    toast.show();
                }
                //On success
                else if (result.getException() == null && result.getResult() != null) {
                    listMessages.clear();
                    for (Message m : result.getResult()) {
                        appendMessage(m);
                    }
                }
                //If exception occurred
                else {
                    Toast toast = Toast.makeText(context, "Не удалось подключиться к серверу.", Toast.LENGTH_LONG);
                    toast.show();
                    throw result.getException();
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
        }
    }
}
