package cn.scbc.lyj.netty.protocolbuffers.handler;

import cn.scbc.lyj.netty.protocolbuffers.util.UserChannelMapUtils;
import cn.scbc.protobuf.demo.v1.Joint;
import cn.scbc.protobuf.demo.v1.JointMessage;
import cn.scbc.protobuf.demo.v1.Model;
import com.google.protobuf.ByteString;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Objects;

/**
 * @Author:SCBC_LiYongJie
 * @time:2022/4/5
 */

public class JointMessageHandler {

    public static void joint(JointMessage jointMessage){
        switch (jointMessage.getType()){
            case 1:
                doJoint(jointMessage.getOtherMemberList().asByteStringList(),1);
                break;
            case 2:
                doJoint(jointMessage.getOtherMemberList().asByteStringList(),2);
                break;
        }

    }

    /**
     * 协同
     * @param receivers   all otherMember
     * @param type  start = 1 , pause = 2
     */
    private static void doJoint(List<ByteString> receivers , int type){
        receivers.forEach(bytes -> {
            String receiver = bytes.toStringUtf8();
            //首先判断receiver bind 的连接是否活跃
            if (UserChannelMapUtils.isOnline(receiver)) {
                Joint joint = Joint.newBuilder().setType(type).build();
                Model model = Model.newBuilder().setDataType(Model.ModelType.JOINT_REC).setJoint(joint).build();
                Channel channel = UserChannelMapUtils.getChannel(receiver);
                if (!Objects.isNull(channel))
                    channel.writeAndFlush(model);
            }
        });
    }

}
