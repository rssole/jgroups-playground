package org.rastko.playground.jgroups.management;


import org.rastko.playground.jgroups.core.registry.DefaultDistributedRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName = "bean:name=testBean4", description = "My Managed Bean")
public class DistributedRegistryManagementBean {
    @Autowired
    private DefaultDistributedRegistry registry;

    @ManagedAttribute(description = "The Age Attribute", currencyTimeLimit = 1)
    public long getMessagesCount() {
        return registry.getMessagesCount();
    }
}
