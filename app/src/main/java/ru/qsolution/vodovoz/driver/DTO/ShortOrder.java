package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.SoapObject;

/**
 * Created by Andrei on 08.06.16.
 */
public class ShortOrder {
    public String Id;
    public String DeliverySchedule;
    public String OrderStatus;
    public String Counterparty;
    public String Address;

    public ShortOrder (SoapObject soapObject) {
        Id = soapObject.getProperty("Id").toString();
        DeliverySchedule = soapObject.getPropertyAsString("DeliverySchedule");
        OrderStatus = soapObject.getPropertyAsString("OrderStatus");
        Counterparty = soapObject.getPropertyAsString("Counterparty");
        Address = soapObject.getPropertyAsString("Address");
    }
}
