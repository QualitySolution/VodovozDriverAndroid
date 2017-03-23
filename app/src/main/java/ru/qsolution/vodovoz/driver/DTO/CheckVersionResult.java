package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;

/**
 * Created by andrew on 23.03.17.
 */

public class CheckVersionResult implements Serializable {

    public enum ResultType{
        Ok,
        CanUpdate,
        NeedUpdate
    }

    public final ResultType Result;
    public final String DownloadUrl;
    public final String NewVersion;

    public CheckVersionResult (SoapObject soapObject) {
        DownloadUrl = soapObject.getPropertyAsString("DownloadUrl");
        Result = ResultType.valueOf(soapObject.getPropertyAsString("Result"));
        NewVersion = soapObject.getPropertyAsString("NewVersion");
    }
}
