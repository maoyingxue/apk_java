package app.user.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthDao {
	@Resource
	private JdbcTemplate jdbc;
	
	/**
	 * 得到权限
	 * @param appId
	 * @param role
	 * @return
	 */
	public List<Map<String, Object>> getQuxian(String appId,String role){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if (role == null || role.equals("")) {
			list = jdbc.queryForList("SELECT *"
					+ " FROM vsys_module"
					+ " WHERE app_id = ?"
					,appId);
			
			return list;
		}
		return list;
	}

}
