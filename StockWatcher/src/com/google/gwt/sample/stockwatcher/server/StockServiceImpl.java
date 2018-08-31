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
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public void addStock(String symbol) throws NotLoggedInException {
		Entity user = checkLoggedIn();

		Entity stock = new Entity("Stock", user.getKey());
		stock.setProperty("symbol", symbol);
		stock.setProperty("createDate", new Date());

		datastore.put(stock);
	}

	public void removeStock(String symbol) throws NotLoggedInException {
		Entity user = checkLoggedIn();

		Query q = new Query("Stock").setAncestor(user.getKey());
		q.setFilter(new FilterPredicate("symbol", FilterOperator.EQUAL, symbol));
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();

		if (result!=null) {
			datastore.delete(result.getKey());
		}
	}

	public String[] getStocks() throws NotLoggedInException {
		Entity user = checkLoggedIn();

		List<String> symbols = new ArrayList<String>();
		Query q = new Query("Stock").setAncestor(user.getKey());

		PreparedQuery p = datastore.prepare(q);
		List<Entity> stocks = p.asList(FetchOptions.Builder.withDefaults());

		for (Entity stock : stocks) {
			symbols.add((String) stock.getProperty("symbol"));
		}
		return (String[]) symbols.toArray(new String[0]);
	}

	private Entity checkLoggedIn() throws NotLoggedInException {
		User u = getUser();
		if (u == null) {
			throw new NotLoggedInException("Not logged in.");
		}
		
		Query q = new Query("User").setFilter(new FilterPredicate("email", FilterOperator.EQUAL, u.getEmail()));
		PreparedQuery pq = datastore.prepare(q);
		Entity user = pq.asSingleEntity();

		if (user == null) {
			user = new Entity("User");
			user.setProperty("user", u);
			user.setProperty("email", u.getEmail());
			datastore.put(user);
		}
		return user;
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}
}
