package app.user.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map; 

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.user.dao.ActivityDao;


@RestController
public class ActivityController {

	
	
	@Resource
	private ActivityDao activitysearch;
	@RequestMapping(value="m/activity/activitymain.do",method=RequestMethod.POST)
	public Map<String, Object> search(HttpServletRequest requst,
			HttpServletResponse response){
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		return activitysearch.find();
	}
	
	@RequestMapping(value="m/activity/activityInfo.do",method=RequestMethod.POST)
	public Map<String, Object> getone(HttpServletRequest requst,
			HttpServletResponse response){
		response.setHeader("Access-Control-Allow-Origin", "*");
		String userId=requst.getParameter("userId");
		
		String activityId=requst.getParameter("activityId");
		
		
		return activitysearch.findone(activityId);
	}	
	
	
	@RequestMapping(value="m/activity/addactivity.do",method=RequestMethod.POST)
	public Map<String, Object> inser(HttpServletRequest requst,
			HttpServletResponse response){
		
		try {
			requst.setCharacterEncoding("utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String userId=requst.getParameter("userId");
	
		String activityTitle=requst.getParameter("activityTitle");
		String activityTime=requst.getParameter("activityTime");
		String activityAddr=requst.getParameter("activityAddr");
		String activityContent=requst.getParameter("activityContent");
		String activityminNumber=requst.getParameter("activityminNumber");
		String lng=requst.getParameter("lng");
		String lat=requst.getParameter("lat");
		System.out.println("chenggong");
		System.out.println(activityTitle);
		return activitysearch.addActivity(userId,activityTitle,activityTime,activityAddr,activityContent,activityminNumber,lng,lat);
	}
	
	
	
	@RequestMapping(value="m/activity/delactivity.do",method=RequestMethod.POST)
	public Map<String, Object> delactivity(HttpServletRequest requst,
			HttpServletResponse response){
		
		try {
			requst.setCharacterEncoding("utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String userId=requst.getParameter("userId");
	
		String activityId=requst.getParameter("activityId");
		
		return activitysearch.delActivity(activityId);
	}
	
	@RequestMapping(value="m/activity/activitypostsend.do",method=RequestMethod.POST)	
	public Map<String, Object> activitypostsend(HttpServletRequest requst,
			HttpServletResponse response){
		
		try {
			requst.setCharacterEncoding("utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String userId=requst.getParameter("userId");
		String userName=requst.getParameter("userName");
		String activityId=requst.getParameter("activityId");
		String postContent=requst.getParameter("postContent");
		System.out.println(userName);
	    
		
		
		
	return activitysearch.postsend(userId,userName,activityId,postContent);}
}
