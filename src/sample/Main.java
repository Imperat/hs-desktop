package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
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
        HBox hBox = new HBox(10);
        hBox.setLayoutX(50);
        hBox.setLayoutY(50);
        TextField firstID = new TextField("Type id..");
        TextField secondID = new TextField("Type id..");
        Button find = new Button("Find");

        hBox.getChildren().add(firstID);
        hBox.getChildren().add(secondID);
        hBox.getChildren().add(find);
        hBox.getChildren().add(result);
        VBox vBox = new VBox(50);
        vBox.setLayoutY(200);
        vBox.setLayoutX(200);
        vBox.getChildren().add(hBox);
        profileImages = new HBox(10);
        vBox.getChildren().add(profileImages);
        Group root = new Group();
        root.getChildren().add(vBox);
        primaryStage.setTitle("Handshake");
        primaryStage.setScene(new Scene(root, 1200, 1200));
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

            for (String id : test) {
                try {

                    URL url = new URL("https://api.vk.com/method/users.get?user_ids=" + id + "&fields=photo_50");
                    URLConnection con = url.openConnection();
                    InputStream in = con.getInputStream();
                    Scanner sc = new Scanner(in);
                    String str = sc.nextLine();
                    JSONObject json = (JSONObject) new JSONParser().parse(str);
                    JSONArray jsonArray = (JSONArray) json.get("response");

                    JSONObject json1 = (JSONObject) new JSONParser().parse(jsonArray.get(0).toString());
                    System.out.println(json1);


                    ImageView person = new ImageView(json1.get("photo_50").toString());
                    profileImages.getChildren().add(person);
                } catch (Exception e) {

                }
            }
        });


    }


    public static void main(String[] args) {
        launch(args);
    }
}