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

package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessopservice.impl;


import java.net.URL;

import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.configurationservice.SharedConfigurationServiceV1Consumer;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.orderprocessorservice.SharedOrderProcessorServiceV1Consumer;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetOPSLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetOPSLocationsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.OrderData;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.SubmitOrderRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.SubmitOrderResponse;

import nl.tudelft.stocktrader.Holding;
import nl.tudelft.stocktrader.Order;
import nl.tudelft.stocktrader.TypeFactory;
import nl.tudelft.stocktrader.dal.DAOException;
import nl.tudelft.stocktrader.dal.DAOFactory;
import nl.tudelft.stocktrader.dal.OrderDAO;
import nl.tudelft.stocktrader.util.StockTraderUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TraderOPServiceManager {

  static {
    //WORKAROUND. TO BE REMOVED.
    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
      new javax.net.ssl.HostnameVerifier(){
        public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
            return true;
        }});
      
  }
                    
	private static final Log logger = LogFactory
			.getLog(TraderOPServiceManager.class);

	private DAOFactory factory = null;

  public TraderOPServiceManager() {
		factory = DAOFactory.getFactory();
	}

	
	
	public OrderData buy(String userID, String symbol, double quantity, String currency)
			throws Exception {
		return placeOrder(StockTraderUtility.ORDER_TYPE_BUY, userID, 0, symbol,
				quantity, currency);
	}

	public OrderData sell(String userID, int holdingID,
			int orderProcessingMode, String currency) throws Exception {
		return placeOrder(StockTraderUtility.ORDER_TYPE_SELL, userID,
				holdingID, null, 0, currency);// why is the quantity "0"?
	}

	public OrderData sellEnhanced(String userID, int holdingID,
			double quantity, String currency) throws Exception {
		return placeOrder(StockTraderUtility.ORDER_TYPE_SELL_ENHANCED, userID,
				holdingID, null, quantity, currency);
	}

	private OrderData placeOrder(String orderType, String userID,
			int holdingID, String symbol, double quantity, String currency) throws Exception {
		
	  	
	  System.setProperty("java.net.debug", "ssl,handshake");	
	  	
		OrderDAO orderDAO = factory.getOrderDAO();
		Order order = null;
		Holding holding = new Holding();
//		try {

//			orderDAO.beginTransaction(); // what does it do? it is an empty interface.......
			
			order = createOrder(orderType, userID, holdingID, symbol, quantity,
					holding, currency);
			if(order!=null){
				
				SharedConfigurationServiceV1Consumer configConsumer = new SharedConfigurationServiceV1Consumer("BusinessOPService","production");

				GetOPSLocationsResponse getOPSResponse = configConsumer.getOPSLocations( new GetOPSLocationsRequest());
				
				if(getOPSResponse.getLocations().size()==0){
//					System.out.println("Unable to locate exchange check service");
					return null;
				}
				
				String opsEndpoint = getOPSResponse.getLocations().get(0).getServiceURL();
				

				SharedOrderProcessorServiceV1Consumer asynClient = new SharedOrderProcessorServiceV1Consumer("BusinessOPService","production");
				asynClient.getService().setServiceLocation(new URL(opsEndpoint));
				
				SubmitOrderRequest request = new SubmitOrderRequest();
				request.setOrderData(TypeFactory.toOrderData(order));

//				asynClient.submitOrderAsync(request);
				SubmitOrderResponse subResponse = asynClient.submitOrder(request);
				OrderData updatedOrderData = subResponse.getUpdatedOrder();
//				orderDAO.commitTransaction();
				
				return updatedOrderData;
				
				
			}
					
			return null;
//		} 
//		catch (Exception e) {
//			try {
//				System.out.println("the content of exception here: " + e.getMessage());
//				orderDAO.rollbackTransaction();
//			} catch (DAOException e2) {
//				throw e2;
//			}
//            logger.error(e);
//			throw new RuntimeException(e);
//		}
	}

	private Order createOrder(String orderType, String userID,
			int holdingID, String symbol, double quantity,
			Holding holding, String currency) throws DAOException {
		Order order = null;
		OrderDAO orderDAO = factory.getOrderDAO();

		if (StockTraderUtility.ORDER_TYPE_SELL.equals(orderType)) {
			// CHECKME holding is the argument
			holding = orderDAO.getHolding(holdingID);
			if (holding == null) {
//				throw new DAOException("No holding entry found for HoldingID<"
//						+ holdingID + ">");
				return null;
			}
			order = orderDAO.createOrder(userID, holding.getQuoteId(),
					StockTraderUtility.ORDER_TYPE_SELL, holding.getQuantity(),
					holdingID, currency);
		} 
		
		
		
		else if (StockTraderUtility.ORDER_TYPE_SELL_ENHANCED
				.equals(orderType)) {
			holding = orderDAO.getHolding(holdingID);
			if (holding == null) {
//				throw new DAOException("No holding entry found for HoldingID<"
//						+ holdingID + ">");
				return null;
			}
			if (quantity > holding.getQuantity()) {
				order = orderDAO.createOrder(userID, holding.getQuoteId(),
						StockTraderUtility.ORDER_TYPE_SELL, holding
								.getQuantity(), holdingID, currency);
			} else {
				order = orderDAO
						.createOrder(userID, holding.getQuoteId(),
								StockTraderUtility.ORDER_TYPE_SELL, quantity,
								holdingID, currency);
			}
		} 
		
		// if the holdingID is -1, it means to BUY, 
		
		else if (StockTraderUtility.ORDER_TYPE_BUY.equals(orderType)) {
			order = orderDAO.createOrder(userID, symbol,
					StockTraderUtility.ORDER_TYPE_BUY, quantity, -1, currency);
		} else {
			throw new IllegalArgumentException("Invalid orderType<" + orderType
					+ ">");
		}
		return order;
	}
}
