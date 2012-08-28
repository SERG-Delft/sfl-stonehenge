
package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.exchangecheckservice.impl;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.tudelft.ewi.st.atlantis.tudelft.intf.ExchangeCheckServiceV1;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.CheckAmountRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.CheckAmountResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.CheckCurrencyRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.CheckCurrencyResponse;
import nl.tudelft.stocktrader.dal.DAOException;

//modified by kathy: logger.debug --> logger.error

public class ExchangeCheckServiceV1Impl
    implements ExchangeCheckServiceV1
{
	private final ExchangeCheckServiceManager mgr = new ExchangeCheckServiceManager();
	private static final Log logger = LogFactory.getLog(ExchangeCheckServiceV1Impl.class);

	public CheckCurrencyResponse checkCurrency(
			CheckCurrencyRequest checkCurrencyRequest) {
		
		boolean result = false;
		try{
			result = mgr.checkCurrency(checkCurrencyRequest.getUserID(), 
					checkCurrencyRequest.getBaseCurrency(), checkCurrencyRequest.getAimCurrency());
		} catch (Exception e){
			logger.error("ExchangeCheckServiceV1Impl.checkCurrency ", e);
		}
		
		CheckCurrencyResponse response = new CheckCurrencyResponse();
		response.setCurrencyExist(result);
		return response;
	}

	public CheckAmountResponse checkAmount(CheckAmountRequest checkAmountRequest) {

		boolean result = false;
		try {
			result = mgr.checkAmount(checkAmountRequest.getUserID(), 
					checkAmountRequest.getCurrencyType(), 
					BigDecimal.valueOf(checkAmountRequest.getCheckAmount()));			
//		} catch (DAOException e) {
		} catch (Exception e){
			logger.error("ExchangeCheckServiceV1Impl.checkAmount ", e);
		}
		
		CheckAmountResponse response = new CheckAmountResponse();
		response.setAmountEnough(result);
		return response;
	}

}
