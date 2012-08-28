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

package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.businessaccountservice.impl;


import nl.tudelft.stocktrader.Account;
import nl.tudelft.stocktrader.AccountProfile;

import nl.tudelft.stocktrader.Wallet;
import nl.tudelft.stocktrader.dal.CustomerDAO;
import nl.tudelft.stocktrader.dal.DAOException;
import nl.tudelft.stocktrader.dal.DAOFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TraderAccountServiceManager {

  static {
    //WORKAROUND. TO BE REMOVED.
    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
      new javax.net.ssl.HostnameVerifier(){
        public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
            return true;
        }});
      
  }
                    
	private static final Log logger = LogFactory
			.getLog(TraderAccountServiceManager.class);

	private DAOFactory factory = null;

  public TraderAccountServiceManager() {
		factory = DAOFactory.getFactory();
	}

	public Account getAccountData(String userId) throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.getCustomerByUserId(userId);
	}

	public AccountProfile getAccountProfileData(String userId)
			throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.getAccountProfileData(userId);
	}

	public AccountProfile updateAccountProfile(
			AccountProfile customAccountProfile) throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.updateAccountProfile(customAccountProfile);
	}
	
	public Account updateAccountForLogin(
			String userID) throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.updateAccountForLogin(userID);
	}

	public void updateAccountForLogout(String userID) throws DAOException{
		CustomerDAO customerDAO = factory.getCustomerDAO();
		customerDAO.logoutUser(userID);
	}
	

	public Wallet getWallet(String userID) 
			throws DAOException {
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.getWallet(userID);
		
	}
	
	public Wallet updateWallet(Wallet wallet) throws DAOException{
		CustomerDAO customerDAO = factory.getCustomerDAO();
		return customerDAO.updateWallet(wallet);
	}

}
