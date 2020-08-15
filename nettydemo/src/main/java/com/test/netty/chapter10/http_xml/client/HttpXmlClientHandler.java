package com.test.netty.chapter10.http_xml.client;

import com.test.netty.chapter10.http_xml.OrderFactory;
import com.test.netty.chapter10.http_xml.HttpXmlRequest;
import com.test.netty.chapter10.http_xml.HttpXmlResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HttpXmlClientHandler extends SimpleChannelInboundHandler<HttpXmlResponse> {
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        HttpXmlRequest request=new HttpXmlRequest(null,OrderFactory.create(123));
        ctx.writeAndFlush(request);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, HttpXmlResponse httpXmlResponse) throws Exception {
        System.out.println("the client receiver response of http header is = [" + httpXmlResponse.getHttpResponse().headers().names() + "]");
        System.out.println("the client receiver response of http body is = [" + httpXmlResponse.getResult() + "]");
    }
}
