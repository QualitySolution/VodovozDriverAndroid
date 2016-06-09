package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import ru.qsolution.vodovoz.driver.DTO.RouteList;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei on 07.06.16.
 */
public class GetRouteListsTask extends AsyncTask<String, Void, ArrayList<RouteList>> {
    @Override
    protected ArrayList<RouteList> doInBackground(String... args) {

        String METHOD_NAME = "GetRouteLists";

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

        SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
        request.addProperty("authKey", args[0]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
        } catch (IOException | XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Object routeListsObj = envelope.getResponse();
            ArrayList<RouteList> result = null;

            if (routeListsObj instanceof SoapObject) {
                SoapObject routeLists = (SoapObject) routeListsObj;

                if (routeLists.getPropertyCount() == 0)
                    return null;

                result = new ArrayList<>();
                for (int i = 0; i < routeLists.getPropertyCount(); i++) {
                    Object property = routeLists.getProperty(i);
                    if (property instanceof SoapObject) {
                        SoapObject soapObject = (SoapObject) property;
                        RouteList routeList = new RouteList(soapObject);
                        result.add(routeList);
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