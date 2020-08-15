package com.test.netty.chapter10.http_xml;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.List;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpXmlResponseEncoder extends AbstractHttpXmlEncoder<HttpXmlResponse> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, HttpXmlResponse httpXmlResponse, List<Object> list) throws Exception {
        ByteBuf body=encode0(channelHandlerContext,httpXmlResponse.getResult());
        FullHttpResponse response=httpXmlResponse.getHttpResponse();
        if(response==null){
            response=new DefaultFullHttpResponse(HTTP_1_1,HttpResponseStatus.OK,body);
        }else{
            response=new DefaultFullHttpResponse(httpXmlResponse.getHttpResponse().protocolVersion(),httpXmlResponse.getHttpResponse().status(),body);
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/xml");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,body.readableBytes());
        list.add(response);

    }
}
