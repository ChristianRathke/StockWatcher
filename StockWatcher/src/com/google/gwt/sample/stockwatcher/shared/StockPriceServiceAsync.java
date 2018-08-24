package com.google.gwt.sample.stockwatcher.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StockPriceServiceAsync {

	void getPrices(ArrayList<String> symbols, AsyncCallback<StockPrice[]> callback);

}
