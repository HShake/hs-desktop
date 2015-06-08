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
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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
    public static VBox profileImages;
    public int person1;
    public int person2;


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
            System.out.println(e.getMessage());
        }

        return strsInt;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        result = new TextArea();
        HBox hBox = new HBox(10);
        hBox.setLayoutX(50);
        hBox.setLayoutY(50);
        TextField firstID = new TextField("Type id..");
        TextField secondID = new TextField("Type id..");
        Button find = new Button("Find");

        hBox.getChildren().add(firstID);
        hBox.getChildren().add(secondID);
        hBox.getChildren().add(find);
        VBox vBox = new VBox(50);
        vBox.setLayoutY(50);
        vBox.setLayoutX(50);
        vBox.getChildren().add(hBox);
        profileImages = new VBox(0);
        profileImages.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().add(profileImages);
        Group root = new Group();
        root.getChildren().add(vBox);
        primaryStage.setTitle("Handshake");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

        find.setOnAction(event -> {
            result.clear();

            this.person1 = Integer.parseInt(new Person(firstID.getText()).getUid());
            this.person2 = Integer.parseInt(new Person(secondID.getText()).getUid());



            //Список моих друзей
            Vector<Integer> list = new Vector<Integer>();
            try {
                list = Main.friends(person1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Vector<Integer> list2 = new Vector<Integer>();
            //Добавим в граф меня
            relationships.addVertex(person1);
            //И всех моих друзей
            for (Integer iter : list) {
                Main.relationships.addVertex(iter);
                Main.relationships.addEdge(iter, person1);

                //Для каждого друга создадим список его друзей
                try {
                    list2 = Main.friends(iter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //И добавим его в граф
                for (Integer iter2 : list2) {
                    Main.relationships.addEdge(iter2, iter);
                }
            }
            //Со мной разобрались. Разберёмся и с Вами.
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
            //Граф заполнен.
            //Выведем путь от меня до Вас.
            Main.relationships.printPath(person1, person2);
            //System.out.println(Main.relationships.hasVertex(person2));

            String[] test = result.getText().trim().split("\\r?\\n");

            profileImages.getChildren().clear();
            for (String id : test) {
                Person person = new Person(id);
                ImageView image = new ImageView(person.getImage());
                Button button = new Button(person.getName(), image);
                button.setOnAction(event1 ->
                                getHostServices().showDocument(person.getURL())
                );
                button.setPrefWidth(200);

                profileImages.getChildren().add(button);

                if (!test[test.length - 1].equals(id)) {

                    Line line = new Line(
                            button.getLayoutX() + 125,
                            button.getLayoutY(),
                            button.getLayoutX() + 125,
                            button.getLayoutY() + 40
                    );

                    line.setStrokeWidth(3);
                    line.setStroke(Color.RED);
                    profileImages.getChildren().add(line);
                }
            }
        });


    }


    public static void main(String[] args) {
        launch(args);
    }
}
