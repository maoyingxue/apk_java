package app.user.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
import app.user.dao.ChatDao;
import app.util.controller.BaseJsonController;

@RestController
public class ChatController extends BaseJsonController {

	@Resource
	private ChatDao chatDao;
	@RequestMapping(value="m/chat/heimd.do",method=RequestMethod.POST)
	public Map<String, Object> seek(HttpServletRequest requst,
			HttpServletResponse response){

		try {
			requst.setCharacterEncoding("utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("hellowold");  
		String deviceId = requst.getParameter("deviceId");
		String phoneNumber = requst.getParameter("phoneNumber");
		String pwdMd5 = requst.getParameter("pwdMd5");
		String myTocken = requst.getSession().getId();
		String appId = requst.getParameter("appId"); 
		String userId = requst.getParameter("userId");
		System.out.println("lian");
		return chatDao.heiMingDan(appId, userId);
	}
	
	@RequestMapping(value="m/chat/messageget.do",method=RequestMethod.POST)
	public Map<String, Object> messageget(HttpServletRequest requst,
			HttpServletResponse response){
		
		try {
			requst.setCharacterEncoding("utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String fromUser=requst.getParameter("fromUser");
		String toUser=requst.getParameter("toUser");
		String content=requst.getParameter("content");
		
		
		
		
		return chatDao.msgget(fromUser,toUser,content);
	}
	
	@RequestMapping(value="m/chat/messagetui.do",method=RequestMethod.POST)
	public Map<String, Object> messagetui(HttpServletRequest requst,
			HttpServletResponse response){
		String userId=requst.getParameter("userId"); 
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		
		
		
		
		return chatDao.messagetui(userId);
	}	
	
	
	
}
