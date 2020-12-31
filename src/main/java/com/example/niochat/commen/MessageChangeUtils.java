package com.example.niochat.commen;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;


public class MessageChangeUtils {

    /**
     * String-->MessageInfo
     */
    public static MessageInfo  stringToMessageInfo(String msg){
        String[] strings =  msg.split("\\*-\\*");
        return MessageInfo.builder()
                .type(strings[0])
                .userIds(strings[1])
                .context(strings[2])
                .build();
    }

    /**
     * MessageInfo--->String
     */
    public static String messageInfoToString(MessageInfo info){

        return info.getType()+"*-*"+info.getUserIds()+"*-*"+info.getContext();
    }

    /**
     * String-->ByteBuffer
     */
    public static  ByteBuffer stringToByteBuffer(String s) throws Exception{
       return ByteBuffer.wrap(s.getBytes("UTF-8"));
    }

    /**
     * byteBuffer-->String
     */
    public static String  byteBufferToString(ByteBuffer byteBuffer) throws Exception{
       return new String(byteBuffer.array(),"UTF-8");
    }
}
