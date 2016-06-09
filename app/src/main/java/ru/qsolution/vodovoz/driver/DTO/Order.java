package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.SoapObject;

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
        Id = soapObject.getProperty("Id").toString();
        Title = soapObject.getProperty("Title").toString();
        LocalityType = soapObject.getProperty("LocalityType").toString();
        City = soapObject.getProperty("City").toString();
        Housing = soapObject.getProperty("Housing").toString();
        Letter = soapObject.getProperty("Letter").toString();
        Structure = soapObject.getProperty("Structure").toString();
        Placement = soapObject.getProperty("Placement").toString();
        Floor = soapObject.getProperty("Floor").toString();
        Region = soapObject.getProperty("Region").toString();
        Street = soapObject.getProperty("Street").toString();
        CityDistrict = soapObject.getProperty("CityDistrict").toString();
        StreetDistrict = soapObject.getProperty("StreetDistrict").toString();
        Building = soapObject.getProperty("Building").toString();
        RoomType = soapObject.getProperty("RoomType").toString();
        Room = soapObject.getProperty("Room").toString();
        DeliveryPointComment = soapObject.getProperty("DeliveryPointComment").toString();
        Contact = soapObject.getProperty("Contact").toString();
        Phone = soapObject.getProperty("Phone").toString();
        DeliverySchedule = soapObject.getProperty("DeliverySchedule").toString();
        OrderStatus = soapObject.getProperty("OrderStatus").toString();
        OrderComment = soapObject.getProperty("OrderComment").toString();
        Counterparty = soapObject.getProperty("Counterparty").toString();
        RouteListItemStatus = "FIX ME";
        Latitude = Float.parseFloat(soapObject.getProperty("CityDistrict").toString());
        Longitude = Float.parseFloat(soapObject.getProperty("CityDistrict").toString());
    }
}
