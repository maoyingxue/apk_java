package app.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import app.util.Constant;

@Repository
public class ChatDao {
	@Resource
	private JdbcTemplate jdbc;

	public Map<String, Object> heiMingDan(String appId, String userId) {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("res", Constant.SUCCESS);
		// 圈信息应不应该加上群主
		if (userId != "") {
			List<Map<String, Object>> list = jdbc.queryForList(
					"select u.* from vim_friends f left join vu_user u on f.user_id = u.id where f.user_id=" + userId + " and f.status = 3 and f.app_id=" + appId);
 
			result.put("flag", Constant.SUCCESS);
			result.put("data", list);
		} else {
			result.put("flag", Constant.FAILURE);
		}

		return result;
	}

	public Map<String, Object> msgget(String fromUser,String toUser,String content){
		Map<String, Object> result = new HashMap<String, Object>();
		int i=jdbc.update("insert into  vim_chat(from_user,to_user,content,chat_status) values("+fromUser+","+toUser+",'"+content+"',6"+")");
		if(i==1){
			result.put("res", Constant.SUCCESS);
			result.put("flag", Constant.SUCCESS);
			result.put("msg", "消息发送成功");
		}else{
			result.put("res", Constant.SUCCESS);
			result.put("flag", Constant.FAILURE);
			result.put("msg", "消息发送失败");
		}								
		return result;
	}	
	
	public Map<String, Object> messagetui(String userId){
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> list = jdbc.queryForList("select * from vim_chat where from_user="+userId+" or to_user="+userId+" order by id DESC LIMIT 10");
//		List<Map<String, Object>> list = jdbc.queryForList("select * from vim_chat where from_user="+userId+" and to_user="+userId+" and chat_status=6");
		//int i=jdbc.update("update vim_chat set chat_status=7 where from_user="+fromUser+" and to_user="+toUser);
		
		result.put("res", Constant.SUCCESS);
		result.put("flag", Constant.SUCCESS);
		result.put("data", list);								
		return result;
	}
		
}
