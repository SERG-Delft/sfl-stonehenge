
package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.quoteservice.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import nl.tudelft.ewi.st.atlantis.tudelft.external.v1.types.RemoteQuoteData;
import nl.tudelft.ewi.st.atlantis.tudelft.intf.QuoteServiceV1;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetQuotesRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetQuotesResponse;

//modified by kathy: logger.debug --> logger.error

public class QuoteServiceV1Impl
    implements QuoteServiceV1
{
	private static final Log logger = LogFactory.getLog(QuoteServiceV1Impl.class);

    public GetQuotesResponse getQuotes(GetQuotesRequest request) {
    	
    	
    	QuoteServiceManager mgr = new QuoteServiceManager();
    	
    	List<RemoteQuoteData> data = null;
       	try {
    	
       		data = mgr.getQuotes(request.getSymbols());
    	
       	} catch (Exception e) {
			logger.error("QuoteServiceV1Impl.getQuotes ", e);
		}
    	

    	GetQuotesResponse response = new GetQuotesResponse();
    	
    	if(data!=null){
    		response.getQuoteData().addAll(data);    		
    	}

    	return response;

    }

}
