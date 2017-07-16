package app.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import app.util.Constant;

@Repository
public class ActivityDao {

	@Resource
	private JdbcTemplate jdbc;
	public Map<String, Object> find(){
		Map<String, Object> activityInfo=new HashMap<String,Object>();
		List<Map<String, Object>> activity=jdbc.queryForList("select * from vim_activity ");
		activityInfo.put("res", Constant.SUCCESS);
		activityInfo.put("flag", Constant.SUCCESS);
		activityInfo.put("data", activity);
		return activityInfo;
	}	
	
	
	
	public Map<String, Object> findone(String activityId){
		Map<String, Object> activityInfo=new HashMap<String,Object>();
		List<Map<String, Object>> activity=jdbc.queryForList("select * from vim_activity where id="+activityId);
		activityInfo.put("res", Constant.SUCCESS);
		activityInfo.put("flag", Constant.SUCCESS);
		activityInfo.put("data", activity);
		return activityInfo;
	}
	
	
	public Map<String, Object> addActivity(String userId,String activityTitle,String activityTime,String activityAddr,String activityContent,String activityminNumber,String lng,String lat){
		Map<String, Object> adactivity=new HashMap<String,Object>();
		//System.out.println(activityTitle);
		int i=jdbc.update("insert into vim_activity(user_id,activity_title,activity_content,activity_time,user_min_count,activity_addr,lng,lat) values("+userId+",'"+activityTitle+"','"+activityContent+"','"+activityTime+"',"+activityminNumber+",'"+activityAddr+"',"+lng+","+lat+")");
		
		if(i==1){
			adactivity.put("res", Constant.SUCCESS);
			adactivity.put("flag", Constant.SUCCESS);
			adactivity.put("msg", "活动添加成功");
			
		}else{
			adactivity.put("res", Constant.SUCCESS);
			adactivity.put("flag", Constant.FAILURE);
		}
		return adactivity;
	}
	
	
	public Map<String, Object> delActivity(String activityId){
		Map<String, Object> delactivity=new HashMap<String,Object>();
		int i=jdbc.update("delete  from vim_activity where id="+activityId);
		int j=jdbc.update("delete  from vim_activity_post where activity_id="+activityId);
		if(i==1&&j>0){
			delactivity.put("res", Constant.SUCCESS);
			delactivity.put("flag", Constant.SUCCESS);
			delactivity.put("msg", "活动删除成功");
		}else{
			delactivity.put("res", Constant.SUCCESS);
			delactivity.put("flag", Constant.FAILURE);
		}
		
		return delactivity;
	}
	
	public Map<String, Object> postsend(String userId,String userName,String activityId,String postContent){
		Map<String, Object> adactivitypost=new HashMap<String,Object>();
		int i=jdbc.update("insert into vim_activity_post(activity_id,user_id,user_name,post_content) values("+activityId+","+userId+",'"+userName+"','"+postContent+"')");
		
		if(i==1){
			adactivitypost.put("res", Constant.SUCCESS);
			adactivitypost.put("flag", Constant.SUCCESS);
			adactivitypost.put("msg", "活动贴发送成功");
			
		}else{
			adactivitypost.put("res", Constant.SUCCESS);
			adactivitypost.put("flag", Constant.FAILURE);
		}
		return adactivitypost;		
	}
	
	
	
	
	
	
	
}
