package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import external.ExternalAPI;
import external.ExternalAPIFactory;
import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;

public class MySQLConnection implements DBConnection {
	// this is a singleton pattern
	private static MySQLConnection instance;
	private Connection conn = null;

	private MySQLConnection() {
		// it is private because this is a singleton pattern
		// we don't want client to create any object using the constructor
		try {
			// Forcing the class representing the MySQL driver to load and
			// initialize
			// https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-usagenotes-connect-drivermanager.html
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// = new com.mysql.jdbc.Driver()
			// forName --> used to initiallize a class

			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DBConnection getInstance() {
		if (instance == null) {
			instance = new MySQLConnection();
		}
		return instance;
	}

	@Override
	public void close() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		String sql = "INSERT INTO history (user_id, item_id) VALUES (?, ?)";
		try{
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String item : itemIds) {
				stmt.setString(1, userId);
				stmt.setString(2, item);
				stmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		String sql = "DELETE FROM history WHERE user_id=? AND item_id=?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String item : itemIds) {
				stmt.setString(1,  userId);
				stmt.setString(2, item);
				stmt.execute();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		Set<String> favoriteItems = new HashSet<String>();
		try {
			String sql = "SELECT item_id FROM history WHERE user_id=" + userId;
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				String itemId = result.getString("item_id");
				favoriteItems.add(itemId);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		Set<String>items = getFavoriteItemIds(userId);
		Set<Item> favItems = new HashSet<Item>();
		try {
			String sql_item = "SELECT * From items WHERE item_id=?";
			PreparedStatement stmt_item = conn.prepareStatement(sql_item);
			String sql_category = "SELECT * FROM categories WHERE item_id=?";
			PreparedStatement stmt_category = conn.prepareStatement(sql_category);
			
			for (String item : items) {
				stmt_item.setString(1, item);
				ResultSet rs = stmt_item.executeQuery();
				ItemBuilder builder = new ItemBuilder();
				//for 1 item_id, there should return 1 item each time
				if (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setCity(rs.getString("city"));
					builder.setState(rs.getString("state"));
					builder.setCountry(rs.getString("country"));
					builder.setZipcode(rs.getString("zipcode"));
					builder.setRating(rs.getDouble("rating"));
					builder.setAddress(rs.getString("address"));
					builder.setLatitude(rs.getDouble("latitude"));
					builder.setLongitude(rs.getDouble("longitude"));
					builder.setDescription(rs.getString("description"));
					builder.setSnippet(rs.getString("snippet"));
					builder.setSnippetUrl(rs.getString("snippet_url"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
				}
				//set categories
				Set<String> categories = new HashSet<>();
				stmt_category.setString(1, item);
				ResultSet rs_category = stmt_category.executeQuery();
				while(rs_category.next()) {
					categories.add(rs_category.getString("category"));
				}
				builder.setCategories(categories);
				favItems.add(builder.build());
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favItems;
		
	}
	
	@Override
	public Set<String> getCategories(String itemId) {
		Set<String> categories = new HashSet<>();
		try {
			String sql = "SELECT category FROM categories WHERE item_id=?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, itemId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				categories.add(rs.getString("category"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categories;
	}

	@Override
	public List<Item> searchItems(String userId, double lat, double lon, String term) {
		// move here: using external api to do search
		ExternalAPI api = ExternalAPIFactory.getExternalAPI();
		List<Item> list = api.search(lat, lon, term);
		for (Item item : list) {
			saveItem(item);
		}
		return list;
	}

	@Override
	public void saveItem(Item item) {
		try {
			String sql = "INSERT IGNORE INTO items VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			// INSERT IGNORE INTO --> insert into a table with primary key,
			// avoid the duplication of
			// primary key. if the new row has duplicate primary key of rows in
			// the table
			// the new row won't be inserted
			//first update item table
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, item.getItemId());
			stmt.setString(2, item.getName());
			stmt.setString(3, item.getCity());
			stmt.setString(4, item.getState());
			stmt.setString(5, item.getCountry());
			stmt.setString(6, item.getZipcode());
			stmt.setDouble(7, item.getRating());
			stmt.setString(8, item.getAddress());
			stmt.setDouble(9, item.getLatitude());
			stmt.setDouble(10, item.getLongitude());
			stmt.setString(11, item.getDescription());
			stmt.setString(12, item.getSnippet());
			stmt.setString(13, item.getSnippetUrl());
			stmt.setString(14, item.getImageUrl());
			stmt.setString(15, item.getUrl());
			stmt.execute();
			
			//second, update category table
			sql = "INSERT IGNORE INTO categories VALUES(?,?)";
			stmt = conn.prepareStatement(sql);
			for (String category : item.getCategories()) {
				stmt.setString(1, item.getItemId());
				stmt.setString(2, category);
				stmt.execute();
			}
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
