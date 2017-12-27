package hiapp.modules.dmxintuo.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;


//import net.sf.json.JSONObject;

@Component("dmMessageProcessor")
public class DmMessage extends Thread {
	private final static Map<String, WebSocketSession> sessions =new HashMap<String, WebSocketSession>();
	
	
    public void addSession(WebSocketSession session,String userid) {
    	DmMessage.sessions.put(userid, session);
    }
    
    public void removeSession(WebSocketSession session) {
    	
    	DmMessage.sessions.remove(session.getId());
    }
    
    public WebSocketSession getSession(String userid) {
    	WebSocketSession session=DmMessage.sessions.get(userid);
    	return  session;
		
    }
    
	
}
