package com.example.niochat.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class GroupChatClient {

    public static void main(String[] args) throws Exception{
        //客户端只有一个组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new GroupChatClientHandler());
                        }
                    });
            System.out.println("客户端ok");

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(7000)).sync();
            //给关闭通道进行监听
            future.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }

    }
}
