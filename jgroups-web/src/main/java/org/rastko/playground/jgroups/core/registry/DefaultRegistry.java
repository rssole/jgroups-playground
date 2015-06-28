package org.rastko.playground.jgroups.core.registry;

import org.rastko.playground.jgroups.registry.RegistryDelegate;

import java.util.HashSet;
import java.util.Set;


public class DefaultRegistry implements RegistryDelegate<String, Set<String>> {
    private Set<String> registry = new HashSet<>();

    @Override
    public Set<String> getState() {
        return new HashSet<>(registry);
    }

    @Override
    public void setState(Set<String> stringSet) {
        registry = stringSet;
    }

    @Override
    public void register(String body) throws Exception {
        registry.add(body);
    }

    @Override
    public void unregister(String body) throws Exception {
        registry.remove(body);
    }
}
