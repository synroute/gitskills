package hiapp.modules.dmxintuo.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;



@Configuration
@EnableWebMvc
@EnableWebSocket
public class DmWebScoketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
	/* (non-Javadoc)
	 * @see org.springframework.web.socket.config.annotation.WebSocketConfigurer#registerWebSocketHandlers(org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry)
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// TODO Auto-generated method stub
		//允许连接的域,只能以http或https开头
        //String[] allowsOrigins = {"http://http://localhost:8080"};
		String[] allowsOrigins = {"*"};
        
       //WebIM WebSocket通道
        registry.addHandler(dmWebSocketHandler(),"/IMDmServer.sock").setAllowedOrigins(allowsOrigins).addInterceptors(DmInterceptor());
        registry.addHandler(dmWebSocketHandler(), "/sockjs/IMDmServer.sock").setAllowedOrigins(allowsOrigins).addInterceptors(DmInterceptor()).withSockJS();
	}
	
    @Bean
    public DmWebSocketHandler dmWebSocketHandler() {
        return new DmWebSocketHandler();
    }
    
    @Bean
    public WebSocketHandlerdm DmInterceptor(){
        return new WebSocketHandlerdm();
    }
}
