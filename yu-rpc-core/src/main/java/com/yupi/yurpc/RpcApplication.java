package com.yupi.yurpc;

import com.yupi.yurpc.config.RpcConfig;
import com.yupi.yurpc.constant.RpcConstant;
import com.yupi.yurpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("RpcApplication init success, config = {}", rpcConfig.toString());
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.getConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
            log.info("RpcApplication init success, config = {}", newRpcConfig.toString());
        } catch (Exception e) {
            log.error("RpcApplication init error", e);
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     *
     * @return
     */
    public static RpcConfig getRpcConfig() {
        // 2. 两次判空的具体作用
        //第一次判空（外层） 快速检查 rpcConfig 是否已初始化。如果已初始化，直接返回，避免不必要的同步锁竞争。
        //第二次判空（同步块内） 防止多个线程同时通过第一次判空后，在同步块内重复初始化 rpcConfig。

        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
