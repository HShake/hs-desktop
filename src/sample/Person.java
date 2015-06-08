package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by Root on 09.06.2015.
 */
public class Person {

    private String uid;
    private JSONObject personInfo;

    Person(String uid) {

        this.uid = uid;

        try {
            URL url = new URL(
                    "https://api.vk.com/method/users.get?user_ids=" + uid +
                    "&fields=photo_50&lang=en"
            );
            URLConnection con = url.openConnection();
            InputStream in = con.getInputStream();
            Scanner sc = new Scanner(in);
            String str = sc.nextLine();
            JSONObject json = (JSONObject) new JSONParser().parse(str);
            JSONArray jsonArray = (JSONArray) json.get("response");

            this.personInfo = (JSONObject) new JSONParser().parse(jsonArray.get(0).toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public String getUid() {

        return this.personInfo.get("uid").toString();

    }

    public String getName() {

        return this.personInfo.get("first_name").toString() + " " +
               this.personInfo.get("last_name").toString();

    }

    public Image getImage() {

        return new Image(this.personInfo.get("photo_50").toString());

    }

    public String getURL() {

        return "https://vk.com/id" + this.personInfo.get("uid").toString();

    }

}
