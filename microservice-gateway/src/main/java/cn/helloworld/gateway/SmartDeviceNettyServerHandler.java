package cn.helloworld.gateway;

import cn.helloworld.gateway.iot.AtomconnectionYZ600;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * 智能设备 tcp 消息处理
 *
 * @author zhangkai
 */
@Slf4j
public class SmartDeviceNettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取客户端发送来的数据
     * @param ctx ChannelHandler的上下文对象 有管道 pipeline 通道 channel 和 请求地址 等信息
     * @param msg 客户端发送的具体数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        // 报文解析调度
        AtomconnectionYZ600.exec(buf.toString(CharsetUtil.UTF_8),ctx);
    }

    /**
     * 读取客户端发送数据完成后的方法
     *    在本方法中可以发送返回的数据
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // TODO: 2021/1/15
    }



    public static void main(String[] args) {
        // 创建对应的 线程池
        // 创建Boss group
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        // 创建 workgroup
        EventLoopGroup workGroup = new NioEventLoopGroup();
        // 创建对应的启动类
        ServerBootstrap bootstrap = new ServerBootstrap();
        try{
            // 设置相关的配置信息
            // 设置对应的线程组
            bootstrap.group(boosGroup,workGroup)
                    // 设置对应的通道
                    .channel(NioServerSocketChannel.class)
                    // 设置线程的连接个数
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        /**
                         * 给 pipeline 设置处理器
                         * @param socketChannel
                         * @throws Exception
                         */
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new SmartDeviceNettyServerHandler());
                        }
                    });
            // 绑定端口  启动服务
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();
            log.info("tcp服务器启动，端口：{}",6668);
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            log.error("tcp服务器异常抛出，端口：{}",6668);
        }finally {
            // 优雅停服
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}