package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterAPI implements ExternalAPI{
	private static final String API_HOST = "app.ticketmaster.com";
	private static final String SEARCH_PATH = "/discovery/v2/events.json";
	private static final String DEFAULT_TERM = "";
	private static final String API_KEY = "rhZwIrR38LBdM6Pkej16riLhhdv9YVz3";
	
	//creates and sends request to the Ticket Master API by term and location
	@Override
	public List<Item> search(double lat, double lon, String term) {
		String url = "http://" + API_HOST + SEARCH_PATH;
		String latlon = lat + "," + lon;
		term = term == null ? DEFAULT_TERM : urlEncodeHelper(term);
		String query = String.format("apikey=%s&latlong=%s&keyword=%s", API_KEY, latlon, term);
		try {
			HttpURLConnection connection = (HttpURLConnection) (new URL(url + "?" + query).openConnection());
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();
			System.out.println(responseCode);
			//extract events only
			JSONObject responseObj = new JSONObject(response.toString());
			JSONObject embedded = (JSONObject) responseObj.getJSONObject("_embedded");
			JSONArray events = (JSONArray) embedded.getJSONArray("events");
			return getItemList(events);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private String urlEncodeHelper(String term) {
		/**
		 * URL encoding is normally performed to convert data passed via html forms, 
		 * because such data may contain special character, such as "/", ".", "#", 
		 * and so on, which could either: a) have special meanings; 
		 * or b) is not a valid character for an URL; 
		 * or c) could be altered during transfer.   
		 * For instance, the "#" character needs to be encoded because it has a special
		 *  meaning of that of an html anchor.   
		 *  The <space> character also needs to be encoded because is not allowed on a 
		 *  valid URL format.   
		 *  Also, some characters, such as "~" might not transport properly across the internet.
		 */
		try {
			term = java.net.URLEncoder.encode(term, "UTF-8");
			//this method would throw UnsuportedEncodingException
		} catch (Exception e){
			e.printStackTrace();
		}
		return term;
	}
	
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList= new ArrayList<Item>();
		for (int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			builder.setItemId(getStringFieldOrNull(event, "id"));
			builder.setName(getStringFieldOrNull(event, "name"));
			builder.setDescription(getDescription(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			builder.setUrl(getStringFieldOrNull(event, "url"));
			JSONObject venue = getVenue(event);
			if (venue != null) {
				if (!venue.isNull("address")) {
					JSONObject address = venue.getJSONObject("address");
					StringBuilder sb = new StringBuilder();
					if (!address.isNull("line1")) {
						sb.append(address.getString("line1"));
					}
					if (!address.isNull("line2")) {
						sb.append(address.getString("line2"));
					}
					if (!address.isNull("line3")) {
						sb.append(address.getString("line3"));
					}
					builder.setAddress(sb.toString());
				}
				if (!venue.isNull("city")) {
					JSONObject city = venue.getJSONObject("city");
					builder.setCity(getStringFieldOrNull(city, "name"));
				}
				if (!venue.isNull("state")) {
					JSONObject state = venue.getJSONObject("state");
					builder.setState(getStringFieldOrNull(state, "name"));
				}
				if (!venue.isNull("country")) {
					JSONObject country = venue.getJSONObject("country");
					builder.setCountry(getStringFieldOrNull(country, "name"));
				}
				builder.setZipcode(getStringFieldOrNull(venue, "postalCode"));
				if (!venue.isNull("location")) {
					JSONObject location = venue.getJSONObject("location");
					builder.setLatitude(getNumericFieldOrNull(location, "latitude"));
					builder.setLongitude(getNumericFieldOrNull(location, "longitude"));
				}
			}
			Item item = builder.build();
			itemList.add(item);
			
		}
		return itemList;
	}
	private JSONObject getVenue(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venue_array = embedded.getJSONArray("venues");
				if (venue_array.length() > 0) {
					return venue_array.getJSONObject(0);
				}
			}
		}
		return null;
	}
	
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray image = event.getJSONArray("images");
			if (image.length() > 0) {
				return getStringFieldOrNull(image.getJSONObject(0), "url");
			}
		}
		return null;
	}
	
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		JSONArray classifications = (JSONArray) event.get("classifications");
		for (int i = 0; i < classifications.length(); i++) {
			JSONObject type = classifications.getJSONObject(i);
			JSONObject segment = type.getJSONObject("segment");
			categories.add(segment.getString("name"));
		}
		return categories;
	}
	private String getDescription(JSONObject event) throws JSONException{
		if (!event.isNull("description")) {
			return event.getString("description");
		} else if (!event.isNull("additionalInfo")) {
			return event.getString("additionalInfo");
		} else if (!event.isNull("info")) {
			return event.getString("info");
		} else if (!event.isNull("pleaseNote")) {
			return event.getString("pleaseNote");
		} else {
			return null;
		}
	}
	
	private String getStringFieldOrNull(JSONObject event, String field) throws JSONException {
		return event.isNull(field) ? null : event.getString(field);
	}
	
	private double getNumericFieldOrNull(JSONObject event, String field) throws JSONException {
		return event.isNull(field) ? 0.0 : event.getDouble(field);
	}
	/*
	private void queryAPI(double lat, double lon) {
		List<Item> itemList = search(lat, lon, null);
		try {
			for (int i = 0; i < itemList.size(); i++) {
				JSONObject aEvent = itemList.get(i).toJSONObject();
				System.out.println(aEvent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		TicketMasterAPI myApi = new TicketMasterAPI();
		myApi.queryAPI(37.38, -122.08);
	}*/
}
