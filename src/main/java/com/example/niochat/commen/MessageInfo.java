package com.example.niochat.commen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageInfo {

    /**
     * 串联标志符号
     */
    private static String  splitInfo = "*-*";
    /**
     * 消息类别,1是注册，2是聊天  3.要求注册 ,4注册成功的信息
     */
    private String type;

    /**
     * 接收的用户
     */
    private String  userIds;

    /**
     * 消息体
     */
    private String context;

}
