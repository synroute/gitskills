package hiapp.modules.dmxintuo.data;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



public class DmWebSocketHandler extends TextWebSocketHandler {
	 @Autowired
	    private DmMessage messageProcessor; 
	 
	 public DmWebSocketHandler() {
		 System.out.println("new DmWebSocketHandler");
	 }
	    
	  
	    //接收文本消息，并发送出去
	    @Override
	    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	    	System.out.println("AgentChatServer " + message.getPayload());
	    	/*messageProcessor.putMessage(session, message.getPayload());*/
	        super.handleTextMessage(session, message);
	    }
	    //连接建立后处理
	    @Override
	    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	        //logger.debug("connect to the websocket chat success......");
	    	System.out.println("connect to the AgentChatServer success......" + session.getId());
	    	messageProcessor.addSession(session);
	    }
	    
	    //抛出异常时处理
	    @Override
	    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
	        if(session.isOpen()){
	            session.close();
	        }
	        System.out.println("AgentChatServer connection closed......");
	        messageProcessor.removeSession(session);
	    }
	    //连接关闭后处理
	    @Override
	    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
	    	System.out.println("AgentChatServer connection closed......");
	    	messageProcessor.removeSession(session);
	    }

	    @Override
	    public boolean supportsPartialMessages() {
	        return false;
	    }
	    public boolean sendMessageToUser(String clientId, TextMessage message) {
	        
	    	WebSocketSession session = messageProcessor.getSession(clientId);
	    	System.out.println("sendMessage:" + session);
	        if (!session.isOpen()) return false;
	        try {
	            session.sendMessage(message);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return false;
	        }
	        return true;
	    }
}
