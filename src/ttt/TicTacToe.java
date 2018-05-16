package ttt;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TicTacToe extends Application {

	Producer producer = new Producer();
	Consumer consumer = new Consumer();
	Label l1;
	private boolean playable = true;

	private Pane paneCenter;
	private Label win = new Label("");
	private Tile[][] board = new Tile[3][3];

	private boolean player = false;

	private List<Combo> combos = new ArrayList<>();

	Stage stage;
	
	public static void main(String[] args) {
		launch(args);

	}

	private void createContent(Pane paneCenter) {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Tile tile = new Tile();
				tile.setTranslateX(j * 200);
				tile.setTranslateY(i * 200);
				tile.position = i * 3 + j;
				paneCenter.getChildren().add(tile);

				board[j][i] = tile;
			}
		}

		// horizontal
		for (int y = 0; y < 3; y++) {
			combos.add(new Combo(board[0][y], board[1][y], board[2][y]));
		}

		// vertical
		for (int x = 0; x < 3; x++) {
			combos.add(new Combo(board[x][0], board[x][1], board[x][2]));
		}

		// diagonals
		combos.add(new Combo(board[0][0], board[1][1], board[2][2]));
		combos.add(new Combo(board[2][0], board[1][1], board[0][2]));
	}

	@Override
	public void start(Stage theStage) {
		stage = theStage;
		theStage.setTitle("Canvas Example");
		theStage.setWidth(800);
		theStage.setHeight(600);

		BorderPane root = new BorderPane();
		paneCenter = new Pane();
		Canvas canvas = new Canvas(600, 600);

		paneCenter.getChildren().addAll(canvas);
		root.setCenter(paneCenter);

		VBox paneRight = new VBox();
		paneRight.setPrefSize(200, 600);
		paneRight.setPadding(new Insets(20));
		paneRight.setAlignment(Pos.TOP_CENTER);
		Button b1 = new Button("New Game");
		b1.setOnAction(e -> newGame());

		win.setFont(Font.font(24));
		win.setPadding(new Insets(50, 10, 10, 10));
		l1 = new Label("Press: New Game");
		l1.setPadding(new Insets(10, 10, 10, 10));

		paneRight.getChildren().addAll(l1, b1, win);
		root.setRight(paneRight);

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTGREY);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		createContent(paneCenter);

		Scene scene = new Scene(root, 800, 600);
		theStage.setScene(scene);
		theStage.show();
		
		newGame();
		
		
	}

	private class Tile extends StackPane {
		private Text text = new Text("");
		public int position;

		Tile() {
			Rectangle border = new Rectangle(200, 200);
			border.setFill(null);
			border.setStroke(Color.BLACK);
			border.setStrokeWidth(4.0);

			text.setFont(Font.font(72));

			setAlignment(Pos.CENTER);
			getChildren().addAll(border, text);

			setOnMouseClicked(event -> {
				if (playable && event.getButton() == MouseButton.PRIMARY) {
					if (!getValue().equals("")) {
						return;
					}
					if(player)
						drawO();
					else
						drawX();

					//turnX = !turnX;
					
					producer.sendQueueMessage(position);
					if( checkState() )
					{
						return;
					}
					
					int tmp = consumer.receiveQueueMessages();
					if( tmp == 42 )
					{
						newGameNoWait();
						return;
					}
					int t1 = tmp%3;
					int t2 = tmp/3;
					if( board[t1][t2].getValue().equals("") )
					{
						if( player == true )
							board[t1][t2].drawX();
						else
							board[t1][t2].drawO();
					}
					else
						System.out.println("BŁĄD - ODEBRANO ZŁY RUCH!");
					
					checkState();
				}
			});

		}

		private void drawX() {
			text.setText("X");
		}

		private void drawO() {
			text.setText("O");
		}

		public String getValue() {
			return text.getText();
		}

		public void reset() {
			text.setText("");
			text.setFill(Color.BLACK);
		}

		public void setWin() {
			text.setFill(Color.RED);
		}
	}

	private class Combo {
		private Tile[] tiles;

		public Combo(Tile... tiles) {
			this.tiles = tiles;
		}

		public boolean isComplete() {
			if (tiles[0].getValue().isEmpty())
				return false;

			if (tiles[0].getValue().equals(tiles[1].getValue()) && tiles[0].getValue().equals(tiles[2].getValue())) {
				tiles[0].setWin();
				tiles[1].setWin();
				tiles[2].setWin();
				return true;
			}
			return false;
		}

		public String getValue() {
			return tiles[0].getValue();
		}
	}

	void newGame() {
		win.setText("");
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[j][i].reset();
			}
		}
		playable = true;
		
		if( consumer.receiveWelcomeMessage() != null )
		{
			player = true;
			int tmp = consumer.receiveQueueMessages();
			int t1 = tmp%3;
			int t2 = tmp/3;
			if( board[t1][t2].getValue().equals("") )
				board[t1][t2].drawX();
			else
				System.out.println("BŁĄD - ODEBRANO ZŁY RUCH!");
			checkState();
		}
		else
		{
			player = false;
			// My mamy ruch jako pierwsi
			producer.sendWelcomeMessage();
		}
		l1.setText("You play: " + (player ? "O" : "X"));
	}
	
	void newGameNoWait() {
		win.setText("");
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[j][i].reset();
			}
		}
		playable = true;
		
		player = true;
		l1.setText("You play: " + (player ? "O" : "X"));
		int tmp = consumer.receiveQueueMessages();
		int t1 = tmp%3;
		int t2 = tmp/3;
		if( board[t1][t2].getValue().equals("") )
			board[t1][t2].drawX();
		else
			System.out.println("BŁĄD - ODEBRANO ZŁY RUCH!");
		checkState();
	}

	private boolean checkState() {
		for (Combo combo : combos) {
			if (combo.isComplete()) {
				playable = false;
				if (combo.getValue().equals("X"))
					win.setText("X has won!");
				else
					win.setText("O has won!");
				return true;
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if( board[j][i].getValue().equals("") )
					return false;
			}
		}
		win.setText("Draw!");
		return true;
	}

}
