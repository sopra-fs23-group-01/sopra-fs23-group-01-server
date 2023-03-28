package ch.uzh.ifi.hase.soprafs23.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j //lombok jar包，帮我们自动生成一些代码：@Data
@Component
@ServerEndpoint("/websocket/{username}")
public class WebSocketService {
    public static final Map<String, Session> CLIENTS = new ConcurrentHashMap<>();
    /**
     * 连接建立时触发
     */
    @OnOpen
    public synchronized void openSession(@PathParam("username") String username, Session session) {
        String message = "[" + username + "]登录";
        //存放到map集合中
        CLIENTS.put(username,session);
        //告诉自己当前在线的人数
        Set<String> userNames = CLIENTS.keySet();
        userNames.forEach(u -> {
            try {
                session.getBasicRemote().sendText("[" + u + "]登录");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //告诉所有人
        CLIENTS.forEach((u,s) -> {
            try {
                if (s != session)  //上面我已经告诉自己了  所以不能再告诉自己了
                    s.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 客户端接收服务端数据时触发
     */
    @OnMessage
    public synchronized void onMessage(@PathParam("username") String username, String message) {
        log.info("发送消息：{}, {}", username, message);
        //告诉所有人发消息
        String value = "["+username+"]:"+ message;
        CLIENTS.forEach((u,s) -> {
            try {
                s.getBasicRemote().sendText(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 连接关闭时触发
     */
    @OnClose
    public synchronized void onClose(@PathParam("username") String username, Session session) {
        // 当前的Session移除某个用户
        CLIENTS.remove(username);
        //离开消息通知所有人有人离开了
        CLIENTS.forEach((u,s) -> {
            try {
                s.getBasicRemote().sendText("[" + username + "]离开");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 通信发生错误时触发
     */
    @OnError
    public synchronized void onError(Session session, Throwable throwable) {
        try {
            //关闭WebSocket Session会话
            System.out.println("被摧毁");
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("onError Exception", e);
        }
        log.info("Throwable msg " + throwable.getMessage());
    }
}
