package com.yupi.yurpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

public class ConfigUtils {
    public static <T> T getConfig(Class<T> clazz, String prefix)
    {
        return loadConfig(clazz, prefix, "");
    }


    /**
     * 加载配置，区分环境
     * @param <T>
     * @param prefix
     * @param environment
     * @return
     */
    private static <T> T loadConfig(Class<T> tClazz, String prefix, String environment) {
        StringBuilder configPath = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)){
            configPath.append("-").append(environment);
        }
        configPath.append(".properties");
        Props props = new Props(configPath.toString());
        return props.toBean(tClazz, prefix);
    }
}
