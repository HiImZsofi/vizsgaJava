package hu.petrik.konyvtarasztali;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.Optional;

public class HelloController {

    @FXML
    private TableColumn<Konyv, String> titleCol;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<Konyv> booksTable;
    @FXML
    private TableColumn<Konyv, String> authorCol;
    @FXML
    private TableColumn<Konyv, Integer> publish_yearCol;
    @FXML
    private TableColumn<Konyv, Integer> page_countCol;
    private DatabaseConn db;


    public void initialize(){
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publish_yearCol.setCellValueFactory(new PropertyValueFactory<>("publish_year"));
        page_countCol.setCellValueFactory(new PropertyValueFactory<>("page_count"));
        try {
            db = new DatabaseConn();
            refreshTableData();
        } catch (SQLException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Hiba történt az adatbázishoz kapcsolódáskor.");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                Platform.exit();
            });
        }
    }

    private void refreshTableData() throws SQLException {
        booksTable.getItems().clear();
        booksTable.getItems().addAll(db.readBooks());
    }

    @FXML
    public void deleteClick(ActionEvent actionEvent) {
        Konyv selected = booksTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Előbb válassz");
            alert.showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Törlöd?");
        Optional<ButtonType> optionalButtonType = confirm.showAndWait();
        if(optionalButtonType.isPresent() && optionalButtonType.get().equals(ButtonType.OK)){
            deleteBook(selected);
        }
    }

    private void deleteBook(Konyv selected) {
        try {
            if(db.deleteBook(selected)){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Sikeres törlés");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Ez már korábban ki lett törölve");
                alert.showAndWait();
            }
            refreshTableData();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Hiba történt a törlés során");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}