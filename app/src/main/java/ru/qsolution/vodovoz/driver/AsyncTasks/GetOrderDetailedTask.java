package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.Order;
import ru.qsolution.vodovoz.driver.R;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei Vinogradov on 07.06.16.
 * (c) Quality Solution Ltd.
 */

public class GetOrderDetailedTask extends AsyncTask<String, Void, AsyncTaskResult<Order>> {
    private List<IAsyncTaskListener<AsyncTaskResult<Order>>> listeners = new ArrayList<>();
    private LinearLayout linlaHeaderProgress;
    private WeakReference<Activity> weakActivity;

    public GetOrderDetailedTask(Activity activity) {
        weakActivity = new WeakReference<>(activity);
        linlaHeaderProgress = (LinearLayout) activity.findViewById(R.id.linlaHeaderProgress);
    }

    public void addListener(IAsyncTaskListener<AsyncTaskResult<Order>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected void onPreExecute() {
        linlaHeaderProgress.setVisibility(View.VISIBLE);
    }

    @Override
    protected AsyncTaskResult<Order> doInBackground(String... args) {
        AsyncTaskResult<Order> result;
        String METHOD_NAME = "GetOrderDetailed";

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

        SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
        request.addProperty("authKey", args[0]);
        request.addProperty("orderId", args[1]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
            Object orderObj = envelope.getResponse();

            SoapObject soapObject = (SoapObject) orderObj;
            result = new AsyncTaskResult<>(new Order(soapObject));
        } catch (XmlPullParserException | IOException e) {
            result = new AsyncTaskResult<>(e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Order> result) {
        for (IAsyncTaskListener<AsyncTaskResult<Order>> listener : listeners) {
            listener.AsyncTaskCompleted(result);
        }
        linlaHeaderProgress.setVisibility(View.GONE);
    }
}