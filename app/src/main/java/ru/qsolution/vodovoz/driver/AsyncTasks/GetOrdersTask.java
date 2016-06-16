package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.ShortOrder;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei Vinogradov on 07.06.16.
 * (c) Quality Solution Ltd.
 */

public class GetOrdersTask extends AsyncTask<String, Void, AsyncTaskResult<ArrayList<ShortOrder>>> {
    private List<IAsyncTaskListener<AsyncTaskResult<ArrayList<ShortOrder>>>> listeners = new ArrayList<>();

    public void addListener(IAsyncTaskListener<AsyncTaskResult<ArrayList<ShortOrder>>> toAdd) {
        listeners.add(toAdd);
    }

    @Override
    protected AsyncTaskResult<ArrayList<ShortOrder>> doInBackground(String... args) {
        AsyncTaskResult<ArrayList<ShortOrder>> result;
        String METHOD_NAME = "GetRouteListOrders";

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

        SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
        request.addProperty("authKey", args[0]);
        request.addProperty("routeListId", args[1]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
            Object ordersListsObj = envelope.getResponse();
            ArrayList<ShortOrder> orders;

            SoapObject ordersList = (SoapObject) ordersListsObj;

            orders = new ArrayList<>();
            for (int i = 0; i < ordersList.getPropertyCount(); i++) {
                Object property = ordersList.getProperty(i);
                if (property instanceof SoapObject) {
                    SoapObject soapObject = (SoapObject) property;
                    ShortOrder order = new ShortOrder(soapObject);
                    orders.add(order);
                }
            }

            result = new AsyncTaskResult<>(orders);
        } catch (XmlPullParserException | IOException e) {
            result = new AsyncTaskResult<>(e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<ArrayList<ShortOrder>> result) {
        for (IAsyncTaskListener<AsyncTaskResult<ArrayList<ShortOrder>>> listener : listeners) {
            listener.AsyncTaskCompleted(result);
        }
    }
}