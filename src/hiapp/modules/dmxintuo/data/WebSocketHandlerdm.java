package hiapp.modules.dmxintuo.data;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import hiapp.system.buinfo.User;

public class WebSocketHandlerdm implements HandshakeInterceptor {
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.server.HandshakeInterceptor#beforeHandshake(org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse, org.springframework.web.socket.WebSocketHandler, java.util.Map)
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
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
                //attributes.put(Constants.WEBSOCKET_USERNAME,userName);  
            }else{  
            	System.out.println("httpsession is null");  
            }  
        }  
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.socket.server.HandshakeInterceptor#afterHandshake(org.springframework.http.server.ServerHttpRequest, org.springframework.http.server.ServerHttpResponse, org.springframework.web.socket.WebSocketHandler, java.lang.Exception)
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub

	}
}
