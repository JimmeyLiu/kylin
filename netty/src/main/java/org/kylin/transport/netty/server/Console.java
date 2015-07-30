package org.kylin.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.kylin.common.command.CommandFactory;
import org.kylin.common.util.Config;
import org.kylin.common.util.StringUtils;

/**
 * Created by jimmey on 15-7-28.
 */
public class Console {
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();
    private static final CommandHandler SERVER_HANDLER = new CommandHandler();

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            pipeline.addLast(DECODER);
                            pipeline.addLast(ENCODER);
                            pipeline.addLast(SERVER_HANDLER);
                        }
                    });

            b.bind(Config.getConsolePort()).syncUninterruptibly();//.syncUninterruptibly().channel().closeFuture().syncUninterruptibly();
        } catch (Exception e) {
        }
    }

    static String LF = "\r\n";
    static String USAGE = "Support Commands: " + CommandFactory.SUPPORT;

    @ChannelHandler.Sharable
    public static class CommandHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.write("Welcome Use Kylin Console");
            ctx.write(LF);
            ctx.write(USAGE);
            ctx.write(LF);
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            if (StringUtils.isEmpty(msg)) {
                return;
            }
            boolean close = false;
            String response;
            if ("bye".equalsIgnoreCase(msg)) {
                response = "Good Bye!!";
                close = true;
            } else {
                response = CommandFactory.handle(msg);
                if (response == null) {
                    response = USAGE;
                }
            }

            ctx.write(response);
            ChannelFuture future = ctx.write(LF);
            if (close) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
        }
    }


}
