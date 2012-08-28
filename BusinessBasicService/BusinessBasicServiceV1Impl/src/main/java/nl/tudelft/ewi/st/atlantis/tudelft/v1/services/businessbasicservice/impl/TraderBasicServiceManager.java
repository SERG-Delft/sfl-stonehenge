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

package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessbasicservice.impl;

import java.math.BigDecimal;
import java.net.URL;

import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetAccountProfileDataRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.GetAccountProfileDataResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateAccountDataForLoginRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.UpdateAccountForLogoutRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessaccountservice.SharedBusinessAccountServiceV1Consumer;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.services.configurationservice.SharedConfigurationServiceV1Consumer;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.AccountData;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSAccountLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSAccountLocationsResponse;
import nl.tudelft.stocktrader.Account;
import nl.tudelft.stocktrader.AccountProfile;
import nl.tudelft.stocktrader.Wallet;
import nl.tudelft.stocktrader.dal.CustomerDAO;
import nl.tudelft.stocktrader.dal.DAOFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TraderBasicServiceManager {

  static {
    //WORKAROUND. TO BE REMOVED.
    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
      new javax.net.ssl.HostnameVerifier(){
        public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
            return true;
        }});
      
  }
                    
	private static final Log logger = LogFactory
			.getLog(TraderBasicServiceManager.class);

	private DAOFactory factory = null;

	public TraderBasicServiceManager() {
		factory = DAOFactory.getFactory();
	}

	public AccountData login(String userId, String password)throws Exception {
		

		SharedConfigurationServiceV1Consumer configConsumer = new SharedConfigurationServiceV1Consumer("BusinessBasicService","production");
		
		GetBSAccountLocationsResponse response = configConsumer.getBSAccountLocations(new GetBSAccountLocationsRequest());
		
		if (response.getLocations().size() == 0) {
//			System.out.println("Unable to locate a business account service");
			return null;
		}
		
		String bsAccountEndpoint = response.getLocations().get(0).getServiceURL();
		
		SharedBusinessAccountServiceV1Consumer bsAccountConsumer = new SharedBusinessAccountServiceV1Consumer("BusinessBasicService", "production");
		bsAccountConsumer.getService().setServiceLocation(new URL(bsAccountEndpoint));

		GetAccountProfileDataRequest profileRequest = new GetAccountProfileDataRequest();
		profileRequest.setUserID(userId);
		
		
		GetAccountProfileDataResponse profileResponse = bsAccountConsumer.getAccountProfileData(profileRequest);
		
		if(profileResponse.getAccountProfileData()!=null){
			if(profileResponse.getAccountProfileData().getPassword().equals(password)){
				
				SharedBusinessAccountServiceV1Consumer updateAccountConsumer = new SharedBusinessAccountServiceV1Consumer("BusinessBasicService", "production");
				updateAccountConsumer.getService().setServiceLocation(new URL(bsAccountEndpoint));

				UpdateAccountDataForLoginRequest loginRequest = new UpdateAccountDataForLoginRequest();
				loginRequest.setUserID(userId);
				AccountData newAccount = updateAccountConsumer.updateAccountDataForLogin(loginRequest).getNewAccountData();
				
				return newAccount;
			}	
		}
		
		return null;
	}

	public void logout(String userId) throws Exception {
//		CustomerDAO customerDAO = factory.getCustomerDAO();
//		customerDAO.logoutUser(userId);
		
		SharedConfigurationServiceV1Consumer configConsumer = new SharedConfigurationServiceV1Consumer("BusinessBasicService","production");
		
		GetBSAccountLocationsResponse response = configConsumer.getBSAccountLocations(new GetBSAccountLocationsRequest());
		
		if (response.getLocations().size() == 0) {
//			System.out.println("Unable to locate a business account service");
			return;
		}
		
		String bsAccountEndpoint = response.getLocations().get(0).getServiceURL();
			
		SharedBusinessAccountServiceV1Consumer accountConsumer = new SharedBusinessAccountServiceV1Consumer("BusinessBasicService", "production");
		accountConsumer.getService().setServiceLocation(new URL(bsAccountEndpoint));
		
		UpdateAccountForLogoutRequest logoutRequest = new UpdateAccountForLogoutRequest();
		logoutRequest.setUserID(userId);
		
		accountConsumer.updateAccountForLogout(logoutRequest);	

		
	}

	
	public String register(String userId, String password,
			String fullName, String address, String email, String creditcard,
			BigDecimal openBalance, String currencyType) throws Exception {
		
		String result = null;
		
		SharedConfigurationServiceV1Consumer configConsumer = new SharedConfigurationServiceV1Consumer("BusinessBasicService","production");		
		GetBSAccountLocationsResponse response = configConsumer.getBSAccountLocations(new GetBSAccountLocationsRequest());
		
		if (response.getLocations().size() == 0) {
//			System.out.println("Unable to locate a business account service");
			return null;
		}
		
		String bsAccountEndpoint = response.getLocations().get(0).getServiceURL();		
		SharedBusinessAccountServiceV1Consumer bsAccountConsumer = new SharedBusinessAccountServiceV1Consumer("BusinessBasicService", "production");
		
		GetAccountProfileDataRequest profileRequest = new GetAccountProfileDataRequest();
		profileRequest.setUserID(userId);
		
		bsAccountConsumer.getService().setServiceLocation(new URL(bsAccountEndpoint));
		
		GetAccountProfileDataResponse profileResponse = bsAccountConsumer.getAccountProfileData(profileRequest);

		if(profileResponse.getAccountProfileData()==null){

			Account a = new Account();
			a.setUserID(userId);
			a.setOpenBalance(openBalance);
			a.setBalance(openBalance);
			a.setCurrencyType(currencyType);
//			a.setLogoutCount(0);	
			
			CustomerDAO customerDAO = factory.getCustomerDAO();
			boolean insertAccount = customerDAO.insertAccount(a);
			boolean insertAP = customerDAO.insertAccountProfile(new AccountProfile(userId, password, 
											fullName, address, email, creditcard));	
			//also need to insert wallet
			Wallet wallet = new Wallet(userId);
			wallet.setMoney(currencyType, openBalance);
			boolean insertWallet = customerDAO.insertWallet(wallet);
			if(insertAP && insertAccount && insertWallet)
				result = "success";
			else
				result = "failed";
		}else
			result = "userexist";	

		return result;
	}



}
