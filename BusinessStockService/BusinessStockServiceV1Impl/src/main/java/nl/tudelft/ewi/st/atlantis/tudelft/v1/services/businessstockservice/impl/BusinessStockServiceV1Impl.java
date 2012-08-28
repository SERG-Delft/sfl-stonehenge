
package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessstockservice.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.tudelft.ewi.st.atlantis.tudelft.intf.BusinessStockServiceV1;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetAllQuotesRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetAllQuotesResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetClosedOrdersRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetClosedOrdersResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetHoldingRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetHoldingResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetHoldingsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetHoldingsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetMarketSummaryRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetMarketSummaryResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetOrdersRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetOrdersResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetQuoteRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetQuoteResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetTopOrdersRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetTopOrdersResponse;
import nl.tudelft.stocktrader.Holding;
import nl.tudelft.stocktrader.MarketSummary;
import nl.tudelft.stocktrader.Order;
import nl.tudelft.stocktrader.Quote;
import nl.tudelft.stocktrader.TypeFactory;
import nl.tudelft.stocktrader.dal.DAOException;

//modified by kathy: logger.debug --> logger.error

public class BusinessStockServiceV1Impl
    implements BusinessStockServiceV1
{

	private final TraderStockServiceManager mgr = new TraderStockServiceManager();
	private static final Log logger = LogFactory.getLog(BusinessStockServiceV1Impl.class);
	
    public GetQuoteResponse getQuote(GetQuoteRequest request) {
    	Quote q = null;
    	
    	try {
			q = mgr.getQuote(request.getSymbol());
//		} catch (DAOException e) {
    	} catch (Exception e) {

			logger.error("BusinessStockServiceV1Impl.getQuote ", e);
		}
    	
		GetQuoteResponse response = new GetQuoteResponse();
		if(q!=null)
			response.setQuoteData(TypeFactory.toQuoteData(q));
		
		return response;
    }

    public GetMarketSummaryResponse getMarketSummary(GetMarketSummaryRequest param0) {
    	MarketSummary summary = null;
    	
    	try {
			summary = mgr.getMarketSummary();
//		} catch (DAOException e) {
    	} catch (Exception e) {

			logger.error("BusinessStockServiceV1Impl.getMarketSummary ", e);
		}
    	
    	GetMarketSummaryResponse response = new GetMarketSummaryResponse();
    	if(summary!=null)
    		response.setMarketSummaryData(TypeFactory.toMarketSummaryData(summary));
    	
    	return response;
    }



    public GetHoldingResponse getHolding(GetHoldingRequest request) {
        Holding h = null;
        try {
			h = mgr.getHolding(request.getUserID(),request.getHoldingID());
//		} catch (DAOException e) {
    	} catch (Exception e) {

			logger.error("BusinessStockServiceV1Impl.getHolding ", e);
		}
		
		GetHoldingResponse response = new GetHoldingResponse();
		if(h!=null)
			response.setHoldingData(TypeFactory.toHoldingData(h));
		
		return response;
    }


    public GetTopOrdersResponse getTopOrders(GetTopOrdersRequest request) {
        List<Order> topOrders = null;
        
        try {
			topOrders = mgr.getTopOrders(request.getUserID());
//		} catch (DAOException e) {
    	} catch (Exception e) {

			logger.error("BusinessStockServiceV1Impl.getTopOrders ", e);
		}
		
		GetTopOrdersResponse response = new GetTopOrdersResponse();
		if(topOrders!=null)
			response.getOrderData().addAll(TypeFactory.toListOrderData(topOrders));
		
		return response;
    }


    public GetOrdersResponse getOrders(GetOrdersRequest request) {
    	List<Order> orders = null;
    	
    	try {
			orders = mgr.getOrders(request.getUserID());
//		} catch (DAOException e) {
    	} catch (Exception e) {

			logger.error("BusinessStockServiceV1Impl.getOrders ", e);
		}
		
		GetOrdersResponse response = new GetOrdersResponse();
		if(orders!=null)
			response.getOrderData().addAll(TypeFactory.toListOrderData(orders));
		
		return response;
    }

    public GetHoldingsResponse getHoldings(GetHoldingsRequest request) {
    	List<Holding> holdings = null;
    	
    	try {
			holdings = mgr.getHoldings(request.getUserID());
//		} catch (DAOException e) {
    	} catch (Exception e) {

			logger.error("BusinessStockServiceV1Impl.getHoldings ", e);
		}
		
		GetHoldingsResponse response = new GetHoldingsResponse();
		if(holdings!=null)
			response.getHoldingData().addAll(TypeFactory.toListHoldingData(holdings));
		
		return response;
    }

    public GetClosedOrdersResponse getClosedOrders(GetClosedOrdersRequest request) {
        List<Order> orders = null;
        
        try {
			orders = mgr.getCompletedOrders(request.getUserID());
//		} catch (DAOException e) {
    	} catch (Exception e) {

			logger.error("BusinessStockServiceV1Impl.getClosedOrders ", e);
		}
		
		GetClosedOrdersResponse response = new GetClosedOrdersResponse();
		if(orders!=null)
			response.getOrderData().addAll(TypeFactory.toListOrderData(orders));
		
		return response;
    }


	public GetAllQuotesResponse getAllQuotes(GetAllQuotesRequest getAllQuotesRequest) {
		List<Quote> quotes = null;
		try {
			quotes = mgr.getAllQuotes();
//		} catch (DAOException e) {
    	} catch (Exception e) {

			logger.error("BusinessStockServiceV1Impl.getAllQuotes ",e);
		}
		
		GetAllQuotesResponse response = new GetAllQuotesResponse();
		if(quotes!=null)
			response.getQuotes().addAll(TypeFactory.toListQuoteData(quotes));

		return response;
	}



}
