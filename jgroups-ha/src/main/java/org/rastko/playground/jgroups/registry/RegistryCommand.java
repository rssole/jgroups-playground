package org.rastko.playground.jgroups.registry;

import java.io.Serializable;

public interface RegistryCommand {
    <TItem extends Serializable> void act(Registry<TItem> registry, TItem payload) throws Exception;
}
