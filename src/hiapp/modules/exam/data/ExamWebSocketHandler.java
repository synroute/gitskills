package hiapp.modules.exam.data;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import hiapp.modules.exam.bean.ExamSession;

public class ExamWebSocketHandler extends TextWebSocketHandler{
	@Autowired
	private Logger logger;
	@Autowired
	private ExamSession examSession;
	
	//接收文本消息，并发送出去
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("sendMessage......" + session.getId());
		super.handleTextMessage(session, message);
	}
	
	//连接建立后处理
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.debug("connect success......" + session.getId());
		String userId=(String) session.getAttributes().get("userId");
		examSession.addSession(session, userId);
		super.afterConnectionEstablished(session);
	}


	//抛出异常时处理
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.debug("error......" + session.getId());
		if(session.isOpen()) {
			session.close();
		}
		examSession.removeSession(session);
		super.handleTransportError(session, exception);
	}
	 //连接关闭后处理
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("close......" + session.getId());
		examSession.removeSession(session);
		super.afterConnectionClosed(session, status);
	}
    public boolean sendMessageToUser(String userId, TextMessage message) {
    	logger.info(String.format("webSocket预约进入"));
    	List<WebSocketSession> userSessions = examSession.getSession(userId);
		if (null == userSessions) {
			return false;
		}else {
			logger.info(String.format("webSocket预约提醒用户数量%s",userSessions.size()));
    		for (int sessionid = 0; sessionid < userSessions.size(); sessionid++) {
    			WebSocketSession session=userSessions.get(sessionid);
    			if (!session.isOpen()) return false;
    	        try {
    	        	logger.info(String.format("webSocket预约发送"));
    	            session.sendMessage(message);
    	        } catch (IOException e) {
    	        	logger.info(String.format("webSocket异常：%s",e.toString()));
    	            e.printStackTrace();
    	            return false;
    	        }
			}
    		return true;
		}
        
    }

	@Override
	public boolean supportsPartialMessages() {
		// TODO Auto-generated method stub
		return super.supportsPartialMessages();
	}
	
	


}
