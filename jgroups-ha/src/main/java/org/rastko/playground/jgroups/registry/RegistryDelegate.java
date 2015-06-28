package org.rastko.playground.jgroups.registry;

import java.io.Serializable;

public interface RegistryDelegate<TItem extends Serializable, TRegistry> extends Registry<TItem> {
    TRegistry getState();
    void setState(TRegistry registry);
}
