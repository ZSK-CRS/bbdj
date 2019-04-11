package com.mt.bbdj.baseconfig.utls;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConvertJson {

	/**
	 * 对象转换为Json
	 *
	 * @param obj
	 * @return
	 */
	public static String object2json(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String || obj instanceof Integer || obj instanceof Float || obj instanceof Boolean || obj instanceof Short || obj instanceof Double || obj instanceof Long || obj instanceof BigDecimal || obj instanceof BigInteger || obj instanceof Byte) {
			json.append("\"").append(string2json(obj.toString())).append("\"");
		} else if (obj instanceof Object[]) {
			json.append(array2json((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(list2json((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(map2json((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(set2json((Set<?>) obj));
		}
		return json.toString();
	}

	/**
	 * List集合转换为Json
	 *
	 * @param list
	 * @return
	 */
	public static String list2json(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * List集合转换为Json
	 *
	 * @param list
	 * @return
	 */

	public static String listMapjson(List<HashMap<String, String>> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Map obj : list) {
				json.append(map2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * 对象数组转换为Json
	 *
	 * @param array
	 * @return
	 */
	public static String array2json(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * Map集合转换为Json
	 *
	 * @param map
	 * @return
	 */
	public static String map2json(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(object2json(key));
				json.append(":");
				json.append(object2json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * Set集合转为Json
	 *
	 * @param set
	 * @return
	 */
	public static String set2json(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * 字符串转换为Json
	 *
	 * @param s
	 * @return
	 */
	public static String string2json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
				case '"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '/':
					sb.append("\\/");
					break;
				default:
					if (ch >= '\u0000' && ch <= '\u001F') {
						String ss = Integer.toHexString(ch);
						sb.append("\\u");
						for (int k = 0; k < 4 - ss.length(); k++) {
							sb.append('0');
						}
						sb.append(ss.toUpperCase());
					} else {
						sb.append(ch);
					}
			}
		}
		return sb.toString();
	}

	public static String sort(String[] str) {
		for (int i = 0; i < str.length - 1; i++) {
			String maxStr = str[i];
			int index = i;
			for (int j = i + 1; j < str.length; j++) {
				if (maxStr.compareTo(str[j]) > 0) {
					maxStr = str[j];
					index = j;
				}
			}
			str[index] = str[i];
			str[i] = maxStr;
		}
		String answer = "";
		for (int i = 0; i < str.length; i++) {
			answer += str[i]+",";
		}
		return answer;
	}
	public static String simpleMapToJsonStr2(Map<String ,String > map){
		if(map==null||map.isEmpty()){
			return "null";
		}
		String jsonStr = "{";
		Set<?> keySet = map.keySet();
		for (Object key : keySet) {

				jsonStr += "\""+key+"\":\""+map.get(key)+"\",";


		}
		jsonStr = jsonStr.substring(0,jsonStr.length()-1);
		jsonStr += ",";
		return jsonStr;
	}

	public static String simpleMapToJsonStr(Map<String ,String > map){
		if(map==null||map.isEmpty()){
			return "null";
		}
		String jsonStr = "{";
		Set<?> keySet = map.keySet();
		for (Object key : keySet) {
			if("verification_list_handle".equals(key)  ||"case_handle".equals(key)||"pic_data".equals(key)  ||"picture".equals(key)  || "ciginfo".equals(key) || "smokesdata".equals(key)|| "smokesdetails".equals(key) || "distribute_data".equals(key)){
				jsonStr += "\"" + key + "\":" + map.get(key)+",";
			}else {
				jsonStr += "\"" + key + "\":\"" + map.get(key) + "\",";
			}
		}
		jsonStr = jsonStr.substring(0,jsonStr.length()-1);
		jsonStr += "}";
		return jsonStr;
	}

	//{"pass":"4355","name":"12342","wang":"fsf"}
	public Map getData(String str){
		String sb = str.substring(1, str.length()-1);
		String[] name =  sb.split("\\\",\\\"");
		String[] nn =null;
		Map map = new HashMap();
		for(int i= 0;i<name.length; i++){
			nn = name[i].split("\\\":\\\"");
			map.put(nn[0], nn[1]);
		}
		return map;
	}
	//map转换为json字符串
	public static String hashMapToJson(HashMap map) {
		String string = "{";
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry e = (Map.Entry) it.next();
			string += "'" + e.getKey() + "':";
			string += "'" + e.getValue() + "',";
		}
		string = string.substring(0, string.lastIndexOf(","));
		string += "}";
		return string;
	}
}