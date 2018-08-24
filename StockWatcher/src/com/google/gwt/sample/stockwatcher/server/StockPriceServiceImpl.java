package com.google.gwt.sample.stockwatcher.server;

import java.util.ArrayList;
import java.util.Random;

import com.google.gwt.sample.stockwatcher.shared.StockPrice;
import com.google.gwt.sample.stockwatcher.shared.StockPriceService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class StockPriceServiceImpl extends RemoteServiceServlet implements
		StockPriceService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final double MAX_PRICE = 100.0; // $100.00
	private static final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

	@Override
	public StockPrice[] getPrices(ArrayList<String> symbols) {
		Random rnd = new Random();

		StockPrice[] prices = new StockPrice[symbols.size()];
		for (int i = 0; i < symbols.size(); i++) {
			double price = rnd.nextDouble() * MAX_PRICE;
			double change = price * MAX_PRICE_CHANGE
					* (rnd.nextDouble() * 2f - 1f);

			prices[i] = new StockPrice(symbols.get(i), price, change);
		}

		return prices;
	}
}
