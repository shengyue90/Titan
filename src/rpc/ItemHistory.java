package rpc;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.*;
import db.*;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
// this class is responsible for doPost, client submit data to backend
@WebServlet("/history")
// to handle the request that associates a user to an event
// like the client asks to retrieve the user's favorites
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private DBConnection conn = DBConnectionFactory.getDBConnection();
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			
			String userId = request.getParameter("user_id");
			Set<Item> favorite = conn.getFavoriteItems(userId);
			JSONArray array = new JSONArray();
			for (Item item : favorite) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", true);
				array.put(obj);
			}
			RpcHelper.writeJsonArray(response, array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			JSONArray array = (JSONArray) input.get("favorite");
			List<String> histories = new ArrayList<String>();
			for (int i = 0; i < array.length(); i++) {
				histories.add((String) array.get(i));
			}
			conn.setFavoriteItems(userId, histories);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	protected void doDelete (HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			JSONArray array = (JSONArray) input.get("favorite");
			List<String> histories = new ArrayList<String>();
			for (int i = 0; i < array.length(); i++) {
				histories.add((String) array.get(i)); 
			}
			conn.unsetFavoriteItems(userId, histories);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}

}
