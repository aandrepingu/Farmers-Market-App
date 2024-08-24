/**
 * 
 */
package gui.java;

import java.io.FileInputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class which loads and runs the GUI program.
 */
public class FarmersMarketGUI extends Application {

//	/**
//	 * 
//	 */
//	public SimGUI() {
//		// TODO Auto-generated constructor stub
//	}

	/**
	 * Starts the program by loading the simulation window from FXML file.
	 * @param arg0 Main window.
	 */
	@Override
	public void start(Stage arg0) {
		// TODO Auto-generated method stub
		try {
			FXMLLoader f = new FXMLLoader();
			Parent root = f.load(new FileInputStream("src/gui/resources/FarmersMarketsApp.fxml"));
			arg0.setTitle("Farmers Markets");
			arg0.setScene(new Scene(root));
			FarmersMarketWindow window = f.getController();
			window.pass(arg0);
			arg0.show();
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Launches the program.
	 * @param args cmd line arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
