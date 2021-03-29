package com.object0r.monitor.tools.reporters;

import com.google.gson.Gson;
import com.object0r.monitor.tools.base.BaseReporter;
import com.object0r.toortools.http.HttpHelper;
import com.object0r.toortools.http.HttpRequestInformation;
import com.object0r.toortools.http.HttpResult;

public class SlackReporter extends BaseReporter {


    public SlackReporter() {

    }

    class SlackMessage {
        public String text = "";
        public String subject = "";
    }

    @Override
    public boolean report(String subject, String body) {
        HttpRequestInformation httpRequestInformation = new HttpRequestInformation();
        for (String recipient : recipients) {
            try {
                httpRequestInformation.setUrl("https://hooks.slack.com/services" + recipient);
                SlackMessage slackMessage = new SlackMessage();
                slackMessage.subject = subject;
                slackMessage.text = body;
                Gson gson = new Gson();
                String jsonified = gson.toJson(slackMessage);
                httpRequestInformation.setMethodPost();
                httpRequestInformation.setBody(jsonified);
                HttpResult httpResult = HttpHelper.request(httpRequestInformation);
                if (!httpResult.isSuccessfull()) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    protected boolean verifyRecipient(String recipient) {
        if (recipient.charAt(0) != '/') {
            return false;
        }

        return true;
    }
}
