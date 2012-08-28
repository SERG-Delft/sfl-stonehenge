/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessstockservice.impl;

import java.util.List;

import nl.tudelft.stocktrader.Holding;
import nl.tudelft.stocktrader.MarketSummary;
import nl.tudelft.stocktrader.Order;
import nl.tudelft.stocktrader.Quote;
import nl.tudelft.stocktrader.dal.CustomerDAO;
import nl.tudelft.stocktrader.dal.DAOException;
import nl.tudelft.stocktrader.dal.DAOFactory;
import nl.tudelft.stocktrader.dal.MarketSummaryDAO;
import nl.tudelft.stocktrader.util.StockTraderUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TraderStockServiceManager {

  static {
    //WORKAROUND. TO BE REMOVED.
    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
      new javax.net.ssl.HostnameVerifier(){
        public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
            return true;
        }});
      
  }
                    
	private static final Log logger = LogFactory
			.getLog(TraderStockServiceManager.class);

	private DAOFactory factory = null;

  public TraderStockServiceManager() {
		factory = DAOFactory.getFactory();
	}


	public List<Order> getOrders(String userId) throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.getOrders(userId, false,
				StockTraderUtility.MAX_QUERY_TOP_ORDERS,
				StockTraderUtility.MAX_QUERY_ORDERS);
	}

	public List<Order> getTopOrders(String userId)
			throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.getOrders(userId, true,
				StockTraderUtility.MAX_QUERY_TOP_ORDERS,
				StockTraderUtility.MAX_QUERY_ORDERS);
	}

	public List<Order> getCompletedOrders(String userId)
			throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.getCompletedOrders(userId);
	}


	public MarketSummary getMarketSummary() throws DAOException {
		MarketSummaryDAO marketSummaryDAO = factory.getMarketSummaryDAO();
		return marketSummaryDAO.getCustomMarketSummary();
	}

	public Quote getQuote(String symbol) throws DAOException {
		MarketSummaryDAO marketSummaryDAO = factory.getMarketSummaryDAO();
		return marketSummaryDAO.getQuote(symbol);
	}

	public List<Quote> getAllQuotes() throws DAOException {
		MarketSummaryDAO marketSummaryDAO = factory.getMarketSummaryDAO();
		return marketSummaryDAO.getAllQuotes();	
	}
	

	public Holding getHolding(String userID, int holdingID)
			throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.getHolding(userID, holdingID);
	}

	public List<Holding> getHoldings(String userID)
			throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.getHoldings(userID);
	}
	


}
