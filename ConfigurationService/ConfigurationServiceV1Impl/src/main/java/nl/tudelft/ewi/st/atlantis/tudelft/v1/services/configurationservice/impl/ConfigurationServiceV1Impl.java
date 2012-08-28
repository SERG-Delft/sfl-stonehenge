
package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.configurationservice.impl;

import java.util.List;

import nl.tudelft.ewi.st.atlantis.tudelft.intf.ConfigurationServiceV1;
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

public class ConfigurationServiceV1Impl
	implements ConfigurationServiceV1
    {

    	private final ConfigurationManager mgr = new ConfigurationManager();    	
        
    

		public GetOPSLocationsResponse getOPSLocations(GetOPSLocationsRequest getOPSLocationsRequest) {
			
			List<ServiceLocation> locations = mgr.getOPSLocations();
			
			GetOPSLocationsResponse getOPSLocationsResponse = new GetOPSLocationsResponse();
			
			if(locations!=null){
				getOPSLocationsResponse.getLocations()
				.addAll(TypeFactory.toRemoteServiceLocationList(locations));
				
			}
						
			return getOPSLocationsResponse;	
		}


		public GetQSLocationsResponse getQSLocations(GetQSLocationsRequest getQSLocationsRequest) {
			
			List<ServiceLocation> locations = mgr.getQSLocations();
			
			GetQSLocationsResponse response = new GetQSLocationsResponse();
			if(locations!=null){
				response.getLocations().addAll(TypeFactory.toRemoteServiceLocationList(locations));
			}
			return response;
		}

		public GetBSAccountLocationsResponse getBSAccountLocations(
				GetBSAccountLocationsRequest getBSAccountLocationsRequest) {
			
			List<ServiceLocation> locations = mgr.getBSAccountLocations();
			
			GetBSAccountLocationsResponse getBSAccountLocationsResponse = new GetBSAccountLocationsResponse();
			
			if(locations!=null){
				getBSAccountLocationsResponse.getLocations()
				.addAll(TypeFactory.toRemoteServiceLocationList(locations));
			}
			
			
			return getBSAccountLocationsResponse;	
		}

		public GetECurrLocationsResponse getECurrLocations(
				GetECurrLocationsRequest getECurrLocationsRequest) {
			
			List<ServiceLocation> locations = mgr.getECurrLocations();
			
			GetECurrLocationsResponse getECurrLocationsResponse = new GetECurrLocationsResponse();
			
			if(locations!=null){
				getECurrLocationsResponse.getLocations()
							.addAll(TypeFactory.toRemoteServiceLocationList(locations));
			}
			return getECurrLocationsResponse;	
			
		}

		public GetBSBasicLocationsResponse getBSBasicLocations(
				GetBSBasicLocationsRequest getBSBasicLocationsRequest) {
			
			List<ServiceLocation> locations = mgr.getBSBasicLocations();
			
			GetBSBasicLocationsResponse getBSBasicLocationsResponse = new GetBSBasicLocationsResponse();
			
			if(locations!=null){
				getBSBasicLocationsResponse.getLocations()
						.addAll(TypeFactory.toRemoteServiceLocationList(locations));
			}
			return getBSBasicLocationsResponse;
			
			
		}

		public GetBSOPLocationsResponse getBSOPLocations(
				GetBSLocationsRequest getBSOPLocationsRequest) {
			
			List<ServiceLocation> locations = mgr.getBSOPLocations();
			
			GetBSOPLocationsResponse getBSOPLocationsResponse = new GetBSOPLocationsResponse();
			
			if(locations!=null){
			
				getBSOPLocationsResponse.getLocations()
						.addAll(TypeFactory.toRemoteServiceLocationList(locations));
			}
			return getBSOPLocationsResponse;
		}

		public GetECheckLocationsResponse getECheckLocations(
				GetECheckLocationsRequest getECheckLocationsRequest) {
			
			List<ServiceLocation> locations = mgr.getECheckLocations();
			
			GetECheckLocationsResponse getECheckLocationsResponse = new GetECheckLocationsResponse();
			
			if(locations!=null){
				getECheckLocationsResponse.getLocations()
						.addAll(TypeFactory.toRemoteServiceLocationList(locations));
			}
			return getECheckLocationsResponse;
		}

		public GetBSStockLocationsResponse getBSStockLocations(
				GetBSStockLocationsRequest getBSStockLocationsRequest) {
			
			List<ServiceLocation> locations = mgr.getBSStockLocations();
			
			GetBSStockLocationsResponse getBSStockLocationsResponse = new GetBSStockLocationsResponse();
			
			if(locations!=null){
				getBSStockLocationsResponse.getLocations()
						.addAll(TypeFactory.toRemoteServiceLocationList(locations));
			}
			return getBSStockLocationsResponse;
		}

}
