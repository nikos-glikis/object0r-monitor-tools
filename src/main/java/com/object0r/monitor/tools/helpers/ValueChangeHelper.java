package com.object0r.monitor.tools.helpers;

import com.object0r.monitor.tools.datatypes.HistoricValue;

public class ValueChangeHelper
{
    public static boolean checkIfChanged(String variableName, String text)
    {
        HistoricValue historicValue = HistoricValuesManager.getSaved(variableName);
        if (historicValue == null || !historicValue.getValue().equals(text))
        {
            HistoricValue historicValue2 = new HistoricValue(text);
            HistoricValuesManager.saveValue(historicValue2, variableName);
            return true;
        }
        return false;
    }
}
