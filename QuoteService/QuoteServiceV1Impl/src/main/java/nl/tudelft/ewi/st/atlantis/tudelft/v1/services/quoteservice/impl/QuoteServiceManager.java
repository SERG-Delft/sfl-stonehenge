package nl.tudelft.ewi.st.atlantis.tudelft.v1.services.quoteservice.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.tudelft.ewi.st.atlantis.tudelft.external.v1.types.RemoteQuoteData;

public class QuoteServiceManager {
	
	
	
	public List<RemoteQuoteData> getQuotes(List<String> symbols) throws Exception{
		
		if (symbols.size() == 0) 
			return null;

       		
		Class.forName("org.apache.derby.jdbc.ClientDriver");
		
		/* Assumes the DB is there. If it isn't, tough luck. */
		Connection c = DriverManager.getConnection("jdbc:derby://localhost:1527/experiment");
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT TICKER, REMOTE_TIMESTAMP, MARKET, VALUE, CURRENCY, CHANGE, CHANGE_PERCENT " +
				  "FROM QUOTEDATA WHERE TICKER = ?");
		
		int i = 0;
		for(i = 0; i<symbols.size()-1;i++){
			sb.append(" OR TICKER = ?");
		}
		
		PreparedStatement ps = c.prepareStatement(sb.toString());
		
		i = 1;
		for(String t : symbols) {
			ps.setString(i++, t);
		}
		
		DatatypeFactory dtf = DatatypeFactory.newInstance();
		
		List<RemoteQuoteData> data = new ArrayList<RemoteQuoteData>();
		
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			RemoteQuoteData rqd = new RemoteQuoteData();
			rqd.setChange(rs.getDouble("CHANGE"));
			rqd.setChangePercent(rs.getDouble("CHANGE_PERCENT"));
			rqd.setMarket(rs.getString("MARKET"));
			rqd.setTicker(rs.getString("TICKER"));
			rqd.setValue(rs.getDouble("VALUE"));
			rqd.setCurrency(rs.getString("CURRENCY"));
			
			/* Tiago: What a mess, I know. In DB we have Timestamp.
			 * We need to go to GregorianCalendar and then XMLGregorianCalendar */
			Timestamp t = rs.getTimestamp("REMOTE_TIMESTAMP");
			GregorianCalendar gc = (GregorianCalendar)Calendar.getInstance();
			gc.setTimeInMillis(t.getTime());
			XMLGregorianCalendar timestamp = dtf.newXMLGregorianCalendar(gc);
			
			rqd.setTimestamp(timestamp);
			
			data.add(rqd);
		}
			
			return data;
		
		
		
	}

}
