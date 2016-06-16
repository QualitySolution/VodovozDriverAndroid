package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

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
    private List<IAsyncTaskListener<AsyncTaskResult<String>>> listeners = new ArrayList<>();

    public void addListener(IAsyncTaskListener<AsyncTaskResult<String>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected AsyncTaskResult<String> doInBackground(String... args) {
        AsyncTaskResult<String> result;
        String METHOD_NAME = "Auth";

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

        SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
        request.addProperty("login", args[0]);
        request.addProperty("password", args[1]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
            SoapPrimitive primitive = (SoapPrimitive)envelope.getResponse();
            result = new AsyncTaskResult<>(primitive.getValue().toString());
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