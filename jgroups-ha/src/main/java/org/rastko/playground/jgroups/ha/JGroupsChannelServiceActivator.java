package org.rastko.playground.jgroups.ha;

import org.jboss.as.clustering.jgroups.ChannelFactory;
import org.jboss.as.clustering.jgroups.subsystem.ChannelFactoryService;
import org.jboss.as.clustering.jgroups.subsystem.ChannelService;
import org.jboss.as.naming.ManagedReferenceFactory;
import org.jboss.as.naming.ManagedReferenceInjector;
import org.jboss.as.naming.ServiceBasedNamingStore;
import org.jboss.as.naming.deployment.ContextNames;
import org.jboss.as.naming.service.BinderService;
import org.jboss.msc.service.*;
import org.jboss.msc.value.InjectedValue;
import org.jgroups.Channel;

/**
 * Custom ServiceActivator that accesses the underlying JGroups protocol stack configuration, creates a new channel and
 * binds it to the JNDI.
 */
public abstract class JGroupsChannelServiceActivator implements ServiceActivator {

    private ServiceName channelServiceName;

    protected abstract String getJndiName();
    protected abstract String getChannelName();
    protected abstract String getStackName();
    protected abstract String getJgroupsChannelServicePrefix();

    @Override
    public void activate(ServiceActivatorContext context) {
        channelServiceName = ChannelService.getServiceName(getChannelName());
        createChannel(context.getServiceTarget());
        bindChannelToJNDI(context.getServiceTarget());
    }

    private void createChannel(ServiceTarget target) {
        InjectedValue<ChannelFactory> channelFactory = new InjectedValue<>();
        ServiceName serviceName = ChannelFactoryService.getServiceName(getStackName());
        ChannelService channelService = new ChannelService(getChannelName(), channelFactory);

        target.addService(channelServiceName, channelService)
                .addDependency(serviceName, ChannelFactory.class, channelFactory).install();

        // Our own service that will just connect to already configured channel
        ServiceName cService = ServiceName.of(getJgroupsChannelServicePrefix(), getChannelName());

        InjectedValue<Channel> channel = new InjectedValue<>();
        target.addService(cService, new JGroupsService(channel, getChannelName()))
                .addDependency(ServiceBuilder.DependencyType.REQUIRED, ChannelService.getServiceName(getChannelName()), Channel.class, channel)
                .setInitialMode(ServiceController.Mode.ACTIVE).install();
    }

    private void bindChannelToJNDI(ServiceTarget target) {
        ContextNames.BindInfo bindInfo = ContextNames.bindInfoFor(getJndiName());

        BinderService binder = new BinderService(bindInfo.getBindName());

        ServiceBuilder<ManagedReferenceFactory> service =
                target.addService(bindInfo.getBinderServiceName(), binder);

        service.addAliases(ContextNames.JAVA_CONTEXT_SERVICE_NAME.append(getJndiName()));
        service.addDependency(channelServiceName, Channel.class, new ManagedReferenceInjector<Channel>(
                binder.getManagedObjectInjector()));
        service.addDependency(bindInfo.getParentContextServiceName(), ServiceBasedNamingStore.class,
                binder.getNamingStoreInjector());

        service.setInitialMode(ServiceController.Mode.PASSIVE).install();
    }
}
