package rpc;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import external.*;
import entity.*;
import db.*;

/**
 * Servlet implementation class SearchItem
 */
//servelet is a java class to respond to a particular type of request
//servelet runs in a servelet container.
//Tomcat is a servlet container, that transfer the java servlet into requests and resopnses
//that server can read.

//this class is responsible for doGet, client request data from backend
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private DBConnection conn = DBConnectionFactory.getDBConnection();
	//we set db connection here
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		Double lat = Double.parseDouble(request.getParameter("lat"));
		Double lon = Double.parseDouble(request.getParameter("lon"));
		String term = request.getParameter("term");
		
		List<Item> item_list = conn.searchItems(userId, lat, lon, term);
		Set<String> favorite = conn.getFavoriteItemIds(userId);
		List<JSONObject> json_list = new ArrayList<>();
		try {
			for (Item item : item_list) {
				json_list.add(item.toJSONObject().put("favorite", favorite.contains(item.getItemId())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray jArray = new JSONArray(json_list);
		RpcHelper.writeJsonArray(response, jArray);
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
