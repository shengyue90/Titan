package db.mongodb;

import java.util.*;

import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.ExternalAPI;
import external.ExternalAPIFactory;

public class MongoDBConnection implements DBConnection {
	private static MongoDBConnection instance;
	private MongoClient mongoClient;
	private MongoDatabase db;
	
	public static DBConnection getInstance() {
		if (instance == null) {
			instance = new MongoDBConnection();
		}
		return instance;
	}
	
	private  MongoDBConnection() {
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}
	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		db.getCollection("users").updateOne(new Document().append("user_id", userId), 
				new Document().append("$push", new Document().append("favorite", 
						new Document().append("$each", itemIds))));
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		db.getCollection("users").updateOne(new Document().append("user_id", userId),
						new Document().append("$pullAll", new Document().append("favorite", itemIds))); 

	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		Set<String> favorites = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		if (iterable.first().containsKey("favorite")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("favorite");
			favorites.addAll(list);
		}
		return favorites;
		
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		Set<String> favoriteIds = getFavoriteItemIds(userId);
		Set<Item> favoriteItems = new HashSet<>();
		for (String itemId : favoriteIds) {
			FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));
			ItemBuilder builder = new ItemBuilder();
			Document doc = iterable.first();
			builder.setItemId(doc.getString("item_id"));
			builder.setName(doc.getString("name"));
			builder.setCity(doc.getString("city"));
			builder.setState(doc.getString("state"));
			builder.setCountry(doc.getString("country"));
			builder.setZipcode(doc.getString("zipcode"));
			builder.setRating(doc.getDouble("rating"));
			builder.setAddress(doc.getString("address"));
			builder.setLatitude(doc.getDouble("latitude"));
			builder.setLongitude(doc.getDouble("longitude"));
			builder.setDescription(doc.getString("description"));
			builder.setSnippet(doc.getString("snippet"));
			builder.setSnippetUrl(doc.getString("snippet_url"));
			builder.setImageUrl(doc.getString("image_url"));
			builder.setUrl(doc.getString("url"));
			favoriteItems.add(builder.build());
		}
		return favoriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		Set<String> categories = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));
		if (iterable.first().containsKey("categories")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("categories"); 
			categories.addAll(list);
		}
		return categories;
	}

	@Override
	public List<Item> searchItems(String userId, double lat, double lon, String term) {
		ExternalAPI api = ExternalAPIFactory.getExternalAPI();
		List<Item> items = api.search(lat, lon, term);
		for (Item item : items) {
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
		UpdateOptions options = new UpdateOptions().upsert(true);
		db.getCollection("items").updateOne(new Document().append("item_id", item.getItemId()),
				new Document("$set",
						new Document().append("item_id", item.getItemId()).append("name", item.getName())
								.append("city", item.getCity()).append("state", item.getState())
								.append("country", item.getCountry()).append("zip_code", item.getZipcode())
								.append("rating", item.getRating()).append("address", item.getAddress())
								.append("latitude", item.getLatitude()).append("longitude", item.getLongitude())
								.append("description", item.getDescription()).append("snippet", item.getSnippet())
								.append("snippet_url", item.getSnippetUrl()).append("image_url", item.getImageUrl())
								.append("url", item.getUrl()).append("categories", item.getCategories())), options);
		

	}

}
