package com.is.websocket;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
@Component
public class SocketHandle implements ApplicationListener<ContextRefreshedEvent>{
	private static final int PORT = 8087;
	private static Logger logger = Logger.getLogger(SocketHandle.class);
	

	//private static final String IP = "120.26.60.164";
	private static final String IP = "127.0.0.1";

	//protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2; // 榛樿

	//protected static final int BIZTHREADSIZE = 4;

	private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

	@SuppressWarnings("rawtypes")
	protected static void start() throws Exception {
		ServerBootstrap b = new ServerBootstrap();
		//b.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024*1024, 1024*1024));
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);
		b.childHandler(new ChannelInitializer() {
			@Override
			public void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast(new LengthFieldPrepender(4, true));
				pipeline.addLast(new HttpServerInboundHandler());
			}

		});

		b.bind(IP, PORT).sync();
	}

	protected static void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}


	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("寮�濮嬪惎鍔═CP鏈嶅姟鍣�...");
		logger.info("start tcp...");
		try {
			SocketHandle.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
