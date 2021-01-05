package com.example.niochat.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 一个Handler--端
 * 服务端--进站--处理器
 * 目前是群发演示，真正的可以向 NIO例子一样，放在attench里
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {



    //定义一个channel 组，管理所有的channel
    //GlobalEventExecutor.INSTANCE  是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * 建立连接时，被执行
     *
     *    将channel加入channelgroup，
     *    广播通知组内其他通道
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //遍历组内，每个都通知，然后加上--这样不会通知自己
        channelGroup.writeAndFlush("客户端"+channel.remoteAddress()+"加入聊天"+LocalDateTime.now().format(format));
        //加入组
        channelGroup.add(channel);
        System.out.println("组的长度是："+channelGroup.size());
    }

    /**
     * 断开连接时执行
     *  断开连接，不用显示的移除
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("客户端"+channel.remoteAddress()+"离开了");
        System.out.println("组的长度是："+channelGroup.size());
    }


    /**
     * channel处于活动状态时--也是发来消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       System.out.println(ctx.channel().remoteAddress()+"上线了");
    }

    /**
     * channel 处于不活动时--下线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"离线了");
    }

    /**
     * 读取数据
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        //拿到客户端dui
        Channel channel = channelHandlerContext.channel();
        channelGroup.stream().filter(i->i!=channel).forEach(i->{
            i.writeAndFlush("客户端"+channel.remoteAddress()+"说："+s);
        });
    }


    /**
     * 发生异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭通道
        ctx.close();
    }
}
