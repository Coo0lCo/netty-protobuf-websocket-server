package cn.scbc.lyj.netty.protocolbuffers.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @Author:SCBC_LiYongJie
 * @time:2022/4/4
 */

@Component
public class NettyServer {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    private final EventLoopGroup boosGroup = new NioEventLoopGroup();
    private final EventLoopGroup workGroup = new NioEventLoopGroup();
    private Channel channel;

    @Resource
    private DefaultChannelInitializer defaultChannelInitializer;

    @Value("${netty.server.hostname}")
    private String hostname ;

    @Value("${netty.server.port}")
    private Integer port;

    public ChannelFuture start() {
        ChannelFuture channelFuture = null;
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(boosGroup, workGroup)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(defaultChannelInitializer);
            InetSocketAddress serverAddress = new InetSocketAddress(hostname,port);
            channelFuture = serverBootstrap.bind(serverAddress).syncUninterruptibly();
            channel = channelFuture.channel();
        } finally {
            if (!Objects.isNull(channelFuture) && channelFuture.isSuccess()) {
                log.info("cn-scbc-liyongjie-cinema netty protobuf server start done.");
            } else {
                log.error("cn-scbc-liyongjie-cinema netty protobuf server start error.");
            }
        }
        return channelFuture;
    }

    public void destroy() {
        if (Objects.isNull(channel)) return;
        channel.close();
        boosGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

}
