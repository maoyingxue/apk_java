package app.user.dao;

import java.text.SimpleDateFormat;  
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import app.util.Constant;
import app.util.SendMsgUtil; 
 

@Repository
public class UserLoginDao {
	@Resource
	private JdbcTemplate jdbc;
	@Resource
	private OtherFunctionDAO otherfunction;
	@Resource
	private MessageDAO msgDao;
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Resource
	private AuthDao auth;

	/**
	 * 判断是否登录
	 * 
	 * @param tocken
	 * @param diviceId
	 * @param app
	 * @param userId
	 * @return
	 */
	public Map<String, Object> vu_user_checktoken(String tocken,
			String diviceId, String app, String userId) {
		Map<String, Object> checkTokenInfo = new HashMap<String, Object>();

		int loginStatu = this.queryUserIsLogin(tocken, app, userId);
		//根据userId获得对应的角色,userId为空则role对应-1
		
		//得到权限
		List<Map<String, Object>> authlist = auth.getQuxian(app,
				"");
		checkTokenInfo.put("authlist", authlist);
		
		if (loginStatu <= 0) {
			checkTokenInfo.put("res", Constant.SUCCESS);
			checkTokenInfo.put("flag", Constant.FAILURE);
			checkTokenInfo.put("msg", "用户处于离线状态");
			return checkTokenInfo;
		}

		Map<String, Object> data = new HashMap<String,Object>();
		if(app.equals(Constant.APP_GAS)){
			//拿到用
			// SELECT value1 AS price,value2 AS bean FROM vsys_config_value WHERE config_status = 1 AND  type_code = 'oil_bean_pay';
			List<Map<String,Object>> bean_rules =  jdbc.queryForList("SELECT value1 AS price,value2 AS bean FROM "
							+"vsys_config_value WHERE config_status = 1"
							+" AND  type_code = 'oil_bean_pay'");
			data.put("rule_oil_bean", bean_rules);
			//查找用户信息
			List<Map<String,Object>> userinfo = jdbc.queryForList("SELECT * FROM vu_user WHERE id=?",userId);
			data.put("user", userinfo.get(0));
		}
		checkTokenInfo.put("data", data);
		checkTokenInfo.put("msg", "处于登录状态");
		return checkTokenInfo;
	}

	/**
	 * 用户登录
	 * 
	 * @param deviceId
	 * @param phoneNumber
	 * @param pwdMd5
	 * @param myTocken
	 * @return
	 */
	public Map<String, Object> vu_user_login(String deviceId,
			String phoneNumber, String pwdMd5, String myTocken, String appId,
			String role) {
		Map<String, Object> loginInfo = new HashMap<String, Object>();

		// 判断用户是否存在
		Map<String, Object> userInfo = this.queryHasReg(phoneNumber, pwdMd5);
		if((userInfo.get("res").toString()).equals("-2")){
			loginInfo.put("res", Constant.FAILURE);
			loginInfo.put("msg", "手机号未注册!");
			return loginInfo;
		}
		
		if ((userInfo.get("res").toString()).equals("-1")) {
			loginInfo.put("res", Constant.SUCCESS);
			loginInfo.put("res", Constant.FAILURE);
			loginInfo.put("msg", "手机号或密码错误!");
			return loginInfo;
		}

		// 获取userId
		int userId = Integer.parseInt(((Map<String, Object>) userInfo
				.get("data")).get("id").toString());

		// 判断是否有登录记录然后更新登录
		int queryResult = this.queryIsLoginLog(userId + "", appId);
		if (queryResult > 0) {
			int loginResult = this.updateLoginInfo(userId + "", appId,
					myTocken, deviceId);
			if (loginResult <= 0) {
				loginInfo.put("res", Constant.SUCCESS);
				loginInfo.put("res", Constant.FAILURE);
				loginInfo.put("msg", "登录失败");
				return loginInfo;
			}

			// 插入登录记录
			int insertLogResult = this.insertLoginLog(userId + "", deviceId,
					myTocken);
			if (insertLogResult <= 0) {
				loginInfo.put("res", Constant.SUCCESS);
				loginInfo.put("res", Constant.FAILURE);
				loginInfo.put("msg", "插入登录消息不存在");
				return loginInfo;
			}

			// 登录发送消息
			int insertMsgResult = msgDao.loginMsg(userId + "", myTocken, appId);

			if (insertMsgResult <= 0) {
				loginInfo.put("res", Constant.SUCCESS);
				loginInfo.put("flag", Constant.FAILURE);
				loginInfo.put("msg", "插入消息失败");

				return loginInfo;
			}
			
			//得到权限
			List<Map<String, Object>> authlist = auth.getQuxian(appId,
					role);

			//查找用户信息
//			List<Map<String,Object>> userinfo = jdbc.queryForList("SELECT * FROM vu_user WHERE id=?",userId);
//			if(userinfo.size()>0){
//				loginInfo.put("data", userinfo.get(0));
//			}
		
//			loginInfo.put("data", data);
			loginInfo.put("res", Constant.SUCCESS);
			loginInfo.put("flag", Constant.SUCCESS);
			loginInfo.put("data", userInfo.get("data"));//用户信息 
			loginInfo.put("tocken", myTocken);
			loginInfo.put("authlist", authlist);
			return loginInfo;
		}

		// 登录
		int loginResult = this.insertLoginInfo(userId + "", deviceId, myTocken,
				appId);
		if (loginResult <= 0) {
			loginInfo.put("res", Constant.SUCCESS);
			loginInfo.put("res", Constant.FAILURE);
			loginInfo.put("msg", "登录失败");
			return loginInfo;
		}

		// 插入登录记录
		int insertLogResult = this.insertLoginLog(userId + "", deviceId,
				myTocken);
		if (insertLogResult <= 0) {
			loginInfo.put("res", Constant.SUCCESS);
			loginInfo.put("res", Constant.FAILURE);
			loginInfo.put("msg", "插入登录消息不存在");
			return loginInfo;
		}

		// 登录发送消息
		int insertMsgResult = msgDao.loginMsg(userId + "", myTocken, appId);

		if (insertMsgResult <= 0) {
			loginInfo.put("res", Constant.SUCCESS);
			loginInfo.put("flag", Constant.FAILURE);
			loginInfo.put("msg", "插入消息失败");

			return loginInfo;
		}
		
//		loginInfo.put("data", data);
		loginInfo.put("res", Constant.SUCCESS);
		loginInfo.put("data", userInfo.get("data"));//用户信息 
		loginInfo.put("tocken", myTocken);
		return loginInfo;
	}

	/**
	 * 用户退出
	 * 
	 * @param uid
	 * @param tocken
	 * @param deviceId
	 * @return
	 */
	public Map<String, Object> vu_user_loginout(String uid, String tocken,
			String deviceId, String appId) {
		Map<String, Object> loginOutInfo = new HashMap<String, Object>();

		int updateLoginOutResult = this.updateLoginOutStatu(uid, tocken, appId);
		if (updateLoginOutResult <= 0) {
			loginOutInfo.put("res", Constant.SUCCESS);
			loginOutInfo.put("flag", Constant.FAILURE);
			loginOutInfo.put("msg", "退出状态改变失败");
			return loginOutInfo;
		}

		int insertLoginOutInfo = this.insertLoginLog(uid, deviceId, tocken);
		if (insertLoginOutInfo <= 0) {
			loginOutInfo.put("res", Constant.SUCCESS);
			loginOutInfo.put("flag", Constant.FAILURE);
			loginOutInfo.put("msg", "插入退出消息失败");
			return loginOutInfo;
		}

		loginOutInfo.put("res", Constant.SUCCESS);
		loginOutInfo.put("flag", Constant.SUCCESS);
		return loginOutInfo;
	}

	/**
	 * 用户退出
	 * 
	 * @param uid
	 * @param tocken
	 * @param deviceId
	 * @return
	 */
	public Map<String, Object> vu_user_feedback(String userId, String feedBack,
			String appId) {
/*		MyJdbcTemplate myjdbcTemplate = MyJdbcTemplate.getJdbcTemplate();*/
		Map<String, Object> loginoutInfo = new HashMap<>();
		int i = jdbc.update("INSERT INTO vsys_feedback" + " (user_id"
				+ ",content,app_type) " + "VALUES (?,?,?)", userId, feedBack,
				appId);

		if (i != 0) {
			loginoutInfo.put("res", Constant.SUCCESS);
			loginoutInfo.put("flag", Constant.SUCCESS);
			return loginoutInfo;
		} else {
			loginoutInfo.put("res", Constant.SUCCESS);
			loginoutInfo.put("flag", Constant.FAILURE);
			return loginoutInfo;
		}
	}

	/**
	 * 判断是否登录
	 * 
	 * @param tocken
	 * @param app
	 * @param userId
	 * @return
	 */
	public int queryUserIsLogin(String tocken, String app, String userId) {
		int queryResult = 0;

		List<Map<String, Object>> data = jdbc.queryForList("SELECT *"
				+ " FROM vu_user WHERE tocken = ?" + " AND app_id = ?"
				+ " AND id = ?", tocken, app, userId);

		queryResult = data.size();
		return queryResult;
	}

	/**
	 * 判断用户是否存在 并返回用户信息
	 * 
	 * @param phone
	 * @param pass
	 * @return
	 */
	public Map<String, Object> queryHasReg(String phone, String pass) {
		Map<String, Object> userInfo = new HashMap<String, Object>();

		//检查手机号和密码是否正确
		List<Map<String, Object>> data = jdbc.queryForList("SELECT * "
				+ "FROM vu_user " + "WHERE app_id = 0" + " AND phone = ?"
				+ " AND pass = ?", phone, pass);

		//检查手机号是否注册
		List<Map<String, Object>> list = jdbc.queryForList("SELECT * FROM "
								+"vu_user WHERE phone = ?",phone);
		
		if(list.size()<=0){
			userInfo.put("res", "-2");
			return userInfo;
		}
		if (data.size() <= 0) {
			userInfo.put("res", "-1");
			return userInfo;
		}

		if (data.get(0).get("pay_pass") == null
				|| data.get(0).get("pay_pass").equals("")) {
			data.get(0).remove("pass");
			data.get(0).put("pay_pass", "0");
		} else {
			data.get(0).remove("pass");
			data.get(0).put("pay_pass", "1");
		}

		userInfo.put("res", "1");
		userInfo.put("data", data.get(0));
		return userInfo;
	}

	/**
	 * 插入登入记录
	 * 
	 * @param userId
	 * @param deviceId
	 * @param myTocken
	 * @return
	 */
	public int insertLoginLog(String userId, String deviceId, String myTocken) {

		int result = jdbc.update(
				"insert into vsys_user_login_log" + "(user_id" + ",device_id"
						+ ",tocken" + ",login_time)" + "values(?,?,?,?)",
				userId, deviceId, myTocken, df.format(new Date()));
		return result;
	}

	/**
	 * 插入登出记录
	 * 
	 * @param tocken
	 * @return
	 */
	public int insertLoginOutLog(String tocken) {
		int loginOutResult = jdbc.update("UPDATE vsys_user_login_log"
				+ " SET logout_time = ?" + " WHERE tocken = ?",
				df.format(new Date()), tocken);

		return loginOutResult;
	}

	/**
	 * 插入登录信息
	 * 
	 * @param userId
	 * @param deviceId
	 * @param myTocken
	 * @param appId
	 * @return
	 */
	public int insertLoginInfo(String userId, String deviceId, String myTocken,
			String appId) {

		int result = jdbc.update("INSERT INTO " + "vu_user (user_id"
				+ ",app_id" + ",tocken" + ",device_id) VALUES" + " (?,?,?,?)",
				userId, appId, myTocken, deviceId);
		return result;
	}

	/**
	 * 更新登出状态
	 * 
	 * @param userId
	 * @param tocken
	 * @param appId
	 * @return
	 */
	public int updateLoginOutStatu(String userId, String tocken, String appId) {
		System.out.println("tocken:" + tocken + "," + userId + "," + appId);
		int result = jdbc.update("UPDATE vu_user" + " SET device_id =''"
				+ " ,tocken = ''" + "" + " WHERE tocken = ? AND user_id = ?",
				tocken, userId);

		return result;
	}

	/**
	 * 判断有无登录记录
	 * 
	 * @param userId
	 * @param appId
	 * @return
	 */
	public int queryIsLoginLog(String userId, String appId) {

		List<Map<String, Object>> result = jdbc.queryForList("SELECT *"
				+ " FROM vu_user WHERE" + " user_id = ? AND app_id = ?",
				userId, appId);
		if (result.size() > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public int updateLoginInfo(String userId, String appId, String tocken,
			String deviceId) {

		int result = jdbc.update("UPDATE vu_user" + " SET device_id = ?"
				+ ",tocken = ?" + " WHERE user_id = ? AND app_id =?", deviceId,
				tocken, userId, appId);

		return result;
	}

	/**
	 * qq登陆
	 * 
	 * @param qqId
	 * @param appId
	 * @param token
	 * @param nick_name
	 * @param province
	 * @param city
	 * @param sex
	 * @param img
	 * @return
	 */
	public Map<String, Object> checkQQLogin(String qqId, String appId,
			String nick_name, String sex, String img,String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if ("男".equals(sex) || "1".equals(sex)) {
			sex = "1";
		} else if ("女".equals(sex) || "2".equals(sex)) {
			sex = "2";
		} else {
			sex = null;
		}
		//查找该qqId是否存在
		List<Map<String, Object>> users = jdbc.queryForList(
				"SELECT * FROM vu_user WHERE union_id=? AND user_from='11'",
				qqId);
		if (users.size() > 0) {
			//该qqId存在记录
			String user_id = users.get(0).get("user_id")+"";
			//判断是否绑定手机注册信息
			if(user_id == null || user_id.equals("null")){
				//未绑定手机
				//清除之前插入的信息
				jdbc.update("DELETE FROM vu_user WHERE union_id = ? AND user_from = '11'", qqId);
				// 用户不存在,生成不带手机号的qq用户信息
				jdbc.update("INSERT INTO vu_user("
						+ "nick_name," + "sex," + "img_s,"
						+ "union_id,user_from) VALUES(?,?,?,?,'11')",  nick_name, sex, img,
						qqId);
				result.put("res", "1");
				result.put("flag", "0");
				return result;
			}else{
				//已绑定手机,查找出所需的用户信息
				//先更新qq用户信息
				jdbc.update("UPDATE vu_user SET nick_name = ?,sex=?,img_s=? WHERE union_id=? AND user_from ='11'", nick_name,sex,img,qqId);
				//再获取
				List<Map<String, Object>> userinfo = jdbc.queryForList("SELECT * FROM vu_user WHERE id = ?", user_id);
				List<Map<String, Object>> userinfo_qq = jdbc.queryForList("SELECT * FROM vu_user WHERE union_id = ? AND user_from ='11'", qqId);
				/*
				 * 判断该id账号信息是否完全,不完全,则用qq的信息去补全
				 */
				if(userinfo.get(0).get("nick_name") == null){
					jdbc.update("UPDATE vu_user SET nick_name = ? WHERE id=?"
							,userinfo_qq.get(0).get("nick_name").toString() , user_id);
				}
				if(userinfo.get(0).get("sex") == null){
					jdbc.update("UPDATE vu_user SET sex = ? WHERE id=?"
							,userinfo_qq.get(0).get("sex").toString() , user_id);
				}
				if(userinfo.get(0).get("img_s") == null){
					jdbc.update("UPDATE vu_user SET img_s = ? WHERE id=?"
							,userinfo_qq.get(0).get("img_s").toString() , user_id);
				}
				
				userinfo.get(0).put("nick_name", userinfo_qq.get(0).get("nick_name").toString());
				userinfo.get(0).put("sex", userinfo_qq.get(0).get("sex"));
				userinfo.get(0).put("img_s", userinfo_qq.get(0).get("img_s"));
				userinfo.get(0).put("qq_id", userinfo_qq.get(0).get("union_id"));
				String pay_pass = userinfo.get(0).get("pay_pass")+"";
				if(pay_pass.equals("null")){
					//没有支付密码
					pay_pass = "0";
				}else{
					pay_pass = "1";
				}
				userinfo.get(0).put("pay_pass", pay_pass);
				
				//检查是否存在登录信息
//				List<Map<String, Object>> user_loginfos = jdbc.queryForList("SELECT * FROM vu_user WHERE user_id = ? AND app_id=? AND user_from = '1'",user_id,appId);

				//不存在登录信息
				//创建新的登录信息
				int n = jdbc.update("INSERT INTO vu_user(user_id,app_id,tocken,user_from) VALUES(?,?,?,'1')", user_id,appId,token);
				if(n>=0){
					//创建的登录信息成功 
					//获取权限列表
					List<Map<String, Object>> authlist = auth.getQuxian(appId,null);
					// 登录发送消息
					int insertMsgResult = msgDao.loginMsg(user_id + "", token, appId);
					if (insertMsgResult <= 0) {
						result.put("res", Constant.SUCCESS);
						result.put("flag", Constant.FAILURE);
						result.put("msg", "插入消息失败");
						return result;
					} 
					
					result.put("res", "1");
					result.put("flag", "1"); 
					result.put("authlist", authlist);
					result.put("tocken", token);
					result.put("data", userinfo.get(0));
				}
			
				return result;
			}
		} else {
			//qqId不存在记录
			//清除之前插入的信息
			jdbc.update("DELETE FROM vu_user WHERE union_id = ? AND user_from = '11'", qqId);
			//生成不带手机号的用户信息
			int row = jdbc.update("INSERT INTO vu_user("
					+ "nick_name," + "sex," + "img_s,"
					+ "union_id,user_from) VALUES(?,?,?,?,'11')",  nick_name, sex, img,
					qqId);
			if (row > 0) {
				result.put("res", "1");
				result.put("flag", "0");
				return result;
			} else {
				result.put("res", "1");
				result.put("flag", "0");
				result.put("msg", "登录失败");
				return result;
			}
		}
	}
	
	/**
	 * 将qqId和手机号绑定,生成userID
	 * 
	 * @param opneId
	 * @param phone
	 * @return
	 */
	public Map<String, Object> bindqqIdAndPhone(String qqId, String phone,String appId,
			String token) {
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		// 检查该手机号是否已经注册
		List<Map<String, Object>> has_phone = jdbc.queryForList(
				"SELECT * FROM vu_user WHERE phone = ?", phone);
		if (has_phone.size() > 0) {
			//手机号已注册,获取已注册记录的id,作为userID绑定给qq登录的记录
			String user_id = has_phone.get(0).get("id").toString();
			jdbc.update("UPDATE vu_user SET user_id = ? WHERE union_id=? AND user_from ='11'", user_id,qqId);
			
			//获得所需的用户信息
			List<Map<String, Object>> userinfo = jdbc.queryForList("SELECT * FROM vu_user WHERE id = ?", user_id);
			List<Map<String, Object>> userinfo_qq = jdbc.queryForList("SELECT * FROM vu_user WHERE union_id = ? AND user_from='11'", qqId);
			/*
			 * 判断该id账号信息是否完全,不完全,则用qq的信息去补全
			 */
			if(userinfo.get(0).get("nick_name") == null){
				jdbc.update("UPDATE vu_user SET nick_name = ? WHERE id=?"
						,userinfo_qq.get(0).get("nick_name").toString() , user_id);
			}
			if(userinfo.get(0).get("sex") == null){
				jdbc.update("UPDATE vu_user SET sex = ? WHERE id=?"
						,userinfo_qq.get(0).get("sex").toString() , user_id);
			}
			if(userinfo.get(0).get("img_s") == null){
				jdbc.update("UPDATE vu_user SET img_s = ? WHERE id=?"
						,userinfo_qq.get(0).get("img_s").toString() , user_id);
			}
			
			userinfo.get(0).put("nick_name", userinfo_qq.get(0).get("nick_name").toString());
			userinfo.get(0).put("sex", userinfo_qq.get(0).get("sex"));
			userinfo.get(0).put("img_s", userinfo_qq.get(0).get("img_s"));
			userinfo.get(0).put("qq_id", userinfo_qq.get(0).get("union_id"));
			String pay_pass = userinfo.get(0).get("pay_pass")+"";
			if(pay_pass.equals("null")){
				//没有支付密码
				pay_pass = "0";
			}else{
				pay_pass = "1";
			}
			userinfo.get(0).put("pay_pass", pay_pass);
			
			jdbc.update("INSERT INTO vu_user(user_id,app_id,tocken,user_from) VALUES(?,?,?,'1')", user_id,appId,token);

			//获取权限列表
			List<Map<String, Object>> authlist = auth.getQuxian(appId,null);
			// 登录发送消息
			int insertMsgResult = msgDao.loginMsg(user_id + "", token, appId);
			if (insertMsgResult <= 0) {
				data.put("res", Constant.SUCCESS);
				data.put("flag", Constant.FAILURE);
				data.put("msg", "插入消息失败");
				return data;
			} 
			
			data.put("res", "1");
			data.put("flag", "1");
			data.put("tocken", token); 
			data.put("authlist", authlist);
			data.put("data", userinfo.get(0));
			return data;
		} else {
			//手机号码未注册
			System.out.println("手机未注册");
			// 生成带手机号码用户信息
			int n = jdbc.update("INSERT INTO vu_user(phone) VALUE(?)", phone);
			if (n > 0) {
				// 拿到userID
				result = jdbc.queryForList(
						"SELECT * FROM vu_user WHERE phone = ? AND user_from IS NULL",phone);
				String user_id = result.get(0).get("id").toString();
				System.out.println("1");
				//给qq登录信息绑定userId
				jdbc.update("UPDATE vu_user SET user_id = ? WHERE union_id=? AND user_from ='11'", user_id,qqId);
				System.out.println("2");
				//获得所需的用户信息
				List<Map<String, Object>> userinfo = jdbc.queryForList("SELECT * FROM vu_user WHERE id = ?", user_id);
				System.out.println(userinfo.get(0));
				List<Map<String, Object>> userinfo_qq = jdbc.queryForList("SELECT * FROM vu_user WHERE union_id = ? AND user_from = '11'", qqId);
				System.out.println(userinfo_qq.get(0));
				/*
				 * 判断该id账号信息是否完全,不完全,则用qq的信息去补全
				 */
				if(userinfo.get(0).get("nick_name") == null){
					jdbc.update("UPDATE vu_user SET nick_name = ? WHERE id=?"
							,userinfo_qq.get(0).get("nick_name").toString() , user_id);
				}
				if(userinfo.get(0).get("sex") == null){
					jdbc.update("UPDATE vu_user SET sex = ? WHERE id=?"
							,userinfo_qq.get(0).get("sex").toString() , user_id);
				}
				if(userinfo.get(0).get("img_s") == null){
					jdbc.update("UPDATE vu_user SET img_s = ? WHERE id=?"
							,userinfo_qq.get(0).get("img_s").toString() , user_id);
				}
				
				userinfo.get(0).put("nick_name", userinfo_qq.get(0).get("nick_name").toString());
				userinfo.get(0).put("sex", userinfo_qq.get(0).get("sex"));
				userinfo.get(0).put("img_s", userinfo_qq.get(0).get("img_s"));
				userinfo.get(0).put("qq_id", userinfo_qq.get(0).get("union_id"));
				String pay_pass = userinfo.get(0).get("pay_pass")+"";
				if(pay_pass.equals("null")){
					//没有支付密码
					pay_pass = "0";
				}else{
					pay_pass = "1";
				}
				userinfo.get(0).put("pay_pass", pay_pass);
				

				jdbc.update("INSERT INTO vu_user(user_id,app_id,tocken,user_from) VALUES(?,?,?,'1')", user_id,appId,token);

				//获取权限列表
				List<Map<String, Object>> authlist = auth.getQuxian(appId,null);
				// 登录发送消息
				int insertMsgResult = msgDao.loginMsg(user_id + "", token, appId);

				if (insertMsgResult <= 0) {
					data.put("res", Constant.SUCCESS);
					data.put("flag", Constant.FAILURE);
					data.put("msg", "插入消息失败");
					return data;
				} 
				data.put("authlist", authlist);
				data.put("res", "1");
				data.put("flag", "1");
				data.put("tocken", token);
				data.put("data", userinfo.get(0));
			}else{
				data.put("res", "1");
				data.put("flag", "0");
				data.put("msg", "系统异常");
			}
			return data;
		}
	}

	/**
	 * 微信登陆
	 * 
	 * @param wxId
	 * @param appId
	 * @param token
	 * @param nick_name
	 * @param province
	 * @param city
	 * @param sex
	 * @param img
	 * @return
	 */
	public Map<String, Object> checkWXLogin(String wxId, String unionId,
			String appId, String nick_name, String sex, String img,String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		if ("男".equals(sex) || "1".equals(sex)) {
			sex = "1";
		} else if ("女".equals(sex) || "2".equals(sex)) {
			sex = "2";
		} else {
			sex = null;
		}
		List<Map<String, Object>> users = jdbc.queryForList(
				"SELECT * FROM vu_user " + "WHERE weixin_id = ? AND user_from = '10'",
				wxId);
		//检查是否存在用户信息
		if (users.size() > 0) {
			// 用户存在
			String user_id = users.get(0).get("user_id")+"";
			if(user_id == null || "null".equals(user_id)){
				//未绑定手机信息
				//清除之前插入的信息
				jdbc.update("DELETE FROM vu_user WHERE weixin_id = ? AND user_from = '10'", wxId);
				// 用户不存在,生成不带手机号的用户信息
				jdbc.update("INSERT INTO vu_user("
						+ "nick_name," + "sex," + "img_s,"
						+ "union_id,weixin_id,user_from) VALUES(?,?,?,?,?,'10')",  nick_name, sex, img,
						unionId,wxId);
				result.put("res", "1");
				result.put("flag", "0");
				return result;
			}else{
				//已绑定手机信息
				//更新用户信息
				jdbc.update("UPDATE vu_user SET nick_name=?,"
						+ "sex=?,img_s=?" + " WHERE weixin_id=? AND union_id = ? AND user_from ='10'",
						nick_name, sex, img, wxId,unionId);
				System.out.println("已更新");
				//再获取
				List<Map<String, Object>> userinfo = jdbc.queryForList("SELECT * FROM vu_user WHERE id = ?", user_id);
				System.out.println(userinfo.get(0));
				List<Map<String, Object>> userinfo_wx = jdbc.queryForList("SELECT * FROM vu_user WHERE union_id = ? AND weixin_id = ? AND user_from ='10'", unionId,wxId);
				System.out.println(userinfo_wx.get(0));
				/*
				 * 判断该id账号信息是否完全,不完全,则用wx的信息去补全
				 */
				if(userinfo.get(0).get("nick_name") == null){
					jdbc.update("UPDATE vu_user SET nick_name = ? WHERE id=?"
							,userinfo_wx.get(0).get("nick_name").toString() , user_id);
				}
				if(userinfo.get(0).get("sex") == null){
					jdbc.update("UPDATE vu_user SET sex = ? WHERE id=?"
							,userinfo_wx.get(0).get("sex").toString() , user_id);
				}
				if(userinfo.get(0).get("img_s") == null){
					jdbc.update("UPDATE vu_user SET img_s = ? WHERE id=?"
							,userinfo_wx.get(0).get("img_s").toString() , user_id);
				}
				
				userinfo.get(0).put("nick_name", userinfo_wx.get(0).get("nick_name").toString());
				userinfo.get(0).put("sex", userinfo_wx.get(0).get("sex"));
				userinfo.get(0).put("img_s", userinfo_wx.get(0).get("img_s"));
				userinfo.get(0).put("union_id", userinfo_wx.get(0).get("union_id"));
				userinfo.get(0).put("weixin_id", userinfo_wx.get(0).get("weixin_id"));
				String pay_pass = userinfo.get(0).get("pay_pass")+"";
				if(pay_pass.equals("null")){
					//没有支付密码
					pay_pass = "0";
				}else{
					pay_pass = "1";
				}
				userinfo.get(0).put("pay_pass", pay_pass);
				
				//检查是否存在登录信息

				//用户存在,但不存在登录信息
				//创建新的登录信息
				int n = jdbc.update("INSERT INTO vu_user(user_id,app_id,tocken) VALUES(?,?,?)", user_id,appId,token);

				//获取权限列表
				List<Map<String, Object>> authlist = auth.getQuxian(appId,null);
				// 登录发送消息
				int insertMsgResult = msgDao.loginMsg(user_id + "", token, appId);

				if (insertMsgResult <= 0) {
					result.put("res", Constant.SUCCESS);
					result.put("flag", Constant.FAILURE);
					result.put("msg", "插入消息失败");
					return result;
				}
				if(n>=0){
					//创建的登录信息成功
					  
					result.put("res", "1");
					result.put("flag", "1"); 
					result.put("authlist", authlist);
					result.put("tocken", token);
					result.put("data", userinfo.get(0));
				}
				return result;	
			}
		} else {
			//用户不存在
			//清除之前插入的用户信息
			jdbc.update("DELETE FROM vu_user WHERE weixin_id = ? AND union_id = ? AND  user_from = '10'", wxId,unionId);
			// 用户不存在,且手机未绑定,生成用户信息
			int row = jdbc.update("INSERT INTO vu_user("
					+ "nick_name," + "sex," + "img_s," + "weixin_id,"
					+ "union_id,user_from) VALUES(?,?,?,?,?,'10')",  nick_name, sex,
					img, wxId, unionId);
			if (row > 0) {
				result.put("res", "1");
				result.put("flag", "0");
				return result;
			} else {
				result.put("res", "1");
				result.put("flag", "0");
				result.put("msg", "登录失败");
				return result;
			}
		}
	}

	/**
	 * 将wxId和手机号绑定,生成userId
	 * 
	 * @param opneId
	 * @param phone
	 * @return
	 */
	public Map<String, Object> bindwxIdAndPhone(String wxId, String phone,String appId,String unionId,
			String token) {
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		// 检查该手机号是否已经注册
		List<Map<String, Object>> has_phone = jdbc.queryForList(
				"SELECT * FROM vu_user WHERE phone = ?", phone);
		if (has_phone.size() > 0) {
			// 已经注册,拿到之前的userId
			String user_id = has_phone.get(0).get("id").toString();
			//把userId给微信登陆的记录绑定
			jdbc.update("UPDATE vu_user SET user_id = ? WHERE union_id=? AND weixin_id=? AND user_from ='10'", user_id,unionId,wxId);
			// 拿到之前的wx相关信息
			//获得所需的用户信息
			List<Map<String, Object>> userinfo = jdbc.queryForList("SELECT * FROM vu_user WHERE id = ?", user_id);
			List<Map<String, Object>> userinfo_wx = jdbc.queryForList("SELECT * FROM vu_user WHERE union_id = ? AND weixin_id = ? AND user_from='10'",unionId, wxId);
			/*
			 * 判断该id账号信息是否完全,不完全,则用wx的信息去补全
			 */
			if(userinfo.get(0).get("nick_name") == null){
				jdbc.update("UPDATE vu_user SET nick_name = ? WHERE id=?"
						,userinfo_wx.get(0).get("nick_name").toString() , user_id);
			}
			if(userinfo.get(0).get("sex") == null){
				jdbc.update("UPDATE vu_user SET sex = ? WHERE id=?"
						,userinfo_wx.get(0).get("sex").toString() , user_id);
			}
			if(userinfo.get(0).get("img_s") == null){
				jdbc.update("UPDATE vu_user SET img_s = ? WHERE id=?"
						,userinfo_wx.get(0).get("img_s").toString() , user_id);
			}
			
			userinfo.get(0).put("nick_name", userinfo_wx.get(0).get("nick_name").toString());
			userinfo.get(0).put("sex", userinfo_wx.get(0).get("sex"));
			userinfo.get(0).put("img_s", userinfo_wx.get(0).get("img_s"));
			userinfo.get(0).put("union_id", userinfo_wx.get(0).get("union_id"));
			userinfo.get(0).put("weixin_id", userinfo_wx.get(0).get("weixin_id"));
			String pay_pass = userinfo.get(0).get("pay_pass")+"";
			if(pay_pass.equals("null")){
				//没有支付密码
				pay_pass = "0";
			}else{
				pay_pass = "1";
			}
			userinfo.get(0).put("pay_pass", pay_pass);
			
			// 检查是否有登陆信息
			jdbc.update("INSERT INTO vu_user(user_id,app_id,tocken) VALUES(?,?,?)", user_id,appId,token);
			//获取权限列表
			List<Map<String, Object>> authlist = auth.getQuxian(appId,null);
			// 登录发送消息
			int insertMsgResult = msgDao.loginMsg(user_id + "", token, appId);

			if (insertMsgResult <= 0) {
				data.put("res", Constant.SUCCESS);
				data.put("flag", Constant.FAILURE);
				data.put("msg", "插入消息失败");
				return data;
			}
			  
//			data1.put("user", userinfo.get(0));
			
			data.put("res", "1");
			data.put("flag", "1"); 
			data.put("authlist", authlist);
			data.put("tocken", token);
			data.put("data", userinfo.get(0));
			return data;
		} else {
			// 没有注册
			System.out.println("没有注册");
			// 生成带手机号码用户信息
			int n = jdbc.update("INSERT INTO vu_user(phone) VALUE(?)", phone);
			if (n >= 0) {
				// 拿到userID
				result = jdbc.queryForList(
						"SELECT * FROM vu_user WHERE phone = ? AND user_from IS NULL",
						phone);
				System.out.println(result.size());
				String userId = "" + result.get(0).get("id");
				//将新注册的手机用户的userId绑定到微信记录上
				jdbc.update("UPDATE vu_user SET user_id = ? WHERE union_id=? AND weixin_id = ? AND user_from ='10", userId,unionId,wxId);
				
				//获得所需的用户信息
				List<Map<String, Object>> userinfo = jdbc.queryForList("SELECT * FROM vu_user WHERE id = ?", userId);
				List<Map<String, Object>> userinfo_wx = jdbc.queryForList("SELECT * FROM vu_user WHERE union_id = ? AND weixin_id = ? AND user_from='10'",unionId, wxId);
				/*
				 * 判断该id账号信息是否完全,不完全,则用wx的信息去补全
				 */
				if(userinfo.get(0).get("nick_name") == null){
					jdbc.update("UPDATE vu_user SET nick_name = ? WHERE id=?"
							,userinfo_wx.get(0).get("nick_name").toString() , userId);
				}
				if(userinfo.get(0).get("sex") == null){
					jdbc.update("UPDATE vu_user SET sex = ? WHERE id=?"
							,userinfo_wx.get(0).get("sex").toString() , userId);
				}
				if(userinfo.get(0).get("img_s") == null){
					jdbc.update("UPDATE vu_user SET img_s = ? WHERE id=?"
							,userinfo_wx.get(0).get("img_s").toString() , userId);
				}
				
				userinfo.get(0).put("nick_name", userinfo_wx.get(0).get("nick_name").toString());
				userinfo.get(0).put("sex", userinfo_wx.get(0).get("sex"));
				userinfo.get(0).put("img_s", userinfo_wx.get(0).get("img_s"));
				userinfo.get(0).put("union_id", userinfo_wx.get(0).get("union_id"));
				userinfo.get(0).put("weixin_id", userinfo_wx.get(0).get("weixin_id"));
				String pay_pass = userinfo.get(0).get("pay_pass")+"";
				if(pay_pass.equals("null")){
					//没有支付密码
					pay_pass = "0";
				}else{
					pay_pass = "1";
				}
				userinfo.get(0).put("pay_pass", pay_pass);
				
				// 检查是否有登陆信息
				jdbc.update("INSERT INTO vu_user(user_id,app_id,tocken) VALUES(?,?,?)", userId,appId,token);

				//获取权限列表
				List<Map<String, Object>> authlist = auth.getQuxian(appId,null);
				// 登录发送消息
				int insertMsgResult = msgDao.loginMsg(userId + "", token, appId);

				if (insertMsgResult <= 0) {
					data.put("res", Constant.SUCCESS);
					data.put("flag", Constant.FAILURE);
					data.put("msg", "插入消息失败");
					return data;
				}
				  
//				data1.put("user", userinfo.get(0));
				 
				data.put("authlist", authlist);
				data.put("res", "1");
				data.put("flag", "1");
				data.put("tocken", token);
				data.put("data", userinfo.get(0));
			}else{
				data.put("res","1");
				data.put("flag", "0");
				data.put("msg", "系统错误");
			}
			return data;
		}
	}

	/**
	 * 发送第三方登录验证码
	 * 
	 * @param phone
	 * @param requst
	 * @return
	 */
	//短信相关
	/*
	public Map<String, Object> sendThirdLoginVarcode(String phone,
			HttpServletRequest requst) {
		Map<String, Object> result = new HashMap<String, Object>();

		Random random = new Random();
		final int x = random.nextInt(899999) + 100000;
		requst.getSession().setAttribute(Constant.VALIDATE_PHONE_CODE,
				String.valueOf(x));
		requst.getSession().setAttribute(Constant.VALIDATE_PHONE, phone);
		System.out.println(requst.getSession().getAttribute(
				Constant.VALIDATE_PHONE)
				+ ":"
				+ requst.getSession()
						.getAttribute(Constant.VALIDATE_PHONE_CODE));
		System.out.println(requst.getSession().getId());
		result.put("flag", Constant.SUCCESS);

		String content = "您的验证码是：" + x + "。请不要把验证码泄露给其他人。";
		int sendMsgResult = SendMsgUtil.sendMsg(phone, content);

		System.out.println("sendMsgR:" + sendMsgResult);
		if (sendMsgResult <= 0) {
			result.put("res", Constant.SUCCESS);
			result.put("flag", Constant.FAILURE);
			return result;
		}
		System.out.println(result.toString());
		return result;
	}*/

	/**
	 * 验证验证码
	 * 
	 * @param phone
	 * @param code
	 * @param requst
	 * @return
	 */
	public Map<String, Object> checkThridValiCode(String phone, String code,
			HttpServletRequest requst) {
		System.out.println("调用方法2成功");
		String myCode = (String) requst.getSession().getAttribute(
				Constant.VALIDATE_PHONE_CODE);
		String myPhone = (String) requst.getSession().getAttribute(
				Constant.VALIDATE_PHONE);
		Map<String, Object> checkValiCodeInfo = new HashMap<String, Object>();
		System.out.println(myPhone + ":" + myCode);
		System.out.println(phone + ":" + code);
		checkValiCodeInfo.put("res", Constant.SUCCESS);
		checkValiCodeInfo.put("flag", (code.equals(myCode) && phone
				.equals(myPhone)) ? Constant.SUCCESS : Constant.FAILURE);
		System.out.println(checkValiCodeInfo.toString());
		return checkValiCodeInfo;
	}
	
	
	/**
	 * 发送注册验证码
	 * @param phoneNumber
	 * @param requst
	 * @return
	 */
	public Map<String, Object> sendValiCode(String phoneNumber,HttpServletRequest requst){
		
		Map<String, Object> result = new HashMap<String, Object>();
		Random random = new Random();
		final int x = random.nextInt(899999) + 100000;
		requst.getSession().setAttribute(Constant.VALIDATE_PHONE_CODE,
				String.valueOf(x));
		requst.getSession().setAttribute(Constant.VALIDATE_PHONE, phoneNumber);
		System.out.println(requst.getSession().getAttribute(
				Constant.VALIDATE_PHONE)
				+ ":"
				+ requst.getSession()
						.getAttribute(Constant.VALIDATE_PHONE_CODE));
		System.out.println(requst.getSession().getId());
		result.put("flag", Constant.SUCCESS);
		int sendMsgResult = SendMsgUtil.sendMsg(phoneNumber, x);

		System.out.println("sendMsgR:" + sendMsgResult);
		if (sendMsgResult <= 0) {
			result.put("res", Constant.SUCCESS);
			result.put("flag", Constant.FAILURE);
			return result;
		}
		System.out.println(result.toString());
		return result;
		
	}
	
/**
 * 	 注册
 * 
 * @param phoneNumber
 * @param pwdMd5
 * @param regCode
 * @param myTocken
 * @return
 */
public Map<String, Object> checkres(String phoneNumber,String pwdMd5,String regCode,String myTocken,HttpServletRequest requst){
	
	
	Map<String, Object> userInfo=new HashMap<String,Object>();
	String myCode = (String) requst.getSession().getAttribute(
			Constant.VALIDATE_PHONE_CODE);
	String myPhone = (String) requst.getSession().getAttribute(
			Constant.VALIDATE_PHONE);
	if(regCode.equals(myCode)&&phoneNumber.equals(myPhone)){
		
	
	List<Map<String, Object>> user=jdbc.queryForList("select * from vu_user where phone="+phoneNumber);
	if(user.size()!=0){
		userInfo.put("res",Constant.SUCCESS);
		userInfo.put("flag", Constant.FAILURE);
		userInfo.put("msg", "该手机号以注册");
		
	}
	else{
		int i=jdbc.update("insert into vu_user(phone,pass,tocken) values("+phoneNumber+","+pwdMd5+",'"+myTocken+"')");
		if(i==1){
			userInfo.put("res",Constant.SUCCESS);
			userInfo.put("flag", Constant.SUCCESS);
			
			List<Map<String, Object>> users=jdbc.queryForList("select * from vu_user where phone="+phoneNumber);
			userInfo.put("data", users);
		}else{
			userInfo.put("res",Constant.SUCCESS);
			userInfo.put("flag", Constant.FAILURE);
			userInfo.put("msg", "注册失败");
		}
	}
	
	}
	
	
	else{
		userInfo.put("res",Constant.SUCCESS);
		userInfo.put("flag", Constant.FAILURE);
		userInfo.put("msg", "验证码错误");
	}
	
	
	return userInfo;
}




}
