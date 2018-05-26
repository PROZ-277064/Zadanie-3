package ttt;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TicTacToe extends Application {

	private Game game;

	Stage stage;

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage theStage) {

		stage = theStage;
		theStage.setTitle("TicTacToe");
		theStage.setWidth(800);
		theStage.setHeight(630);
		
		theStage.setMaxHeight(630);
		theStage.setMaxWidth(800);
		theStage.setMinHeight(630);
		theStage.setMinWidth(800);

		BorderPane root = new BorderPane();
		Pane paneCenter = new Pane();
		Canvas canvas = new Canvas(604, 604);

		paneCenter.getChildren().addAll(canvas);
		root.setCenter(paneCenter);

		VBox paneRight = new VBox();
		paneRight.setPrefSize(196, 604);
		paneRight.setMinHeight(604);
		paneRight.setMaxHeight(604);
		paneRight.setMinWidth(196);
		paneRight.setMaxWidth(196);
		paneRight.setPadding(new Insets(20));
		paneRight.setAlignment(Pos.TOP_CENTER);

		root.setRight(paneRight);

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGREY);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		Scene scene = new Scene(root, 800, 604);
		theStage.setScene(scene);
		theStage.show();

		game = new Game(paneCenter, paneRight);
	}

	@Override
	public void stop() {
		game.close();
	}

}
