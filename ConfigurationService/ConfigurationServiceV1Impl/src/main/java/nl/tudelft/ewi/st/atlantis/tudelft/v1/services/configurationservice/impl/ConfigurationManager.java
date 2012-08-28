package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.configurationservice.impl;

import java.util.List;

import nl.tudelft.ewi.st.atlantis.tudelft.util.TypeFactory;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSAccountLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSAccountLocationsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSBasicLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSBasicLocationsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSOPLocationsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSStockLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetBSStockLocationsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetECheckLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetECheckLocationsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetECurrLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetECurrLocationsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetOPSLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetOPSLocationsResponse;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetQSLocationsRequest;
import nl.tudelft.ewi.st.atlantis.tudelft.v1.types.GetQSLocationsResponse;
import nl.tudelft.stocktrader.dal.ConfigServiceDAO;
import nl.tudelft.stocktrader.dal.DAOFactory;
import nl.tudelft.stocktrader.dal.configservice.ServiceLocation;

public class ConfigurationManager {

	private final DAOFactory factory;

	public ConfigurationManager() {
		factory = DAOFactory.getFactory();
	}
	
    public ConfigServiceDAO getConfigServiceDAO() {
        return factory.getConfigServiceDAO();
    }
	
	
	public List<ServiceLocation> getOPSLocations() {
		
		
		List<ServiceLocation> locations = getConfigServiceDAO().getOPSLocations();
		
		return locations;	
	}


	public List<ServiceLocation> getQSLocations() {
		
		List<ServiceLocation> locations = getConfigServiceDAO().getQSLocations();
		
		return locations;	
	}

	public List<ServiceLocation> getBSAccountLocations() {
		
		List<ServiceLocation> locations = getConfigServiceDAO().getBSAccountLocations();
		
		return locations;	
	}
	
	public List<ServiceLocation> getECurrLocations() {
		
		List<ServiceLocation> locations = getConfigServiceDAO().getECurrLocations();
		
		return locations;	
	}
	

	public List<ServiceLocation> getBSBasicLocations() {
		
		List<ServiceLocation> locations = getConfigServiceDAO().getBSBasicLocations();
		
		return locations;	
	}


	public List<ServiceLocation> getBSOPLocations() {
		
		List<ServiceLocation> locations = getConfigServiceDAO().getBSOPLocations();
		
		return locations;	
	}


	public List<ServiceLocation> getECheckLocations() {
		
		List<ServiceLocation> locations = getConfigServiceDAO().getECheckLocations();
		
		return locations;	
	}


	public List<ServiceLocation> getBSStockLocations() {
		
		List<ServiceLocation> locations = getConfigServiceDAO().getBSStockLocations();
		
		return locations;	
	}


	
}
