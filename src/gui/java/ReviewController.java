package gui.java;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.java.FarmersMarketConnector;

/**
 * Window that allows user to input review.
 */
public class ReviewController {
	@FXML
	private TextField username,review;
	@FXML
	private Spinner<Integer> rating;
	private FarmersMarketConnector connection;
	private int fmid;
	private FarmersMarketWindow window;
	
	/**
	 * Set up default values for rating
	 */
	@FXML
	void initialize() {
		SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 0);
        rating.setValueFactory(valueFactory);
	}
	
	/**
	 * Post review
	 * @throws SQLException when a database error occurs.
	 */
	@FXML
	private void postReview() throws SQLException{
		String user = this.username.getText();
		String text = review.getText();
		Integer r = rating.getValue();
		if(text.length() > 255) {
			ErrorWindow.displayErrorWindow("Review text must not exceed 255 characters.");
			return;
		}
		
		String update = "INSERT IGNORE INTO users (username) VALUES (\""+user+"\");";
		
		
		this.connection.executeUpdate(update);

		int id = -1;
		// get user ID now
		ResultSet set = this.connection.executeQuery("SELECT users.userid from users where users.username= \"" + user + "\";");
		if(set.next())id = set.getInt("userid");
		
		update=String.format("INSERT INTO reviews (review_text, score, fmid, userid) VALUES(\"%s\",%d,%d,%d);", text,r,this.fmid,id);
		try {
			this.connection.executeUpdate(update);
		}catch(SQLException e) {
			ErrorWindow.displayErrorWindow(user + " has already reviewed this market.");
			return;
		}
		this.window.redisplay();
		Stage currentWindow = (Stage) rating.getScene().getWindow();
	    currentWindow.close();
		
		
		
	}

	/**
	 * Pass connection, window, fmid to controller
	 * @param c database connection
	 * @param fmid farmers market id
	 * @param w main window
	 */
	public void pass(FarmersMarketConnector c, int fmid, FarmersMarketWindow w) {
		this.connection = c;
		this.fmid = fmid;
		this.window = w;
	}
}
