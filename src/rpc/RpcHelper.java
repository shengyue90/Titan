package rpc;

import java.io.*;
import javax.servlet.http.*;
import org.json.*;
import java.util.*;
import entity.Item;
public class RpcHelper {
	// read a http request and return the content as a json obj
	public static JSONObject readJSONObject(HttpServletRequest request) {
		try {
			// here the request is of json type
			StringBuffer sb = new StringBuffer();
			String line = null;
			// string buffer is thread-safe while stringbuilder is not.So
			// stringbuilder is faster
			BufferedReader reader = request.getReader();//getReader return the body of the request
			//doGet has no body?
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			return new JSONObject(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// write a json obj to a response
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) {
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			out.print(obj);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// write a json array to a response
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) {
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			out.print(array);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	public static JSONArray getJSONArray(List<Item>items) {
		JSONArray res = new JSONArray();
		for (Item item : items) {
			res.put(item.toJSONObject());
		}
		return res;
	}*/
}
