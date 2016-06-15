package ru.qsolution.vodovoz.driver.Workers;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

/**
 * Created by Andrei on 06.06.16.
 */
public class NetworkWorker {
    private static final String actionInterface = "IAndroidDriverService/";

    public static final String Namespace = "http://tempuri.org/";
    public static final String ServiceUrl = "http://saas.qsolution.ru:9000/AndroidDriverService";
    //public static final String ServiceUrl = "http://10.204.250.124:9000/AndroidDriverService";
    //public static final String ServiceUrl = "http://vinogradov.sknt.ru:9000/AndroidDriverService";

    public static SoapSerializationEnvelope CreateEnvelope (SoapObject soapObject) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.env=SoapSerializationEnvelope.ENV;
        envelope.setOutputSoapObject(soapObject);

        return envelope;
    }

    public static String GetSoapAction (String actionName) {
        return Namespace + actionInterface + actionName;
    }
}
