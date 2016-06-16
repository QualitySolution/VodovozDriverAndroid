package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.qsolution.vodovoz.driver.Workers.SoapWorker;

/**
 * Created by Andrei on 08.06.16.
 */
public class Order implements Serializable {
    public static final String STATUS_EN_ROUTE = "EnRoute";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELED = "Canceled";
    public static final String STATUS_OVERDUE = "Overdue";

    public String Id;
    public String Title;
    public String Region;                   //FIXME: Not used
    public String CityDistrict;             //FIXME: Not used
    public String StreetDistrict;           //FIXME: Not used
    public String DeliveryPointComment;     //TODO: Not used
    public String Contact;
    public String Phone;
    public String DeliverySchedule;
    public String RouteListItemStatus;
    public String OrderComment;             //TODO: Not used
    public String Counterparty;
    public String Address;
    public Float Latitude;
    public Float Longitude;
    public ArrayList<String> Phones;
    public ArrayList<String> OrderItems;
    public ArrayList<String> OrderEquipment;

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
        OrderComment = SoapWorker.SafeGetPropertyAsString(soapObject, "OrderComment");
        Counterparty = SoapWorker.SafeGetPropertyAsString(soapObject, "Counterparty");
        RouteListItemStatus = SoapWorker.SafeGetPropertyAsString(soapObject, "RouteListItemStatus");
        Phones = new ArrayList<>();
        if (soapObject.getProperty("CPPhones") instanceof SoapObject) {
            SoapObject phones = (SoapObject)soapObject.getProperty("CPPhones");
            for (int i = 0; i < phones.getPropertyCount(); i++) {
                Phones.add(phones.getProperty(i).toString());
            }
        }
        OrderItems = new ArrayList<>();
        if (soapObject.getProperty("OrderItems") instanceof SoapObject) {
            SoapObject items = (SoapObject)soapObject.getProperty("OrderItems");
            for (int i = 0; i < items.getPropertyCount(); i++) {
                OrderItems.add(items.getProperty(i).toString());
            }
        }
        OrderEquipment = new ArrayList<>();
        if (soapObject.getProperty("OrderEquipment") instanceof SoapObject) {
            SoapObject equipment = (SoapObject)soapObject.getProperty("OrderEquipment");
            for (int i = 0; i < equipment.getPropertyCount(); i++) {
                OrderEquipment.add(equipment.getProperty(i).toString());
            }
        }
        try {
            Latitude = Float.parseFloat(SoapWorker.SafeGetPropertyAsString(soapObject, "Latitude"));
            Longitude = Float.parseFloat(SoapWorker.SafeGetPropertyAsString(soapObject, "Longitude"));
        } catch (NumberFormatException e) {
            Latitude = Longitude = null;
        }
    }

    public static String GetPhoneNumberUri(String phone) {
        if (phone != null && !phone.equals("")) {
            return "tel: +7" + phone.replaceAll("[^\\d]", "");
        }
        return null;
    }
}
