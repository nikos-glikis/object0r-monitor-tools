package com.object0r.monitor.tools.helpers;

import com.object0r.monitor.tools.datatypes.HistoricValue;
import com.object0r.toortools.DB;
import com.object0r.toortools.helpers.DateHelper;
import org.apache.xerces.impl.dv.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HistoricValuesManager
{
    static DB values = new DB("historicValues", "values");

    public static HistoricValue getSaved(String id)
    {
        String historicValueString = values.get(id);
        if (historicValueString != null)
        {
            try
            {
                byte b[] = Base64.decode(historicValueString);
                ByteArrayInputStream bi = new ByteArrayInputStream(b);
                ObjectInputStream si = new ObjectInputStream(bi);
                HistoricValue historicValue = (HistoricValue) si.readObject();

                return historicValue;
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }

        return null;
    }

    public static void saveValue(HistoricValue historicValue, String id)
    {
        try
        {
            String serializedObject;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(historicValue);
            so.flush();
            serializedObject = Base64.encode(bo.toByteArray());
            values.put(id, serializedObject);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public static long getDateDiff(HistoricValue hv1, HistoricValue hv2, TimeUnit timeUnit)
    {
        return DateHelper.getDateDiff(hv1.getTime(), hv2.getTime(), timeUnit);
    }

    /**
     * Returns true if the last update of the value was more tham timeUnitValue timeUnit ago.
     *
     * @param historicValue - The actual value
     * @param timeUnitValue - Time unit value. For example "20 hours" timeUnitValue is 20, and timeUnit = hours
     * @param timeUnit - Time unit value. For example "20 hours" timeUnitValue is 20, and timeUnit = hours
     * @return true if latest value is older than historic
     */
    public static boolean isValueOlderThan(HistoricValue historicValue, int timeUnitValue, TimeUnit timeUnit)
    {
        return DateHelper.getDateDiff(historicValue.getTime(),new Date(), timeUnit) > timeUnitValue;
    }
}
