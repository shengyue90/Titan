package algorithm;

import java.util.*;
import entity.*;
import db.*;
public class GeoRecommendation implements Recommendation {
	/**
	 * Rcommendation based on geo distance and similar categories
	 */
	
	@Override
	public List<Item> recommendItems(String userId, double latitude, double longitude) {
		
		DBConnection conn = DBConnectionFactory.getDBConnection();
		//step 1: get favorite categories by userId
		Set<String> favoriteItems = conn.getFavoriteItemIds(userId);
		Set<String> allCategories = new HashSet<>();
		for (String itemId : favoriteItems) {
			allCategories.addAll(conn.getCategories(itemId));
		}
		//step 2: use external api to search items
		Set<Item> recommendedItems = new HashSet<>();
		for (String category : allCategories) {
			recommendedItems.addAll(conn.searchItems(userId, latitude, longitude, category));
		}
		//step 3: delete those items that already favorite
		List<Item> filteredItems = new ArrayList<>();
		for (Item item : recommendedItems) {
			if (!favoriteItems.contains(item.getItemId())) {
				filteredItems.add(item);
			}
		}
		//step 4: sorting the items based on distance
		Collections.sort(filteredItems, new Comparator<Item>(){
			@Override
			public int compare(Item a, Item b) {
				return (int) (getDistance(a.getLatitude(), a.getLongitude(), latitude, longitude)
				- getDistance(b.getLatitude(), b.getLongitude(), latitude, longitude));
			}
		});
		return filteredItems;
	}
	private static double getDistance(double lat1, double lon1, double lat2, double lon2) {
		double R = 3959;//radius of Earth in mile
		double angle = Math.acos(Math.cos(lat1 / 180 * Math.PI) * Math.cos(lat2 / 180 * Math.PI)
				* Math.cos((lon2 - lon1) / 180 * Math.PI) 
				+ Math.sin(lat1 / 180 * Math.PI) * Math.sin(lat2 / 180 * Math.PI));
		return R * angle;
	}
}
