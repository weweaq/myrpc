package com.yupi.yurpc.registry;

import com.yupi.yurpc.spi.SpiLoader;

public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

}
