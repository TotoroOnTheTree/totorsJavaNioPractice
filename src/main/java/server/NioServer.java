package server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description
 * @Author mao.xu
 * @Version 1.0
 * @Since 1.0
 * @Date 2021/7/20
 **/
public class NioServer {

    private static Selector selector;
    private static ServerSocketChannel channel;

    public static void main(String[] args) {
        try {
            selector = Selector.open();
            channel = ServerSocketChannel.open();
            SocketAddress local = new InetSocketAddress("localhost", 9092);
            channel.configureBlocking(false);
            channel.socket().bind(local);
            channel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                if (selector.select() == 0) {
                    System.out.println("没有准备好的连接，稍等");
                    continue;
                }
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isConnectable()) {
                        handleConnectable(key);
                    }
                    if (key.isAcceptable()) {
                        handleAcceptable(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    if (key.isWritable()) {
                        handleWrite(key);
                    }
                    //移除channel
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (selector != null) {
                    selector.close();
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleWrite(SelectionKey key) {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel accept = channel.accept();
            ByteBuffer attachment = (ByteBuffer) key.attachment();
            SocketAddress remoteAddress = accept.getRemoteAddress();
            String client = "你好：" + remoteAddress.toString();
            attachment.put(client.getBytes(StandardCharsets.UTF_8));
            while (attachment.hasRemaining()) {
                accept.write(attachment);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRead(SelectionKey key) {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel accept = channel.accept();
            ByteBuffer attachment = (ByteBuffer) key.attachment();
            int read = accept.read(attachment);
            StringBuffer buffer = new StringBuffer();
            do {
                attachment.flip();
                while (attachment.hasRemaining()) {
                    buffer.append(attachment.get());
                }
                attachment.clear();
                read = accept.read(attachment);
            } while (read > 0);
            System.out.println("接收到消息：" + buffer.toString());
            channel.register(selector, SelectionKey.OP_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void handleAcceptable(SelectionKey key) {
        SelectableChannel channel = key.channel();
        try {
            System.out.println("channel已被接受");
            channel.register(selector, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    private static void handleConnectable(SelectionKey key) {
        SelectableChannel channel = key.channel();
        try {
            channel.configureBlocking(false);
            System.out.println("channel准备就绪");
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
