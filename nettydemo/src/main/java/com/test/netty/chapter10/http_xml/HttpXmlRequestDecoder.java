package com.test.netty.chapter10.http_xml;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpXmlRequestDecoder extends AbstractHttpXmlDecoder<FullHttpRequest> {
    public HttpXmlRequestDecoder(Class<?> clazz) {
        this(clazz,false);
    }

    public HttpXmlRequestDecoder(Class<?> clazz, boolean isPrint) {
        super(clazz, isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest, List<Object> list) throws Exception {
        if(!fullHttpRequest.decoderResult().isSuccess()){
            sendError(channelHandlerContext,BAD_REQUEST);
            return;
        }
        HttpXmlRequest request=new HttpXmlRequest(fullHttpRequest,decode0(channelHandlerContext,fullHttpRequest.content()));
        list.add(request);
    }

    private void sendError(ChannelHandlerContext channelHandlerContext, HttpResponseStatus badRequest) {
        FullHttpResponse response=new DefaultFullHttpResponse(HTTP_1_1,badRequest, Unpooled.copiedBuffer("Failure:"+badRequest.toString()
                +"\r\n",CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=UTF-8");
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
