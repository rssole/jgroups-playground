package org.rastko.playground.jgroups.core.messaging;

import org.rastko.playground.jgroups.registry.Registry;
import org.rastko.playground.jgroups.registry.RegistryCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageQueueListener implements SessionAwareMessageListener<Message> {

    public static final String ADD_ACTION = "0";
    public static final String REMOVE_ACTION = "1";

    @Autowired
    @Qualifier("distributedRegistry")
    private Registry<String> registry;

    private Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Map<String, RegistryCommand> commandMap = new HashMap<>();

    public MessageQueueListener() {
        commandMap.put(ADD_ACTION, new RegistryCommand() {
            @Override
            public <TItem extends Serializable> void act(Registry<TItem> registry, TItem payload) throws Exception {
                registry.register(payload);
            }
        });
        commandMap.put(REMOVE_ACTION, new RegistryCommand() {
            @Override
            public <TItem extends Serializable> void act(Registry<TItem> registry, TItem payload) throws Exception {
                registry.unregister(payload);
            }
        });
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        final TextMessage textMessage = (TextMessage) message;
        try {
            final String correlationID = textMessage.getJMSCorrelationID();
            final String text = textMessage.getText();
            commandMap.get(correlationID).act(registry, text);
        } catch (Exception e) {
            logger.error("Failed to receive message", e);
        }
    }

}
