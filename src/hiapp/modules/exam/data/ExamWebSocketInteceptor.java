package hiapp.modules.exam.data;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import hiapp.system.buinfo.User;

public class ExamWebSocketInteceptor implements HandshakeInterceptor{
	@Autowired
	private Logger logger;
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		try {
			
			logger.info(String.format("beforeHandshake"));
		// TODO Auto-generated method stub
		System.out.println(request.getURI());
        System.out.println("beforeHandshake start.....");  
               System.out.println(request.getClass().getName());  
        if (request instanceof ServletServerHttpRequest) {  
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;  
            HttpSession session = servletRequest.getServletRequest().getSession(false);  
            if (session != null) {
            	System.out.println("get httpsession success");
                //使用userName区分WebSocketHandler，以便定向发送消息  
            	
                User user = (User)session.getAttribute("user");
                if (null != user) {
                	System.out.println(user.getId() + " login");
                }
                attributes.put("userId",user.getId());  
            }else{  
            	System.out.println("httpsession is null");  
            }  
        }  
		} catch (Exception e) {
			// TODO: handle exception
			logger.info(String.format(e.toString()));
		}
		return true;
	}
	
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception arg3) {
		// TODO Auto-generated method stub
		
	}
}
