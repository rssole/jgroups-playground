package org.rastko.playground.jgroups.core.registry;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.rastko.playground.jgroups.dto.JGroupsMessagePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DefaultDistributedRegistry {

    private final JChannel jgroupsChannel;
    private final String clusterName;

    private Set<JGroupsMessagePayload> messages = new HashSet<>();

    private Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public DefaultDistributedRegistry(JChannel jgroupsChannel, String clusterName) {
        this.jgroupsChannel = jgroupsChannel;
        this.clusterName = clusterName;
    }

    @PostConstruct
    public void init() throws Exception {
        jgroupsChannel.receiver(new ReceiverAdapter() {
            @Override
            public void receive(Message msg) {
                logger.debug("Message received: {}", msg);
                final JGroupsMessagePayload msgPayload;
                try {
                    msgPayload = fromBytes(msg.getBuffer());
                    logger.info("Message received: {}", msgPayload);
                } catch (Exception e) {
                    logger.error("Failed to receive message", e);
                }
            }

            @Override
            public void getState(OutputStream output) throws Exception {
                ObjectOutputStream oos = new ObjectOutputStream(output);
                oos.writeObject(messages);
            }

            @Override
            @SuppressWarnings("unchecked")
            public void setState(InputStream input) throws Exception {
                ObjectInputStream ois = new ObjectInputStream(input);
                messages = (Set<JGroupsMessagePayload>) ois.readObject();
            }
        });

        jgroupsChannel.connect(clusterName, null, TimeUnit.SECONDS.toMillis(10));
        jgroupsChannel.getState(null, TimeUnit.SECONDS.toMillis(10), true);
    }

    public void send(JGroupsMessagePayload payload) throws Exception {
        messages.add(payload);
        final Message msg = new Message();
        final byte[] obj = toBytes(payload);
        msg.setBuffer(obj);
        jgroupsChannel.send(msg);
    }

    @SuppressWarnings("unchecked")
    private <T extends Serializable> T fromBytes(byte[] input) throws Exception {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        return (T) ois.readObject();
    }

    private <T extends Serializable> byte[] toBytes(T input) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(input);
        return outputStream.toByteArray();
    }

    public long getMessagesCount() {
        return messages.size();
    }
}
