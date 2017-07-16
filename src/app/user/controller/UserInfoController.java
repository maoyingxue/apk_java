package app.user.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.user.dao.UserInfoDao;
import app.util.controller.BaseJsonController;


@RestController
public class UserInfoController extends BaseJsonController{
	
	@Resource
	private UserInfoDao userDao;
 
 
	@RequestMapping(value="/m/user/changeInfo.do", method = RequestMethod.POST)
	public  Map<String, Object> changeAllInfo(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setHeader("Access-Control-Allow-Origin", "*");
//		String appId = requst.getParameter("appId");
//		String userId = requst.getParameter("userId");
//		String sex = requst.getParameter("sex");
//		String nick_name = requst.getParameter("nick_name");
//		String user_name = requst.getParameter("user_name");
//		String birthday = requst.getParameter("birthday");
//		String tocken = requst.getParameter("tocken");
//		return userDao.changeInfo(sex,nick_name,user_name,birthday,tocken,appId,userId);
		return userDao.changeInfo(requst);
	}
	 
}
