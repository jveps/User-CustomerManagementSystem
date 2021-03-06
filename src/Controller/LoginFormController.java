package Controller;

import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import DAO.JDBC;
import javafx.stage.Stage;
/** This class controls the login form.
 * @author Jessie Van Epps*/
public class LoginFormController implements Initializable {
    @FXML
    private Label loginUsernameLabel;

    @FXML
    private Label loginPasswordLabel;

    @FXML
    private TextField loginUsernameField;

    @FXML
    private TextField loginPasswordField;

    @FXML
    private Button loginOkButton;

    @FXML
    private Button loginCancelButton;

    @FXML
    private Label zoneIDLabel;

    Scene scene;
    Stage stage;

    /** Controls the actions of the OK button. Gets username and password, queries database to see if the login is successful. Logs successful and
     * unsuccessful attempts. Checks if there is an appointment within 15 mins of user login. */
    @FXML
    void onActionOkButton(ActionEvent event) throws IOException, SQLException {
        String uName = loginUsernameField.getText();
        String pWord = loginPasswordField.getText();
        if (JDBC.loginTest(uName,pWord)){
            attemptLogger(uName, true);
            System.out.println("CORRECT PASSWORD");
            User currentUser = new User(uName, JDBC.getCurrentUserID(uName));
            System.out.println("The current user is: " + currentUser.getUsername());

            if (JDBC.checkFifteenMins()){
                /*Alert _15MinAlert = new Alert(Alert.AlertType.INFORMATION);
                _15MinAlert.setTitle("IMPORTANT");
                _15MinAlert.setContentText("Appointment within 15 minutes");
                _15MinAlert.showAndWait();*/
                System.out.println("Appointment within 15 mins found");
            }
            //Open RecordOverview screen
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/View/RecordOverview.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        else{
            //Show error in appropriate language for incorrect username or password
            attemptLogger(uName, false);
            ResourceBundle rb = ResourceBundle.getBundle("sample/Nat", Locale.getDefault());
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle(rb.getString("Error"));
            a.setContentText(rb.getString("Username or password incorrect"));
            a.showAndWait();

        }

    }

    /** This method records login attempts. Records both successful and unsuccessful login attempts with data and time and username.*/
    public void attemptLogger(String uName, boolean successful) throws IOException {
        String fileName = "src/login_activity.txt";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime attemptedTime = LocalDateTime.now();
        ZonedDateTime zdt = attemptedTime.atZone(ZoneOffset.UTC);
        FileWriter fwriter = new FileWriter(fileName, true);
        PrintWriter output = new PrintWriter(fwriter);
        if (successful){
            output.println(uName + " logged in successfully at " + zdt.format(dtf));

        }
        else{
            output.println(uName + " tried unsuccessfully to log in at " + zdt.format(dtf));
        }
        output.close();
    }

    /** Initializes the class. Gets default language, changes text based on users default language.*/
    public void initialize(URL url, ResourceBundle rb){
        //Gets default language
        rb = ResourceBundle.getBundle("sample/Nat", Locale.getDefault());

        //Applies text based on default language
        loginUsernameLabel.setText(rb.getString("Username") + ":");
        loginPasswordLabel.setText(rb.getString("Password") + ":");
        loginOkButton.setText(rb.getString("Okay"));
        loginCancelButton.setText(rb.getString("Cancel"));
        ZoneId zID = ZoneId.of(TimeZone.getDefault().getID());
        zoneIDLabel.setText(zID.toString());

        //TEST
        /*
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zonedLDT = ldt.atZone(ZoneId.systemDefault());
        ZonedDateTime zonedEST = zonedLDT.withZoneSameInstant(ZoneId.of("America/New_York"));


        ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdDate= zdt.format(dtf);
        System.out.println("Created date: " + createdDate);
        System.out.println("DEFAULT: " + zonedLDT.format(dtf));
        System.out.println("EST: " + zonedEST);
        System.out.println("OFFSET: " + ldt.atOffset(ZoneOffset.UTC));

        LocalDateTime sLDT = LocalDateTime.parse("2022-04-04 01:11:11",dtf);
        System.out.println(sLDT.isAfter(ldt));
        System.out.println("SLDT " + sLDT.toString());*/
        System.out.println("LDT now: " + LocalDateTime.now());
        System.out.println("LDT UTC: " + LocalDateTime.now(ZoneOffset.UTC));
        System.out.println("LDT East " + LocalDateTime.now(ZoneId.of("America/New_York")));

        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.systemDefault());
        System.out.println("ZDT LOCAL: " + zdt.getZone() + zdt.toString());
        System.out.println("ZDT UTC: " + zdt.withZoneSameInstant(ZoneOffset.UTC));
        System.out.println("ZDT EST: " + zdt.withZoneSameInstant(ZoneId.of("America/New_York")));


    }

}
