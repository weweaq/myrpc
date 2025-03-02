package com.yupi.yurpc.server;

import io.vertx.core.Vertx;
import com.yupi.yurpc.server.HttpServer;

public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 HTTP 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求
        server.requestHandler(new HttpServerHandler());


        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("HTTP com.yupi.yurpc.server started on port " + port);
            } else {
                System.out.println("Failed to start HTTP com.yupi.yurpc.server: " + result.cause());
            }
        });

    }
}
