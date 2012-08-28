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

package nl.tudelft.stocktrader.derby;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//import nl.tudelft.stocktrader.Account;
import nl.tudelft.stocktrader.Account;
import nl.tudelft.stocktrader.AccountProfile;
import nl.tudelft.stocktrader.Holding;
import nl.tudelft.stocktrader.Order;
import nl.tudelft.stocktrader.Wallet;
import nl.tudelft.stocktrader.dal.CustomerDAO;
import nl.tudelft.stocktrader.dal.DAOException;
import nl.tudelft.stocktrader.util.StockTraderUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//modified by kathy: logger.debug --> logger.error

public class DerbyCustomerDAO extends AbstractDerbyDAO implements CustomerDAO {
    private static final Log logger = LogFactory.getLog(DerbyCustomerDAO.class);
//select profile_userid from APP.ACCOUNT where accountid = 1
    private static final String SQL_GET_USERID = "SELECT profile_userid FROM account WHERE accountid = ?";
    private static final String SQL_DEBIT_ACCOUNT = "UPDATE account SET balance= balance - ? WHERE accountid = ?";
    private static final String SQL_SELECT_HOLDING_LOCK = "SELECT h.account_accountid, h.holdingid, h.quantity, h.purchaseprice, h.purchasedate, h.quote_symbol FROM holding as h INNER JOIN orders as o on h.holdingid = o.holding_holdingid WHERE (o.orderid = ?)";
    private static final String SQL_SELECT_HOLDING_NOLOCK = "SELECT account_accountid, quantity, purchaseprice, purchasedate, quote_symbol FROM holding WHERE holdingid = ? AND account_accountid IN (SELECT accountid FROM account WHERE profile_userid = ?)";
    private static final String SQL_SELECT_CUSTOMER_PROFILE_BY_USERID = "SELECT userid, password, fullname, address, email, creditcard FROM accountprofile WHERE userid = ?";
    private static final String SQL_UPDATE_CUSTOMER_LOGIN = "UPDATE account SET logincount = logincount + 1, lastlogin = current_timestamp where profile_userid = ?";
    private static final String SQL_SELECT_CUSTOMER_LOGIN = "SELECT accountid, creationdate, openbalance, logoutcount, balance, lastlogin, logincount FROM account WHERE profile_userid = ?";
    private static final String SQL_UPDATE_LOGOUT = "UPDATE account SET logoutcount = logoutcount + 1 WHERE profile_userid = ?";
    private static final String SQL_SELECT_GET_CUSTOMER_BY_USERID = "SELECT account.ACCOUNTID, account.PROFILE_USERID, account.CREATIONDATE, account.OPENBALANCE, account.LOGOUTCOUNT, account.BALANCE, account.LASTLOGIN, account.LOGINCOUNT, account.CURRENCY FROM account WHERE account.PROFILE_USERID = ?";
    private static final String SQL_SELECT_ORDERS_BY_ID = " o.orderid, o.ordertype, o.orderstatus, o.opendate, o.completiondate, o.quantity, o.price, o.orderfee, o.quote_symbol, o.currency FROM orders o WHERE o.account_accountid IN (SELECT a.accountid FROM account a WHERE a.profile_userid = ?) ORDER BY o.orderid DESC";
    private static final String SQL_SELECT_COMPLETED_ORDERS = "SELECT orderid, ordertype, orderstatus, completiondate, opendate, quantity, price, orderfee, quote_symbol, currency FROM orders WHERE account_accountid IN (SELECT accountid FROM account WHERE profile_userid = ?) AND orderstatus = 'completed'";
    private static final String SQL_UPDATE_CLOSED_ORDERS = "UPDATE orders SET orderstatus = 'completed' WHERE orderstatus = 'closed' AND account_accountid IN (SELECT accountid FROM account WHERE profile_userid = ?)";
    private static final String SQL_INSERT_ACCOUNT_PROFILE = "INSERT INTO accountprofile VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_ACCOUNT = "INSERT INTO account (creationdate, openbalance, logoutcount, balance, logincount, profile_userid, currency) VALUES (current_timestamp, ?, ?, ?, ?, ?, ?)";//; SELECT LAST_INSERT_ID()
    private static final String SQL_UPDATE_ACCOUNT_PROFILE = "UPDATE accountprofile SET address = ?, password = ?, email = ?, creditcard = ?, fullname = ? WHERE userid = ?";
    private static final String SQL_SELECT_HOLDINGS = "SELECT holdingid, quantity, purchaseprice, purchasedate, quote_symbol, account_accountid FROM holding WHERE account_accountid IN (SELECT accountid FROM account WHERE profile_userid = ?) ORDER BY holdingid DESC";
    //for register
//    private static final String SQL_SELECT_USER_LIST = "SELECT userid FROM accountprofile";
    private static final String SQL_INSERT_WALLET = "INSERT INTO wallet (userid, usd, eur, gbp, cny, inr) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_WALLET = "SELECT userid, usd, eur, gbp, cny, inr FROM wallet where userid = ?";
    private static final String SQL_UPDATE_WALLET = "UPDATE wallet SET usd = ?, eur = ?, gbp = ?, cny = ?, inr = ? WHERE userid = ?";

 
    
    public DerbyCustomerDAO(Connection sqlConnection) throws DAOException {
        super(sqlConnection);
    }

    public Holding getHoldingForUpdate(int orderId) throws DAOException {
        if (logger.isDebugEnabled()) {
            logger.error("MySQLCustomerDAO.getHoldingForUpdate(int)\nOrder ID :" + orderId);
        }

        Holding holding = null;
        PreparedStatement selectHoldingLockStat = null;
        try {
            selectHoldingLockStat = sqlConnection.prepareStatement(SQL_SELECT_HOLDING_LOCK);
            selectHoldingLockStat.setInt(1, orderId);
            ResultSet rs = selectHoldingLockStat.executeQuery();
            if (rs.next()) {
                try {
                    holding = new Holding(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getDouble(3),
                            rs.getBigDecimal(4),
                            StockTraderUtility.convertToCalendar(rs.getDate(5)),
                            rs.getString(6));
                    return holding;
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        logger.error("", e);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Exception is thrown when selecting the holding entry for order ID :" + orderId, e);
        } finally {
            if (selectHoldingLockStat != null) {
                try {
                    selectHoldingLockStat.close();
                } catch (SQLException e) {
                    logger.error("", e);                    
                }
            }
        }
        return holding;
    }

    public Holding getHolding(String userId, int holdingID) throws DAOException {
        if (logger.isDebugEnabled()) {
            logger.error("MSSQLCustomerDAO.getHolding(String,int)\nUserID :" + userId + "\nOrder ID :" + holdingID);
        }
        Holding holding = null;
        PreparedStatement selectHoldingNoLockStat = null;
        try {
            selectHoldingNoLockStat = sqlConnection.prepareStatement(SQL_SELECT_HOLDING_NOLOCK);
            selectHoldingNoLockStat.setInt(1, holdingID);
            selectHoldingNoLockStat.setString(2, userId);

            ResultSet rs = selectHoldingNoLockStat.executeQuery();
            if (rs.next()) {
                try {
                    holding = new Holding(
                            rs.getInt(1),
                            holdingID,
                            rs.getDouble(2),
                            rs.getBigDecimal(3),
                            StockTraderUtility.convertToCalendar(rs.getDate(4)),
                            rs.getString(5));
                    return holding;
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        logger.error("", e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("", e);
            throw new DAOException("Exception is thrown when selecting the holding entry for userID :" + userId + " and orderID :" + holdingID, e);

        } finally {
            if (selectHoldingNoLockStat != null) {
                try {
                    selectHoldingNoLockStat.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        return holding;
    }

    public void updateAccountBalance(int accountId, BigDecimal total) throws DAOException {
        if (logger.isDebugEnabled()) {
            logger.error("MySQLCustomerDAO.updateAccoutBalance(int,BigDecimal)\n Account ID :" + accountId + "\nTotal :" + total);
        }
        PreparedStatement debitAccountStat = null;
        try {
        	/* Tiago: we need to be very careful with BigDecimals. Round to 2 places */
        	total = total.setScale(2, RoundingMode.HALF_UP);

        	debitAccountStat = sqlConnection.prepareStatement(SQL_DEBIT_ACCOUNT);
            debitAccountStat.setBigDecimal(1, total);
            debitAccountStat.setInt(2, accountId);
            debitAccountStat.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Excpetion is thrown when updating the account balance for accountID :" + accountId + " total :" + total, e);
        } finally {
            if (debitAccountStat != null) {
                try {
                    debitAccountStat.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
    }
        
    

    public Account login(String userId, String password) throws DAOException {
        PreparedStatement selectCustomerProfileByUserId = null;
        PreparedStatement updateCustomerLogin = null;
        PreparedStatement selectCustomerLogin = null;
        try {
            selectCustomerProfileByUserId = sqlConnection.prepareStatement(SQL_SELECT_CUSTOMER_PROFILE_BY_USERID);
            selectCustomerProfileByUserId.setString(1, userId);
            ResultSet customerProfileRS = selectCustomerProfileByUserId.executeQuery();
            if (customerProfileRS.next()) {
                try {
                    String userPassword = customerProfileRS.getString(2);
                    if (userPassword.equals(password)) {
                        try {
                            updateCustomerLogin = sqlConnection.prepareStatement(SQL_UPDATE_CUSTOMER_LOGIN);
                            updateCustomerLogin.setString(1, userId);
                            updateCustomerLogin.executeUpdate();
                            selectCustomerLogin = sqlConnection.prepareStatement(SQL_SELECT_CUSTOMER_LOGIN);
                            selectCustomerLogin.setString(1, userId);
                            ResultSet rs = selectCustomerLogin.executeQuery();
                            if (rs.next()) {
                                try {
                                    Account accountData = new Account(
                                            rs.getInt(1),
                                            userId,
                                            StockTraderUtility.convertToCalendar(rs.getDate(2)),
                                            rs.getBigDecimal(3),
                                            rs.getInt(4),
                                            rs.getBigDecimal(5),
                                            StockTraderUtility.convertToCalendar(rs.getDate(6)),
                                            rs.getInt(7));
                                    return accountData;
                                } finally {
                                    try {
                                        rs.close();
                                    } catch (SQLException e) {
                                        logger.error("", e);
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            throw new DAOException("", e);
                        } finally {
                            if (updateCustomerLogin != null) {
                                try {
                                    updateCustomerLogin.close();
                                } catch (SQLException e) {
                                    logger.error("", e);
                                }
                            }
                            if (selectCustomerLogin != null) {
                                try {
                                    selectCustomerLogin.close();
                                } catch (SQLException e) {
                                    logger.error("", e);
                                }
                            }
                        }
                    }
                } finally {
                    try {
                        customerProfileRS.close();
                    } catch (SQLException e) {
                        logger.error("", e);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (selectCustomerProfileByUserId != null) {
                try {
                    selectCustomerProfileByUserId.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        return null;
    }

    public void logoutUser(String userId) throws DAOException {
        PreparedStatement updateLogout = null;
        try {
            updateLogout = sqlConnection.prepareStatement(SQL_UPDATE_LOGOUT);
            updateLogout.setString(1, userId);
            updateLogout.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (updateLogout != null) {
                try {
                    updateLogout.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
    }

    public Account getCustomerByUserId(String userId) throws DAOException {
        PreparedStatement getCustomerByUserId = null;

        try {
            getCustomerByUserId = sqlConnection.prepareStatement(SQL_SELECT_GET_CUSTOMER_BY_USERID);
            getCustomerByUserId.setString(1, userId);
            ResultSet rs = getCustomerByUserId.executeQuery();
            if (rs.next()) {
                try {
                    Account bean = new Account(
                            rs.getInt(1),
                            rs.getString(2),
                            StockTraderUtility.convertToCalendar(rs.getDate(3)),
                            rs.getBigDecimal(4),
                            rs.getInt(5),
                            rs.getBigDecimal(6),
                            StockTraderUtility.convertToCalendar(rs.getDate(7)),
                            rs.getInt(8),
                            rs.getString(9));
                    return bean;
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        logger.error("", e);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (getCustomerByUserId != null) {
                try {
                    getCustomerByUserId.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        return null;
    }

    public AccountProfile getAccountProfileData(String userId) throws DAOException {

        PreparedStatement customerProfileByUserId = null;
        try {
            customerProfileByUserId = sqlConnection.prepareStatement(SQL_SELECT_CUSTOMER_PROFILE_BY_USERID);
            customerProfileByUserId.setString(1, userId);
            ResultSet rs = customerProfileByUserId.executeQuery();
            if (rs.next()) {
                try {
                    AccountProfile accountProfileDataBean = new AccountProfile(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6));
                    return accountProfileDataBean;
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        logger.error("", e);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (customerProfileByUserId != null) {
                try {
                    customerProfileByUserId.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        return null;
    }

    public List<Order> getOrders(String userId, boolean top, int maxTop, int maxDefault) throws DAOException {
        PreparedStatement selectOrdersById = null;
        try {
            String sqlQuery;
            if (top) {
                sqlQuery = "SELECT " + SQL_SELECT_ORDERS_BY_ID + " FETCH FIRST " + maxTop + " ROWS ONLY";
            } else {
                sqlQuery = "SELECT " + SQL_SELECT_ORDERS_BY_ID + " FETCH FIRST " + maxDefault + " ROWS ONLY";
            }
            selectOrdersById = sqlConnection.prepareStatement(sqlQuery);
            selectOrdersById.setString(1, userId);
            ResultSet rs = selectOrdersById.executeQuery();
            List<Order> orders = new ArrayList<Order>();

            try {
                while (rs.next()) {
                    int orderId = rs.getInt(1);
                    Calendar openDate = StockTraderUtility.convertToCalendar(rs.getDate(4));
                    Calendar completionDate = null;
                    try {
                        if (rs.getDate(5) != null) {
                            completionDate = StockTraderUtility.convertToCalendar(rs.getDate(5));
                        } else {
                            completionDate = Calendar.getInstance();
                            completionDate.setTimeInMillis(0);
                        }
                    } catch (SQLException e) {
                        logger.error("", e);
                        completionDate = Calendar.getInstance();
                        completionDate.setTimeInMillis(0);
                    }

                    Order orderBean = new Order(
                            orderId,
                            rs.getString(2),
                            rs.getString(3),
                            openDate,
                            completionDate,
                            rs.getDouble(6),
                            rs.getBigDecimal(7),
                            rs.getBigDecimal(8),
                            rs.getString(9),
                    		rs.getString(10));
                    orders.add(orderBean);
                }

            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
            return orders;

        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (selectOrdersById != null) {
                try {
                    selectOrdersById.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
    }

    public List<Order> getCompletedOrders(String userId) throws DAOException {
        PreparedStatement selectClosedOrders = null;
        PreparedStatement updateClosedOrders = null;
        try {
            selectClosedOrders = sqlConnection.prepareStatement(SQL_SELECT_COMPLETED_ORDERS);
            selectClosedOrders.setString(1, userId);
            ResultSet rs = selectClosedOrders.executeQuery();
            List<Order> closedOrders = new ArrayList<Order>();

            try {
                while (rs.next()) {
                    int orderId = rs.getInt(1);
                    Calendar openDate = StockTraderUtility.convertToCalendar(rs.getDate(4));
                    Calendar completionDate = null;
                    try {
                        completionDate = StockTraderUtility.convertToCalendar(rs.getDate(5));
                    } catch (SQLException e) {
                        logger.error("", e);
                        completionDate = Calendar.getInstance();
                        completionDate.setTimeInMillis(0);
                    }
                    Order closedOrderBean = new Order(
                            orderId,
                            rs.getString(2),
                            rs.getString(3),
                            openDate,
                            completionDate,
                            rs.getDouble(6),
                            rs.getBigDecimal(7),
                            rs.getBigDecimal(8),
                            rs.getString(9),
                            rs.getString(10));
                    closedOrderBean.setOrderStatus(StockTraderUtility.ORDER_STATUS_CLOSED);
                    closedOrders.add(closedOrderBean);
                }
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }

            if (!closedOrders.isEmpty()) {
                updateClosedOrders = sqlConnection.prepareStatement(SQL_UPDATE_CLOSED_ORDERS);
                updateClosedOrders.setString(1, userId);
                updateClosedOrders.executeUpdate();
            }

            return closedOrders;
        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (selectClosedOrders != null) {
                try {
                    selectClosedOrders.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
            if (updateClosedOrders != null) {
                try {
                    selectClosedOrders.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }

        }
    }

    public boolean insertAccountProfile(AccountProfile accountProfileBean) throws DAOException {
        PreparedStatement insertAccountProfile = null;
        boolean insertSuccess = false;
        try {
            insertAccountProfile = sqlConnection.prepareStatement(SQL_INSERT_ACCOUNT_PROFILE);
            insertAccountProfile.setString(1, accountProfileBean.getAddress());
            insertAccountProfile.setString(2, accountProfileBean.getPassword());
            insertAccountProfile.setString(3, accountProfileBean.getUserID());
            insertAccountProfile.setString(4, accountProfileBean.getEmail());
            insertAccountProfile.setString(5, accountProfileBean.getCreditCard());
            insertAccountProfile.setString(6, accountProfileBean.getFullName());
            insertAccountProfile.executeUpdate();
            insertSuccess = true;
        } catch (SQLException e) {
        	insertSuccess = false;
            throw new DAOException("", e);
        } finally {
            if (insertAccountProfile != null) {
                try {
                    insertAccountProfile.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        return insertSuccess;
    }
//  "INSERT INTO account (creationdate, openbalance, logoutcount, balance, logincount, profile_userid) VALUES (current_timestamp, ?, ?, ?, ?, ?, ?); SELECT LAST_INSERT_ID();";

    public boolean insertAccount(Account accountBean) throws DAOException {
        PreparedStatement insertAccount = null;
        boolean insertSuccess = false;
        try {
            insertAccount = sqlConnection.prepareStatement(SQL_INSERT_ACCOUNT);
            insertAccount.setBigDecimal(1, accountBean.getOpenBalance());
            insertAccount.setInt(2, 0);
            insertAccount.setBigDecimal(3, accountBean.getBalance());
            //first insert: the user didn't exist before, no last login
//            insertAccount.setDate(4, StockTraderUtility.convertToSqlDate(accountBean.getLastLogin()));
            insertAccount.setInt(4, 0);
            insertAccount.setString(5, accountBean.getUserID());
            insertAccount.setString(6, accountBean.getCurrencyType());
            insertAccount.executeUpdate();
            insertSuccess = true;

        } catch (SQLException e) {
        	insertSuccess = false;
            throw new DAOException("", e);

        } finally {
            if (insertAccount != null) {
                try {
                    insertAccount.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        return insertSuccess;
    }

    public AccountProfile updateAccountProfile(AccountProfile customerAccountProfile) throws DAOException {
        PreparedStatement updateAccountProfile = null;
        try {
            updateAccountProfile = sqlConnection.prepareStatement(SQL_UPDATE_ACCOUNT_PROFILE);
            updateAccountProfile.setString(1, customerAccountProfile.getAddress());
            updateAccountProfile.setString(2, customerAccountProfile.getPassword());
            updateAccountProfile.setString(3, customerAccountProfile.getEmail());
            updateAccountProfile.setString(4, customerAccountProfile.getCreditCard());
            updateAccountProfile.setString(5, customerAccountProfile.getFullName());
            updateAccountProfile.setString(6, customerAccountProfile.getUserID());
            updateAccountProfile.executeUpdate();
            return customerAccountProfile;
        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (updateAccountProfile != null) {
                try {
                    updateAccountProfile.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
    }

    public List<Holding> getHoldings(String userID) throws DAOException {
        PreparedStatement selectHoldings = null;
        try {
            selectHoldings = sqlConnection.prepareStatement(SQL_SELECT_HOLDINGS);
            selectHoldings.setString(1, userID);
            ResultSet rs = selectHoldings.executeQuery();
            List<Holding> holdings = new ArrayList<Holding>();
            try {
                while (rs.next()) {
                    Holding holding = new Holding(
                            rs.getInt(1),
                            rs.getDouble(2),
                            rs.getBigDecimal(3),
                            StockTraderUtility.convertToCalendar(rs.getDate(4)),
                            rs.getString(5),
                            rs.getInt(6));
                    holdings.add(holding);
                }
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
            return holdings;
        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (selectHoldings != null) {
                try {
                    selectHoldings.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
    }

//userid, usd, eur, gbp, cny, inr
    
	public boolean insertWallet(Wallet wallet) throws DAOException {
		PreparedStatement insertWallet = null;
		boolean insertSuccess = false;
        try {
            insertWallet = sqlConnection.prepareStatement(SQL_INSERT_WALLET);
            insertWallet.setString(1, wallet.getUserID());
            insertWallet.setBigDecimal(2, wallet.getUsd());
            insertWallet.setBigDecimal(3, wallet.getEur());
            insertWallet.setBigDecimal(4, wallet.getGbp());
            insertWallet.setBigDecimal(5, wallet.getCny());
            insertWallet.setBigDecimal(6, wallet.getInr());
            insertWallet.executeUpdate();
            insertSuccess = true;
        } catch (SQLException e) {
        	insertSuccess = false;
            throw new DAOException("", e);
        } finally {
            if (insertWallet != null) {
                try {
                    insertWallet.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        return insertSuccess;
	}
	
	public Wallet getWallet(int accountID){

		PreparedStatement getUserIDfromAccountID = null;	
				
		PreparedStatement selectWallet = null;
		
        try {
        	String userID = null;
        	getUserIDfromAccountID = sqlConnection.prepareStatement(SQL_GET_USERID);
			getUserIDfromAccountID.setInt(1, accountID);
			ResultSet result = getUserIDfromAccountID.executeQuery();
			if (result.next()) {
				userID = result.getString(1);
			}
        	
            selectWallet = sqlConnection.prepareStatement(SQL_SELECT_WALLET);
            selectWallet.setString(1, userID);
            ResultSet rs = selectWallet.executeQuery();
            try {
                while (rs.next()) {
            		Wallet wallet = new Wallet(
            				rs.getString(1),
            				rs.getBigDecimal(2),
            				rs.getBigDecimal(3),
            				rs.getBigDecimal(4),
            				rs.getBigDecimal(5),
            				rs.getBigDecimal(6)
            			); 
                    return wallet;
                }
            } finally {
                try {
                	result.close();
                    rs.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        } catch (SQLException e) {
        	 logger.error("", e);
        } finally {
        	if(getUserIDfromAccountID!=null){
        		try {
					getUserIDfromAccountID.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
            if (selectWallet != null) {
                try {
                    selectWallet.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        return null;
	}
	
	//userid, usd, eur, gbp, cny, inr
	public Wallet getWallet(String userID) throws DAOException {
		PreparedStatement selectWallet = null;
        try {
            selectWallet = sqlConnection.prepareStatement(SQL_SELECT_WALLET);
            selectWallet.setString(1, userID);
            ResultSet rs = selectWallet.executeQuery();
            try {
                while (rs.next()) {
            		Wallet wallet = new Wallet(
            				rs.getString(1),
            				rs.getBigDecimal(2),
            				rs.getBigDecimal(3),
            				rs.getBigDecimal(4),
            				rs.getBigDecimal(5),
            				rs.getBigDecimal(6)
            			); 
                    return wallet;
                }
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (selectWallet != null) {
                try {
                    selectWallet.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
        return null;
	}

	//userid, usd, eur, gbp, cny, inr
	public Wallet updateWallet(Wallet wallet) throws DAOException {
		PreparedStatement updateWallet = null;
        try {
            updateWallet = sqlConnection.prepareStatement(SQL_UPDATE_WALLET);
            updateWallet.setBigDecimal(1, wallet.getUsd());
            updateWallet.setBigDecimal(2, wallet.getEur());
            updateWallet.setBigDecimal(3, wallet.getGbp());
            updateWallet.setBigDecimal(4, wallet.getCny());
            updateWallet.setBigDecimal(5, wallet.getInr());
            updateWallet.setString(6, wallet.getUserID());
            updateWallet.executeUpdate();
            return wallet;
        } catch (SQLException e) {
            throw new DAOException("", e);
        } finally {
            if (updateWallet != null) {
                try {
                    updateWallet.close();
                } catch (SQLException e) {
                    logger.error("", e);
                }
            }
        }
	}


	public Account updateAccountForLogin(String userID) throws DAOException {
        PreparedStatement updateCustomerLogin = null;
        PreparedStatement selectCustomerLogin = null;
                        try {
                            updateCustomerLogin = sqlConnection.prepareStatement(SQL_UPDATE_CUSTOMER_LOGIN);
                            updateCustomerLogin.setString(1, userID);
                            updateCustomerLogin.executeUpdate();
                            selectCustomerLogin = sqlConnection.prepareStatement(SQL_SELECT_CUSTOMER_LOGIN);
                            selectCustomerLogin.setString(1, userID);
                            ResultSet rs = selectCustomerLogin.executeQuery();
                            if (rs.next()) {
                                try {
                                    Account account = new Account(
                                            rs.getInt(1),
                                            userID,
                                            StockTraderUtility.convertToCalendar(rs.getDate(2)),
                                            rs.getBigDecimal(3),
                                            rs.getInt(4),
                                            rs.getBigDecimal(5),
                                            StockTraderUtility.convertToCalendar(rs.getDate(6)),
                                            rs.getInt(7));
                                    return account;
                                } finally {
                                    try {
                                        rs.close();
                                    } catch (SQLException e) {
                                        logger.error("", e);
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            throw new DAOException("", e);
                        } finally {
                            if (updateCustomerLogin != null) {
                                try {
                                    updateCustomerLogin.close();
                                } catch (SQLException e) {
                                    logger.error("", e);
                                }
                            }
                            if (selectCustomerLogin != null) {
                                try {
                                    selectCustomerLogin.close();
                                } catch (SQLException e) {
                                    logger.error("", e);
                                }
                            }
                        }
         return null;
	}

	
	

//	public List<String> getUserList() throws DAOException {	
//		PreparedStatement selectUserList = null;
//    	try {
//			selectUserList = sqlConnection.prepareStatement(SQL_SELECT_USER_LIST);
//			ResultSet rs = selectUserList.executeQuery();
//			List<String> userList = new ArrayList<String>();
//			
//			while(rs.next()){
//				userList.add(rs.getString(1));
//			}			
//			return userList;
//    	} catch (SQLException e) {
//            throw new DAOException("", e);
//        } finally {
//            if (selectUserList != null) {
//                try {
//                    selectUserList.close();
//                } catch (SQLException e) {
//                    logger.error("", e);
//                }
//            }
//        }
//	}
}
