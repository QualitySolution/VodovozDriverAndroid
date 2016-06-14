package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei on 07.06.16.
 */
public class SendCoordinatesTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... args) {

        String METHOD_NAME = "SendCoordinates";

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

        SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
        request.addProperty("authKey", args[0]);
        request.addProperty("lat", args[1]);
        request.addProperty("lon", args[2]);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
        } catch (IOException | XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            return (Void) envelope.getResponse();
        } catch (SoapFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}