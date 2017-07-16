package app.user.controller;



import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.user.dao.A;



@RestController
public class AController {
	@RequestMapping(value="/a.do",method=RequestMethod.POST)
	public Map<String, Object> a(A a){
		//System.out.println(requst.getParameter("a"));
		System.out.println(a);
		return null;
	}
	
	
}
