package com.object0r.monitor.tools.helpers;

import com.object0r.toortools.Utilities;

public class TrelloHelper
{
    public static String getTrelloBoardCardsOutput(String boardId, String applicationKey, String applicationToken) throws Exception
    {
        return Utilities.readUrl("https://api.trello.com/1/boards/" + boardId + "/cards?lists=open&list_fields=name&fields=name,desc&key=" + applicationKey + "&token=" + applicationToken + "");
    }
}
