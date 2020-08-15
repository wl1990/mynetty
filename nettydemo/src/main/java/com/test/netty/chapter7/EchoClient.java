package com.test.netty.chapter7;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class EchoClient {
    private final String host;
    private final int port;
    private final int sendNumber;
    public EchoClient(String host,int port,int sendNumber){
        this.host=host;
        this.port=port;
        this.sendNumber=sendNumber;
    }
    public void run() throws InterruptedException {
        EventLoopGroup group=new NioEventLoopGroup();
        try{
            Bootstrap b=new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                            socketChannel.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                            socketChannel.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                            socketChannel.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                            socketChannel.pipeline().addLast(new EchoClientHandler(sendNumber));
                            System.out.println("socketChannel = [" + socketChannel + "]");
                        }
                    });
            ChannelFuture channelFuture=b.connect(host,port).sync();
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
        new EchoClient("127.0.0.1",port,1000).run();
    }
}
