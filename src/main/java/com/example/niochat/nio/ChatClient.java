package com.example.niochat.nio;

import com.example.niochat.commen.MessageChangeUtils;
import com.example.niochat.commen.MessageInfo;
import com.example.niochat.commen.Port;
import lombok.Data;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 连接服务端
 * select轮询接收信号
 * 简化版
 */
@Data
public class ChatClient {

        SocketChannel socketChannel ;
        Selector selector;

        public  void  initMethod() throws Exception{
            try {
                socketChannel = SocketChannel.open();
                selector =Selector.open();
                socketChannel.configureBlocking(false);
                socketChannel.connect(new InetSocketAddress(Port.port));
                System.out.println("客户端连接成功");
                socketChannel.register(selector, SelectionKey.OP_CONNECT);

                while (true){
                   int count = selector.select(100);
                   if(count==0){
                       continue;
                   }
                 Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        dealEvent(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(selector != null && selector.isOpen()){
                    selector.close();
                }
                if(socketChannel != null && socketChannel.isConnected()){
                    socketChannel.close();
                }
            }
        }

    /**
     * 客户端的select是两个事件
     * 一个连接
     * 一个待读
     * @param key
     */
    private void dealEvent(SelectionKey key) throws Exception{
    if(key.isConnectable()){
        SocketChannel socketChannel = (SocketChannel)key.channel();
        //判断连接操作是否正在进行
        if(socketChannel.isConnectionPending()){
            socketChannel.finishConnect();
        }
        socketChannel.configureBlocking(false);
        socketChannel.register(selector,SelectionKey.OP_READ);
    }else if(key.isReadable()){
        //读取信息做回复
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        MessageInfo messageInfo = MessageChangeUtils.stringToMessageInfo(new String(byteBuffer.array(), Charset.forName("UTF-8")));
        dealInfo(messageInfo,socketChannel);
     }



    }

    /**
     * 不同的消息做不同的处理
     * @param messageInfo
     * @param socketChannel
     */
    private void dealInfo(MessageInfo messageInfo, SocketChannel socketChannel) throws Exception{
        switch (messageInfo.getType()){
            //要求注册
            case "3":
                System.out.println("来自服务器的信息是：{}"+messageInfo.getContext());
                socketChannel.write(MessageChangeUtils.stringToByteBuffer(
                        MessageChangeUtils.messageInfoToString(
                                MessageInfo.builder().context("小红").type("1").build()
                        )));
                break;
            case "4":
                System.out.println("来自服务器的信息是：「」"+messageInfo.getContext());
                break;

            case "2":
                System.out.println("正常的聊天");
        }



    }

    public static void main(String[] args) throws Exception{
        new ChatClient().initMethod();
    }



}
