
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.util.Date;

public class TestClient {

    public static void main(String[] args) throws Exception {
        test();
    }

    public static void test() throws Exception {
        URI uri = new URI("http://www.baidu.com");
        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = "www.baidu.com";
        int port = 80;

        boolean ssl = "https".equalsIgnoreCase(scheme);

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            NettyClientInitializer nettyClientInitializer = new NettyClientInitializer(ssl);

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(nettyClientInitializer);
            // Make the connection attempt.
            Date aDate =new Date();
            Channel ch = b.connect(host, port).sync().channel();
            // Prepare the HTTP request.
            HttpRequest request = new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);

            ch.writeAndFlush(request);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
            Date bDate=new Date();
            System.out.println(String.valueOf(bDate.getTime()-aDate.getTime()));
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }
}

class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private final boolean ssl;

    public static ChannelTrafficShapingHandler channelTrafficShapingHandler;

    public NettyClientInitializer(boolean ssl) {
        this.ssl = ssl;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline p = ch.pipeline();

        // 2G
        long writeLimit =2764;
        long readLimit = 9830;
        
        // 3G
        //long writeLimit =1024*384;
        //long readLimit = (long) (1024*1024*3.6);
        
        
        channelTrafficShapingHandler = new ChannelTrafficShapingHandler(writeLimit, readLimit);
        p.addLast(channelTrafficShapingHandler);

        p.addLast("codec", new HttpClientCodec());

        p.addLast("handler", new TestClientHandler());
    }
}

class TestClientHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
       if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;

           // System.out.println("STATUS: " + response.getStatus());
           // System.out.println("VERSION: " + response.getProtocolVersion());
           // System.out.println();

            if (!response.headers().isEmpty()) {
                for (String name: response.headers().names()) {
                    for (String value: response.headers().getAll(name)) {
                     //   System.out.println("HEADER: " + name + " = " + value);
                    }
                }
               // System.out.println();
            }

            if (HttpHeaders.isTransferEncodingChunked(response)) {
            //    System.out.println("CHUNKED CONTENT {");
            } else {
             //   System.out.println("CONTENT {");
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;

            //System.out.println(content.content().toString(CharsetUtil.UTF_8));
           // System.out.flush();

            if (content instanceof LastHttpContent) {
            //    System.out.println("} END OF CONTENT");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}