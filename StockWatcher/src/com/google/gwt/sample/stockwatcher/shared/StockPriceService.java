package com.google.gwt.sample.stockwatcher.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("stockPrices")
public interface StockPriceService extends RemoteService {
	StockPrice[] getPrices(ArrayList<String> symbols);
}
