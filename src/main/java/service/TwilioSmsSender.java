package service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSmsSender {

    public static final String ACCOUNT_SID = "AC075535eda3322958e7302935c2c09a0a";
    public static final String AUTH_TOKEN = "a430657c6bb4705a5b3ad31dac9a6926";
    public static final String TWILIO_NUMBER = "+12566458944";

    public static void sendSms(String toPhoneNumber, String messageBody) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(TWILIO_NUMBER),
                messageBody
        ).create();
        System.out.println("Message SID: " + message.getSid());
    }
}


