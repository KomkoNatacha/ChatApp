
package ca.dmi.uqtr.applicationchat.services.dto;

import java.util.HashMap;
import java.util.Map;

import ca.dmi.uqtr.applicationchat.bdlocal.model.Message;

public class FcmMessageDTO {
    public static final String MESSAGE_KEY = "message";
    public static final String EMITTER_KEY = "emitter";
    public static final String TYPE_KEY = "type";

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE_KEY = 1;
    public static final int TYPE_AUDIO = 2;


    public final String to;
    public final Map<String, Object> data;

    public FcmMessageDTO(Message messageInfo, int type) {
        data = new HashMap<>();
        data.put(TYPE_KEY, type);
        data.put(MESSAGE_KEY, messageInfo);
        data.put(EMITTER_KEY, messageInfo.getSenderId());
        to = "/topics/" + messageInfo.getDestinater();

    }
}

