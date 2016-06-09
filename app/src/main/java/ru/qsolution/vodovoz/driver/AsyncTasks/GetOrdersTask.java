package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.DTO.Order;
import ru.qsolution.vodovoz.driver.DTO.RouteList;
import ru.qsolution.vodovoz.driver.DTO.ShortOrder;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei on 07.06.16.
 */
public class GetOrdersTask extends AsyncTask<String, Void, ArrayList<ShortOrder>> {
    @Override
    protected ArrayList<ShortOrder> doInBackground(String... args) {

        String METHOD_NAME = "GetRouteListOrders";

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

        SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
        request.addProperty("authKey", args[0]);
        request.addProperty("routeListId", args[1]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
        } catch (IOException | XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Object ordersListsObj = envelope.getResponse();
            ArrayList<ShortOrder> result = null;

            if (ordersListsObj instanceof SoapObject) {
                SoapObject ordersList = (SoapObject) ordersListsObj;

                if (ordersList.getPropertyCount() == 0)
                    return null;

                result = new ArrayList<>();
                for (int i = 0; i < ordersList.getPropertyCount(); i++) {
                    Object property = ordersList.getProperty(i);
                    if (property instanceof SoapObject) {
                        SoapObject soapObject = (SoapObject) property;
                        ShortOrder order = new ShortOrder(soapObject);
                        result.add(order);
                    }
                }
            }
            return result;
        } catch (SoapFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}