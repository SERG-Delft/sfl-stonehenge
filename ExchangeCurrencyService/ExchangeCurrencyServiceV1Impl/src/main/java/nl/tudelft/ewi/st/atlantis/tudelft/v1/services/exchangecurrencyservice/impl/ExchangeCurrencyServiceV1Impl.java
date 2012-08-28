
package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.exchangecurrencyservice.impl;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.tudelft.ewi.st.atlantis.tudelft.intf.ExchangeCurrencyServiceV1;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.ExchangeCurrencyRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.ExchangeCurrencyResponse;
//modified by kathy: logger.debug --> logger.error

public class ExchangeCurrencyServiceV1Impl
    implements ExchangeCurrencyServiceV1
{
	private final ExchangeCurrencyManager mgr = new ExchangeCurrencyManager();
	private static final Log logger = LogFactory.getLog(ExchangeCurrencyServiceV1Impl.class);


	public ExchangeCurrencyResponse exchangeCurrency(
			ExchangeCurrencyRequest exchangeCurrencyRequest) {
		
		String result = null;

		try {
			result = mgr.exchangeCurrency(exchangeCurrencyRequest.getUserID(),
					BigDecimal.valueOf(exchangeCurrencyRequest.getExchAmount()), 
					exchangeCurrencyRequest.getBaseCurrency(), 
					exchangeCurrencyRequest.getAimCurrency(),
					exchangeCurrencyRequest.isIsChecked());
//		} catch (IOException e) {
//
//			logger.error("", e);
//		} catch (ServiceException e) {
//			
//			logger.error("", e);
//		}
			
		} catch (Exception e){
			logger.error("ExchangeCurrencyServiceV1Impl.exchangeCurrency ", e);
		}

	    ExchangeCurrencyResponse response = new ExchangeCurrencyResponse();
    	response.setExchResult(result);		
		
		return response;
	}



}
