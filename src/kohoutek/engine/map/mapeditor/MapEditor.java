package kohoutek.engine.map.mapeditor;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MapEditor extends Application {
	public static Stage stage;
	public Pane mainLayout;

	
	@Override
	public void start(Stage primaryStage) throws IOException {
		stage = primaryStage;
		stage.setResizable(false);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui2.fxml"));

		mainLayout = loader.load();

		Scene scene = new Scene(mainLayout);

		stage.setScene(scene);
		stage.setTitle("Map Editor");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
