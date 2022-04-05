package cn.scbc.lyj.netty.protocolbuffers.server;

import cn.scbc.lyj.netty.protocolbuffers.handler.ProtobufHandler;
import cn.scbc.protobuf.demo.v1.Model;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:SCBC_LiYongJie
 * @time:2022/4/4
 *
 */

@Component
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private ProtobufHandler protobufHandler;

    @Value("${netty.server.websocket.path}")
    private String WEBSOCKET_PATH;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(1024 * 64))
                .addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH))
                //将WebSocketFrame转为ByteBuf 以便后面的 ProtobufDecoder 解码
                .addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
                        ByteBuf byteBuf = frame.content();
                        byteBuf.retain();
                        out.add(byteBuf);
                    }
                })
                .addLast(new ProtobufDecoder(Model.getDefaultInstance()))
                .addLast(protobufHandler)
                .addLast(new ProtobufEncoder() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                        Model model = (Model) msg.getDefaultInstanceForType();
                        WebSocketFrame frame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(model.toByteArray()));
                        out.add(frame);
                    }
                });
    }

}
