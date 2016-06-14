package ru.qsolution.vodovoz.driver.DTO;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.sql.Time;
import java.util.Hashtable;

/**
 * Created by Andrei on 14.06.16.
 */
public class TrackPoint implements KvmSerializable {
    public String Latitude;
    public String Longitude;
    public String TimeStamp;

    public TrackPoint(Double latitude, Double longitude, Long timeStamp) {
        Latitude = latitude.toString();
        Longitude = longitude.toString();
        TimeStamp = timeStamp.toString();
    }

    @Override
    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return Latitude;
            case 1:
                return Longitude;
            case 2:
                return TimeStamp;
        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 3;
    }

    @Override
    public void setProperty(int index, Object value) {
        switch(index)
        {
            case 0:
                Latitude = value.toString();
                break;
            case 1:
                Longitude = value.toString();
                break;
            case 2:
                TimeStamp = value.toString();
                break;
            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch(index)
        {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Latitude";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Longitude";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TimeStamp";
                break;
            default:break;
        }
    }
}
