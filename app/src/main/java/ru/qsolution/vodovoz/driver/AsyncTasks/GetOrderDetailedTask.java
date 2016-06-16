package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.Order;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei Vinogradov on 07.06.16.
 * (c) Quality Solution Ltd.
 */

public class GetOrderDetailedTask extends AsyncTask<String, Void, AsyncTaskResult<Order>> {
    private List<IAsyncTaskListener<AsyncTaskResult<Order>>> listeners = new ArrayList<>();

    public void addListener(IAsyncTaskListener<AsyncTaskResult<Order>> toAdd) {
        listeners.add(toAdd);
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
    }
}