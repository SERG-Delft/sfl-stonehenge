
package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessaccountservice.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import nl.tudelft.ewi.st.atlantis.tudelft.intf.BusinessAccountServiceV1;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetAccountDataRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetAccountDataResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetAccountProfileDataRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetAccountProfileDataResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetWalletDataRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetWalletDataResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateAccountDataForLoginRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateAccountDataForLoginResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateAccountForLogoutRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateAccountForLogoutResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateAccountProfileRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateAccountProfileResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateWalletDataRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateWalletDataResponse;
import nl.tudelft.stocktrader.Account;
import nl.tudelft.stocktrader.AccountProfile;
import nl.tudelft.stocktrader.TypeFactory;
import nl.tudelft.stocktrader.Wallet;
//import nl.tudelft.stocktrader.dal.DAOException;

//modified by kathy: logger.debug --> logger.error

public class BusinessAccountServiceV1Impl
    implements BusinessAccountServiceV1
{

	private final TraderAccountServiceManager mgr = new TraderAccountServiceManager();
	private static final Log logger = LogFactory.getLog(BusinessAccountServiceV1Impl.class);
	

    public GetAccountDataResponse getAccountData(GetAccountDataRequest request) {
    	Account a = null;
    	try {
			a = mgr.getAccountData(request.getUserID());
//		} catch (DAOException e) {
    	} catch (Exception e) {	
			logger.error("BusinessAccountServiceV1Impl.getAccountData ", e);
			
		}
		
		GetAccountDataResponse response = new GetAccountDataResponse();
		if(a!=null)
			response.setAccountData(TypeFactory.toAccountData(a));
	 
		return response;
    }


    public UpdateAccountProfileResponse updateAccountProfile(UpdateAccountProfileRequest request) {
    	AccountProfile accProfile = null;
    	
    	try {
			accProfile = mgr.updateAccountProfile(
							TypeFactory.toAccountProfile(request.getAccountProfileData()));
//		} catch (DAOException e) {
    	} catch (Exception e) {	
	
			logger.error("BusinessAccountServiceV1Impl.updateAccountProfile ", e);
		}
    	
		UpdateAccountProfileResponse response = new UpdateAccountProfileResponse();
		
		if(accProfile!=null)
			response.setAccountProfileData(TypeFactory.toAccountProfileData(accProfile));
		
		return response;
    }

   
    public GetAccountProfileDataResponse getAccountProfileData(GetAccountProfileDataRequest request) {
    	AccountProfile ap = null;
        
    	try {
			ap = mgr.getAccountProfileData(request.getUserID());
//		} catch (DAOException e) {
    	} catch (Exception e) {	

			logger.error("BusinessAccountServiceV1Impl.getAccountProfileData ", e);
		}
		
		GetAccountProfileDataResponse response = new GetAccountProfileDataResponse();
		if(ap!=null)
			response.setAccountProfileData(TypeFactory.toAccountProfileData(ap));
		
		return response;
	}


	public GetWalletDataResponse getWalletData(GetWalletDataRequest getWalletRequest) {
		Wallet wallet = null;
    	try {
			wallet = mgr.getWallet(getWalletRequest.getUserID());
//		} catch (DAOException e) {
    	} catch (Exception e) {	

			logger.error("BusinessAccountServiceV1Impl.getWalletData ", e);
		}
		
		GetWalletDataResponse response = new GetWalletDataResponse();
		
		if(wallet!=null)
			response.setWalletData(TypeFactory.toWalletData(wallet));
		
		return response;
	}


	public UpdateWalletDataResponse updateWalletData(
			UpdateWalletDataRequest updateWalletDataRequest) {
		Wallet wallet = null;
    	try {
			wallet = mgr.updateWallet(
					TypeFactory.toWallet(updateWalletDataRequest.getWalletData()));
			
//		} catch (DAOException e) {
    	} catch (Exception e) {	

			logger.error("BusinessAccountServiceV1Impl.updateWalletData ", e);
		}
		
		UpdateWalletDataResponse response = new UpdateWalletDataResponse();
		
		if(wallet!=null)
			response.setNewWalletData(TypeFactory.toWalletData(wallet));
		
		return response;
	}



	public UpdateAccountDataForLoginResponse updateAccountDataForLogin(
			UpdateAccountDataForLoginRequest updateAccountDataForLoginRequest) {
		
		Account account = null;

			try {
				account = mgr.updateAccountForLogin(updateAccountDataForLoginRequest.getUserID());
//			} catch (DAOException e) {
	    	} catch (Exception e) {	

	    		logger.error("BusinessAccountServiceV1Impl.updateAccountDataForLogin ", e);
			}

		
    	UpdateAccountDataForLoginResponse response = new UpdateAccountDataForLoginResponse();
    	if(account!=null)
    		response.setNewAccountData(TypeFactory.toAccountData(account));
		
		return response;
	}


	public UpdateAccountForLogoutResponse updateAccountForLogout(
			UpdateAccountForLogoutRequest updateAccountForLogoutRequest) {
			try {
				mgr.updateAccountForLogout(updateAccountForLogoutRequest.getUserID());
//			} catch (DAOException e) {
	    	} catch (Exception e) {	

	    		logger.error("BusinessAccountServiceV1Impl.updateAccountForLogout ", e);
			}
			return new UpdateAccountForLogoutResponse();
	}

}
