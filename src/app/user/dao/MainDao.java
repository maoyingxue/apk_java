package app.user.dao;



import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import app.util.Constant;
import app.util.map.MapCal;

@Repository
public class MainDao {

	@Resource
	private JdbcTemplate jdbc;
	
	public Map<String, Object>  query(String latitude,String longtitude){
		
		Map<String, Object> loginInfo = new HashMap<String, Object>();
		MapCal map=new MapCal();
		double lat=Double.parseDouble(latitude);
		double lng=Double.parseDouble(longtitude);
		List list=map.cal(lat, lng);
		//System.out.println(list.get(0));
		//System.out.println(list.get(1));
		DecimalFormat df = new DecimalFormat( "0.000000000000 "); 
		//System.out.println(df.format(list.get(0)));
		//DecimalFormat de = new DecimalFormat( "0.000000000000 "); 
		//System.out.println(de.format(list.get(1)));
		List<Map<String,Object>> beans=jdbc.queryForList("select * from vim_circle where lat="+df.format(list.get(0))+" AND "+"lng="+df.format(list.get(1)));
		//System.out.println(beans.size());
	
		

		List<Map<String,Object>> bean=jdbc.queryForList("select * from vim_activity where lat between "+(Double.parseDouble(latitude)-0.1)+" and "+(Double.parseDouble(latitude)+0.1)+
				" AND "+"lng between "+(Double.parseDouble(longtitude)-0.1)+" and "+(Double.parseDouble(longtitude)+0.1));
		
        loginInfo.put("res", Constant.SUCCESS);
        
		loginInfo.put("circle_Info", beans.get(0));
		loginInfo.put("activity_Info", bean); 
		
		System.out.println(bean.size());
		
		return loginInfo;
	}
	
	
	
	
	
}
