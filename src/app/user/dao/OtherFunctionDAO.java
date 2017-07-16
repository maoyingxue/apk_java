package app.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OtherFunctionDAO {
	@Resource
	private JdbcTemplate jdbc;
	/**
	 * 通过UserId得到用户信息
	 * @param userId
	 * @return
	 */
	public  Map<String, Object> vu_user_getUserInfo(String userId){
		Map<String, Object> userInfo = new HashMap<String,Object>();
		
		List<Map<String, Object>> user = jdbc.queryForList("SELECT phone"
											+ " FROM vu_user "
											+ "WHERE id = ?"
											,userId);
		
		if (user.size()<=0) {
			userInfo.put("res", -1);
			return userInfo;
		}
		
		userInfo.put("res", 1);
		userInfo.put("phone",user.get(0).get("phone").toString()); 
		return userInfo;
	}

	
}
