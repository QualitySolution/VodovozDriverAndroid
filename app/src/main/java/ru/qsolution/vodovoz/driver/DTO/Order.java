package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.Workers.SoapWorker;

/**
 * Created by Andrei on 08.06.16.
 */
public class Order {
    public String Id;
    public String Title;
    public String Region;                   //FIXME: Not used
    public String CityDistrict;             //FIXME: Not used
    public String StreetDistrict;           //FIXME: Not used
    public String DeliveryPointComment;     //TODO: Not used
    public String Contact;
    public String Phone;
    public ArrayList<String> Phones;
    public String DeliverySchedule;
    public String OrderStatus;
    public String RouteListItemStatus;      //TODO: Not used
    public String OrderComment;             //TODO: Not used
    public String Counterparty;
    public String Address;
    public Float Latitude;
    public Float Longitude;

    public Order (SoapObject soapObject) {
        Id = SoapWorker.SafeGetPropertyAsString(soapObject, "Id");
        Title = SoapWorker.SafeGetPropertyAsString(soapObject, "Title");
        Region = SoapWorker.SafeGetPropertyAsString(soapObject, "Region");
        CityDistrict = SoapWorker.SafeGetPropertyAsString(soapObject, "CityDistrict");
        StreetDistrict = SoapWorker.SafeGetPropertyAsString(soapObject, "StreetDistrict");
        DeliveryPointComment = SoapWorker.SafeGetPropertyAsString(soapObject, "DeliveryPointComment");
        Contact = SoapWorker.SafeGetPropertyAsString(soapObject, "DPContact");
        Phone = SoapWorker.SafeGetPropertyAsString(soapObject, "DPPhone");
        Address = SoapWorker.SafeGetPropertyAsString(soapObject, "Address");
        DeliverySchedule = SoapWorker.SafeGetPropertyAsString(soapObject, "DeliverySchedule");
        OrderStatus = SoapWorker.SafeGetPropertyAsString(soapObject, "OrderStatus");
        OrderComment = SoapWorker.SafeGetPropertyAsString(soapObject, "OrderComment");
        Counterparty = SoapWorker.SafeGetPropertyAsString(soapObject, "Counterparty");
        RouteListItemStatus = "FIX ME";
        Phones = new ArrayList<>();
        if (soapObject.getProperty("CPPhones") instanceof SoapObject) {
            SoapObject phones = (SoapObject)soapObject.getProperty("CPPhones");
            for (int i = 0; i < phones.getPropertyCount(); i++) {
                Phones.add(phones.getProperty(i).toString());
            }
        }
        try {
            Latitude = Float.parseFloat(SoapWorker.SafeGetPropertyAsString(soapObject, "Latitude"));
            Longitude = Float.parseFloat(SoapWorker.SafeGetPropertyAsString(soapObject, "Longitude"));
        } catch (NumberFormatException e) {
            Latitude = Longitude = null;
        }
    }

    public String GetPhoneNumberUri() {
        if (Phone != null && !Phone.equals("")) {
            return "tel: +7" + Phone.replaceAll("[^\\d]", "");
        }
        return null;
    }
}
