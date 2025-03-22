package com.skyeye.school.chat.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ChatWebSocketMsgService {
    void sendChatWebSocketMsgToUser(InputObject inputObject, OutputObject outputObject);

    void sendChatWebSocketMsgToAll(InputObject inputObject, OutputObject outputObject);
}
