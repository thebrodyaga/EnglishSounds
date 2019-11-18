package com.thebrodyaga.englishsounds.navigation;

import java.util.HashMap;

import ru.terrakok.cicerone.Cicerone;

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
