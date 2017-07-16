package app.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import app.user.dao.Post;
@Repository
public class PostCal {

	@Resource
	private JdbcTemplate jdbc;
	

	
	List<Post> posts = new ArrayList<Post>();
	
	   public List<Post> pocal(int id){
		   posts.clear();
		   Post pos =new Post();
		   pos.setId(id);
		   posts.add(pos);
		   List<Map<String,Object>> data = jdbc.queryForList("select * from vim_circle_post where super_id="+id);
		   
		   Post postt;
		   for (Map<String,Object> post : data) {
				postt = new Post();
				int Id=Integer.valueOf(post.get("id").toString());
				int SuperId=Integer.valueOf(post.get("super_id").toString());
				postt.setId(Id);
				postt.setSuperId(SuperId);
				
				
				posts.add(postt);
				posecondcal(postt.getId());
				//System.out.println("daozhele");
				
		   }
	   return posts;
	   }
	   
	   public void posecondcal(int i){  
		   List<Map<String,Object>> data = jdbc.queryForList("select * from vim_circle_post where super_id="+i);
		   Post postt;
		   for (Map<String,Object> post : data) {
				postt = new Post();
				int Id=Integer.valueOf(post.get("id").toString());
				int SuperId=Integer.valueOf(post.get("super_id").toString());
				postt.setId(Id);
				postt.setSuperId(SuperId);
				posts.add(postt);
				
				//System.out.println(postt.getId());
				
		   }
		    }
	   
	
}
