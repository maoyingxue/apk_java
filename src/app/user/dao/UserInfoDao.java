package app.user.dao;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
 
@Repository
public class UserInfoDao { 
	@Resource
	private JdbcTemplate jdbc;

	@Resource
	private UserLoginDao userLogin;
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
 /*
  
	public Map<String, Object> changeInfo(String sex
												,String nick_name
												,String user_name
												,String birthday
												,String tocken
												,String appId
												,String userId)
  */
	public Map<String, Object> changeInfo(HttpServletRequest requst){
		Map<String, Object> changeInfo = new HashMap<String,Object>();

		String appId = requst.getParameter("appId");
		String userId = requst.getParameter("userId");
		String sex = requst.getParameter("sex");
		String nick_name = requst.getParameter("nick_name");
		String user_name = requst.getParameter("user_name");
		String birthday = requst.getParameter("birthday");
		String tocken = requst.getParameter("tocken");
		String star = requst.getParameter("star");
		String sign = requst.getParameter("sign");
		String province = requst.getParameter("province");
		String country = requst.getParameter("country");
		String city = requst.getParameter("city");
		String home_address = requst.getParameter("home_address");
		if (StringUtils.isEmpty(userId)) {
			changeInfo.put("res", 1);
			changeInfo.put("flag", 0);
			changeInfo.put("msg", "用户未登录");
			return changeInfo;
			
		}
		/*
		//判断是否登录
		int iSLogin = userLogin.queryUserIsLogin(tocken, appId, userId);
		if (iSLogin<=0) {
			changeInfo.put("res", Constant.SUCCESS);
			changeInfo.put("flag", Constant.FAILURE);
			changeInfo.put("msg", "用户未登录");
			return changeInfo;
		}
		*/
		String str = "UPDATE vu_user SET";
		
		if (!StringUtils.isEmpty(sex)) {
			str = str + " sex = " + sex + "," ;
			System.out.println("" + str);
			
		}
		if (!StringUtils.isEmpty(nick_name)) {
			str = str + " nick_name = '" + nick_name + "'," ;
			System.out.println("" + str);
		}
		if (!StringUtils.isEmpty(user_name)) {
			str = str + " user_name = '" + user_name + "'," ;
			System.out.println("" + str);
		}
		if (!StringUtils.isEmpty(birthday)) {
			str = str + " birthday = '" + birthday + "'," ;
			System.out.println("" + str);
		}
		if (!StringUtils.isEmpty(star)) {
			str = str + " star = '" + star + "'," ;
			System.out.println("" + str);
		}
		if (!StringUtils.isEmpty(sign)) {
			str = str + " sign = '" + sign + "'," ;
			System.out.println("" + str);
		}
		if (!StringUtils.isEmpty(province)) {
			str = str + " province = '" + province + "'," ;
			System.out.println("" + str);
		}
		if (!StringUtils.isEmpty(country)) {
			str = str + " country = '" + country + "'," ;
			System.out.println("" + str);
		}
		if (!StringUtils.isEmpty(city)) {
			str = str + " city = '" + city + "'," ;
			System.out.println("" + str);
		}
		if (!StringUtils.isEmpty(home_address)) {
			str = str + " home_address = '" + home_address + "'," ;
			System.out.println("" + str);
		}
		if (str.endsWith(",")) {
			int sizeStr = str.length();
			str = str.substring(0,sizeStr - 1);
		}  
		str = str + "  WHERE id = " + userId + " AND app_id = " + appId ;
		System.out.println("" + str);
		int i = jdbc.update(str);
		if (i != 0) {
			changeInfo.put("res", 1);
			changeInfo.put("flag", 1);
			return changeInfo;
		}else {
			changeInfo.put("res", 1);
			changeInfo.put("flag", 0);
			return changeInfo;
		}
	}
	 	
}
