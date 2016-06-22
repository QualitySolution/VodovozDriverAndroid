package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.TrackPoint;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei Vinogradov on 22.06.16.
 * (c) Quality Solution Ltd.
 */
public class SendMessageTask extends AsyncTask<Object, Void, AsyncTaskResult<Boolean>> {
    private final List<IAsyncTaskListener<AsyncTaskResult<Boolean>>> listeners = new ArrayList<>();

    public void addListener(IAsyncTaskListener<AsyncTaskResult<Boolean>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected AsyncTaskResult<Boolean> doInBackground(Object... args) {
        AsyncTaskResult<Boolean> result;
        String METHOD_NAME = NetworkWorker.METHOD_SEND_MESSAGE;

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.CHAT_SERVICE_URL);

        SoapObject request = new SoapObject(NetworkWorker.NAMESPACE, METHOD_NAME);
        request.addProperty(NetworkWorker.FIELD_AUTH_KEY, args[0].toString());
        request.addProperty(NetworkWorker.FIELD_MESSAGE, args[1].toString());

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        System.setProperty("http.keepAlive", "false");

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME, NetworkWorker.ACTION_INTERFACE_CHAT), envelope, headerPropertyArrayList);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = new AsyncTaskResult<>(Boolean.parseBoolean(response.getValue().toString()));
        } catch (XmlPullParserException | IOException e) {
            result = new AsyncTaskResult<>(e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Boolean> result) {
        for (IAsyncTaskListener<AsyncTaskResult<Boolean>> listener : listeners) {
            listener.AsyncTaskCompleted(result);
        }
    }

}
