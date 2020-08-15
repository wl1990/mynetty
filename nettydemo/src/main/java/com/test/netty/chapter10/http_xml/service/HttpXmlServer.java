package com.test.netty.chapter10.http_xml.service;

import com.test.netty.chapter10.http_xml.HttpXmlRequestDecoder;
import com.test.netty.chapter10.http_xml.HttpXmlResponseEncoder;
import com.test.netty.chapter10.http_xml.Order;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;

public class HttpXmlServer {
    public void run(final int port) throws Exception{
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try{
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>(){

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("http-decoder",new HttpRequestDecoder());
                            socketChannel.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                            socketChannel.pipeline().addLast("xml-decoder",new HttpXmlRequestDecoder(Order.class,true));
                            socketChannel.pipeline().addLast("http-encoder",new HttpResponseEncoder());
                            socketChannel.pipeline().addLast("xml-encoder",new HttpXmlResponseEncoder());
                            socketChannel.pipeline().addLast("xmlServerHandler",new HttpXmlServerHandler());
                        }
                    });
                    ChannelFuture future=b.bind(new InetSocketAddress(port));
                    System.out.println("http 订购服务器启动 网址是 = [ http://localhost:" + port + "]");
                    future.channel().closeFuture().sync();
        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        int port=8080;
        if(args.length>0){
            try{
                port=Integer.parseInt(args[0]);
            }catch(NumberFormatException e){
                e.printStackTrace();
            }
        }
        new HttpXmlServer().run(port);
    }
}
