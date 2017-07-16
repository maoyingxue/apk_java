package app.util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.request.TimeGetRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import com.taobao.api.response.TimeGetResponse;

public class SendMsgUtil {

	//private static String Url = "http://localhost/webservice/sms.php?method=Submit";
	private static String url = "http://gw.api.taobao.com/router/rest";
	private static String appkey = "23354875";
	private static String secret = "a79981463eb438d5c1cd253012d9fe85";
	public static int sendMsg(String phone,int content) {
		
		TaobaoClient client = new DefaultTaobaoClient(url,appkey,secret);
		
		TimeGetRequest request = new TimeGetRequest();
		TimeGetResponse response = null;
		try {
			response = client.execute(request);
		} catch (ApiException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(response.getBody());
		
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend("123456");
		req.setSmsType("normal");
		req.setSmsFreeSignName("注册验证");
		req.setSmsParamString("{\"code\":\""+content+"\",\"product\":\"quandi\"}");
		
		req.setRecNum(phone);//18119953197
		req.setSmsTemplateCode("SMS_8180494");
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = client.execute(req);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		System.out.println(rsp.getBody());
		
		
		
		return 1;
	}

	
	 public static void main(String[] args) throws ApiException{
		 SendMsgUtil a=new SendMsgUtil();
		 a.sendMsg("1",1);
	 }
	
	
	
	
	
	
	/*public static int sendMsg(String phone,String content){
		HttpClient client = new HttpClient(); 
		PostMethod method = new PostMethod(Url); 
			
		//client.getParams().setContentCharset("GBK");		
		client.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");	
		
	    String content1 = new String(content); 
	    
		NameValuePair[] data = {//提交短信
			    new NameValuePair("account", "cf_yunchewei"), 
			    new NameValuePair("password", "yunchewei"), //密码可以使用明文密码或使用32位MD5加密
			    //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
			    new NameValuePair("mobile", phone), 
			    new NameValuePair("content", content1),
		};
		
		method.setRequestBody(data);		
		try {
			client.executeMethod(method);	
			
			String SubmitResult =method.getResponseBodyAsString();
					
			//System.out.println(SubmitResult);

			Document doc = DocumentHelper.parseText(SubmitResult); 
			Element root = doc.getRootElement();


			String code = root.elementText("code");	
			String msg = root.elementText("msg");	
			String smsid = root.elementText("smsid");	
			
			
			System.out.println(code);
			System.out.println(msg);
			System.out.println(smsid);
						
			if(code.equals("2")){
				return 1;
			}else {
				return 0;
			}
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return 0;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			return 0;
		}
	}*/
}
