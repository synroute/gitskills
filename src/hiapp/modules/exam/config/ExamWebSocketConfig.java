package hiapp.modules.exam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import hiapp.modules.exam.data.ExamWebSocketHandler;
import hiapp.modules.exam.data.ExamWebSocketInteceptor;
@Configuration
@EnableWebMvc
@EnableWebSocket
public class ExamWebSocketConfig  extends WebMvcConfigurerAdapter implements WebSocketConfigurer{

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// TODO Auto-generated method stub
		//允许连接的域,只能以http或https开头
        //String[] allowsOrigins = {"http://http://localhost:8080"};
		String[] allowsOrigins = {"*"};
		//WebIM WebSocket通道
        registry.addHandler(examWebSocketHandler(),"/ExamServer.sock").setAllowedOrigins(allowsOrigins).addInterceptors(examWebSocketInteceptor());
        registry.addHandler(examWebSocketHandler(), "/sockjs/ExamServer.sock").setAllowedOrigins(allowsOrigins).addInterceptors(examWebSocketInteceptor()).withSockJS();
	}
		
	@Bean
	public ExamWebSocketHandler examWebSocketHandler() {
		return new ExamWebSocketHandler();
	}
	@Bean
	public ExamWebSocketInteceptor examWebSocketInteceptor() {
		return new ExamWebSocketInteceptor();
	}
}
