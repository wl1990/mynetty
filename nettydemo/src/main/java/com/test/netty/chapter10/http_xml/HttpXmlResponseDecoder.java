package com.test.netty.chapter10.http_xml;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

import java.util.List;

public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<DefaultFullHttpResponse> {
    public HttpXmlResponseDecoder(Class<?> clazz){
        this(clazz,false);
    }
    public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrint) {
        super(clazz, isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DefaultFullHttpResponse defaultFullHttpResponse, List<Object> list) throws Exception {
        HttpXmlResponse httpXmlResponse=new HttpXmlResponse(defaultFullHttpResponse,decode0(channelHandlerContext,defaultFullHttpResponse.content()));
        list.add(httpXmlResponse);
    }
}
