
package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessopservice.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.tudelft.ewi.st.atlantis.tudelft.intf.BusinessOPServiceV1;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.BuyRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.BuyResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.SellEnhancedRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.SellEnhancedResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.SellRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.SellResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.OrderData;
import nl.tudelft.stocktrader.Order;
import nl.tudelft.stocktrader.TypeFactory;
import nl.tudelft.stocktrader.dal.DAOException;


//modified by kathy: logger.debug --> logger.error

public class BusinessOPServiceV1Impl
    implements BusinessOPServiceV1
{

	private final TraderOPServiceManager mgr = new TraderOPServiceManager();
	private static final Log logger = LogFactory.getLog(BusinessOPServiceV1Impl.class);
	
	public BuyResponse buy(BuyRequest request) {
		// TODO Auto-generated method stub
//		Order o = null;
//    	
//    	try {
//			o = mgr.buy(request.getUserID(), request.getSymbol(), request.getQuantity(), request.getUsedCurrency());
//		} catch (DAOException e) {
//    	} catch (Exception e) {
//			logger.error("BusinessOPServiceV1.buy ", e);
//		}
    	
		OrderData orderData = null;
		try {
			orderData = mgr.buy(request.getUserID(), request.getSymbol(), request.getQuantity(), request.getUsedCurrency());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("BusinessOPServiceV1.buy ", e);
		}

    	BuyResponse response = new BuyResponse();
    	response.setOrderData(orderData);
    	
    	return response;
	}
	
	
	public SellEnhancedResponse sellEnhanced(
			SellEnhancedRequest request) {
		// TODO Auto-generated method stub
//		Order o = null;
//    	try {
//			o = mgr.sellEnhanced(request.getUserID(),
//							request.getHoldingID(), request.getQuantity(), null);
////		} catch (DAOException e) {
//    	} catch (Exception e) {
//			logger.error("BusinessOPServiceV1.sellEnhanced ", e);
//		}
		
		OrderData orderData = null;
		try {
			orderData = mgr.sellEnhanced(request.getUserID(),
					request.getHoldingID(), request.getQuantity(), null);
		} catch (Exception e) {
			logger.error("BusinessOPServiceV1.sellEnhanced ", e);
		}

		SellEnhancedResponse response = new SellEnhancedResponse();
//		if(o!=null)
//			response.setOrderData(TypeFactory.toOrderData(o));
		response.setOrderData(orderData);
		return response;
	}
	
	
	public SellResponse sell(SellRequest request) {
		// TODO Auto-generated method stub
//        Order o = null;
//    	
//    	try {
//			o = mgr.sell(request.getUserID(), request.getHoldingID(),
//					request.getOrderProcessingMode(), null);
////		} catch (DAOException e) {
//    	} catch (Exception e) {
//			logger.error("BusinessOPServiceV1.sell ", e);
//		}
		
		OrderData orderData = null;
		try {
			orderData = mgr.sell(request.getUserID(), request.getHoldingID(),
					request.getOrderProcessingMode(), null);
		} catch (Exception e) {
			logger.error("BusinessOPServiceV1.sell ", e);
		}
    	
    	SellResponse response = new SellResponse();
//    	if(o!=null)
//    		response.setOrderData(TypeFactory.toOrderData(o));
    	response.setOrderData(orderData);
    	
    	return response;
	}
	
   
}
