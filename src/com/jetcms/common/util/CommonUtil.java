package com.jetcms.common.util;

 
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 


/**
 * 基础公共方法
 * 
 * @author 
 * @version 1.0 2013-8-8
 * @since 1.0
 */
public class CommonUtil {

//	private static SimpleLogger logger = SimpleLogger.getLogger(CommonUtil.class);
	private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	/**
	 * 第一个参数为当前的权限值,第二个参数为权限基值 .返回是否有操作权限
	 */
	public static boolean checkPower(long userPurview, long optPurview) {
		long purviewValue = 1 << optPurview;
		return (userPurview & purviewValue) == purviewValue;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0 || "".equals(str.trim());
	}

	public static String replaceHtmlSymbols(String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		return str.trim().replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
	}

	public static String replaceSymbolsHtml(String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		return str.trim().replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"")
				.replaceAll("	", "    ");
	}
	/**
	 * 描述：判斷一個字符串是否是數字
	 * @author jiaxingHuang
	 * @version 1.0 2014年1月20日
	 * @since 1.0
	 */
	public static boolean isNumeric(String str){
		if(str.matches("\\d*"))
			return true;
		return false;
	}
	/***
	 * 判断该字符串是否能转换成数字类型(用户查询条件的check)
	 * 
	 * @param sourceStr
	 * @return
	 */
	public static boolean isIllegalInt(String sourceStr) {
		boolean ret = false;
		if (!(isBlank(sourceStr)) && !(isEmpty(sourceStr))) {
			Pattern p = Pattern.compile("\\d{1,6}");
			Matcher m = p.matcher(sourceStr.toString());
			if (m.matches()) {
				ret = true;
			} else {
				logger.info("can't format Integer:" + sourceStr);
			}
		}
		return ret;
	}
	/***
	 * 判断字符串是否为""
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if ((str == null) || ((strLen = str.length()) == 0)) {
			return true;
		}
		for (int i = 0; i < strLen; ++i) {
			if (!(Character.isWhitespace(str.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 处理加密字符串中的特殊字符
	 */
	public static String replacePlusStr(String str) {
		str = str.replaceAll("\\%", "%25");
		str = str.replaceAll("\\+", "%2B");
		str = str.replaceAll(" ", "%20");
		str = str.replaceAll("\\/", "%2F");
		str = str.replaceAll("\\?", "%3F");
		str = str.replaceAll("\\#", "%23");
		str = str.replaceAll("\\&", "%26");
		str = str.replaceAll("\\=", "%3D");
		return str;
	}

	public static String replaceStrPlus(String str) {
		str = str.replaceAll("%2B", "\\+");
		str = str.replaceAll("%20", " ");
		str = str.replaceAll("%2F", "\\/");
		str = str.replaceAll("%3F", "\\?");
		str = str.replaceAll("%23", "\\#");
		str = str.replaceAll("%26", "\\&");
		str = str.replaceAll("%3D", "\\=");
		str = str.replaceAll("%25", "\\%");
		return str;
	}

	public static double count(double cash, double rate, double year) {
		double trate, P;
		rate = rate / 12;
		trate = rate + (double) 1;
		for (int i = 0; i < year * 12 - 1; i++) {
			trate *= (rate + (double) 1);
		}
		P = (cash * trate * rate) / (trate - (double) 1);
		return P;
	}

	/**
	 * 数字转大写
	 * 
	 * @param money
	 * @return
	 * @throws Exception
	 */
	public static String toChineseCharacter(String moneyIn) {
		String result = "零";
		if (StringUtils.isBlank(moneyIn))
			return result;
		try {
			double money = Double.valueOf(moneyIn);
			double temp = 0;
			long l = Math.abs((long) money);
			BigDecimal bil = new BigDecimal(l);
			if (bil.toString().length() > 14) {
				// "数字太大，计算精度不够!"
				return result;
			}
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			int i = 0;
			String sign = "", tempStr = "", temp1 = "";
			String[] arr = null;
			sign = money < 0 ? "负" : "";
			temp = Math.abs(money);
			if (l == temp) {
				result = doForEach(new BigDecimal(temp).multiply(new BigDecimal(100)).toString(), sign);
			} else {
				nf.setMaximumFractionDigits(2);
				temp1 = nf.format(temp);
				arr = temp1.split(",");
				while (i < arr.length) {
					tempStr += arr[i];
					i++;
				}
				BigDecimal b = new BigDecimal(tempStr);
				b = b.multiply(new BigDecimal(100));
				tempStr = b.toString();
				if (tempStr.indexOf(".") == tempStr.length() - 3) {
					result = doForEach(tempStr.substring(0, tempStr.length() - 3), sign);
				} else {
					result = doForEach(tempStr.substring(0, tempStr.length() - 3) + "0", sign);
				}
			}
		} catch (Exception e) {
			logger.error("toChineseCharacter  Exception", e);
		}
		return result;
	}

	private static String doForEach(String result, String sign) {
		String flag = "", b_string = "";
		String[] arr = { "分", "角", "圆", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾" };
		String[] arr1 = { "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		boolean zero = true;
		int len = 0, i = 0, z_count = 0;
		if (result == null) {
			len = 0;
		} else {
			len = result.length();
		}
		while (i < len) {
			flag = result.substring(i, i + 1);
			i++;
			if (flag.equals("0")) {
				if (len - i == 10 || len - i == 6 || len - i == 2 || len == i) {
					if (zero) {
						b_string = b_string.substring(0, (b_string.length()) - 1);
						zero = false;
					}
					if (len - i == 10) {
						b_string = b_string + "亿";
					}
					if (len - i == 6) {
						b_string = b_string + "万";
					}
					if (len - i == 2) {
						b_string = b_string + "圆";
					}
					if (len == i) {
						b_string = b_string + "整";
					}
					z_count = 0;
				} else {
					if (z_count == 0) {
						b_string = b_string + "零";
						zero = true;
					}
					z_count = z_count + 1;
				}
			} else {
				b_string = b_string + arr1[Integer.parseInt(flag) - 1] + arr[len - i];
				z_count = 0;
				zero = false;
			}
		}
		b_string = sign + b_string;
		return b_string;
	}
	/**
     * 手机号验证
     * 
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str)
    {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

	public static void main(String[] args) {
		double cash = 100000, rate = 0.11, Benxi;
		int year = 1;
		System.out.println("---------------等额本息还款计算--------------");
		System.out.println("本金：" + cash);
		System.out.println("利率：" + rate);
		System.out.println("贷款年限：" + year);
		Benxi = count(cash, rate, year);
		System.out.println("商业性贷款" + cash + "元" + "贷款年限为" + year + ",每月等额 还本付息额为：" + Benxi);
	}
	 public static boolean isChinese(char c) {  
		    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
		    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
		        || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
		        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
		        || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
		        || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
		        || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
		      return true;  
		    }  
		    return false;  
		  } 
	 
	 
	 public static boolean isMessyCode(String strName) {  
		    Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");  
		    Matcher m = p.matcher(strName);  
		    String after = m.replaceAll("");  
		    String temp = after.replaceAll("\\p{P}", "");  
		    char[] ch = temp.trim().toCharArray();  
		    float chLength = ch.length;  
		    float count = 0;  
		    for (int i = 0; i < ch.length; i++) {  
		      char c = ch[i];  
		      if (!Character.isLetterOrDigit(c)) {  
		  
		        if (!isChinese(c)) {  
		          count = count + 1;  
		          System.out.print(c);  
		        }  
		      }  
		    }  
		    float result = count / chLength;  
		    if (result > 0.4) {  
		      return true;  
		    } else {  
		      return false;  
		    }  
		  
		  }  
}
