package cn.scbc.lyj.netty.protocolbuffers.util;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author:SCBC_LiYongJie
 * @time:2022/4/5
 */

public class UserChannelMapUtils {

    private static final Logger log = LoggerFactory.getLogger(UserChannelMapUtils.class);

    //可以用来广播消息
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //实现点对点的消息传播--后期会整合kafka消息队列
    private static final Map<String, Channel> USER_CHANNEL = new ConcurrentHashMap<>(256);
    private static final Map<Channel, String> CHANNEL_USER = new ConcurrentHashMap<>(256);

    public synchronized static void doBind(String number,Channel channel){

        //之前该用户已经绑定过，再次绑定的时候需要从CHANNEL_GROUP删除该channel并且随手close
        if (USER_CHANNEL.containsKey(number)){
            Channel oldChannel = USER_CHANNEL.get(number);
            CHANNEL_GROUP.remove(oldChannel);
            CHANNEL_USER.remove(oldChannel);
            oldChannel.close();
        }

        CHANNEL_GROUP.add(channel);
        USER_CHANNEL.put(number,channel);
        CHANNEL_USER.put(channel,number);

        log.info("User<---->Channel Map bind renew:[{}]",USER_CHANNEL);
        log.info("Channel<---->User Map bind renew:[{}]",CHANNEL_USER);
        log.info("Channel Group bind renew:[{}]\n",CHANNEL_GROUP);
    }

    public synchronized static void unBind(Channel channel){
        String number = CHANNEL_USER.get(channel);
        if (!Objects.isNull(number)){
            USER_CHANNEL.remove(number);
        }

        CHANNEL_USER.remove(channel);
        CHANNEL_GROUP.remove(channel);

        if (!ObjectUtils.isEmpty(channel))
            channel.close();

        log.info("User<---->Channel Map unBind renew:[{}]",USER_CHANNEL);
        log.info("Channel<---->User Map unBind renew:[{}]",CHANNEL_USER);
        log.info("Channel Group bind renew:[{}]\n",CHANNEL_GROUP);
    }

    public synchronized static void unBind(String number){

        Channel channel = USER_CHANNEL.get(number);
        USER_CHANNEL.remove(number);
        CHANNEL_USER.remove(channel);
        CHANNEL_GROUP.remove(channel);

        if (!ObjectUtils.isEmpty(channel))
            channel.close();

    }

    public static Boolean isOnline(String number){
        return USER_CHANNEL.containsKey(number);
    }

    public static Channel getChannel(String number){
        return USER_CHANNEL.get(number);
    }

}
