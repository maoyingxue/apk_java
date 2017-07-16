package app.user.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.user.dao.CircleDao;
import app.user.dao.MainDao;

@RestController
public class CircleController {
	@Resource
	private CircleDao circleDao;
	@RequestMapping(value="m/circle/circlemain.do",method=RequestMethod.POST)
	public Map<String, Object> seek(HttpServletRequest requst,
			HttpServletResponse response){
		
		String circleId=requst.getParameter("circleId");
		System.out.println("lian");
		return circleDao.select(circleId);
	}
	
	
	
	@RequestMapping(value="m/circle/addcircle.do",method=RequestMethod.POST)
	public Map<String, Object> add(HttpServletRequest requst,
			HttpServletResponse response){
		
		String userId=requst.getParameter("userId");
		String circleName=requst.getParameter("circleName");
		String circleAddr=requst.getParameter("circleAddr");
		String lng=requst.getParameter("lng");
		String lat=requst.getParameter("lat");
		
		
		return circleDao.add(userId,circleName,circleAddr,lng,lat);
	}	
}
