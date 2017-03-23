package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.BuildConfig;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;
import ru.qsolution.vodovoz.driver.DTO.CheckVersionResult;

/**
 * Created by Andrei Vinogradov on 17.06.16.
 * (c) Quality Solution Ltd.
 */

public class CheckAppVersionTask extends AsyncTask<Void, Void, AsyncTaskResult<CheckVersionResult>> {
    private final List<IAsyncTaskListener<AsyncTaskResult<CheckVersionResult>>> listeners = new ArrayList<>();

    public void addListener(IAsyncTaskListener<AsyncTaskResult<CheckVersionResult>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected AsyncTaskResult<CheckVersionResult> doInBackground(Void... args) {
        AsyncTaskResult<CheckVersionResult> result;
        String METHOD_NAME = NetworkWorker.METHOD_CHECK_APP_VERSION;

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ANDROID_SERVICE_URL);

        SoapObject request = new SoapObject(NetworkWorker.NAMESPACE, METHOD_NAME);
        request.addProperty(NetworkWorker.FIELD_VERSION_CODE, BuildConfig.VERSION_CODE);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        System.setProperty("http.keepAlive", "false");

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME, NetworkWorker.ACTION_INTERFACE_ANDROID), envelope, headerPropertyArrayList);
            SoapObject soapObject = (SoapObject) envelope.getResponse();
            result = new AsyncTaskResult<>(new CheckVersionResult(soapObject));
        } catch (XmlPullParserException | IOException e) {
            result = new AsyncTaskResult<>(e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<CheckVersionResult> result) {
        for (IAsyncTaskListener<AsyncTaskResult<CheckVersionResult>> listener : listeners) {
            listener.AsyncTaskCompleted(result);
        }
    }
}

