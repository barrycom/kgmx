package com.jetcms.common.util; 
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

public class SendPhoneUtils {
	
	private final static String serverUrl = "http://gw.api.taobao.com/router/rest";
	
	private final static String appKey = "24494301";
	
	private final static String appSecret = "116cb28ebb969a65e4b45a03b866a49c";
	
	/**
	 * 阿里发送短信
	 * 
	 * @param mobile
	 * @param code
	 * @throws IOException 
	 * @throws DocumentException
	 */
	public static void sendPhone(String mobile, String code){
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend("123456");
		req.setSmsType("normal");
		req.setSmsFreeSignName("蜜蜂");
		req.setSmsParamString("{\"code\":\""+code+"\"}");
		req.setRecNum(mobile);
		req.setSmsTemplateCode("SMS_71020221");
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = client.execute(req);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(rsp.getBody());
	}
	
	public static void sendUserMessage(String mobile, String smsTemplateCode){
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend("123456");
		req.setSmsType("normal");
		req.setSmsFreeSignName("蜜蜂");
		req.setSmsParamString("");
		req.setRecNum(mobile);
		req.setSmsTemplateCode(smsTemplateCode);
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = client.execute(req);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(rsp.getBody());
	}
	
	public static void sendPlatformMessage(String mobile,String smsParam, String smsTemplateCode){
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend("123456");
		req.setSmsType("normal");
		req.setSmsFreeSignName("嗑股明星");
		//req.setSmsParamString("{\"code\":\"123456\",\"product\":\"嗑股明星\"}");
		req.setSmsParamString(smsParam);
		req.setRecNum(mobile);
		req.setSmsTemplateCode(smsTemplateCode);
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = client.execute(req);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(rsp.getBody());
	}
	
	public static void main(String[] args) {
		sendPlatformMessage("13554235476", "{'code':'123456','product':'嗑股明星'}","SMS_71810278");
		//sendPlatformMessage("18086302679", "SMS_71090179");
	}
}
