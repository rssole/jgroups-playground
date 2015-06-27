package org.rastko.playground.jgroups.ha;


public class JGroupsPlagroundChannelServiceActivator extends JGroupsChannelServiceActivator {

    public static final String JNDI_NAME = "java:jboss/channel/myChannel";

    public static final String JGROUPS_CHANNEL_SERVICE_PREFIX = "Example.jgroups";

    public static final String STACK_NAME = "rs-udp";

    public static final String CHANNEL_NAME = "myChannel";

    @Override
    protected String getJndiName() {
        return JNDI_NAME;
    }

    @Override
    protected String getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    protected String getStackName() {
        return STACK_NAME;
    }

    @Override
    protected String getJgroupsChannelServicePrefix() {
        return JGROUPS_CHANNEL_SERVICE_PREFIX;
    }
}
