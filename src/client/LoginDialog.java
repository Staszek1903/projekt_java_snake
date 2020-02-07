package client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class LoginDialog extends Dialog<Pair<String,String>> {
    public LoginDialog(String msg, String button){
        setTitle(msg);
        setHeaderText((msg));

        //setGraphic(new ImageView(this.getClass()));
        ButtonType loginButton = new ButtonType(button, ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(loginButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"),0,0);
        grid.add(username,1,0);
        grid.add(new Label("Password:"),0,1);
        grid.add(password,1,1);

        Node login = getDialogPane().lookupButton(loginButton);
        login.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) ->{
            login.setDisable(newValue.trim().isEmpty());
        });

        getDialogPane().setContent(grid);

        Platform.runLater(() ->{username.requestFocus();});

        setResultConverter(dialogButton -> {
            if(dialogButton == loginButton){
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
    }
}
