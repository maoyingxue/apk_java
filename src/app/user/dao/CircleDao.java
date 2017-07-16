package app.user.dao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import app.user.service.PostCal;
import app.util.Constant;
import app.util.map.MapCal;
@Repository
public class CircleDao {

	
	@Resource
	private JdbcTemplate jdbc;
	@Resource
	private PostCal cal;
	public Map<String, Object> select(String circleId){
		
		Map<String, Object> circleInfo=new HashMap<String,Object>();
		//圈信息应不应该加上群主
		if(circleId!=""){
		List<Map<String, Object>> circle=jdbc.queryForList("select * from vim_circle where id="+circleId);
		
		List<Map<String, Object>> posts=jdbc.queryForList("SELECT post.* , u.phone, u.user_name, u.nick_name, u.sex,u.img_l,u.img_s FROM vim_circle_post post LEFT JOIN vu_user u ON post.user_id = u.id WHERE post.circle_id="+circleId+" ORDER BY post.id ");
		//System.out.println(circle_post.size());
		// 取出置顶帖和最上层帖子
		List<Map<String, Object>> top_posts = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> level_1_posts = new ArrayList<Map<String, Object>>();
        
		int i = 0, j = 0,k;
		if(posts != null && posts.size()>0){
			// 取最上层的帖子
			for(i = (posts.size()-1) ; i>=0 ; i--){
				Map<String, Object> data = posts.get(i);
				if(null == data.get("super_id")){
					if(data.get("is_top") != null && 1 == (int)data.get("is_top") && j<2){
						top_posts.add(data);
						j++;
					}else{
						level_1_posts.add(data); 
					}
				}
			}
			// 取回复的帖子
			for(i = 0 ; i < level_1_posts.size() ; i++){
				long id = (long) level_1_posts.get(i).get("id");
				List<Map<String, Object>> level_2_posts = new ArrayList<Map<String, Object>>();
				for(j = 0 ; j < posts.size() ; j++){
					if(posts.get(j).get("super_id") != null && id == (long)posts.get(j).get("super_id")){
						level_2_posts.add(posts.get(j));
					}
				}

				for(k = 0 ; k < level_2_posts.size() ; k++){
					long level2_id = (long) level_2_posts.get(k).get("id");
					List<Map<String, Object>> level_3_posts = new ArrayList<Map<String, Object>>();
					for(j = 0 ; j < posts.size() ; j++){
						if(posts.get(j).get("super_id") != null && level2_id == (long)posts.get(j).get("super_id")){
							level_3_posts.add(posts.get(j));
						}
					}
					level_2_posts.get(k).put("sub", level_3_posts); 
				}
				level_1_posts.get(i).put("sub", level_2_posts); 
			}
		} else{
			// 没有帖子
		}
	 
		
		List<Map<String, Object>> circle_user=jdbc.queryForList("select * from vim_circle_user where circle_id="+circleId+" and user_type=1");
		List<Map<String, Object>> circleuser=jdbc.queryForList("select * from vu_user where id="+circle_user.get(0).get("user_id")); 
		System.out.println(top_posts.size());
		circleInfo.put("res", Constant.SUCCESS);
		circleInfo.put("circleownr", circleuser);
		circleInfo.put("circle", circle);
		circleInfo.put("topPost", top_posts);
		circleInfo.put("toppostsize", top_posts.size());
		circleInfo.put("circle_post", level_1_posts);
		}else{
			circleInfo.put("res", Constant.FAILURE);
		}		
		return circleInfo;
	}
	
	public Map<String, Object> add(String userId,String circleName,String circleAddr,String lng,String lat){
		Map<String, Object> loginInfo = new HashMap<String, Object>();
		MapCal map=new MapCal();
		double lat1=Double.parseDouble(lat);
		double lng1=Double.parseDouble(lng);
		List list=map.cal(lat1, lng1);
		DecimalFormat df = new DecimalFormat("0.000000000000");
		System.out.println(df.format(list.get(0)));
		System.out.println(df.format(list.get(1)));
		List<Map<String,Object>> beans=jdbc.queryForList("select * from vim_circle where lat="+df.format(list.get(0))+" AND "+"lng="+df.format(list.get(1)));
		System.out.println(beans.size());
		if(beans.size()>0){
			loginInfo.put("res", Constant.SUCCESS);
			loginInfo.put("flag", Constant.FAILURE);
			loginInfo.put("msg", "该本地圈以被占");
			}else{
				
				int i=jdbc.update("insert into vim_circle(circle_name,circle_addr,lng,lat,lng1,lat1) values("+circleName+","+circleAddr+","+list.get(1)+","+list.get(0)+","+list.get(3)+","+list.get(2)+")");
				List<Map<String,Object>> bean=jdbc.queryForList("select max(id) from vim_circle");
				//System.out.println(bean.get(0).get("max(id)"));
				int j=jdbc.update("insert into vim_circle_user(circle_id,user_id,user_type) values("+bean.get(0).get("max(id)")+","+userId+",1)");
				if(i>0&&j>0){
					loginInfo.put("res", Constant.SUCCESS);
					loginInfo.put("flag", Constant.SUCCESS);
					loginInfo.put("msg", "占圈成功");
				}else{
					loginInfo.put("res", Constant.SUCCESS);
					loginInfo.put("flag", Constant.FAILURE);
					loginInfo.put("msg", "占圈失败");
				}
				
			}
		
		
		
		return loginInfo;
	}
	
}
