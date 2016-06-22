package ru.qsolution.vodovoz.driver.Workers;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

/**
 * Created by Andrei on 06.06.16.
 */
public class NetworkWorker {
    public static final String ACTION_INTERFACE_ANDROID = "IAndroidDriverService/";
    public static final String ACTION_INTERFACE_CHAT = "IChatService/";

    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String ANDROID_SERVICE_URL = "http://saas.qsolution.ru:9000/AndroidDriverService";
    public static final String CHAT_SERVICE_URL = "http://saas.qsolution.ru:9000/ChatService";

    public static final String METHOD_CHANGE_ORDER_STATUS = "ChangeOrderStatus";
    public static final String METHOD_CHECK_APP_VERSION = "CheckAppCodeVersion";
    public static final String METHOD_CHECK_AUTH = "CheckAuth";
    public static final String METHOD_GET_ORDER_DETAILED = "GetOrderDetailed";
    public static final String METHOD_GET_ORDERS = "GetRouteListOrders";
    public static final String METHOD_GET_ROUTE_LISTS = "GetRouteLists";
    public static final String METHOD_LOGIN = "Auth";
    public static final String METHOD_SEND_COORDINATES = "SendCoordinates";
    public static final String METHOD_START_TRACK = "StartOrResumeTrack";
    public static final String METHOD_ENABLE_PUSH = "EnablePushNotifications";
    public static final String METHOD_SEND_MESSAGE = "SendMessageToLogistician";
    public static final String METHOD_GET_MESSAGES = "AndroidGetChatMessages";

    public static final String FIELD_AUTH_KEY = "authKey";
    public static final String FIELD_ORDER_ID = "orderId";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_BOTTLES_RETURNED = "bottlesReturned";
    public static final String FIELD_VERSION_CODE = "versionCode";
    public static final String FIELD_ROUTE_LIST_ID = "routeListId";
    public static final String FIELD_LOGIN = "login";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_TRACK_ID = "trackId";
    public static final String FIELD_TRACK_POINT = "TrackPoint";
    public static final String FIELD_TRACK_POINT_LIST = "TrackPointList";
    public static final String FIELD_TOKEN = "token";
    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_DAYS = "days";


    public static SoapSerializationEnvelope CreateEnvelope (SoapObject soapObject) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;
        envelope.env=SoapSerializationEnvelope.ENV;
        envelope.setOutputSoapObject(soapObject);

        return envelope;
    }

    public static String GetSoapAction (String actionName, String actionInterface) {
        return NAMESPACE + actionInterface + actionName;
    }
}
