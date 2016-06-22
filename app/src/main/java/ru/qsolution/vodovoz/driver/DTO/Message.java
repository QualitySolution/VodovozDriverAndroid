package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.SoapObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.qsolution.vodovoz.driver.Workers.SoapWorker;

/**
 * Created by Andrei Vinogradov on 22.06.16.
 * (c) Quality Solution Ltd.
 */

public class Message {
    public final String Sender;
    public final String Message;
    public final boolean IsSelf;
    public Date DateTime;

    public Message(String sender, String message, Date dateTime) {

        Sender = sender;
        Message = message;
        DateTime = dateTime;
        IsSelf = (Sender == null || Sender.equals(""));
    }

    public Message (SoapObject soapObject) {
        Sender = SoapWorker.SafeGetPropertyAsString(soapObject, "Sender");
        Message = SoapWorker.SafeGetPropertyAsString(soapObject, "Message");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            DateTime = sdf.parse(soapObject.getProperty("DateTime").toString());
        } catch (ParseException e) {
            e.printStackTrace();
            DateTime = new Date();
        }
        IsSelf = (Sender == null || Sender.equals(""));
    }
}
