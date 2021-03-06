package Controller;

import DAO.JDBC;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/** This class controls the additional reports screen.
 * @author Jessie Van Epps*/
public class AdditionalReportsController implements Initializable {
    @FXML
    private ChoiceBox<String> reportsMonthBox;

    @FXML
    private ChoiceBox<String> reportsTypeBox;

    @FXML
    private TextField reportsTotalField;

    @FXML
    private TableView<Appointment> contactScheduleTableView;

    @FXML
    private TableColumn<Appointment, String> appointmentIdCol;

    @FXML
    private TableColumn<Appointment, String> appointmentTitleCol;

    @FXML
    private TableColumn<Appointment, String> appointmentDescrCol;

    @FXML
    private TableColumn<Appointment, String> appointmentContactCol;

    @FXML
    private TableColumn<Appointment, String> appointmentTypeCol;

    @FXML
    private TableColumn<Appointment, String> appointmentStartCol;

    @FXML
    private TableColumn<Appointment, String> appointmentEndCol;

    @FXML
    private TableColumn<Appointment, String> appointmentCustIdCol;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private ComboBox<String> byYearContactCB;

    @FXML
    private TextField byYearTextField;

    @FXML
    private TextField yearInputField;

    private ObservableList<Appointment> contactAppointmentsOL = FXCollections.observableArrayList();
    private ObservableList<Appointment> contactAppointmentsByYear = FXCollections.observableArrayList();

    /** This controls the action of the month/type search button. This gets the month and type selected in choice boxes and queries database for total. */
    @FXML
    void searchReportsButtonPressed(ActionEvent event) throws SQLException {
        if (reportsMonthBox.getSelectionModel().isEmpty() || reportsTypeBox.getSelectionModel().isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("ERROR");
            a.setContentText("Please select a month and type");
            a.showAndWait();
        }else{
            String month = reportsMonthBox.getValue();
            String type = reportsTypeBox.getValue();

            int totalOfType = JDBC.getAmountOfType(month,type);
            reportsTotalField.setText(String.valueOf(totalOfType));
        }
    }

    /** This controls the actions of the contacts combobox associated with the tableview on the screen. When a contact is selected, a schedule
     * of the contacts appointments is displayed. The lambda expression filters the appointments by contact name.*/
    @FXML
    void contactSelected(ActionEvent event) throws SQLException {
        contactAppointmentsOL = JDBC.getContactAppointments();
        contactScheduleTableView.setItems(contactAppointmentsOL.filtered(appointment -> appointment.getContact().equals(contactComboBox.getValue())));
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescrCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        appointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        appointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        appointmentCustIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }

    /** This controls the action of the back button. Returns user to the record overview screen.*/
    @FXML
    void reportsBackButtonPressed(ActionEvent event) throws IOException {

        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/View/RecordOverview.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** This controls the actions of the additional report search button on the bottom of screen. Queries database for total appointments by contact and year.*/
    @FXML
    void totalByYearButtonPressed(ActionEvent event) throws SQLException {
        if (yearInputField.getText().isBlank() || (!isNumeral(yearInputField.getText()) || yearInputField.getText().trim().length() != 4)){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("ERROR");
            a.setContentText("Please input a valid year");
            a.showAndWait();
        }else{
            byYearTextField.setText(JDBC.getYearlyContactCount(yearInputField.getText(), byYearContactCB.getSelectionModel().getSelectedItem()));
        }

    }

    /** This method checks if data entered is numeric. Based on: https://stackoverflow.com/a/1102916*/
    public static boolean isNumeral(String n){
        try {
            Double.parseDouble(n);
            return true;
        }catch (NumberFormatException error) {
            return false;
        }
    }

    /** Initializes the class. Sets two text boxes to not be edited. Adds data to choice box. Queries database for appointment types, contacts and sets them
     * to comboboxes.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reportsTotalField.setEditable(false);
        byYearTextField.setEditable(false);
        reportsMonthBox.getItems().addAll("1","2","3","4","5","6","7","8","9","10","11","12");


        try {
            reportsTypeBox.setItems(JDBC.getAppointmentTypes());
            contactComboBox.setItems(JDBC.getContacts());
            byYearContactCB.setItems(JDBC.getContacts());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
