package com.example.niochat.commen;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码
 */
public class MyMessageEncoder extends MessageToByteEncoder<MessageProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageProtocol messageProtocol, ByteBuf out) throws Exception {
       System.out.println("编码调用");
        out.writeInt(messageProtocol.getLen());
        out.writeBytes(messageProtocol.getContent());
    }
}
