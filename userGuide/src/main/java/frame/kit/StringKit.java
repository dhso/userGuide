package frame.kit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * 
 */
public class StringKit extends StringUtils {

	/**
	 * DateType "yyyy-MM-dd"
	 */
	public final static String DateType = "yyyy-MM-dd";
	/**
	 * DateTimeType "yyyy-MM-dd HH:mm:ss"
	 */
	public final static String DateTimeType = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 邮箱地址正则
	 */
	public final static String emailAddressPattern = "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";

	public static String lowerFirst(String str) {
		if (StringKit.isBlank(str)) {
			return "";
		} else {
			return str.substring(0, 1).toLowerCase() + str.substring(1);
		}
	}

	public static String upperFirst(String str) {
		if (StringKit.isBlank(str)) {
			return "";
		} else {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)) {
			return "";
		}
		String regxpForHtml = "<.*>";
		Pattern pattern = Pattern.compile(regxpForHtml);
		Matcher matcher = pattern.matcher(html);
		StringBuffer sb = new StringBuffer();
		boolean result = matcher.find();
		while (result) {
			matcher.appendReplacement(sb, "");
			result = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * 
	 * @param str
	 *            目标字符串
	 * @param length
	 *            截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 缩略字符串（替换html）
	 * 
	 * @param str
	 *            目标字符串
	 * @param length
	 *            截取长度
	 * @return
	 */
	public static String rabbr(String str, int length) {
		return abbr(replaceHtml(str), length);
	}

	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val) {
		if (val == null) {
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val) {
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val) {
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val) {
		return toLong(val).intValue();
	}

	/**
	 * 转换为Date类型
	 * 
	 * @param val
	 * @param DateType
	 * @return
	 * @throws ParseException
	 */
	public static Date toDate(Object val, String DateType) throws ParseException {
		Date date = null;
		if (null != val && StringKit.isNotEmpty(val.toString())) {
			SimpleDateFormat sdf = new SimpleDateFormat(DateType);
			date = sdf.parse(val.toString());
		}
		return date;
	}

	/**
	 * InputStream转String类型
	 * 
	 * @param is
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 将url参数转换成map
	 * 
	 * @param param
	 * @return
	 */
	public static Map<String, Object> getUrlParams(String param) {
		Map<String, Object> map = new HashMap<String, Object>(0);
		if (StringKit.isBlank(param)) {
			return map;
		}
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String[] p = params[i].split("=");
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}

	/**
	 * 正则表达式
	 * 
	 * @param resource
	 * @param pattern
	 * @return
	 */
	public static String regex(String resource, String pattern) {
		Matcher mat = Pattern.compile(pattern).matcher(resource);
		String result = null;
		while (mat.find()) {
			result = mat.group(1);
		}
		return result;
	}

	/**
	 * 正则校验
	 * 
	 * @param resource
	 * @param pattern
	 * @return
	 */
	public static boolean validator(String resource, String pattern) {
		Pattern pat = Pattern.compile(pattern);
		Matcher mat = pat.matcher(resource);
		if (mat.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> convertStreamToJsonMap(InputStream is) throws JsonParseException, JsonMappingException, IOException {
		String jsonStr = convertStreamToString(is);
		Map<String, String> maps = new ObjectMapper().readValue(jsonStr, Map.class);
		return maps;
	}

	/**
	 * 严格包含对象
	 * 
	 * @param origin
	 * @param object
	 * @return
	 */
	public static boolean hasObject(Object origin, Object... object) {
		boolean result = false;
		for (int i = 0; i < object.length; i++) {
			if (origin.equals(object[i])) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 非严格包含字符串
	 * 
	 * @param origin
	 * @param str
	 * @return
	 */
	public static boolean containStr(String origin, Object... str) {
		boolean result = false;
		for (int i = 0; i < str.length; i++) {
			if (origin.contains(str[i].toString())) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 批量替换
	 * 
	 * @param origin
	 * @param newChar
	 * @param str
	 * @return
	 */
	public static String replaceStrs(String origin, String newChar, Object... str) {
		for (int i = 0; i < str.length; i++) {
			origin = origin.replace(str[i].toString(), newChar);
		}
		return origin;
	}

}