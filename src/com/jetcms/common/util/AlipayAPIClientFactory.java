/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.jetcms.common.util;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;


/**
 * API调用客户端工厂
 */
public class AlipayAPIClientFactory {

    /** API调用客户端 */
    private static AlipayClient alipayClient;
    
    /**
     * 获得API调用客户端
     * 
     * @return
     */
    public static AlipayClient getAlipayClient(String url,String appId,
    		String privateKey,String publicKey,String charset){
        
        if(null == alipayClient){ 
        	/*url = "https://openapi.alipaydev.com/gateway.do";
        	appId = "2016080500171239";
        	  privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIzDZYvrwDDlH9WtJgO5cC3YmsAMSPIi15HRTqNq8hWB4qUFWKHpD5N4V/ily6vuYTnWrwvbH2tozeoH/mf5fsRSEy5NjBkcntqCivj8zMAXTWY6ZE57aE52VKi3BHaMwHhd2DFkSF6fCHrmuldTyGY9Pbn7Fg8dQsZAebHXc8YXAgMBAAECgYBYlwkr3H2iyDEW69hG88hUMkSCpbirG56MWSyi0MX3Hz2jGhBEJkf1SgIjrUTXujlai3Czh37ovJcLuWg1y+IpeiRS09XBQQnVRnWFrLXKU7d5v6kayx9AMPXQsWXHn6oHge0h9UuzMUlOX1vAa4usn3H+i2o8a7/mPBddb+3/8QJBAPM33Pw4l/xF6WLVy0WOGPDLQ97hPkmcTnnGuARx0roH8m7eAtCFfSXSlfY0UouRc65Kzlvc3PebpZ8NoG9xfvsCQQCUKSfU1E+K6Lk2YcXKPY3a6ZPoUswkrPu6OuKDld+b8WTNeECccLbD+Q8HbOIQ/k3wN0SqmgIoQfV+iw344DqVAkA6sDIs0mMqRpxocvpX0FAIWqQH9gYXsRXmoKLqv6R/q0c+pWe8dVmbdNMm071PXEuztO1SkVrojLBMTvd1rMddAkBMvqC2Op9VKcU+aRjF8Gp5WYJW1gSOftgJmBTa6hggs8JuN/rnze6txmXFh931xXRjmI1F/W5eogPi326GWsedAkA1OVcNIZlUQbwfgQo3oGgfkYQTnJPL9hffRI7ikVfdFazfFBQHsaG0+i8NWmcJri5RY2ldWjgb/TOkK4qeb8V1";
        	  publicKey ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB";
        	  */alipayClient = new DefaultAlipayClient(url, appId, 
            		privateKey, "json",charset,publicKey,"RSA");
        }
        return alipayClient;
    }
}
