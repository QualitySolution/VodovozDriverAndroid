package ru.qsolution.vodovoz.driver.Workers;

import org.ksoap2.serialization.SoapObject;

/**
 * Created by Andrei on 09.06.16.
 */
public class SoapWorker {
    private static final String EMPTY_STRING = "";

    public static String SafeGetPropertyAsString (SoapObject soapObject, String propertyName) {
        try {
            Object property = soapObject.getProperty(propertyName);
            if (property == null)
                return EMPTY_STRING;
            else {
                String result = property.toString();
                if (result.equals("anyType{}"))
                    return EMPTY_STRING;
                return result;
            }

        } catch (RuntimeException e) {
            return EMPTY_STRING;
        }
    }
}
