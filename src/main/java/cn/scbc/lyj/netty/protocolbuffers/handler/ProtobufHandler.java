package cn.scbc.lyj.netty.protocolbuffers.handler;

import cn.scbc.lyj.netty.protocolbuffers.util.UserChannelMapUtils;
import cn.scbc.protobuf.demo.v1.BindMessage;
import cn.scbc.protobuf.demo.v1.Model;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author:SCBC_LiYongJie
 * @time:2022/4/4
 */

@Component
@ChannelHandler.Sharable
public class ProtobufHandler extends SimpleChannelInboundHandler<Model> {

    private static final Logger log = LoggerFactory.getLogger(ProtobufHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        log.info("链接报告信息:有一客户端链接到本服务端。channelId：[{}]",channel.id());
        log.info("链接报告IP:[{}]",channel.localAddress().getHostString());
        log.info("链接报告Port:[{}]\n",channel.localAddress().getPort());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端--[{}]--断开链接\n",ctx.channel().localAddress().toString());
        UserChannelMapUtils.unBind(ctx.channel());
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端--[{}]--handlerRemoved\n",ctx.channel().localAddress().toString());
        UserChannelMapUtils.unBind(ctx.channel());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常信息：\\r\\n[{}]\n",cause.getMessage());
        UserChannelMapUtils.unBind(ctx.channel());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Model model) throws Exception {
        log.info("接收到的数据类型为:[{}]",model.getDataType());

        switch (model.getDataType()){
            case BIND:
                log.info("BindMessage数据为:[{}]",model.getBindMessage());
                BindMessage bindMessage = model.getBindMessage();
                UserChannelMapUtils.doBind(bindMessage.getNumber(),channelHandlerContext.channel());
                break;
            case JOINT_SEND:
                log.info("Joint数据为:[{}]\n",model.getJointMessage());
                JointMessageHandler.joint(model.getJointMessage());
                break;
            case CHAT_SEND:
                log.info("ChatMessage数据为:[{}]\n",model.getChatRequest());
                ChatMessageHandler.sendChatMessage(model.getChatRequest());
                break;
            case UNRECOGNIZED:
                SocketChannel channel = (SocketChannel) channelHandlerContext.channel();
                log.warn("检测到非法/无法鉴别的消息类型到来:IP--[{}]--Port--[{}]",channel.localAddress().getHostString(),channel.localAddress().getPort());
                log.warn("非法/无法鉴别的消息:[{}]\n",Model.getDescriptor().toString());
                //对于无法识别的类型 unbind and remove channel
                UserChannelMapUtils.unBind(channelHandlerContext.channel());
                break;
        }

    }
}
