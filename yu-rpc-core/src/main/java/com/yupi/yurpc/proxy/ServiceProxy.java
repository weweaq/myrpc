package com.yupi.yurpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.yupi.yurpc.RpcApplication;
import com.yupi.yurpc.config.RpcConfig;
import com.yupi.yurpc.model.RpcRequest;
import com.yupi.yurpc.model.RpcResponse;
import com.yupi.yurpc.model.ServiceMetaInfo;
import com.yupi.yurpc.protocol.*;
import com.yupi.yurpc.registry.Registry;
import com.yupi.yurpc.registry.RegistryFactory;
import com.yupi.yurpc.serializer.JdkSerializer;
import com.yupi.yurpc.serializer.Serializer;
import com.yupi.yurpc.serializer.SerializerFactory;
import com.yupi.yurpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .serviceVersion(RpcApplication.getRpcConfig().getVersion())
                .build();
        try {
            // 序列化
            byte[] bytes = serializer.serialize(rpcRequest);
            // 这里通过注册中心和服务发现机制解决
            ServiceMetaInfo selectedServiceMetaInfo = getSelectedService(rpcRequest);

            // 发送 TCP 请求
            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
            return rpcResponse.getData();


            // Http调用
//            try (HttpResponse httpResponse = HttpRequest.post(selectedService.getServiceAddress()).body(bytes).execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }

            // 测试不调用实际远程服务
//            System.out.println("调用远程服务: " + rpcRequest.getServiceName());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 当 try 块中的代码抛出 IOException 或其子类异常时，进入 catch 块。由于 catch 块中没有显式的 return 语句，程序会继续执行 catch 块之后的代码，最终返回 null。
        return null;
    }

    private ServiceMetaInfo getSelectedService(RpcRequest rpcRequest) {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(rpcRequest.getServiceName());
        serviceMetaInfo.setServiceVersion(rpcRequest.getServiceVersion());
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("服务不存在");
        }
        // 暂时取第一个
        return serviceMetaInfoList.get(0);
    }
}
