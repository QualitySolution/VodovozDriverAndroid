package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andrei on 08.06.16.
 */
public class RouteList implements Serializable{
    public final String Id;
    public final String Forwarder;
    public final String DeliveryShift;
    public final String Status;
    public Date Date;



    public RouteList (SoapObject soapObject) {
        Id = soapObject.getProperty("Id").toString();
        Status = soapObject.getProperty("Status").toString();
        Forwarder = soapObject.getProperty("Forwarder").toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date = sdf.parse(soapObject.getProperty("Date").toString());
        } catch (ParseException e) {
            e.printStackTrace();
            Date = new Date();
        }
        DeliveryShift = soapObject.getProperty("DeliveryShift").toString();
    }

}
