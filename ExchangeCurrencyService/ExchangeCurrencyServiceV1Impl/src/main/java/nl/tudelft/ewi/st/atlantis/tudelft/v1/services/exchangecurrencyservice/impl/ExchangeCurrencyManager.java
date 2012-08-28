package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.exchangecurrencyservice.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;


//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.CheckAmountRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.CheckAmountResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.CheckCurrencyRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.CheckCurrencyResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetWalletDataRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetWalletDataResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateWalletDataRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessaccountservice.SharedBusinessAccountServiceV1Consumer;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.configurationservice.SharedConfigurationServiceV1Consumer;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.exchangecheckservice.SharedExchangeCheckServiceV1Consumer;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSAccountLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSAccountLocationsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetECheckLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetECheckLocationsResponse;
import nl.tudelft.stocktrader.TypeFactory;
import nl.tudelft.stocktrader.Wallet;
import com.google.gson.Gson;

public class ExchangeCurrencyManager {
	
	
	  static {
		    //WORKAROUND. TO BE REMOVED.
		    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
		      new javax.net.ssl.HostnameVerifier(){
		        public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
		            return true;
		        }});
		      
		  }
		                    
 
		
 	public String exchangeCurrency(String userID, BigDecimal exchAmount, String baseCurrency, String aimCurrency, boolean isChecked) throws Exception {

 		
    	if(isChecked){
    			BigDecimal x = onlineExchange(exchAmount, baseCurrency, aimCurrency);
				return x.toString();
	    	}
 		
    	else{

    			//get url
    			
    			SharedConfigurationServiceV1Consumer configConsumer = new SharedConfigurationServiceV1Consumer("ExchangeCurrencyService","production");
    			
    			GetECheckLocationsResponse echeckResponse = configConsumer.getECheckLocations(new GetECheckLocationsRequest());
    			
    			if(echeckResponse.getLocations().size() == 0){
//    				System.out.println("Unable to locate exchange check service");
    				return "Unable to locate exchange check service";
    			}
    			
    			String echeckEndpoint = echeckResponse.getLocations().get(0).getServiceURL();    			
    			
				SharedExchangeCheckServiceV1Consumer checkConsumer = new SharedExchangeCheckServiceV1Consumer("ExchangeCurrencyService", "production");
				checkConsumer.getService().setServiceLocation(new URL(echeckEndpoint));
	    		//check  currency				
				
				CheckCurrencyRequest ccr = new CheckCurrencyRequest();
//				ccr.setUserID(userID);
				ccr.setBaseCurrency(baseCurrency);
				ccr.setAimCurrency(aimCurrency);
				
				CheckCurrencyResponse ccResponse = checkConsumer.checkCurrency(ccr);
				
				if(ccResponse.isCurrencyExist()){
					//check amount
					
					CheckAmountRequest car = new CheckAmountRequest();
					car.setCheckAmount(exchAmount.doubleValue());
					car.setCurrencyType(baseCurrency);
					car.setUserID(userID);
					
					CheckAmountResponse caResponse = checkConsumer.checkAmount(car);
					
					if(caResponse.isAmountEnough()){
		    		
			    		//exchange

						BigDecimal exchResult = onlineExchange(exchAmount, baseCurrency, aimCurrency);
	
						//get business account service
		    			SharedConfigurationServiceV1Consumer configAccountConsumer = new SharedConfigurationServiceV1Consumer("ExchangeCurrencyService","production");

						GetBSAccountLocationsResponse getBSresponse = configAccountConsumer.getBSAccountLocations(new GetBSAccountLocationsRequest());
		    			
		    			if (getBSresponse.getLocations().size() == 0) {
//		    				System.out.println("Unable to locate a business account service");
		    				return null;
		    			}
		    			
		    			String bsAccountEndpoint = getBSresponse.getLocations().get(0).getServiceURL();
//						
						SharedBusinessAccountServiceV1Consumer basConsumer = new SharedBusinessAccountServiceV1Consumer("ExchangeCurrencyService", "production");										
						basConsumer.getService().setServiceLocation(new URL(bsAccountEndpoint));
									    		
			    		//get wallet
						GetWalletDataRequest getWalletReq = new GetWalletDataRequest();
						getWalletReq.setUserID(userID);
						GetWalletDataResponse getWalletRes = basConsumer.getWalletData(getWalletReq);
						Wallet wallet = TypeFactory.toWallet(getWalletRes.getWalletData());						
						
						//update wallet
						wallet.setMoney(baseCurrency, wallet.getMoney(baseCurrency).subtract(exchAmount));
						wallet.setMoney(aimCurrency, wallet.getMoney(aimCurrency).add(exchResult));
						
						SharedBusinessAccountServiceV1Consumer updateWalletConsumer = new SharedBusinessAccountServiceV1Consumer("ExchangeCurrencyService", "production");										
						updateWalletConsumer.getService().setServiceLocation(new URL(bsAccountEndpoint));
						
						UpdateWalletDataRequest updateWalletRequest = new UpdateWalletDataRequest();
						updateWalletRequest.setWalletData(TypeFactory.toWalletData(wallet));	
						updateWalletConsumer.updateWalletData(updateWalletRequest);
						
						return exchResult.toString();
					}
					else
						return "Your account doesn't have enough amount.";
										
				}	
				else
					return "Currency failure.";

    		   		
    	}
 		   	
		}
 	
 	private BigDecimal onlineExchange(BigDecimal exchAmount, String baseCurrency, String aimCurrency) throws IOException{
 		
 		
    	String google = "http://www.google.com/ig/calculator?hl=en&q=";
	    BigDecimal result = null;
				
				URL url = new URL(google + exchAmount + baseCurrency + "%3D%3F" + aimCurrency);
			    Reader reader = new InputStreamReader(url.openStream());
			    nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GsonResult res = new Gson().fromJson(reader, nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GsonResult.class);
			    
			    String money = res.getRhs().replaceAll(new String(new byte[]{-96}), "");
			    result = new BigDecimal(money.split(" ")[0]);
//			    if(!(money.contains("million")||money.contains("billion")||money.contains("trillion")))
//			    if(!(money.contains("million")||money.contains("billion")))	
//			    	return result;
//			    else if(money.contains("million")){
//			    	return result = BigDecimal.valueOf(1000000).multiply(result);
//			    }else if(money.contains("billion")){
//			    	return result = BigDecimal.valueOf(1000000000).multiply(result);
//			    }
//			    else if(money.contains("trillion")){
//			    	return result = BigDecimal.valueOf(1000000000000).multiply(result);
//			    }
			return result;
 		
 	}
 	

}
