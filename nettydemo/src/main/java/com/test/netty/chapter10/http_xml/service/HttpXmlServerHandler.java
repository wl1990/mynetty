package com.test.netty.chapter10.http_xml.service;

import com.test.netty.chapter10.http_xml.Address;
import com.test.netty.chapter10.http_xml.HttpXmlRequest;
import com.test.netty.chapter10.http_xml.HttpXmlResponse;
import com.test.netty.chapter10.http_xml.Order;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpXmlServerHandler extends SimpleChannelInboundHandler<HttpXmlRequest> {

    @Override
    protected void messageReceived(final ChannelHandlerContext channelHandlerContext, HttpXmlRequest httpXmlRequest) throws Exception {
        HttpRequest request=httpXmlRequest.getRequest();
        Order order= (Order) httpXmlRequest.getBody();
        System.out.println("http server receiver request = [" + order + "]");
        dobusiness(order);
        ChannelFuture future=channelHandlerContext.writeAndFlush(new HttpXmlResponse(null,order));
        if(!isKeepAlive(request)){
            future.addListener(new GenericFutureListener<Future<? super Void>>(){

                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    channelHandlerContext.close();
                }
            });
        }
    }

    private void dobusiness(Order order) {
        order.getCustomer().setFirstName("狄");
        order.getCustomer().setLastName("仁杰");
        List<String> midNames = new ArrayList<String>();
        midNames.add("李元芳");
        order.getCustomer().setMiddleNames(midNames);
        Address address = order.getBillTo();
        address.setCity("洛阳");
        address.setCountry("大唐");
        address.setState("河南道");
        address.setPostCode("123456");
        order.setBillTo(address);
        order.setShipTo(address);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause)throws Exception{
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            sendError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response=new DefaultFullHttpResponse(HTTP_1_1,status,
                Unpooled.copiedBuffer("失败："+status.toString()+"\r\n",CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
