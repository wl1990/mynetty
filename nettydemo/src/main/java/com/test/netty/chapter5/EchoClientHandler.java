package com.test.netty.chapter5;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author wanglei
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
    private int counter;
    static final String ECHO_REQ="Hi,Lilinfeng.Welcome to Netty.$_";
    public EchoClientHandler(){

    }
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        for(int i=0;i<10;i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        System.out.println("This is = [" + ++counter + "], times receive serveer :[" + msg + "]");
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)throws Exception{
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable causee){
        causee.printStackTrace();
        ctx.close();
    }
}
