package com.h.msgservice.services;

import com.alibaba.fastjson.JSON;
import com.h.common.bean.Response;
import com.h.msgservice.bean.Message;
import com.h.msgservice.util.Encryption;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/user/ws")
@Component
public class WebSocketServer {
    private static Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static UserBasicService userBasicService;
    private static RedisTemplate redisTemplate;
    private static Encryption encryption;
    private static AuthorizeCenter authorizeCenter;

    @Resource
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Resource
    public void setAuthorizeCenter(AuthorizeCenter authorizeCenter) {
        this.authorizeCenter = authorizeCenter;
    }

    @Resource
    public void setUserBasicService(UserBasicService userBasicService) {
        this.userBasicService = userBasicService;
    }

    @Resource
    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    /**
     * 连接成功
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) throws Exception {
        //验证合法性
        System.out.println(authorizeCenter);
        Response response = authorizeCenter.authorize(session.getRequestParameterMap().get("token").get(0));
        System.out.println(response);
//        if (response.getCode() == 401) {
//            sendMessageToUser(session, new Message("server", session.getRequestParameterMap().get("uid").get(0), "authorize fail", "authorizeFail"));
//            return;
//        }
        String uid = encryption.desDecrypt(String.valueOf(session.getRequestParameterMap().get("uid").get(0)));
        sessions.put(uid, session);
        System.out.println(uid + " connect");
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessageByCache(session, uid + "_unread_message");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessageByCache(session, uid + "_unread_request");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t1.start();
        t2.start();
    }

    /**
     * 连接关闭
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) throws Exception {
        //移除session
        String uid = encryption.desDecrypt(String.valueOf(session.getRequestParameterMap().get("uid").get(0)));
        sessions.remove(uid);
        System.out.println(uid + "connection close");
    }

    /**
     * 接收到消息
     *
     * @param text
     */
    @OnMessage
    public void onMsg(Session session, String text) throws Exception {
        String uid = encryption.desDecrypt(String.valueOf(session.getRequestParameterMap().get("uid").get(0)));
        System.out.println("test:" + text);
        Message msg = JSON.parseObject(text, Message.class);
        //System.out.println(msg);
        if (msg.getType().equals("heart")) {
            //心跳
            System.out.println("heart check");
            return;
        }
        Session toSession = sessions.get(msg.getTo());
        //正常信息
        if (msg.getType().equals("message")) {
            //判断对方好友列表是否有该位用户
            if (!userBasicService.ifExitFriend(msg.getTo(), msg.getFrom())) {
                sendMessageToUser(session, new Message(uid, uid,
                        "no this friend", "friend_not_found"));
                return;
            }
            if (toSession != null) {
                try {
                    sendMessageToUser(toSession, msg);
                } catch (IOException e) {
                    //发送失败返回客户端错误信息
                    Message message = new Message(uid, uid
                            , "fail to send", "error");
                    sendMessageToUser(session, msg);
                }
            } else {
                //不在线情况处理
                System.out.println("no session");
                messageCache(msg, msg.getTo() + "_unread_message");
            }
        } else if (msg.getType().equals("friend_request")) {
            //好友请求
            if (toSession != null) {
                //在线情况
                sendMessageToUser(toSession, msg);
            } else {
                //不在线情况
                requestCache(msg, msg.getTo() + "_unread_request");
            }
        } else if (msg.getType().equals("friend_request_confirm")) {
            //同意请求
            if (toSession != null) {
                //在线情况
                sendMessageToUser(toSession, msg);
            } else {
                //不在线情况
                requestCache(msg, msg.getTo() + "_unread_request");
            }
        }

    }

    /**
     * 给指定用户发信息
     */
    public void sendMessageToUser(Session session, Message msg) throws IOException {
        session.getBasicRemote().sendText(JSON.toJSONString(msg));
    }

    //信息缓存
    public void messageCache(Message msg, String key) {
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForList().rightPush(key, msg);
            redisTemplate.persist(key);
        } else {
            redisTemplate.opsForList().rightPush(key, msg);
        }
    }

    //请求缓存
    public void requestCache(Message msg, String key) {
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForList().rightPush(key, msg);
            redisTemplate.persist(key);
        } else {
            //对于相同的添加好友请求只存一条，相同的判定为from和to相同
            List<Message> messages = redisTemplate.opsForList().range(key, 0, -1);
            boolean b = true;
            for (Message message : messages) {
                if (message.getTo().equals(msg.getTo()) && message.getFrom().equals(msg.getFrom())) {
                    b = false;
                    break;
                }
            }
            if (b) {
                redisTemplate.opsForList().rightPush(key, msg);
            }
        }
    }

    //取出缓存的数据发送给相应的客户端
    public void sendMessageByCache(Session session, String key) throws IOException {
        List<Message> messages = redisTemplate.opsForList().range(key, 0, -1);
        if (messages.size() == 0) {
            return;
        }
        redisTemplate.delete(key);
        for (Message message : messages) {
            sendMessageToUser(session, message);
        }
    }
//    @OnError
//    public void error(Session session, Exception e) {
//        System.out.println(e.getMessage());
//    }
}