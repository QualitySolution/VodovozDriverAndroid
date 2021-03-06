package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei Vinogradov on 07.06.16.
 * (c) Quality Solution Ltd.
 */

public class LoginTask extends AsyncTask<String, Void, AsyncTaskResult<String>> {
    private final List<IAsyncTaskListener<AsyncTaskResult<String>>> listeners = new ArrayList<>();

    public void addListener(IAsyncTaskListener<AsyncTaskResult<String>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected AsyncTaskResult<String> doInBackground(String... args) {
        AsyncTaskResult<String> result;
        String METHOD_NAME = NetworkWorker.METHOD_LOGIN;

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ANDROID_SERVICE_URL);
        SoapObject request = new SoapObject(NetworkWorker.NAMESPACE, METHOD_NAME);
        request.addProperty(NetworkWorker.FIELD_LOGIN, args[0]);
        request.addProperty(NetworkWorker.FIELD_PASSWORD, args[1]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        System.setProperty("http.keepAlive", "false");

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME, NetworkWorker.ACTION_INTERFACE_ANDROID), envelope, headerPropertyArrayList);
            SoapPrimitive primitive = (SoapPrimitive)envelope.getResponse();
            if (primitive != null && primitive.getValue() != null) {
                result = new AsyncTaskResult<>(primitive.getValue().toString());
            } else {
                result = new AsyncTaskResult<>(new NullPointerException());
            }
        } catch (XmlPullParserException | IOException e) {
            result = new AsyncTaskResult<>(e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<String> result) {
        for (IAsyncTaskListener<AsyncTaskResult<String>> listener : listeners) {
            listener.AsyncTaskCompleted(result);
        }
    }
}