package ru.qsolution.vodovoz.driver.AsyncTasks;

import android.os.AsyncTask;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import ru.qsolution.vodovoz.driver.DTO.TrackPoint;
import ru.qsolution.vodovoz.driver.Workers.NetworkWorker;

/**
 * Created by Andrei Vinogradov on 07.06.16.
 * (c) Quality Solution Ltd.
 */

public class SendCoordinatesTask extends AsyncTask<Object, Void, AsyncTaskResult<Boolean>> {
    @Override
    protected AsyncTaskResult<Boolean> doInBackground(Object... args) {
        AsyncTaskResult<Boolean> result;
        String METHOD_NAME = "SendCoordinates";

        HttpTransportSE httpTransport = new HttpTransportSE(NetworkWorker.ServiceUrl);

        SoapObject request = new SoapObject(NetworkWorker.Namespace, METHOD_NAME);
        request.addProperty("authKey", args[0].toString());
        request.addProperty("trackId", args[1].toString());

        List<TrackPoint> list = (List<TrackPoint>) args[2];
        SoapObject soapDetails = new SoapObject(NetworkWorker.Namespace, "TrackPointList");

        for (int i = 0; i < list.size(); i++) {
            PropertyInfo inf = new PropertyInfo();
            inf.setName("TrackPoint");
            inf.setValue(list.get(i));
            inf.setType(list.get(i).getClass());
            soapDetails.addProperty(inf);
        }

        request.addSoapObject(soapDetails);

        SoapSerializationEnvelope envelope = NetworkWorker.CreateEnvelope(request);
        envelope.addMapping(NetworkWorker.Namespace, "TrackPoint", list.get(0).getClass());
        envelope.addMapping(NetworkWorker.Namespace, "TrackPointList", list.getClass());

        try {
            httpTransport.call(NetworkWorker.GetSoapAction(METHOD_NAME), envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = new AsyncTaskResult<>(Boolean.parseBoolean(response.getValue().toString()));
        } catch (XmlPullParserException | IOException e) {
            result = new AsyncTaskResult<>(e);
        }
        return result;
    }
}