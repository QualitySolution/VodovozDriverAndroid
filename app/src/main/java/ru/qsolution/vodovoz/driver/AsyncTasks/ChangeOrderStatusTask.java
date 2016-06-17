package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
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

import ru.qsolution.vodovoz.driver.R;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei Vinogradov on 17.06.16.
 * (c) Quality Solution Ltd.
 */

public class ChangeOrderStatusTask extends AsyncTask<String, Void, AsyncTaskResult<Boolean>> {
    private final List<IAsyncTaskListener<AsyncTaskResult<Boolean>>> listeners = new ArrayList<>();
    ProgressDialog progressDialog;
    Activity activity;

    public ChangeOrderStatusTask(Activity activity) {
        this.activity = activity;
    }

    public void addListener(IAsyncTaskListener<AsyncTaskResult<Boolean>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.wait_for_status_change), true);
    }


    @Override
    protected AsyncTaskResult<Boolean> doInBackground(String... args) {
        AsyncTaskResult<Boolean> result;
        String METHOD_NAME = NetworkWorker.METHOD_CHANGE_ORDER_STATUS;

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.SERVICE_URL);

        SoapObject request = new SoapObject(NetworkWorker.NAMESPACE, METHOD_NAME);
        request.addProperty(NetworkWorker.FIELD_AUTH_KEY, args[0]);
        request.addProperty(NetworkWorker.FIELD_ORDER_ID, args[1]);
        request.addProperty(NetworkWorker.FIELD_STATUS, args[2]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        System.setProperty("http.keepAlive", "false");

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope, headerPropertyArrayList);
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
        progressDialog.dismiss();
    }
}
