package hiapp.modules.exam.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;




@Component
public class ExamSession {
	@Autowired
	private Logger logger;
	private final static Map<String, List<WebSocketSession>> sessions = Collections.synchronizedMap(new HashMap<String, List<WebSocketSession>>());
    public void addSession(WebSocketSession session,String userid) {
    	try{
    	logger.info(String.format("webSocket进入添加"));
    	synchronized(sessions) {
    		List<WebSocketSession> userSessions = sessions.get(userid);
    		logger.info(String.format("webSocket添加用户：%s",userid));
    		if (null == userSessions) {
    			userSessions = new ArrayList<WebSocketSession>();
    			sessions.put(userid, userSessions);
    		}
    		userSessions.add(session);
    	}
    	}catch(Exception e){
    		logger.info(String.format("webSocket添加用户：异常：%s",e.toString()));
    	}
    }
    
    public void removeSession(WebSocketSession session) {
    	
    	synchronized(sessions) {
    		String userid = (String) session.getAttributes().get("userId");
    		logger.info(String.format("webSocket移除用户：%s",userid));
    		List<WebSocketSession> userSessions = sessions.get(userid);
    		if (null == userSessions) {
    			userSessions = new ArrayList<WebSocketSession>();
    			sessions.put(userid, userSessions);
    		}

    		for (int idx = userSessions.size() - 1; idx >= 0; idx--) {
    			if (userSessions.get(idx).getId().compareTo(session.getId()) == 0) {
    				userSessions.remove(idx);
    			}
    		}
    	}
    }
    
    public List<WebSocketSession> getSession(String userid) {
    	synchronized(sessions) {
    		List<WebSocketSession> userSessions = sessions.get(userid);
    		/*if (null == userSessions) {
    			userSessions = new ArrayList<WebSocketSession>();
    			DmMessage.sessions.put(userid, userSessions);
    		}*/
    		return userSessions;
    	}
		
    }
}
