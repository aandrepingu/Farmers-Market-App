package gui.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.java.FarmersMarketConnector;

/**
 * Main app window
 */
public class FarmersMarketWindow {
	private Stage stage;
	@FXML
	private Button postReviewButton;
	@FXML
	private TextField cityField,stateField,zipField, distanceField;
	
	@FXML
	private ListView<String> marketsDisplay;
	private ObservableList<String> displayed;
	
	private FarmersMarketConnector connection;
	
	private boolean displayedByZip, displayedByCityState;
	
	/**
	 * Connect to database and display all
	 */
	@FXML
	void initialize() {
//		this.postReviewButton.setVisible(false);
		this.connection = new FarmersMarketConnector("jdbc:mysql://localhost:3306/farmers_markets", "appuser","ilovejava");
		displayed = FXCollections.observableArrayList();
		displayAllMarkets();
	}
	
	
	/**
	 * Display a result set to the app.
	 * @param markets markets displayed from a SQL query.
	 */
	private void displayResultSetBasic(ResultSet markets) {
		try {
			this.displayed.clear();
			while(markets.next()) {
				// display 
				String name = markets.getString("marketname");
				int zip = markets.getInt("zip");
				String zipF = Integer.toString(zip);
				if(zipF.length() == 3) {
					zipF=String.format("00%s", zipF);
				}
				else if(zipF.length() == 4) {
					zipF=String.format("0%s", zipF);
				}
				String city = markets.getString("cityname");
				String state = markets.getString("statename");
				String rating = markets.getString("avg(score)");
				String fmid = markets.getString("fmid");
				String display = String.format("%s: %s, %s %s; FMID %s, Average rating: %s", name,city,state,zipF,fmid, rating);
				this.displayed.add(display);
			}
			this.marketsDisplay.setItems(this.displayed);
		}catch(SQLException e) {
			e.printStackTrace();
			closeConnection();
			this.connection = null;
		}
	}
	
	/**
     * Upon double clicking a farmers market 
     *
     * @param event mouse event.
     */
    @FXML
    private void displayDetailed(MouseEvent event)throws IOException {

        String market = this.marketsDisplay.getSelectionModel().getSelectedItem();
        if (market!= null) {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
            	String fmid = market.substring(market.indexOf("FMID ")+5, market.indexOf(',', market.indexOf("FMID ")+5));
                this.postReviewButton.setVisible(true);
                Stage s = new Stage();
                FXMLLoader f = new FXMLLoader();
    			Parent root = f.load(new FileInputStream("src/gui/resources/detailedWindow.fxml"));
    			
    			s.setTitle("Farmers Markets");
    			s.setScene(new Scene(root));
    			s.show();
    			DetailedWindowController controller = (DetailedWindowController)f.getController();
    			controller.query(this.connection, this, fmid);
            }
        }
    }
	
    /**
     * Display all markets again.
     */
    @FXML
    private void displayAllMarkets() {
    	displayed.clear();
    	this.displayedByCityState = false;
    	this.displayedByZip = false;
    	try {
			ResultSet markets = connection.executeQuery("SELECT markets.fmid, markets.marketname, markets.zip, cities.cityname, states.statename,avg(score) "
					+ "FROM farmers_markets.markets "
					+ "JOIN farmers_markets.zipcodes ON markets.zip = zipcodes.zip "
					+ "JOIN farmers_markets.cities ON zipcodes.cityid = cities.cityid "
					+ "JOIN farmers_markets.states ON zipcodes.stateid = states.stateid "
					+ "LEFT JOIN farmers_markets.reviews ON reviews.fmid = markets.fmid "
					+ "GROUP BY markets.fmid, markets.marketname, markets.zip, cities.cityname, states.statename "
					+ "ORDER BY TRIM(markets.marketname);");
			

			displayResultSetBasic(markets);
		}catch(SQLException e) {
			e.printStackTrace();
			closeConnection();
			this.connection = null;
		}

    }
    
    
	/**
	 * Display by inputted city and state.
	 */
	@FXML
	private void displayByCity() {
		double distance;
		String city=cityField.getText(), state = stateField.getText();
		if(city.isBlank()) {
			ErrorWindow.displayErrorWindow("Please enter a city to search by.");
			return;
		}
		if(state.isBlank()) {
			ErrorWindow.displayErrorWindow("Please enter a state to search by.");
			return;
		}
		try {
			distance = Double.parseDouble(distanceField.getText()) * 1.609; // distance in km
			if(distance <= 0.0) throw new NumberFormatException();
		}catch(NumberFormatException e) {
			ErrorWindow.displayErrorWindow("Distance must be a positive decimal number!");
			return;
		}
		String getIDs = String.format("select cities.cityid, states.stateid from cities, states where cities.cityname = '%s' and states.statename = '%s'",city, state);
		int cityid, stateid;
		try {
			ResultSet r = this.connection.executeQuery(getIDs);
			if(r.next()) {
				cityid = r.getInt("cityid");
				stateid = r.getInt("stateid");
			}
			else {
				ErrorWindow.displayErrorWindow("City or State not found.");
				return;
			}
		}catch(Exception e) {
			e.printStackTrace();
			closeConnection();
			return;
		}
		
		String query = "SELECT markets.fmid, markets.marketname, markets.zip, cities.cityname, states.statename,avg(score)"
				+ "				FROM farmers_markets.markets "
				+ "				JOIN farmers_markets.zipcodes ON markets.zip = zipcodes.zip"
				+ "				JOIN farmers_markets.cities ON zipcodes.cityid = cities.cityid "
				+ "				JOIN farmers_markets.states ON zipcodes.stateid = states.stateid "
				+ "				LEFT JOIN farmers_markets.reviews ON reviews.fmid = markets.fmid "
				+ "                WHERE distance_citystate(markets.fmid,"+cityid+","+stateid+") < " + distance 
				+ "				GROUP BY markets.fmid, markets.marketname, markets.zip, cities.cityname, states.statename"
				+ "                ORDER BY TRIM(markets.marketname);";
		try {
		// execute query
			ResultSet r = this.connection.executeQuery(query);
			if(r != null) {
				this.displayResultSetBasic(r);
				this.displayedByCityState = true;
			}
			else System.out.println("null result set! displayByCity");
		}catch(SQLException e) {
			e.printStackTrace();
			closeConnection();
			this.connection = null;
			return;
		}
		
	}

	/**
	 * Display by inputted zip.
	 */
	@FXML
	private void displayByZip() {
		
		int zip;
		String z = zipField.getText();
		if(z.isBlank()) {
			ErrorWindow.displayErrorWindow("Please enter a zip code to search by.");
			return;
		}
		if(!isValidZip(z)) {
			ErrorWindow.displayErrorWindow("Invalid zip code!");
			return;
		}
		double distance;
		try {
			distance = Double.parseDouble(distanceField.getText()) * 1.609;
			if(distance <= 0.0) throw new Exception();
		}catch(Exception e) {
			ErrorWindow.displayErrorWindow("Distance must be a decimal greater than 0!");
			return;
		}
		zip = Integer.parseInt(z);
		String query = "SELECT markets.fmid, markets.marketname, markets.zip, cities.cityname, states.statename,avg(score)"
				+ "				FROM farmers_markets.markets"
				+ "				JOIN farmers_markets.zipcodes ON markets.zip = zipcodes.zip"
				+ "				JOIN farmers_markets.cities ON zipcodes.cityid = cities.cityid "
				+ "				JOIN farmers_markets.states ON zipcodes.stateid = states.stateid "
				+ "				LEFT JOIN farmers_markets.reviews ON reviews.fmid = markets.fmid "
				+ "                WHERE distance_zip(markets.fmid, "+zip+") < " + distance
				+ "				GROUP BY markets.fmid, markets.marketname, markets.zip, cities.cityname, states.statename "
				+ "                ORDER BY TRIM(markets.marketname);"; 
		try {
		// execute query
			ResultSet r = this.connection.executeQuery(query);
			if(r != null) {
				this.displayResultSetBasic(r);
				this.displayedByZip = true;
			}
			else System.out.println("null result set! displayByCity");
		}catch(SQLException e) {
			e.printStackTrace();
			closeConnection();
			this.connection = null;
			return;
		}
			
		
	}
	
	/**
	 * Post review
	 * @throws IOException if error occurs opening new fxml window
	 */
	@FXML
	private void postReview() throws IOException {
		String market = this.marketsDisplay.getSelectionModel().getSelectedItem();
		if(market != null) {
			String fmid = market.substring(market.indexOf("FMID ")+5, market.indexOf(',', market.indexOf("FMID ")+5));
			Stage s = new Stage();
			FXMLLoader f = new FXMLLoader();
			Parent root = f.load(new FileInputStream("src/gui/resources/review.fxml"));
			
			s.setTitle("Farmers Markets");
			s.setScene(new Scene(root));
			s.show();
			ReviewController controller = (ReviewController)f.getController();
			controller.pass(this.connection,Integer.parseInt(fmid), this);
		}
		
		
		
	}

	
	/**
	 * redisplay displayed markets to update when a review was posted.
	 */
	void redisplay() {
		if(this.displayedByZip) {
			this.displayByZip();
		}
		else if(this.displayedByCityState) {
			this.displayByCity();
		}
		else {
			this.displayAllMarkets();
		}
	}
	/**
	 * Determine if a string is a valid zip code
	 * @param zip zip code string
	 * @return true if zip is a valid zipcode false otherwise.
	 */
	private boolean isValidZip(String zip) {
		// TODO Auto-generated method stub
		if(zip.length() != 5) return false;
		for(int i = 0; i < 5; ++i) {
			char c = zip.charAt(i);
			if(c < '0' || c > '9') return false;
		}
		return true;}
	
	


	/**
	 * Display help window directing user to manual
	 */
	@FXML
	private void displayHelpWindow() {
		ErrorWindow.displayInfoWindow("Please consult the user manual for\ndetailed information about this app.");
	}

	/**
	 * Pass the stage/window to the controller to close.
	 * @param arg0 main stage.
	 */
	public void pass(Stage arg0) {
		// TODO Auto-generated method stub
		this.stage = arg0;
		this.stage.setOnCloseRequest(event->{
			closeConnection();
			Platform.exit();
		});
		
	}
	/**
	 * Close connection.
	 */
	void closeConnection() {
		try {
			this.connection.closeConnection();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
}
