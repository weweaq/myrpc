package com.yupi.yurpcspringbootstarter.annotation;

import com.yupi.yurpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.yupi.yurpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.yupi.yurpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 Rpc 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     *
     * @return
     */
    boolean needServer() default true;
}
