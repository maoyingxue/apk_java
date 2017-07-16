package app.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import app.util.Constant;



@Repository
public class HobbyDao {

	@Resource
	private JdbcTemplate jdbc;
	
	
	public Map<String, Object> selectAll(){
		
		
		Map<String, Object> hobbyInfo=new HashMap<String,Object>();
		
		
		List<Map<String, Object>> hobby=jdbc.queryForList("select * from vim_hobby ");

		hobbyInfo.put("res", Constant.SUCCESS);
		hobbyInfo.put("data", hobby);
		return hobbyInfo;
		 
	}
	
	
public Map<String, Object> selectOneUser(String userId){
		
		
		Map<String, Object> hobbyInfo=new HashMap<String,Object>();
		
	
		List<Map<String, Object>> hobby=jdbc.queryForList("select * from vim_user_hobby where  user_id= "+userId);

		hobbyInfo.put("res", Constant.SUCCESS);
		hobbyInfo.put("data", hobby); 

		return hobbyInfo;
		
	}


public Map<String, Object> addHobby(String userId,String hobbyId){
	Map<String, Object> hobbyInfo=new HashMap<String,Object>();
	int j;
	List<Map<String, Object>> i=jdbc.queryForList("select * from vim_user_hobby  where user_id="+userId+" AND hobby_id="+hobbyId);
	System.out.println(i.size());
	if(i.size()==0){
	j=jdbc.update("insert into vim_user_hobby(user_id,hobby_id) values ("+userId+","+hobbyId+")");
	System.out.println(j);
	if(j==1){
		hobbyInfo.put("res", Constant.SUCCESS);
		hobbyInfo.put("msg", "插入成功");
	}else{
		hobbyInfo.put("res", Constant.FAILURE);
		hobbyInfo.put("msg", "删除失败");
	}
	}else{
		hobbyInfo.put("res", Constant.SUCCESS);
		hobbyInfo.put("msg", "插入成功");
	}
	return hobbyInfo;
}
public Map<String, Object> delHobby(String userId,String hobbyId){
	Map<String, Object> hobbyInfo=new HashMap<String,Object>();
	
	int i=jdbc.update("delete from vim_user_hobby where user_id="+userId+" AND hobby_id="+hobbyId);
	System.out.println(i);
	if(i==1){
		hobbyInfo.put("res", Constant.SUCCESS);
		hobbyInfo.put("msg", "删除成功");
	}
	else{
		hobbyInfo.put("res", Constant.FAILURE);
		hobbyInfo.put("msg", "删除失败");
	}
	return hobbyInfo;
}



}
