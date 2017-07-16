package app.user.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.user.dao.MainDao;

@RestController
public class MainController {

	
	@Resource
	private MainDao mainSearch;
	@RequestMapping(value="m/map/main.do",method=RequestMethod.POST)
	public Map<String, Object> Main(
			HttpServletRequest requst,
			HttpServletResponse response){
		
		System.out.println("hello");
		String longtitude=requst.getParameter("lng");
		String latitude=requst.getParameter("lat");
		
		
	
		
		return mainSearch.query(latitude,longtitude);
	}
	
	
	
}
