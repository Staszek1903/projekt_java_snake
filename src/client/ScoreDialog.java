package client;

import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;

import java.sql.Timestamp;



public class ScoreDialog extends Alert {

    private ScoreTable table = new ScoreTable();

    public ScoreDialog() {
        super(AlertType.INFORMATION);
        setTitle("SCORE");
        setHeaderText("SCORE");
        //setContentText("SCORE");

        GridPane grid = new GridPane();
        grid.add(table,0,0);

        getDialogPane().setExpandableContent(grid);
        getDialogPane().setExpanded(true);
    }

    public void add(String login, int score, Timestamp stamp){
        table.add(login, score,stamp);
    }


}
