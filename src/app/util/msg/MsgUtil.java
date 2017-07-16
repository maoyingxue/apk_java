package app.util.msg;

import java.util.List;

import app.user.dao.Message;
import app.user.dao.MessageDAO;
 

public class MsgUtil {

	private static List<Message> messages = null;


	public static synchronized List<Message> getMessages() {
		System.out.println("获取消息中心");
		if(messages == null)
			MsgUtil.setMessages();
		return messages;
	}

	public static synchronized void setMessages() {
		System.out.println("设置消息并初始化");
		MessageDAO dao = new MessageDAO();
		MsgUtil.messages = dao.initMessages();
	}
	
	
	
	
}
