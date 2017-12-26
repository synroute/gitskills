package hiapp.modules.dmxintuo.data;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class DMSession {
	private WebSocketSession session;
	
	private String clientId;
	private String clientName;
	
	public DMSession(WebSocketSession session) {
		this.session = session;
	}
	
	
	/**
	 * @return the session
	 */
	public WebSocketSession getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(WebSocketSession session) {
		this.session = session;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the clientName
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 * @param clientName the clientName to set
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	
}
