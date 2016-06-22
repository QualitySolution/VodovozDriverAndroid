package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.Message;
import ru.qsolution.vodovoz.driver.R;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei Vinogradov on 22.06.16.
 * (c) Quality Solution Ltd.
 */
public class GetChatMessagesTask extends AsyncTask<String, Void, AsyncTaskResult<ArrayList<Message>>> {
    private final List<IAsyncTaskListener<AsyncTaskResult<ArrayList<Message>>>> listeners = new ArrayList<>();
    private final LinearLayout linlaHeaderProgress;

    public GetChatMessagesTask(Activity activity) {
        linlaHeaderProgress = (LinearLayout) activity.findViewById(R.id.linlaHeaderProgress);
    }

    public void addListener(IAsyncTaskListener<AsyncTaskResult<ArrayList<Message>>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected AsyncTaskResult<ArrayList<Message>> doInBackground(String... args) {
        AsyncTaskResult<ArrayList<Message>> result;
        String METHOD_NAME = NetworkWorker.METHOD_GET_MESSAGES;

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.CHAT_SERVICE_URL);

        SoapObject request = new SoapObject(NetworkWorker.NAMESPACE, METHOD_NAME);
        request.addProperty(NetworkWorker.FIELD_AUTH_KEY, args[0]);
        request.addProperty(NetworkWorker.FIELD_DAYS, Integer.parseInt(args[1]));

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        System.setProperty("http.keepAlive", "false");

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME, NetworkWorker.ACTION_INTERFACE_CHAT), envelope, headerPropertyArrayList);
            ArrayList<Message> messages;
            SoapObject messagesList = (SoapObject) envelope.getResponse();
            messages = new ArrayList<>();

            if (messagesList != null) {
                for (int i = 0; i < messagesList.getPropertyCount(); i++) {
                    Object property = messagesList.getProperty(i);
                    if (property instanceof SoapObject) {
                        SoapObject soapObject = (SoapObject) property;
                        Message order = new Message(soapObject);
                        messages.add(order);
                    }
                }
            }

            result = new AsyncTaskResult<>(messages);
        } catch (XmlPullParserException | IOException e) {
            result = new AsyncTaskResult<>(e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<ArrayList<Message>> result) {
        for (IAsyncTaskListener<AsyncTaskResult<ArrayList<Message>>> listener : listeners) {
            listener.AsyncTaskCompleted(result);
        }
    }
}
