package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;

/**
 * Created by Andrei on 08.06.16.
 */
public class ShortOrder implements Serializable {
    public final String Id;
    public final String DeliverySchedule;
    public final String OrderStatus;
    public final String Counterparty;
    public final String Address;

    public ShortOrder (SoapObject soapObject) {
        Id = soapObject.getPrimitiveProperty("Id").toString();
        DeliverySchedule = soapObject.getPrimitivePropertyAsString("DeliverySchedule");
        OrderStatus = soapObject.getPrimitivePropertyAsString("OrderStatus");
        Counterparty = soapObject.getPrimitivePropertyAsString("Counterparty");
        Address = soapObject.getPrimitivePropertyAsString("Address");
    }
}
