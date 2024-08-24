package gui.java;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.java.FarmersMarketConnector;

/**
 * Display detailed info about a market
 */
public class DetailedWindowController {
	@FXML private ListView<String> marketInfo, reviews;
	
	
	/**
	 * Query database for all info about farmers market
	 * @param connection database connection
	 * @param w main window
	 * @param fmid id of market.
	 */
	public void query(FarmersMarketConnector connection, FarmersMarketWindow w, String fmid) {
		// TODO Auto-generated method stub
		ObservableList<String> l = FXCollections.observableArrayList();
		ObservableList<String> l2 = FXCollections.observableArrayList();

		String infoQuery = "SELECT markets.fmid, markets.marketname, markets.street, markets.zip, cities.cityname, states.statename,avg(score)"
				+ " FROM farmers_markets.markets"
				+ " JOIN farmers_markets.zipcodes ON markets.zip = zipcodes.zip"
				+ " JOIN farmers_markets.cities ON zipcodes.cityid = cities.cityid"
				+ " JOIN farmers_markets.states ON zipcodes.stateid = states.stateid"
				+ " LEFT JOIN farmers_markets.reviews ON reviews.fmid = markets.fmid"
				+ " WHERE markets.fmid = " + fmid
				+ " GROUP BY markets.fmid, markets.marketname, markets.zip, cities.cityname, states.statename"
				+ " ORDER BY TRIM(markets.marketname) ;";
		
		String paymentsQuery = "SELECT DISTINCT payment_methods.paymentname from payment_methods, market_payments, markets "
				+ "WHERE market_payments.fmid=markets.fmid and payment_methods.paymentid = market_payments.paymentid and markets.fmid = " + fmid + ";";
		String productsQuery = "SELECT products.productname from products, market_products, markets"
				+ " WHERE market_products.fmid=markets.fmid and products.productid = market_products.productid and markets.fmid = "+fmid+";";
		
		String reviewsQuery = "SELECT users.username, reviews.score, reviews.review_text from users,reviews, markets "
				+ "WHERE users.userid = reviews.userid and reviews.fmid = markets.fmid and reviews.fmid = "+fmid+";";
			
		try {
			ResultSet r = connection.executeQuery(infoQuery);
			if (r.next()) {
                // Get metadata to determine column details
                ResultSetMetaData metaData = r.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Loop over all columns
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = r.getString(i);
                    String display = String.format("%s: %s", columnName, columnValue);
                    l.add(display);
                }
            } else {
                ErrorWindow.displayErrorWindow("ERROR:No data found for the specified query.");
                return;
            }
			
			ResultSet r2 = connection.executeQuery(paymentsQuery);
			while(r2.next()) {
				l.add(String.format("Accepted Payment Method: %s", r2.getString("paymentname")));
			}
			ResultSet r3 = connection.executeQuery(productsQuery);
			while(r3.next()) {
				l.add(String.format("Product: %s", r3.getString("productname")));
			}
			
			ResultSet r4 = connection.executeQuery(reviewsQuery);
			while(r4.next()) {
				String reviewer = r4.getString("username");
				String text = r4.getString("review_text");
				int rating = r4.getInt("score");
				
				l2.add(String.format("%s: \"%s\". Rating: %d", reviewer,text,rating));
			}
			
			marketInfo.setItems(l);
			reviews.setItems(l2);
		}catch(Exception e) {
			e.printStackTrace();
			w.closeConnection();
		}
		
		
	}

}
