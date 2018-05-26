package ttt;

import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Tile extends StackPane {
	private Text text = new Text("");

	private Game game;

	public int position;

	Tile(Game game) {
		this.game = game;

		Rectangle border = new Rectangle(200, 200);
		border.setFill(null);
		border.setStroke(Color.BLACK);
		border.setStrokeWidth(4.0);

		text.setFont(Font.font(72));

		setAlignment(Pos.CENTER);
		getChildren().addAll(border, text);

		setOnMouseClicked(event -> clicked(event));

	}

	private void clicked( MouseEvent event ) {
		if( event.getButton() != MouseButton.PRIMARY )
			return;
		if (!game.isPlayable())
			return;

		if (getValue().equals(""))
		{
			if( game.playingCross )
				drawX();
			else
				drawO();
			
			game.producer.sendMove(position);
			
			game.setPlayable(false);
			
			game.lblWin.setText("Wait for your turn.");
			
			game.checkState();
		}
	}

	void drawX() {
		text.setText("X");
	}

	void drawO() {
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

class Combo {
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
