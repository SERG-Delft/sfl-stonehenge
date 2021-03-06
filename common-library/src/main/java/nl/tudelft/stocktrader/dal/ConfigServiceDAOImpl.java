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
 
 package nl.tudelft.stocktrader.dal;

import nl.tudelft.stocktrader.dal.configservice.BSConfig;
import nl.tudelft.stocktrader.dal.configservice.ClientConfig;
import nl.tudelft.stocktrader.dal.configservice.OPSConfig;
import nl.tudelft.stocktrader.dal.configservice.ServiceLocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigServiceDAOImpl implements ConfigServiceDAO {
    private static Log logger = LogFactory.getLog(ConfigServiceDAOImpl.class);

    private Connection connection;

    public ConfigServiceDAOImpl() {
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    public Connection getConnection() {
    	return connection;
    }

    private static final String SQL_SELECT_QSLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_QS%'";
    private static final String SQL_SELECT_ESLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_ES%'";
    private static final String SQL_SELECT_BSLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_BS%'";
    private static final String SQL_SELECT_OPSLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_OPS%'";
    
    /*
     * new operations to get added services
     */
    
    private static final String SQL_SELECT_ECHECKLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_ECHECK%'";
    private static final String SQL_SELECT_ECURRLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_ECURR%'";
    private static final String SQL_SELECT_BSACCOUNTLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_BS_ACCOUNT%'";
    private static final String SQL_SELECT_BSSTOCKLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_BS_STOCK%'";    
    private static final String SQL_SELECT_BSBASICLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_BS_BASIC%'";
    private static final String SQL_SELECT_BSOPLOCATION_FROM_SERVICE = "SELECT servicename,url,sec FROM service WHERE servicename LIKE '%_BS_OP%'";
    
    private static final String SQL_INSERT_VALUE_INTO_CLIENT_TO_BS = "INSERT INTO clienttobs (client , bs) VALUES (?,?)";
    private static final String SQL_UPDATE_CLIENT_TO_BS = "UPDATE clienttobs SET bs=? WHERE client = ?";
    private static final String SQL_INSERT_VALUE_INTO_BS_TO_OPS = "INSERT INTO bstoops (bs, ops) VALUES (?,?)";
    private static final String SQL_UPDATE_BS_TO_OPS = "UPDATE bstoops SET ops=? WHERE bs  = ?";
    private static final String SQL_SELECT_BSSERVICE_ADDRESS_BY_ClIENTNAME = "SELECT servicename,url,sec FROM service INNER JOIN clienttobs ON service.servicename = clienttobs.bs  WHERE client=?";
    private static final String SQL_SELECT_OPSSERVICE_ADDRESS_BY_CLIENTNAME = "SELECT servicename,url,sec,dbname, hostname,port FROM service INNER JOIN bstoops ON service.servicename = bstoops.ops CROSS JOIN dbconfig WHERE bstoops.bs=?";
    private static final String SQL_SELECT_FROM_CLIENTTOBS = "SELECT * FROM clienttobs WHERE client = ?";
    private static final String SQL_SELECT_FROM_BSTOOPS = "SELECT * FROM bstoops WHERE bs = ?";
    private static final String SQL_SELECT_OPS_CONFIG = "SELECT * FROM dbconfig";
    private static final String SQL_UPDATE_SERVICE = "UPDATE service SET url = ?, sec = ? WHERE servicename = ?";
    private static final String SQL_SELECT_SERVICE = "SELECT * FROM service WHERE servicename = ?";
    private static final String SQL_INSERT_VALUE_INTO_SERVICE = "INSERT INTO service VALUES (?,?,?)";

    private static final String SQL_SELECT_URL_FROM_CONFIGSERVICE = "SELECT url FROM service WHERE servicename LIKE 'CONFIG_SERVICE'";
    private static final String SQL_UPDATE_URL_INTO_CONFIGSERVICE = "UPDATE service SET url=? WHERE servicename LIKE 'CONFIG_SERVICE'";
    
    public boolean updateConfigService(String url) {
        int updateStatus;
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_URL_INTO_CONFIGSERVICE);
            statement.setObject(1, url);
            updateStatus = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error(e, e);
            }
        }
        return updateStatus > 0;
    }
    
    public String getConfigService() {
        String configServiceUrl = null;
        ResultSet rs = null;
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_URL_FROM_CONFIGSERVICE);
            rs = statement.executeQuery();
            while (rs.next()) {
                configServiceUrl = rs.getString("url");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException exception) {
                logger.error(exception, exception);
            }
        }
        return configServiceUrl;
    }

    public List<ServiceLocation> getQSLocations() {
    	return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_QSLOCATION_FROM_SERVICE),
                new ServiceResultSetExtractor());
    }
    
	public List<ServiceLocation> getESLocations() {
		 return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_ESLOCATION_FROM_SERVICE),
	                new ServiceResultSetExtractor());
	}
    
    public List<ServiceLocation> getBSLocations() {
        return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_BSLOCATION_FROM_SERVICE),
                new ServiceResultSetExtractor());
    }

    public List<ServiceLocation> getOPSLocations() {
        return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_OPSLOCATION_FROM_SERVICE),
                new ServiceResultSetExtractor());
    }

	public List<ServiceLocation> getECheckLocations() {
		return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_ECHECKLOCATION_FROM_SERVICE),
                new ServiceResultSetExtractor());
	}

	public List<ServiceLocation> getECurrLocations() {
		return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_ECURRLOCATION_FROM_SERVICE),
                new ServiceResultSetExtractor());
	}

	public List<ServiceLocation> getBSAccountLocations() {
		return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_BSACCOUNTLOCATION_FROM_SERVICE),
                new ServiceResultSetExtractor());
	}

	public List<ServiceLocation> getBSStockLocations() {
		return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_BSSTOCKLOCATION_FROM_SERVICE),
                new ServiceResultSetExtractor());
	}

	public List<ServiceLocation> getBSBasicLocations() {
		return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_BSBASICLOCATION_FROM_SERVICE),
                new ServiceResultSetExtractor());
	}

	public List<ServiceLocation> getBSOPLocations() {
		return (List<ServiceLocation>) executeQuery(new SimpleStatementPopulator(SQL_SELECT_BSOPLOCATION_FROM_SERVICE),
                new ServiceResultSetExtractor());
	}
    
    
    public ClientConfig getClientConfig(String clientName) {
        return (ClientConfig) executeQuery(
                new SimpleStatementPopulator(SQL_SELECT_BSSERVICE_ADDRESS_BY_ClIENTNAME, clientName),
                new ClientConfigResponseResultSetExtractor());
    }

    public BSConfig getBSConfig(String bsName) {
        return (BSConfig) executeQuery(
                new SimpleStatementPopulator(SQL_SELECT_OPSSERVICE_ADDRESS_BY_CLIENTNAME, bsName),
                new BSConfigResponseResultSetExtractor());
    }

    public OPSConfig getOPSConfig(String opsName) {
        return (OPSConfig) executeQuery(new SimpleStatementPopulator(SQL_SELECT_OPS_CONFIG),
                new OPSConfigResponseResultSetExtractor());
    }

    public boolean setClientToBS(String clientName, String bsName) {
        StatementPopulator queryStatement = new SimpleStatementPopulator(SQL_SELECT_FROM_CLIENTTOBS, clientName);
        StatementPopulator updateStatement = new SimpleStatementPopulator(SQL_UPDATE_CLIENT_TO_BS, bsName, clientName);
        StatementPopulator insertStatement = new SimpleStatementPopulator(SQL_INSERT_VALUE_INTO_CLIENT_TO_BS,
                clientName, bsName);
        return executeTransaction(
                new SaveOrUpdateTransactionCallback(queryStatement, updateStatement, insertStatement));
    }

    public boolean setBSToOPS(String bs, String ops) {
    	if (getOPSConfig(ops) == null) return false;
    	
    	try {
			connection = DAOFactory.getConnectionProvider().provide(DAOFactory.prop);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
        StatementPopulator queryStatement = new SimpleStatementPopulator(SQL_SELECT_FROM_BSTOOPS, bs);
        StatementPopulator updateStatement = new SimpleStatementPopulator(SQL_UPDATE_BS_TO_OPS, ops, bs);
        StatementPopulator insertStatement = new SimpleStatementPopulator(SQL_INSERT_VALUE_INTO_BS_TO_OPS,
                bs, ops);
        return executeTransaction(
                new SaveOrUpdateTransactionCallback(queryStatement, updateStatement, insertStatement));
    }

    public boolean setServiceLocation(final String serviceName, final String serviceUrl, final Boolean isSec) {
    	if (isSec == null) return false;
    	
        int sec = (isSec == true) ? 1 : 0;
        StatementPopulator queryStatement = new SimpleStatementPopulator(SQL_SELECT_SERVICE, serviceName);
        StatementPopulator updateStatement = new SimpleStatementPopulator(SQL_UPDATE_SERVICE, serviceUrl, sec,
                serviceName);
        StatementPopulator insertStatement = new SimpleStatementPopulator(SQL_INSERT_VALUE_INTO_SERVICE, serviceName,
                serviceUrl, sec);
        return executeTransaction(
                new SaveOrUpdateTransactionCallback(queryStatement, updateStatement, insertStatement));
    }

    class SaveOrUpdateTransactionCallback implements TransactionCallback {
        private final StatementPopulator queryStatementPopulator;
        private final StatementPopulator updateStatementPopulator;
        private final StatementPopulator insertStatementPopulator;

        SaveOrUpdateTransactionCallback(
                StatementPopulator queryStatementPopulator,
                StatementPopulator updateStatementPopulator,
                StatementPopulator insertStatementPopulator) {
            this.queryStatementPopulator = queryStatementPopulator;
            this.updateStatementPopulator = updateStatementPopulator;
            this.insertStatementPopulator = insertStatementPopulator;
        }

        public void executeTransaction(Connection connection) throws SQLException {
            boolean hasEntry = checkUpdateStatus(queryStatementPopulator, connection);
            if (hasEntry) {
                executeUpdate(updateStatementPopulator, connection);
            } else {
                executeInsert(insertStatementPopulator, connection);
            }
        }

        private boolean checkUpdateStatus(StatementPopulator statementPopulator, Connection connection) throws
                SQLException {
            PreparedStatement statement1 = statementPopulator.populateStatement(connection);
            ResultSet resultSet = statement1.executeQuery();
            boolean hasEntry = resultSet.next();
            return hasEntry;
        }

        private void executeUpdate(StatementPopulator updateStatementPopulator, Connection connection) throws
                SQLException {
            PreparedStatement statement2 = updateStatementPopulator.populateStatement(connection);
            statement2.executeUpdate();
        }

        private void executeInsert(StatementPopulator insertStatementPopulator, Connection connection) throws
                SQLException {
            PreparedStatement statement3 = insertStatementPopulator.populateStatement(connection);
            statement3.executeUpdate();
        }
    }

    private class SimpleStatementPopulator implements StatementPopulator {
        private final String sql;
        private final Object[] params;

        private SimpleStatementPopulator(String sql, Object... params) {
            this.sql = sql;
            this.params = params;
        }

        public PreparedStatement populateStatement(Connection connection) throws
                SQLException {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int index = 1;
            for (Object param : params) {
                preparedStatement.setObject(index++, param);
            }
            return preparedStatement;
        }

    }

    class ServiceResultSetExtractor implements ResultSetExtractor<List<ServiceLocation>> {

        public List<ServiceLocation> extractResultSet(ResultSet rs) throws SQLException {
            List<ServiceLocation> serviceLocations = new ArrayList<ServiceLocation>();
            while (rs.next()) {
                ServiceLocation serviceLocation = new ServiceLocation(rs.getString("serviceName"),
                													  rs.getString("url"),
                													  rs.getBoolean("sec"));
                serviceLocations.add(serviceLocation);
            }
            return serviceLocations;
        }

    }

    class OPSConfigResponseResultSetExtractor implements ResultSetExtractor<OPSConfig> {
        public OPSConfig extractResultSet(ResultSet rs) throws SQLException {
        	OPSConfig response = null;
            while (rs.next()) {
            	response = new OPSConfig(rs.getString("dbname"),
            							 rs.getString("hostname"),
            							 rs.getInt("port"));
            }
            return response;
        }
    }

    class ClientConfigResponseResultSetExtractor implements ResultSetExtractor<ClientConfig> {
        public ClientConfig extractResultSet(ResultSet rs) throws SQLException {
            ClientConfig response = null;
            while (rs.next()) {
            	response = new ClientConfig(rs.getString("serviceName"),
            								rs.getString("url"),
            								rs.getBoolean("sec"));
            }
            return response;
        }
    }

    class BSConfigResponseResultSetExtractor implements ResultSetExtractor<BSConfig> {

        public BSConfig extractResultSet(ResultSet rs) throws SQLException {
        	BSConfig response = null;
            while (rs.next()) {
            	response = new BSConfig(rs.getString("serviceName"),
            							rs.getString("url"),
            							rs.getBoolean("sec"),
            							rs.getString("dbname"),
            							rs.getString("hostname"),
            							rs.getInt("port"));
            }
            return response;
        }
    }

    private interface StatementPopulator {
        public PreparedStatement populateStatement(Connection connection) throws SQLException;
    }

    private interface ResultSetExtractor<T> {
        T extractResultSet(ResultSet rs) throws SQLException;
    }

    private Object executeQuery(StatementPopulator stPopulator, ResultSetExtractor rsExtractor) {
        Object result = null;
        ResultSet rs = null;
        try {
            PreparedStatement statement = stPopulator.populateStatement(connection);
            rs = statement.executeQuery();
            result = rsExtractor.extractResultSet(rs);
        } catch (SQLException exception) {
            logger.error(exception, exception);
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException exception) {
                logger.error(exception, exception);
            }
        }
        return result;
    }

    interface TransactionCallback {
        void executeTransaction(Connection connection) throws SQLException;
    }

    private boolean executeTransaction(TransactionCallback transactionCallback) {
        try {
            connection.setAutoCommit(false);

            transactionCallback.executeTransaction(connection);

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            logger.error(e, e);
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
