package org.rastko.playground.jgroups.core.messaging;

import org.rastko.playground.jgroups.core.registry.DefaultDistributedRegistry;
import org.rastko.playground.jgroups.dto.JGroupsMessagePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.lang.invoke.MethodHandles;

@Component
public class MessageQueueListener implements SessionAwareMessageListener {

    @Autowired
    private DefaultDistributedRegistry registry;

    private Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        final TextMessage textMessage = (TextMessage) message;
        try {
            registry.send(new JGroupsMessagePayload(textMessage.getText()));
        } catch (Exception e) {
            logger.error("Failed to receive message", e);
        }
    }
}
