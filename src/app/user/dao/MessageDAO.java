package app.user.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
 
import app.util.Constant;
import app.util.msg.MsgUtil; 

@Repository
public class MessageDAO {
	@Resource
	private OtherFunctionDAO otherFunction;

	@Resource
	private JdbcTemplate jdbc;
	/**
	 * 初始化平台公共信息
	 * @return
	 */
	public List<Message> initMessages(){
		List<Map<String,Object>> data 
			= jdbc.queryForList("select id,"
							   		 + "user_id,"
							   		 + "msg_type,"
							   		 + "msg_user_type,"
							   		 + "add_time,"
							   		 + "content,"
							   		 + "expiration_time,"
							   		 + "msg_status from vsys_msg ORDER BY add_time DESC");
		
		List<Message> messages = new ArrayList<Message>();
		
		Message msg = null;
		for (Map<String,Object> message : data) {
			msg = new Message();
			
			long id = Long.valueOf(message.get("id").toString());
			
			long userId = Long.valueOf(message.get("user_id").toString()) ;
			
			int msgType = Integer.valueOf(message.get("msg_type").toString()) ;
			
			int msgUserType = Integer.valueOf(message.get("msg_user_type").toString()) ;
			
			String addTime = "0000-00-00 00:00:00";
			
			if(null != message.get("add_time")) 
				addTime = (message.get("add_time")).toString().substring(0, 19);
			
			String content = (String) message.get("content");
			
			String expirationTime = "0000-00-00 00:00:00";
			
			if(null != message.get("expiration_time"))
				expirationTime = (message.get("expiration_time")).toString().substring(0, 19);
			
			int msgStatus = Integer.valueOf(message.get("msg_status").toString()) ;
			
			msg.setId(id);
			msg.setUserId(userId);
			msg.setAddTime(addTime);
			msg.setMsgType(msgType);
			msg.setMsgUserType(msgUserType);
			msg.setContent(content);
			msg.setExpirationTime(expirationTime);
			msg.setMsgStatus(msgStatus);
			messages.add(msg);
		}
		System.out.println("消息初始化："+messages.size());
		return messages;
	}
	
	
	/**
	 * 添加公共信息
	 * @param userId
	 * @param msgType
	 * @param content
	 * @param msgStatus
	 * @return
	 */
	public Map<String,Object> insertMessage(String userId,String msgType,String msgUserType,String content,String msgStatus){
		Map<String,Object> insertInfo = new HashMap<String,Object>();
		int result = jdbc.update("INSERT INTO vsys_msg VALUES(null,?,?,?,null,?,null,?)", 
								  Integer.valueOf(userId),
								  Integer.valueOf(msgType),
								  Integer.valueOf(msgUserType),
								  content,
								  Integer.valueOf(msgStatus));
		insertInfo.put("res", result>0?Constant.SUCCESS:Constant.FAILURE);
		
		Message message = this.queryMessageByMaxId();
		
		MsgUtil.getMessages().add(message);
		
		return insertInfo;
		
	}
	
	/**
	 * 得到消息
	 * @param userId
	 * @param msgType
	 * @param content
	 * @param msgStatus
	 * @return
	 */
	public Map<String,Object> getMsg(String userId,String msgType,String queryTime){
		Map<String,Object> getMsgInfo = new HashMap<String,Object>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		System.out.println("queryTime:" + queryTime);
		if (queryTime==null||queryTime.equals("")) {
			result = jdbc.queryForList("SELECT * FROM"
					+ " vsys_msg WHERE"
					+ " user_id = ? AND msg_type = ? ORDER BY add_time DESC LIMIT 10"
					,userId
					,msgType);
		}else {
			result = jdbc.queryForList("SELECT *"
					+ " FROM vsys_msg"
					+ " WHERE user_id = ? AND msg_type = ? AND add_time > ? ORDER BY add_time DESC"
					,userId
					,msgType
					,queryTime);
			System.out.println("");
		}
		
		if (result.size()<=0) {
			getMsgInfo.put("res", Constant.SUCCESS);
			getMsgInfo.put("flag", Constant.FAILURE);
			getMsgInfo.put("msg", "获取消息失败");
			return getMsgInfo;
		}
		
		getMsgInfo.put("res", Constant.SUCCESS);
		getMsgInfo.put("flag", Constant.SUCCESS);
		//研究下这里
		Gson gson = new Gson();
		for (int i = 0; i < result.size(); i++) {
			String content = result.get(i).get("content").toString();
			System.out.println(content);
			
			if (content.contains("}]")) {
				result.get(i).remove("content");
				List<Map<String,Object>> contentList = gson.fromJson(content, List.class);
				result.get(i).put("content", contentList);
			}
		}
		long currentTime = new Date().getTime();
		getMsgInfo.put("data", result);
		getMsgInfo.put("msg", "" + currentTime);
		return getMsgInfo;
	}
	
	/**
	 * 获取系统消息
	 * @param msgType
	 * @return
	 */
	public Map<String,Object> getSysMsg(String msgType, String pageIndex,String startNub,String appId){
		Map<String,Object> getSysMsgInfo = new HashMap<String,Object>();
		List<Map<String,Object>> msgInfo = jdbc.queryForList("SELECT "
										+ "* FROM vsys_msg"
										+ " WHERE msg_type = ? AND app_id = ? ORDER BY  add_time DESC  LIMIT ?,?"
										,msgType
										,appId
										,Integer.parseInt(pageIndex)
										,Integer.parseInt(startNub));
		
		if (msgInfo.size()<=0) {
			getSysMsgInfo.put("res", Constant.SUCCESS);
			getSysMsgInfo.put("flag", Constant.FAILURE);
			getSysMsgInfo.put("msg", "无消息可获取");
			return getSysMsgInfo;
		}
		
		getSysMsgInfo.put("res", Constant.SUCCESS);
		getSysMsgInfo.put("flag", Constant.SUCCESS);
		getSysMsgInfo.put("msg", msgInfo);
		return getSysMsgInfo;	
	}
	
	/**
	 * 获取ID获取Message
	 * @param mId
	 * @return where id = (select MAX(id) FROM vsys_msg)
	 */   
	public Message queryMessageByMaxId(){
		Map<String,Object> message 
				= jdbc.queryForMap("select id,"
								 + "user_id,"
								 + "msg_type,"
								 + "msg_user_type,"
								 + "add_time,"
								 + "content,"
								 + "expiration_time,"
								 + "msg_status from vsys_msg ORDER BY add_time DESC"
								 + "where id = (select MAX(id) FROM vsys_msg)");
		Message msg = new Message();
		
		long id = (long) message.get("id");
		
		long userId = (long) message.get("user_id");
		
		int msgType = (int) message.get("msg_type");
		
		int msgUserType = (int) message.get("msg_user_type");
		
		String addTime = "0000-00-00 00:00:00";
		
		if(null != message.get("add_time")) 
			addTime = (message.get("add_time")).toString().substring(0, 19);
		
		String content = (String) message.get("content");
		
		String expirationTime = "0000-00-00 00:00:00";
		
		if(null != message.get("expiration_time"))
			expirationTime = (message.get("expiration_time")).toString().substring(0, 19);
		
		int msgStatus = (int) message.get("msg_status");
		
		msg.setId(id);
		msg.setUserId(userId);
		msg.setAddTime(addTime);
		msg.setMsgType(msgType);
		msg.setMsgUserType(msgUserType);
		msg.setContent(content);
		msg.setExpirationTime(expirationTime);
		msg.setMsgStatus(msgStatus);
	
		return msg;
	}
	/**
	 * 更新消息状态
	 * @param id
	 */
	public Map<String, Object> updateMsgStatus(String id){
		Map<String, Object> statusInfo = new HashMap<String,Object>();
		int i =  jdbc.update("UPDATE vsys_msg SET msg_status = 1 WHERE id = ?",id);
		if (i > 0) {
			statusInfo.put("res",Constant.SUCCESS);
			statusInfo.put("flag", Constant.SUCCESS);
			return statusInfo;
		}else {
			statusInfo.put("res",Constant.SUCCESS);
			statusInfo.put("flag", Constant.FAILURE);
			return statusInfo;
		}
	}
	/**
	 * 进场发消息
	 * @param userId
	 * @param parkId
	 */
	public int parkInMesg(String userId,String parkId, List<Map<String, Object>> content){

		long timeStempIn = this.formatTime(content.get(0).get("in_time").toString());
		content.get(0).remove("in_time");
		content.get(0).put("in_time", timeStempIn + "");
		
		Map<String,Object> userInfo = otherFunction.vu_user_getUserInfo(userId);
		content.get(0).put("userInfo", userInfo);
		int sendPResult = 1;
		if (sendPResult>0) {
			return 1;
		}else {
			return 0;
		}
	}

	/**
	 * 插入个人消息
	 * @param userId
	 * @param msgType
	 * @param msgUserType
	 * @param content
	 * @return
	 */
	public int insertMsg(String userId,String msgType,String msgUserType,String content){
		
		int i = jdbc.update("INSERT INTO"
							+ " vsys_msg (user_id"
							+ ",msg_type"
							+ ",msg_user_type"
							+ ",content"
							+ ",msg_status)VALUES(?,?,?,?,0)"
							,userId
							,msgType
							,msgUserType
							,content);
		return i;
	} 
	/**
	 * 登录插入消息
	 * @param userId
	 * @param content
	 * @return
	 */
	public int loginMsg(String userId , String content,String appId){
		
		int result = jdbc.update("INSERT INTO vsys_msg"
								+ " (user_id"
								+ ",msg_type"
								+ ",msg_user_type"
								+ ",content"
								+ ",msg_status"
								+ ",app_id)VALUES(?,2,10,?,0,?)"
								,userId
								,content
								,appId);
		System.out.println("登录发消息了吗");
		return result;
	}
	
	/**
	 * 登录插入消息
	 * @param userId
	 * @param content
	 * @return
	 */
	public Map<String, Object> getLoginMsg(String userId,String msgType,String queryTime,String appId){
		Map<String, Object> getMsgInfo = new HashMap<String,Object>();
		System.out.println(userId+":" + msgType +"," + queryTime);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (queryTime==null||queryTime.equals("")) {
			
			result = jdbc.queryForList("SELECT * FROM"
					+ " vsys_msg WHERE"
					+ " user_id = ? AND msg_type = ? AND msg_user_type = 10 AND app_id = ? ORDER BY add_time DESC LIMIT 1"
					,userId
					,msgType
					,appId);
		}else {
			result = jdbc.queryForList("SELECT *"
					+ " FROM vsys_msg"
					+ " WHERE user_id = ? AND msg_type = ?"
					+ " AND add_time > ?"
					+ " AND msg_user_type = 10 AND app_id = ? ORDER BY add_time DESC LIMIT 1 "
					,userId
					,msgType
					,queryTime
					,appId);
		}
		System.out.println("data:" + result.size());
		if (result.size()<=0) {
			getMsgInfo.put("res", Constant.SUCCESS);
			getMsgInfo.put("flag", Constant.FAILURE);
			getMsgInfo.put("msg", "获取消息失败");
		}
		
		getMsgInfo.put("res", Constant.SUCCESS);
		getMsgInfo.put("flag", Constant.SUCCESS);
		long currentTime = new Date().getTime(); 
		getMsgInfo.put("data", result);
		getMsgInfo.put("msg", "" + currentTime);
		return getMsgInfo;
	}
	/**
	 * 改变时间格式
	 * @param myTime
	 * @return
	 */
	public long formatTime(String myTime){
		//改变世界格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		java.util.Date date = new java.util.Date();
		try {
			date= df.parse(myTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		long timeStemp = date.getTime();
		return timeStemp;
	}
	 
	
}
