package app.user.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import app.user.dao.HobbyDao;
import app.user.dao.UserLoginDao;
import app.util.controller.BaseJsonController;

@RestController
public class UserLoginController extends BaseJsonController{
	
	@Resource
	private UserLoginDao user_login;
	
	/**
	 * 检查用户是否处于登录状态
	 * @param diviceId
	 * @param tocken
	 * @return
	 */
	
	
	
	@RequestMapping(value="/m/user/checkToken.do", method = RequestMethod.POST)
	public Map<String, Object>  user_checkToken(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String diviceId = requst.getParameter("diviceId");
		String tocken = requst.getParameter("tocken");
		String appId = requst.getParameter("appId");
		String userId = requst.getParameter("userId");
		return user_login.vu_user_checktoken(tocken, diviceId,appId,userId);
	}
	
	/**
	 * 用户登录
	 * @param deviceId
	 * @param phoneNumber
	 * @param pwdMd5
	 * @param requst
	 * @return
	 */
	@RequestMapping(value="/m/user/login.do", method = RequestMethod.POST)
	public Map<String, Object> user_doLogin(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		try {
			requst.setCharacterEncoding("utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("hellowold");  
		String deviceId = requst.getParameter("deviceId");
		String phoneNumber = requst.getParameter("phoneNumber");
		String pwdMd5 = requst.getParameter("pwdMd5");
		String myTocken = requst.getSession().getId();
		String appId = requst.getParameter("appId"); 
		String role = "";
		return user_login.vu_user_login(deviceId, phoneNumber, pwdMd5, myTocken,appId,role);
	}
	
	
	

	/**
	 * 用户注销
	 * @param phone
	 * @param tocken
	 * @param deviceId
	 * @return
	 */
	@RequestMapping(value="/m/user/logout.do", method = RequestMethod.POST)
	public Map<String, Object>  user_logout(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String userId = requst.getParameter("userId");
		String tocken = requst.getParameter("tocken");
		String deviceId = requst.getParameter("deviceId");
		String appId = requst.getParameter("appId");
		return user_login.vu_user_loginout(userId, tocken, deviceId,appId);
	}
	
	/**
	 * 添加反馈消息
	 * @param requst
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/m/user/yu_user_feedback.do", method = RequestMethod.POST)
	public  Map<String, Object> yu_user_feedback(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String userId = requst.getParameter("userId");
		String content = requst.getParameter("content");
		String appId = requst.getParameter("appId");
		return user_login.vu_user_feedback(userId, content,appId);
	}
	/**
	 * qq登录验证
	 * @param requst
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/m/user/qq_login.do", method = RequestMethod.POST)
	public Map<String,Object> qq_user_login(HttpServletRequest requst,
			HttpServletResponse response){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String qqId = requst.getParameter("openId");
		String appId = requst.getParameter("appId");
		String nick_name = requst.getParameter("nickname");
		String sex = requst.getParameter("sex");
		String img =requst.getParameter("touxiang");
		//获取token
		String token = requst.getSession().getId();
		return user_login.checkQQLogin(qqId, appId ,nick_name,sex,img,token);
	}
	
	/**
	 * qq绑定手机号
	 * @param requst
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/m/user/qqid_and_phone.do", method = RequestMethod.POST)
	public Map<String,Object> qqIdAndPhone(HttpServletRequest requst,
			HttpServletResponse response){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String openId = requst.getParameter("qqId");
		String phone = requst.getParameter("phone");
		String appId = requst.getParameter("appId");
//		获取token
		String token = requst.getSession().getId();
		return user_login.bindqqIdAndPhone(openId,phone,appId,token);
	}
	
	/**
	 * 微信登录验证
	 * @param requst
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/m/user/wx_login.do", method = RequestMethod.POST)
	public Map<String,Object> wx_user_login(HttpServletRequest requst,
			HttpServletResponse response){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String wxId = requst.getParameter("openId");
		String unionId = requst.getParameter("unionId");
		String appId = requst.getParameter("appId");
		String nick_name = requst.getParameter("nickname");
		String sex = requst.getParameter("sex");
		String img =requst.getParameter("touxiang");
		//获取token
		String token = requst.getSession().getId();
		return user_login.checkWXLogin(wxId,unionId, appId ,nick_name,sex,img,token);
	}


	/**
	 * 微信绑定手机号
	 * @param requst
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/m/user/wxid_and_phone.do", method = RequestMethod.POST)
	public Map<String,Object> wxIdAndPhone(HttpServletRequest requst,
			HttpServletResponse response){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		Map<String, Object> data = new HashMap<String, Object>();
		String openId = requst.getParameter("wxId");
		String phone = requst.getParameter("phone");
		String appId = requst.getParameter("appId");
		String unionId = requst.getParameter("unionId");
//		String checkCode = requst.getParameter("checkCode");
//		String checkCodeT = (String) requst.getSession().getAttribute(Constant.VALIDATE_PHONE_CODE);
//		String phoneT = (String) requst.getSession().getAttribute(Constant.VALIDATE_PHONE);
//		String checkCodeT = "1111";
//		String phoneT = "2225";
		//获取token
		String token = requst.getSession().getId();
		
		return user_login.bindwxIdAndPhone(openId,phone,appId,unionId,token);
	}
	
	/**
	 * 微信,QQ登录发送验证码
	 * @param requst
	 * @param response
	 * @return
	 */
	/*@RequestMapping(value="/m/user/sendValiCodeForThird.do", method = RequestMethod.POST)
	public Map<String, Object>  sendValiCode(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String phone = requst.getParameter("phone");
		return user_login.sendThirdLoginVarcode(phone, requst);
	}*/
	
	
	
	
	
	
	
	/**
	 * 确认验证码
	 * @param phone
	 * @param code
	 * @param requst
	 * @return
	 */
	@RequestMapping(value="/m/user/checkValiCodeForThird.do", method = RequestMethod.POST)
	public Map<String, Object>  checkValiCode(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		try {
			requst.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String phone = requst.getParameter("phone");
		String code = requst.getParameter("code");
		System.out.println(phone+":"+ code);
		System.out.println("调用方法成功");
		return user_login.checkThridValiCode(phone, code, requst);
	}
	
	

	
	@RequestMapping(value="/m/user/sendValiCodeForresg.do", method = RequestMethod.POST)
	public Map<String, Object>  sendValiCodeForresg(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		
		String phoneNumber=requst.getParameter("phoneNumber");
		
		return user_login.sendValiCode(phoneNumber,requst);
	}
	
	@RequestMapping(value="/m/user/reg.do", method = RequestMethod.POST)
	public Map<String, Object> user_reg(
			HttpServletRequest requst,
			HttpServletResponse response
			){
		String phoneNumber = requst.getParameter("phoneNumber");
		String pwdMd5 = requst.getParameter("pwdMd5");
		String regCode =requst.getParameter("regCode");
		String mTocken = requst.getSession().getId();
		
		return user_login.checkres(phoneNumber,pwdMd5,regCode,mTocken,requst);
	}
	
	
	
	
	
}
