package com.thebrodyaga.englishsounds.navigation;

import com.thebrodyaga.core.navigation.api.cicerone.Cicerone;

import java.util.HashMap;

/**
 * Created by terrakok 27.11.16
 */
public class LocalCiceroneHolder {
    private HashMap<String, Cicerone<RouterTransition>> containers;

    public LocalCiceroneHolder() {
        containers = new HashMap<>();
    }

    public Cicerone<RouterTransition> getCicerone(String containerTag) {
        if (!containers.containsKey(containerTag)) {
            containers.put(containerTag, Cicerone.create(new RouterTransition()));
        }
        return containers.get(containerTag);
    }
}
