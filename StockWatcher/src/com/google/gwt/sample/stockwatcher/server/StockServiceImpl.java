package com.google.gwt.sample.stockwatcher.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.sample.stockwatcher.shared.NotLoggedInException;
import com.google.gwt.sample.stockwatcher.shared.StockService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class StockServiceImpl extends RemoteServiceServlet implements StockService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void addStock(String symbol) throws NotLoggedInException {
		checkLoggedIn();

		Entity stock = new Entity("Stock", symbol);
		stock.setProperty("user", getUser());
		stock.setProperty("symbol", symbol);
		stock.setProperty("createDate", new Date());

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(stock);
	}

	public void removeStock(String symbol) throws NotLoggedInException {
		checkLoggedIn();
		

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("Stock").setFilter(new FilterPredicate("symbol", FilterOperator.EQUAL, symbol));
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		
		datastore.delete(result.getKey());
	}

	public String[] getStocks() throws NotLoggedInException {
		checkLoggedIn();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		List<String> symbols = new ArrayList<String>();
		Query q = new Query("Stock").setFilter(new FilterPredicate("user", FilterOperator.EQUAL, getUser()));

		PreparedQuery p = datastore.prepare(q);
		List<Entity> stocks = p.asList(FetchOptions.Builder.withDefaults());

		for (Entity stock : stocks) {
			symbols.add((String) stock.getProperty("symbol"));
		}
		return (String[]) symbols.toArray(new String[0]);
	}

	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
}
