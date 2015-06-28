package org.rastko.playground.jgroups.registry;

import java.io.Serializable;


public interface Registry<TItem extends Serializable> {
    void register(TItem body) throws Exception;
    void unregister(TItem body) throws Exception;
}
