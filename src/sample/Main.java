package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

public class Main extends Application {

    private static Graph relationships = new Graph();
    public static TextArea result;
    public static HBox profileImages;


    static Vector<Integer> friends (int id) throws Exception{
        URL url  = new URL("https://api.vk.com/method/friends.get?user_id=" + id + "&..");
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        Scanner sc = new Scanner(in);
        String str = sc.nextLine();
        Vector<Integer> strsInt = new Vector<>();
        JSONObject friendsResponse = (JSONObject) new JSONParser().parse(str);
        JSONArray friendsArray = (JSONArray)friendsResponse.get("response");
        try {
            for (int i = 0; i < friendsArray.size(); i++) {
                strsInt.add(Integer.parseInt(friendsArray.get(i).toString()));
            }
        } catch (NullPointerException e) {
            System.out.println("Catched");
        }

        return strsInt;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        result = new TextArea();
        Pane grid = new Pane();
        grid.setPrefHeight(600);
        HBox hBox = new HBox(10);
        hBox.setLayoutX(50);
        hBox.setLayoutY(50);
        TextField firstID = new TextField("Type id..");                 firstID.relocate(20, 20);
        TextField secondID = new TextField("Type id..");                secondID.relocate(200, 20);
        Button find = new Button("Find");                               find.relocate(380, 20);

        grid.getChildren().add(firstID);
        grid.getChildren().add(secondID);
        grid.getChildren().add(find);
        //hBox.getChildren().add(result);
        VBox vBox = new VBox(50);
        vBox.setLayoutY(200);
        vBox.setLayoutX(200);
        vBox.getChildren().add(hBox);
        profileImages = new HBox(10);
        grid.getChildren().add(profileImages);
        profileImages.relocate(60, 300);
        Group root = new Group();
        grid.getChildren().add(vBox);
        primaryStage.setTitle("Handshake");
        Scene scene = new Scene(grid, 800, 600);
        scene.getStylesheets().add(Main.class.getResource("main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        find.setOnAction(event -> {
            result.clear();

            int person1 = Integer.parseInt(firstID.getText());
            int person2 = Integer.parseInt(secondID.getText());


            //������ ���� ������
            Vector<Integer> list = new Vector<Integer>();
            try {
                list = Main.friends(person1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Vector<Integer> list2 = new Vector<Integer>();
            //������� � ���� ����
            relationships.addVertex(person1);
            //� ���� ���� ������
            for (Integer iter : list) {
                Main.relationships.addVertex(iter);
                Main.relationships.addEdge(iter, person1);

                //��� ������� ����� �������� ������ ��� ������
                try {
                    list2 = Main.friends(iter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //� ������� ��� � ����
                for (Integer iter2 : list2) {
                    Main.relationships.addEdge(iter2, iter);
                }
            }
            //�� ���� �����������. ��������� � � ����.
            try {
                list = Main.friends(person2);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            relationships.addVertex(person2);
            for (Integer iter : list) {
                Main.relationships.addVertex(iter);
                Main.relationships.addEdge(iter, person2);

                try {
                    list2 = Main.friends(iter);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                for (Integer iter2 : list2) {
                    Main.relationships.addEdge(iter2, iter);
                }
            }
            //���� ��������.
            //������� ���� �� ���� �� ���.
            Main.relationships.printPath(person1, person2);
            //System.out.println(Main.relationships.hasVertex(person2));

            String[] test = result.getText().trim().split("\\r?\\n");

            profileImages.getChildren().clear();
            for (String id : test) {
                try {
                    /*
                    * 5592362
59628
14035574
89491385*/
                    URL url = new URL("https://api.vk.com/method/users.get?user_ids=" + id + "&fields=photo_50&lang=en");
                    URLConnection con = url.openConnection();
                    InputStream in = con.getInputStream();
                    Scanner sc = new Scanner(in);
                    String str = sc.nextLine();
                    JSONObject json = (JSONObject) new JSONParser().parse(str);
                    JSONArray jsonArray = (JSONArray) json.get("response");

                    JSONObject json1 = (JSONObject) new JSONParser().parse(jsonArray.get(0).toString());
                    System.out.println(json1);

                    //Label name = new Label(json1.get("first_name").toString() + " " + json1.get("last_name"));
                    ImageView image = new ImageView(json1.get("photo_50").toString());
                    Button button = new Button(json1.get("first_name").toString() + " " + json1.get("last_name"), image);
                    button.setOnAction(event1 ->
                        getHostServices().showDocument("https://vk.com/id" + json1.get("uid").toString())
                    );

                    //name.setLabelFor(button);
                    //name.setAlignment(Pos.TOP_CENTER);


                    //profileImages.getChildren().add(name);
                    profileImages.getChildren().add(button);
                } catch (Exception e) {

                }
            }
        });


    }


    public static void main(String[] args) {
        launch(args);
    }
}
