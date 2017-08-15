package db;

import java.util.*;

import entity.Item;

public interface DBConnection {
	/**
	 * Close the connection
	 */
	public void close();
	
	/**
	 * Insert the favorite items for a user
	 * @param userId
	 * @param itemIds
	 */
	public void setFavoriteItems(String userId, List<String> itemIds);
	
	/**
	 * Delete the favorite items for a user
	 * @param userId
	 * @param itemIds
	 */
	public void unsetFavoriteItems(String userId, List<String> itemIds);
	
	/**
	 * Get the favorite items for a user
	 * @param userId
	 * @return itemIds
	 */
	public Set<String> getFavoriteItemIds(String userId);
	
	/**
	 * Get the favorite items for a user
	 * @param userId
	 * @return items
	 */
	public Set<Item> getFavoriteItems(String userId);
	
	/**
	 * Get categories based on item id
	 * @param itemId
	 * @return set of categories
	 */
	public Set<String> getCategories(String itemId) ;
	
	/**
	 * Search items near a geolocation and a term (optional)
	 * @param userId
	 * @param lat
	 * @param lon
	 * @param term (Nullable)
	 * @return list of items
	 */
	public List<Item> searchItems(String userId, double lat, double lon, String term);
	
	/**
	 * Save item to db
	 * @param item
	 */
	public void saveItem(Item item);
	
	//saveItem and searchItem here is used to save the user's searching history
	// and use the history to do recommendation
	
}
