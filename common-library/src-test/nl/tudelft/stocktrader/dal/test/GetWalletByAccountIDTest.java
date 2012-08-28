package nl.tudelft.stocktrader.dal.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import nl.tudelft.stocktrader.Order;
import nl.tudelft.stocktrader.Wallet;
import nl.tudelft.stocktrader.dal.DAOException;
import nl.tudelft.stocktrader.derby.DerbyCustomerDAO;
import nl.tudelft.stocktrader.derby.DerbyOrderDAO;

public class GetWalletByAccountIDTest {
	
	
	
	private static String dbURL = "jdbc:derby://localhost:1527/stonehenge";
    private static Connection conn = null;
    private static Statement stmt = null;


    public static void main(String[] args) {
    	createConnection();
    	
    	try {
//    		DerbyCustomerDAO derbyDao = new DerbyCustomerDAO(conn);
		
			DerbyOrderDAO orderDAO = new DerbyOrderDAO(conn);
			
			Order o = orderDAO.createOrder("uid:0", "MSFT", "buy", 3, -1, "CNY");
			
			System.out.println("get account id is " + o.getAccountId());
//	        Wallet w = orderDAO.getWallet(1);
//			Wallet w = derbyDao.getWallet(1);
	        
//	        System.out.println(w.getUserID()+w.getEur());
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    private static void createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            conn = DriverManager.getConnection(dbURL); 
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }
    
    
    
}
