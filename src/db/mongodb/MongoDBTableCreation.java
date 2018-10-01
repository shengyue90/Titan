package db.mongodb;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBTableCreation {

	public static void main(String[] args) {
		// run as java application to create table with index
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
		//Step 1: remove old tables
		db.getCollection("users").drop();
		db.getCollection("items").drop();
		//create new tables, populate data and create index
		db.getCollection("users").insertOne(new Document().append("first_name", "John").append("last_name", "Smith")
	            .append("password", "3229c1097c00d497a0fd282d586be050").append("user_id", "1111"));
		//make sure user_id is unique
		IndexOptions indexOptions = new IndexOptions().unique(true);
		//1 ascending order; -1 descending order
		db.getCollection("users").createIndex(new Document("user_id", 1), indexOptions);
		//make sure item_id is unique in items collection
		db.getCollection("item").createIndex(new Document("item_id", 1), indexOptions);
		// use a compound text index of name, address, etc. for search.
	    db.getCollection("items").createIndex(new Document().append("description", "text")
	        .append("snippet", "text").append("address", "text").append("name", "text"));
	    mongoClient.close();
	    System.out.println("mongodb import is done successfully");

	}

}
