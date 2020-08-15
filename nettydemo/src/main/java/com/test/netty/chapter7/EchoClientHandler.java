package com.test.netty.chapter7;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {
    private final int sendNumber;
    public EchoClientHandler(int sendNumber){
        this.sendNumber=sendNumber;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        Userinfo[] infos= UserInfo();
        for(Userinfo userinfo:infos){
            ctx.write(userinfo);
        }
        ctx.flush();
    }

    private Userinfo[] UserInfo() {
        System.out.println("sendNumber="+sendNumber);
        Userinfo[] infos= new Userinfo[sendNumber];
        for(int i=0;i<sendNumber;i++){
            Userinfo userinfo=new Userinfo();
            userinfo.setAge(i);
            userinfo.setName("abc-"+i);
            infos[i]=userinfo;
        }
        return infos;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        System.out.println("client receive the msgpack message:" + msg );
        ctx.write(msg);
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }

}
