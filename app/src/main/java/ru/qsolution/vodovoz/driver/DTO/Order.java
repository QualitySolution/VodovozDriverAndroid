package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.SoapObject;

import ru.qsolution.vodovoz.driver.Workers.SoapWorker;

/**
 * Created by Andrei on 08.06.16.
 */
public class Order {
    public String Id;
    public String Title;
    public String LocalityType;         //
    public String City;
    public String Housing;
    public String Letter;
    public String Structure;
    public String Placement;
    public String Floor;
    public String Region;
    public String CityDistrict;
    public String Street;
    public String StreetDistrict;
    public String Building;
    public String RoomType;             //
    public String Room;
    public String DeliveryPointComment;
    public String Contact;
    public String Phone;
    public String DeliverySchedule;
    public String OrderStatus;          //
    public String RouteListItemStatus;  //
    public String OrderComment;
    public String Counterparty;
    public Float Latitude;
    public Float Longitude;

    public Order (SoapObject soapObject) {
        Id = SoapWorker.SafeGetPropertyAsString(soapObject, "Id");
        Title = SoapWorker.SafeGetPropertyAsString(soapObject, "Title");
        LocalityType = SoapWorker.SafeGetPropertyAsString(soapObject, "LocalityType");
        City = SoapWorker.SafeGetPropertyAsString(soapObject, "City");
        Housing = SoapWorker.SafeGetPropertyAsString(soapObject, "Housing");
        Letter = SoapWorker.SafeGetPropertyAsString(soapObject, "Letter");
        Structure = SoapWorker.SafeGetPropertyAsString(soapObject, "Structure");
        Placement = SoapWorker.SafeGetPropertyAsString(soapObject, "Placement");
        Floor = SoapWorker.SafeGetPropertyAsString(soapObject, "Floor");
        Region = SoapWorker.SafeGetPropertyAsString(soapObject, "Region");
        Street = SoapWorker.SafeGetPropertyAsString(soapObject, "Street");
        CityDistrict = SoapWorker.SafeGetPropertyAsString(soapObject, "CityDistrict");
        StreetDistrict = SoapWorker.SafeGetPropertyAsString(soapObject, "StreetDistrict");
        Building = SoapWorker.SafeGetPropertyAsString(soapObject, "Building");
        RoomType = SoapWorker.SafeGetPropertyAsString(soapObject, "RoomType");
        Room = SoapWorker.SafeGetPropertyAsString(soapObject, "Room");
        DeliveryPointComment = SoapWorker.SafeGetPropertyAsString(soapObject, "DeliveryPointComment");
        Contact = SoapWorker.SafeGetPropertyAsString(soapObject, "Contact");
        Phone = SoapWorker.SafeGetPropertyAsString(soapObject, "Phone");
        DeliverySchedule = SoapWorker.SafeGetPropertyAsString(soapObject, "DeliverySchedule");
        OrderStatus = SoapWorker.SafeGetPropertyAsString(soapObject, "OrderStatus");
        OrderComment = SoapWorker.SafeGetPropertyAsString(soapObject, "OrderComment");
        Counterparty = SoapWorker.SafeGetPropertyAsString(soapObject, "Counterparty");
        RouteListItemStatus = "FIX ME";
        Latitude = Float.parseFloat(SoapWorker.SafeGetPropertyAsString(soapObject, "Latitude"));
        Longitude = Float.parseFloat(SoapWorker.SafeGetPropertyAsString(soapObject, "Longitude"));
    }
}
