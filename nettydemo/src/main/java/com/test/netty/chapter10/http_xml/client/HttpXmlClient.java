package com.test.netty.chapter10.http_xml.client;

import com.test.netty.chapter10.http_xml.Order;
import com.test.netty.chapter10.http_xml.HttpXmlRequestEncoder;
import com.test.netty.chapter10.http_xml.HttpXmlResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;

public class HttpXmlClient {
    public void connect(int port) throws Exception{
        EventLoopGroup group=new NioEventLoopGroup();
        try{
            Bootstrap b=new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>(){

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("http-decoder",new HttpResponseDecoder());
                            socketChannel.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                            socketChannel.pipeline().addLast("xml-decoder",new HttpXmlResponseDecoder(Order.class,true));
                            socketChannel.pipeline().addLast("http-encoder",new HttpRequestEncoder());
                            socketChannel.pipeline().addLast("xml-encoder",new HttpXmlRequestEncoder());
                            socketChannel.pipeline().addLast("xmlClientHandler",new HttpXmlClientHandler());
                        }
                    });
            ChannelFuture channelFuture=b.connect(new InetSocketAddress(port)).sync();
            channelFuture.channel().closeFuture().sync();
        }finally{
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port=8080;
        if(args!=null && args.length>0){
            try{
                port=Integer.valueOf(args[0]);
            }catch(NumberFormatException e){

            }
        }
        new HttpXmlClient().connect(port);
    }

}
