package cn.scbc.lyj.netty.protocolbuffers.handler;

import cn.scbc.lyj.netty.protocolbuffers.util.UserChannelMapUtils;
import cn.scbc.protobuf.demo.v1.ChatRequest;
import cn.scbc.protobuf.demo.v1.ChatResponse;
import cn.scbc.protobuf.demo.v1.Model;
import io.netty.channel.Channel;

import java.util.Objects;

/**
 * @Author:SCBC_LiYongJie
 * @time:2022/4/5
 */

public class ChatMessageHandler {

    public static void sendChatMessage(ChatRequest chatRequest){
        String senderNickName = chatRequest.getSenderNickName();
        String senderAvatar = chatRequest.getSenderAvatar();
        String msg = chatRequest.getChatMessage();
        chatRequest.getOtherMemberList().asByteStringList().forEach(bytes -> {
            String receiver = bytes.toStringUtf8();
            //判断receiver bind 的 连接是否活跃
            if (UserChannelMapUtils.isOnline(receiver)) {
                ChatResponse chatResponse = ChatResponse.newBuilder()
                        .setSenderAvatar(senderAvatar)
                        .setSenderNickName(senderNickName)
                        .setChatMessage(msg)
                        .build();
                Model model = Model.newBuilder().setDataType(Model.ModelType.CHAT_REC).setChatResponse(chatResponse).build();
                Channel channel = UserChannelMapUtils.getChannel(receiver);
                if (!Objects.isNull(channel))
                    channel.writeAndFlush(model);
            }
        });
    }


}
