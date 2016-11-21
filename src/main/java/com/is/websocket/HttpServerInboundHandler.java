package com.is.websocket;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
	private static Logger logger = Logger.getLogger(HttpServerInboundHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// super.channelRead(ctx, msg);
		ByteBuf result = (ByteBuf) msg;
		byte[] result1 = new byte[result.readableBytes()];
		result.readBytes(result1);
		result.release();
		SocketService.handleSocketMsg(result1,ctx);

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelReadComplete(ctx);
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("throw exption..."+cause.getMessage());
		ctx.writeAndFlush("error:"+cause.getMessage());
		ctx.close();
	}

}
