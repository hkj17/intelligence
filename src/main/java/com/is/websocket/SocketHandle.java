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
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
@Component
public class SocketHandle implements ApplicationListener<ContextRefreshedEvent>{
	private static final int PORT = 8087;
	private static Logger logger = Logger.getLogger(SocketHandle.class);
	

	private static final String IP = "120.26.60.164";
	//private static final String IP = "192.168.223.31";

	protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2; // 默认

	protected static final int BIZTHREADSIZE = 4;

	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);

	@SuppressWarnings("rawtypes")
	protected static void start() throws Exception {
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);
		b.childHandler(new ChannelInitializer() {
			@Override
			public void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				//pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(1024*1024, 0, 2, 0, 2));
				/*pipeline.addLast("decoder", new ());
				pipeline.addLast("encoder", new ByteArrayEncoder());*/
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
		System.out.println("开始启动TCP服务器...");
		logger.info("start tcp...");
		try {
			SocketHandle.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
