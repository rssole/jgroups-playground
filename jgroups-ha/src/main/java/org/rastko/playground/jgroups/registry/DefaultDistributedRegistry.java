package org.rastko.playground.jgroups.registry;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.rastko.playground.jgroups.message.ActionHeader;
import org.rastko.playground.jgroups.message.JGroupsMessagePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DefaultDistributedRegistry<TItem extends Serializable, TRegistry> implements Registry<TItem> {

    private final JChannel jgroupsChannel;
    private final String clusterName;
    private RegistryDelegate<TItem, TRegistry> delegate;
    private Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<ActionHeader.Action, RegistryCommand> registryCommandMap;

    public DefaultDistributedRegistry(JChannel jgroupsChannel, String clusterName, RegistryDelegate<TItem, TRegistry> delegate) {
        this.jgroupsChannel = jgroupsChannel;
        this.clusterName = clusterName;
        this.delegate = delegate;

        registryCommandMap = loadRegistryCommands();
    }

    @PostConstruct
    public void init() throws Exception {
        jgroupsChannel.receiver(new ReceiverAdapter() {
            @Override
            public void receive(Message msg) {
                logger.debug("Message received: {}", msg);
                final JGroupsMessagePayload<TItem> msgPayload;
                try {
                    msgPayload = fromBytes(msg.getBuffer());
                    registryCommandMap.get(msgPayload.getHeader().getAction()).act(delegate, msgPayload.getBody());
                    logger.info("Message received: {}", msgPayload);
                } catch (Exception e) {
                    logger.error("Failed to receive body", e);
                }
            }

            @Override
            public void getState(OutputStream output) throws Exception {
                ObjectOutputStream oos = new ObjectOutputStream(output);
                oos.writeObject(delegate.getState());
            }

            @Override
            @SuppressWarnings("unchecked")
            public void setState(InputStream input) throws Exception {
                ObjectInputStream ois = new ObjectInputStream(input);
                final TRegistry tRegistry = (TRegistry) ois.readObject();
                delegate.setState(tRegistry);
            }
        });

        jgroupsChannel.connect(clusterName, null, TimeUnit.SECONDS.toMillis(10));
        jgroupsChannel.getState(null, TimeUnit.SECONDS.toMillis(10), true);
    }

    @Override
    public void register(TItem body) throws Exception {
        delegate.register(body);
        doSend(body, ActionHeader.Action.ADD);
    }

    @Override
    public void unregister(TItem body) throws Exception {
        delegate.unregister(body);
        doSend(body, ActionHeader.Action.REMOVE);
    }

    private void doSend(TItem body, ActionHeader.Action action) throws Exception {
        final Message msg = new Message();
        JGroupsMessagePayload<TItem> payload = new JGroupsMessagePayload<>(new ActionHeader(action), body);
        final byte[] obj = toBytes(payload);
        msg.setBuffer(obj);
        jgroupsChannel.send(msg);
    }

    @SuppressWarnings("unchecked")
    private JGroupsMessagePayload<TItem> fromBytes(byte[] input) throws Exception {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        return (JGroupsMessagePayload<TItem>) ois.readObject();
    }

    private byte[] toBytes(JGroupsMessagePayload<TItem> input) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(input);
        return outputStream.toByteArray();
    }

    private Map<ActionHeader.Action, RegistryCommand> loadRegistryCommands() {
        Map<ActionHeader.Action, RegistryCommand> commandMap = new HashMap<>();
        commandMap.put(ActionHeader.Action.ADD, new RegistryCommand() {
            @Override
            public <T extends Serializable> void act(Registry<T> registry, T payload) throws Exception {
                registry.register(payload);
            }
        });
        commandMap.put(ActionHeader.Action.REMOVE, new RegistryCommand() {
            @Override
            public <T extends Serializable> void act(Registry<T> registry, T payload) throws Exception {
                registry.unregister(payload);
            }
        });
        return commandMap;
    }
}
