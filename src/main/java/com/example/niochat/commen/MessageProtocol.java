package com.example.niochat.commen;

import lombok.Data;

/**
 * 先规定一个协议包，含有长度
 */
@Data
public class MessageProtocol {
    private int len; //关键
    private byte[] content;

}