package client;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Timestamp;
import java.util.Comparator;

public class ScoreTable extends TableView {
    public static class Record{
        private final SimpleStringProperty login;
        private final SimpleStringProperty score;
        private final SimpleStringProperty stamp;

        public Record(String login, int score, Timestamp stamp){
            this.login = new SimpleStringProperty(login);
            this.score = new SimpleStringProperty(String.valueOf(score));
            this.stamp = new SimpleStringProperty(stamp.toString());
        }

        public String getScore() {
            return score.get();
        }
        public String getStamp() { return stamp.get(); }
        public String getLogin() { return login.get(); }
    }

    private ObservableList<Record> items = FXCollections.observableArrayList();

    public ScoreTable(){
        TableColumn login =  new TableColumn("player");
        login.setCellValueFactory(new PropertyValueFactory<>("login"));
        TableColumn score = new TableColumn("score");
        score.setCellValueFactory(new PropertyValueFactory<>("score"));
        TableColumn time  = new TableColumn("time");
        time.setCellValueFactory(new PropertyValueFactory<>("stamp"));
        getColumns().addAll(login,score,time);
        setItems(items);
    }

    void add(String login, int score, Timestamp stamp){
        items.add(new Record(login, score,stamp));
        FXCollections.sort(items, new Comparator<Record>() {
            @Override
            public int compare(Record record, Record t1) {
                int r = Integer.parseInt(record.score.get());
                int t = Integer.parseInt(t1.score.get());
                return -Integer.compare(r, t);
            }
        });
    }
}
