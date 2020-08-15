package com.test.netty.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.omg.CORBA.BAD_CONTEXT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaderUtil.setContentLength;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    public HttpFileServerHandler(String url){

    }
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if(!request.decoderResult().isSuccess()){
            sendError(ctx,HttpResponseStatus.BAD_REQUEST);
            return;
        }
      /*  if(request.method()!=HttpMethod.GET){
            sendError(ctx,HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }*/
        final String uri=request.uri();
        final String path=sanitizeUri(uri);
        if(path==null){
            sendError(ctx,HttpResponseStatus.FORBIDDEN);
            return;
        }
        File file=new File(path);
        if(file.isHidden() || !file.exists()){
            sendError(ctx,HttpResponseStatus.NOT_FOUND);
            return;
        }
        if(!file.isFile()){
            sendError(ctx,HttpResponseStatus.FORBIDDEN);
            return;
        }
        RandomAccessFile randomAccessFile=null;
        try{
            randomAccessFile=new RandomAccessFile(file,"r");
        }catch(FileNotFoundException fnfe){
            sendError(ctx,HttpResponseStatus.NOT_FOUND);
            return;
        }
        long fileLength=randomAccessFile.length();
        HttpResponse response=new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        setContentLength(response,fileLength);
        setContentTypeHeader(response,file);
        if(isKeepAlive(request)){
            response.headers().set("connection",HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);
        ChannelFuture sendFileFuture;
        sendFileFuture=ctx.write(new ChunkedFile(randomAccessFile,0,fileLength,8192),ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long l, long l1) throws Exception {

            }

            @Override
            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {

            }
        });

    }

    private void setContentTypeHeader(HttpResponse response, File file) {
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus notFound) {
    }
    private static final Pattern INSECURE_URI=Pattern.compile("");
    private String sanitizeUri(String uri) {
        try{
            uri=URLDecoder.decode(uri,"UTF-8");
        }catch(UnsupportedEncodingException e){
            try{
                uri=URLDecoder.decode(uri,"ISO-8859-1");
            }catch(UnsupportedEncodingException e1){
                throw new Error();
            }
        }
        uri=uri.replace('/',File.separatorChar);
        if(uri.contains(File.separator+'.') || uri.contains('.'+File.separator)
                || uri.startsWith(".")
                || uri.endsWith(".")
                || INSECURE_URI.matcher(uri).matches()){
            return null;
        }
        return System.getProperty("user.dir")+File.separator+uri;
    }
    private static final Pattern ALLOW_FILE_NAME=Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*]");
    private static void sendListing(ChannelHandlerContext ctx,File dir){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        response.headers().set("content_type","text/html;charset=UTF-8");
        StringBuilder buf=new StringBuilder();
        buf.append("<li>connect:<a  href=\"../\">..</a></li>\r\n");
        for(File f:dir.listFiles()){
            buf.append("<li>connect:<a href=\">");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer= Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


}
