package com.example.niochat.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.TimeUnit;

/**
 * 心跳检测
 * 继承的不一样
 */
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
       if(evt instanceof IdleStateEvent) {
           //向下转型
          IdleStateEvent event = (IdleStateEvent)evt;
          String eventType = null;
          switch (event.state()){
              case READER_IDLE:
                  eventType ="读空闲";
                  break;
              case WRITER_IDLE:
                  eventType ="写空闲";
                  break;
              case ALL_IDLE:
                  eventType = "读写空闲";
                  break;
          }
           System.out.println(ctx.channel().remoteAddress() + "--超时时间--" + eventType);
           System.out.println("服务器做相应处理..");
       }
    }
}
