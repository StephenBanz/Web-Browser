package sample;
import com.sun.prism.shader.Solid_TextureRGB_AlphaTest_Loader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    String htLink="http://";
    LinkedList<String> backList=new LinkedList<String>();
    LinkedList<String> nextList=new LinkedList<String>();
    @FXML
    TextField addressBar;
    String adrsLink;
    String Link;
    @FXML
    ProgressBar indicator;
    @FXML
    WebView web;
    @FXML
    ComboBox comboBox;
    WebEngine engine;
    String value;



    public void Loading(String adrs){
        try{
            String httpRmv=adrs;
            System.out.println("inside loading()");
            if(adrs.substring(0,7).equals("http://")){ }
            else if( adrs.substring(0,8).equals("https://")){ }
            else {
                httpRmv="http://"+adrs;
                System.out.println(httpRmv);
            }
            //engine.load(httpRmv);

            engine.load(httpRmv);
        }catch(Exception e){System.out.println(e);}
    }

    public void go(ActionEvent actionEvent){              //Loads the URL entered
        System.out.println("go() called");
        adrsLink = addressBar.getText().toString();
        addressBar.setOnAction(a ->Loading(adrsLink));
        if(adrsLink.equals(backList.getLast())){}
        else {
            backList.addLast(adrsLink);
        }
        engine.getLoadWorker().stateProperty().addListener((observable , oldValue , newState) ->{
            if(newState== Worker.State.FAILED)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText("Wrong URL");
                alert.setContentText("Please check the URL and try again..");
                alert.showAndWait();
            }

        });
        engine.getLoadWorker().runningProperty().addListener((observable , oldValue , newValue) ->{
        if (!newValue) // if !running
            addressBar.textProperty().unbind();
        else
            addressBar.textProperty().bind(engine.locationProperty());
    });
    Loading(adrsLink);
nextList.clear();
indicator.progressProperty().bind(engine.getLoadWorker().progressProperty());
indicator.visibleProperty().bind(engine.getLoadWorker().runningProperty());
addressBar.focusedProperty().addListener((observable,oldValue , newValue) -> {
        if (newValue)
            Platform.runLater(() -> addressBar.selectAll());
    });}

    public void add(){
        System.out.println("add() called");
        System.out.println(web.getEngine().getLocation());
        Link=String.valueOf(web.getEngine().getLocation());
        if(Link.equals(backList.getLast())){}
        else {
            backList.addLast(Link);
        }
    }

    public void refresh(){                                                //refresh the page
        System.out.println("refresh() called");
        Loading(backList.getLast());
    }

    public void back(){                                                    //Goes 1 page back
        System.out.println("back() called");
        System.out.println(backList);
        System.out.println(nextList);
        System.out.println(backList.getLast());
        nextList.addLast(backList.getLast());
        backList.removeLast();
        System.out.println(backList.getLast());
        Loading(backList.getLast());
        System.out.println(backList);
        System.out.println(nextList);
    }

    public void next(){                                                   //Goes 1 page next
        System.out.println("next() called");
        backList.addLast(nextList.getLast());
        Loading(nextList.getLast());
        System.out.println(nextList);
        nextList.removeLast();
    }

    public void bookmark() throws Exception{     //Bookmarks the current page
        System.out.println("bookmark() called");
        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection( "jdbc:mysql://localhost:3306/browser?autoReconnect=true&useSSL=false", "root","root123");
        PreparedStatement ps=con.prepareStatement("insert into bookmark values(?)");
        adrsLink = addressBar.getText().toString();
        if(adrsLink != null && !adrsLink.isEmpty())
        {
            ps.setString(1, adrsLink);
            int r = ps.executeUpdate();
            System.out.println(r + "records affected");
        }
    }

    public void bookmarkSelect()                                           //Loads selected bookmarked page
    {
        System.out.println("bookmarkSelect() called");
        value=comboBox.getValue().toString();

        Loading(value);
        backList.addLast(value);
    }

    public void getBookmark() throws Exception{                           //Displaybookmarks
        System.out.println("getBookmark() called");
        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/browser?autoReconnect=true&useSSL=false","root","root123");
        Statement ps=con.createStatement();
        ResultSet rs=ps.executeQuery("select * from bookmark");
        //String[] book=new String[50];
        //int i=0;
        while(rs.next()){
            comboBox.getItems().add(rs.getString(1));
        }

    }
    public static boolean isReachableByPing(String host) {
        try{
            String cmd = "";
            if(System.getProperty("os.name").startsWith("Windows")) {
// For Windows
                cmd = "ping -n 1 " + host;
            } else {
// For Linux and OSX
                cmd = "ping -c 1 " + host;
            }
            Process myProcess = Runtime.getRuntime().exec(cmd);
            myProcess.waitFor();
            if(myProcess.exitValue() == 0) {
                return true;
            } else {
                return false;
            }
        } catch( Exception e ) {

            e.printStackTrace();
            return false;
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        engine = web.getEngine();
        engine.load(htLink+"www.google.com");
        backList.addLast("www.google.com");
        try{
            getBookmark();
        }catch(Exception e){System.out.println(e);}
        System.out.println(isReachableByPing("www.google.com"));
        while(!isReachableByPing("www.google.com"))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("NETWORK ERROR");
            alert.setContentText("Please check your internet connection and try again..");
            alert.showAndWait();
        }
        engine.load(htLink+"www.google.com");
        indicator.progressProperty().bind(engine.getLoadWorker().progressProperty());
        indicator.visibleProperty().bind(engine.getLoadWorker().runningProperty());
    }
}
