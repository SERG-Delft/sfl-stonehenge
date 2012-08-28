
package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessbasicservice.impl;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.tudelft.ewi.st.atlantis.tudelft.intf.BusinessBasicServiceV1;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.LoginRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.LoginResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.LogoutRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.LogoutResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.RegisterRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.RegisterResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.AccountData;


//modified by kathy: logger.debug --> logger.error

public class BusinessBasicServiceV1Impl
    implements BusinessBasicServiceV1
{

	private final TraderBasicServiceManager mgr = new TraderBasicServiceManager();
	private static final Log logger = LogFactory.getLog(BusinessBasicServiceV1Impl.class);
	
    
    	public RegisterResponse register(RegisterRequest registerRequest) {
    		// TODO Auto-generated method stub
    		String registerResult = null;
    		
    		try {
    			registerResult = mgr.register(registerRequest.getUserID(), registerRequest.getPassword(), 
    					registerRequest.getFullname(), registerRequest.getAddress(), 
    					registerRequest.getEmail(), registerRequest.getCreditcard(), 
    					BigDecimal.valueOf(registerRequest.getOpenBalance()), registerRequest.getCurrencyType());
//    		} catch (DAOException e) {
    		} catch (Exception e) {
    			logger.error("BusinessBasicServiceV1Impl.register ", e);
    		}
    		RegisterResponse response = new RegisterResponse();
    		response.setOut(registerResult);
    		
    		return response;
	}

	public LogoutResponse logout(LogoutRequest logoutRequest) {
		// TODO Auto-generated method stub
		try {
			mgr.logout(logoutRequest.getUserID());
//		} catch (DAOException e) {
		} catch (Exception e) {

			logger.error("BusinessBasicServiceV1Impl.logout ", e);
		}
		
		LogoutResponse response = new LogoutResponse();
		
		return response;
	}

	public LoginResponse login(LoginRequest loginRequest) {
		// TODO Auto-generated method stub
		AccountData a = null;
		try {
			a = mgr.login(loginRequest.getUserID(), loginRequest.getPassword());
//		} catch (DAOException e) {
		} catch (Exception e) {

			logger.error("BusinessBasicServiceV1Impl.login ", e);
		}
		
		LoginResponse response = new LoginResponse();
		response.setAccount(a);
		
		return response;
	}

	
}
