package com.example.niochat.server;

import com.example.niochat.commen.MessageChangeUtils;
import com.example.niochat.commen.MessageInfo;
import com.example.niochat.commen.Port;
import lombok.Data;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
@Data
public class ChatServer {
      ServerSocketChannel serverSocketChannel;
      Selector selector ;

    public void initServer() throws Exception{
        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(Port.port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server is running...");
            dealSelect();
        }finally {
            if(selector !=null){
                selector.close();
            }
            if(serverSocketChannel !=null){
                serverSocketChannel.close();
            }
        }
    }

    /**
     * 处理连接
     */
    private void dealSelect() {
        while (true){
            try {
               int count = selector.select(100);
                if(count ==0){
                    continue;
                }
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeySet.iterator();
                while (keyIterator.hasNext()){
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    dealKey(key);
                }
            }catch (Exception e){
                System.out.println("拿到的异常是："+e);
            }
        }
    }

    /**
     * 处理key
     * @param key
     */
    private void dealKey(SelectionKey key) throws Exception{
        //连接的请求
        if(key.isAcceptable()){
               //为此客户的请求划分一个channel
            SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector,SelectionKey.OP_READ);
            System.out.println("连接成功");
            //连接成功，服务端发信息让客户端注册--简化版不做校验，默认全都不同
            MessageInfo messageInfo = MessageInfo.builder().context("请输入你的用户名").type("3").userIds("wu").build();
            socketChannel.write(MessageChangeUtils.stringToByteBuffer(MessageChangeUtils.messageInfoToString(messageInfo)));
        }
        if(key.isReadable()){
            SocketChannel socketChannel = null;
            //拿到已经划好的管道
            try {
                socketChannel = (SocketChannel) key.channel();
                if(socketChannel !=null){
                   dealReadMessage(socketChannel,key);
                }
            }catch (Exception e){
                //这个地方防止是断开连接的请求，要注意异常包起来
                if(socketChannel != null){
                    socketChannel.close();
                    System.out.println("(简化版不通知其他人)离线的是："+(String)key.attachment());
                }
            }
        }
    }

    private void dealReadMessage(SocketChannel socketChannel,SelectionKey key) throws Exception{
        //拿到管道里的数据,简化版不考虑数据超长等一系列问题
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //判断消息的类型
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        String msg = MessageChangeUtils.byteBufferToString(byteBuffer);
        MessageInfo messageInfo = MessageChangeUtils.stringToMessageInfo(msg);
        if("1".equals(messageInfo.getType())){
            //如果是注册信息,拿到用户名，放到key里
          key.attach(messageInfo.getContext());
          System.out.println("收到的注册信息是：「」"+ messageInfo.getContext());
          socketChannel.write(MessageChangeUtils.stringToByteBuffer(MessageChangeUtils.messageInfoToString(MessageInfo.builder().userIds("null").type("4").context("注册成功").build())));
        }
        if("2".equals(messageInfo.getType())){
            String toUser = messageInfo.getUserIds();
            //TODO动态获取最新的
            Set<SelectionKey> allKey = selector.keys().stream().filter(i->i.channel() instanceof SocketChannel && i.channel().isOpen()).collect(Collectors.toSet());

            //服务端将收件人换成发件人发给收件人
                if("all".equals(toUser)){
                    allKey.stream().forEach(i->{
                        SocketChannel socketChannel1 = (SocketChannel) i.channel();
                        try {
                            socketChannel1.write(
                                    MessageChangeUtils.stringToByteBuffer(
                                            MessageChangeUtils.messageInfoToString(
                                                    MessageInfo.builder()
                                                            .context(messageInfo.getContext())
                                                            .type("2").userIds((String)key.attachment()).build())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }else {
                    for (SelectionKey key2:allKey) {
                        String userName = (String)key2.attachment();
                        if(toUser.equals(userName)){
                            SocketChannel socketChannel1 = (SocketChannel) key2.channel();
                            try {
                                socketChannel1.write(MessageChangeUtils.stringToByteBuffer(MessageChangeUtils.messageInfoToString(
                                        MessageInfo.builder()
                                                .context(messageInfo.getContext())
                                                .type("2").userIds((String)key.attachment()).build())));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        }
    }


    public static void main(String[] args) throws Exception{
        new ChatServer().initServer();
    }
}
