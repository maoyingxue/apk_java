package app.user.controller;

import java.util.Map; 

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.user.dao.HobbyDao;
@RestController
public class HobbyController {
	@Resource
	private HobbyDao hobby_select;
	
	
	@RequestMapping(value="/m/hobby/list.do",method=RequestMethod.POST)
	public Map<String, Object>  list(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		System.out.println("hellowold");
		
		return hobby_select.selectAll();
	}
	
	
	
	@RequestMapping(value="/m/hobby/userHobbies.do",method=RequestMethod.POST)
	public Map<String, Object>  userHobbies(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		String userId=requst.getParameter("userId");
		System.out.println("hellowold");
		return hobby_select.selectOneUser(userId);
	}
	
	
	
	@RequestMapping(value="/m/hobby/addUserHobby.do",method=RequestMethod.POST)
	public Map<String, Object>  addUserHobby(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		String userId=requst.getParameter("userId");
		String hobbyId=requst.getParameter("hobbyId");
		System.out.println("hellowold");
		return hobby_select.addHobby(userId,hobbyId);
	}
	
	@RequestMapping(value="/m/hobby/delUserHobby.do",method=RequestMethod.POST)
	public Map<String, Object>  delUserHobby(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		String userId=requst.getParameter("userId");
		String hobbyId=requst.getParameter("hobbyId");
		System.out.println("hellowold");
		
		return hobby_select.delHobby(userId, hobbyId);
	}
	
}
