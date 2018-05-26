package ttt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Game {

	private Tile[][] board = new Tile[3][3];
	private List<Combo> combos = new ArrayList<>();

	// private Pane paneCenter;
	// private VBox paneRight;

	private Button btnNewGame = new Button("New Game");
	private Label lblInfo = new Label("Press: New Game");
	Label lblWin = new Label("");

	private Label lblOpponentSelector = new Label("Opponent ID: ");
	private Label lblSelector = new Label("");

	private boolean playable = false;
	boolean playingCross = false;
	boolean waitingForNewGame = false;

	Producer producer;
	Consumer consumer;

	String selector = "";
	String opponentSelector = "";

	Game(Pane paneCenter, VBox paneRight) {
		// this.paneCenter = paneCenter;
		// this.paneRight = paneRight;

		selector = String.valueOf(new Random().nextInt(654321));
		lblSelector.setText("Your ID: " + selector);

		producer = new Producer(this);
		consumer = new Consumer(this);

		createContent(paneCenter, paneRight);
	}

	private void createContent(Pane paneCenter, VBox paneRight) {

		lblSelector.setFont(Font.font(12));
		lblOpponentSelector.setFont(Font.font(12));

		lblWin.setFont(Font.font(12));
		lblWin.setWrapText(true);
		lblWin.setPadding(new Insets(20, 10, 10, 10));

		lblInfo.setFont(Font.font(12));
		lblInfo.setPadding(new Insets(20, 10, 10, 10));
		paneRight.getChildren().addAll(lblSelector, lblOpponentSelector, lblInfo, btnNewGame, lblWin);
		btnNewGame.setOnAction(e -> newGame());

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Tile tile = new Tile(this);
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

	// Only for opponent moves
	public void makeMove(int position) {
		int t1 = position % 3;
		int t2 = position / 3;
		if (board[t1][t2].getValue().equals("")) {
			if (playingCross == false)
				board[t1][t2].drawX();
			else
				board[t1][t2].drawO();

			Platform.runLater(() -> lblWin.setText("Your turn."));
			setPlayable(true);
		} else
			System.out.println("ERROR: Received wrong move!");

		checkState();
	}

	public void newGame() {
		if (waitingForNewGame)
			return;
		if( !opponentSelector.equals("") )
			producer.sendClose();
		
		opponentSelector = "";
		lblOpponentSelector.setText("Opponent ID: " + opponentSelector);
		
		waitingForNewGame = true;
		playingCross = false;
		setPlayable(false);
		lblWin.setText("Looking for opponent to join the game.");

		lblInfo.setText("You are playing as O.");

		if (!consumer.checkNewGameMessage()) {
			producer.sendNewGame();
			playingCross = true;
			lblInfo.setText("You are playing as X.");
		} else {
			producer.sendStart();
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[j][i].reset();
			}
		}
	}

	public void setOpponent(String opponentSelector) {
		this.opponentSelector = opponentSelector;
		if (opponentSelector.equals("") ) {
			Platform.runLater(() -> lblWin.setText("Opponent has left the game. Press New Game to start again."));
			Platform.runLater(() -> lblOpponentSelector.setText("Opponent ID: " + opponentSelector));

			setPlayable(false);
			waitingForNewGame = false;

			return;
		}

		Platform.runLater(() -> lblOpponentSelector.setText("Opponent ID: " + opponentSelector));

		if (playingCross) {
			Platform.runLater(() -> lblWin.setText("Opponent has joined the game. Your turn."));
			setPlayable(true);
		} else {
			Platform.runLater(() -> lblWin.setText("Opponent has joined the game. Wait for your turn."));
			setPlayable(false);
		}
		waitingForNewGame = false;
	}

	public boolean isPlayable() {
		return playable;
	}

	public void setPlayable(boolean value) {
		playable = value;
	}

	boolean checkState() {
		for (Combo combo : combos) {
			if (combo.isComplete()) {
				playable = false;
				if (combo.getValue().equals("X"))
					Platform.runLater(() -> lblWin.setText("X has won!"));
				else
					Platform.runLater(() -> lblWin.setText("O has won!"));
				return true;
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[j][i].getValue().equals(""))
					return false;
			}
		}
		playable = false;
		Platform.runLater(() -> lblWin.setText("Draw!"));
		return true;
	}

	public void close() {

		consumer.removeNewGameMessage();
		producer.sendClose();

		producer.closeConnection();
		consumer.closeConnection();
	}

}
