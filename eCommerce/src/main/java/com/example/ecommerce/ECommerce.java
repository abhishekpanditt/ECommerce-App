package com.example.ecommerce;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ECommerce extends Application {

    //Login login = new Login();
    private final int width = 500, height = 400, headerLine = 50;
    Pane bodyPane;
    Pane root = new Pane();
    GridPane footerBar;
    ObservableList<Product> cartItemList = FXCollections.observableArrayList();

    //buttons created
    Button signInButton = new Button("Sign In");
    Button placeOrderButton = new Button("Place order");
    Label welcomeLabel = new Label("Welcome customer");
    Button searchButton = new Button("Search");
    Button cartButton = new Button("Cart");
    Button ordersButton = new Button("Orders");
    Button buyNowButton = new Button("Buy now");
    Button addToCartButton = new Button("Add to cart");
    Button signOutButton = new Button("Sign Out");
    Button closeButton = new Button("Exit");
    Customer loggedInCustomer = null;
    Order order = new Order();
    ProductList productList = new ProductList();

    private void showDialogue(String message){                                          //for showing dialogue box
        Dialog<String> dialog = new Dialog<String>();
        //Setting the title
        dialog.setTitle("Order status");
        ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        //Setting the content of the dialog
        dialog.setContentText(message);
        //Adding buttons to the dialog pane
        dialog.getDialogPane().getButtonTypes().add(type);

        dialog.showAndWait();

    }

    private void addItemsToCart(Product product){                                       //adding items to cart

        if (!cartItemList.contains(product)) {
            cartItemList.add(product);
            showDialogue(product.getName() + " is added to the cart");
        }
    }
    private GridPane headerBar(){                                                       //headerbar
        GridPane header = new GridPane();

        //creating searchbar and searchButton
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search an item");
        ordersButton.setOnAction(new EventHandler<ActionEvent>() {                       //order button
            @Override
            public void handle(ActionEvent actionEvent) {
                bodyPane.getChildren().clear();
                bodyPane.getChildren().add(order.getOrders(loggedInCustomer));
            }
        });

        searchButton.setOnAction(new EventHandler<ActionEvent>() {                      //search button
            @Override
            public void handle(ActionEvent actionEvent) {
                bodyPane.getChildren().clear();
                bodyPane.getChildren().add(productList.getAllProducts());
            }
        });

        cartButton.setOnAction(new EventHandler<ActionEvent>() {                        //Add to cart button
            @Override
            public void handle(ActionEvent actionEvent) {
                bodyPane.getChildren().clear();
                bodyPane.getChildren().add(productList.productsInCart(cartItemList));   // only searched products to show
            }
        });

        signInButton.setOnAction(new EventHandler<ActionEvent>() {                      //sign in button
            @Override
            public void handle(ActionEvent actionEvent) {
                bodyPane.getChildren().clear();
                bodyPane.getChildren().add(loginPage());
            }
        });

        //placing searchbar and searchButton
        header.setHgap(10);

        header.add(searchBar, 0, 0);
        header.add(searchButton, 1, 0);
        header.add(signInButton, 2, 0);
        header.add(welcomeLabel, 3, 0);
        header.add(cartButton, 4,0);
        header.add(ordersButton, 5, 0);

        return header;
    }
    private GridPane loginPage(){                                                       //login page

        //creating components
        Label userLabel = new Label("User name");
        Label passLabel = new Label("Password");
        TextField userName = new TextField();
        //    userName.setText("rohan@gmail.com");
        userName.setPromptText("Enter User name");
        PasswordField password = new PasswordField();
        //    password.setText("abc");
        password.setPromptText("Enter password");
        Button loginButton = new Button("login");
        Label messageLabel = new Label("Login - Message");

        loginButton.setOnAction(new EventHandler<ActionEvent>() {                       //login button
            @Override
            public void handle(ActionEvent actionEvent) {
                String user = userName.getText();
                String pass = password.getText();
                loggedInCustomer = Login.customerLogin(user, pass);

                if(loggedInCustomer != null){                                           //if correct credentials entered
                    messageLabel.setText("Login successful!");
                    welcomeLabel.setText("Welcome " + loggedInCustomer.getName());
                }
                else{
                    messageLabel.setText("Login failed!");                              //if wrong credentials entered
                }
            }
        });

        //placing components
        GridPane loginPane = new GridPane();
        loginPane.setTranslateY(50);
        loginPane.setTranslateX(50);
        loginPane.setVgap(10);
        loginPane.setHgap(10);
        loginPane.add(userLabel, 0, 0);
        loginPane.add(userName, 1, 0);
        loginPane.add(passLabel, 0, 1);
        loginPane.add(password, 1, 1);
        loginPane.add(loginButton, 0, 2);
        loginPane.add(messageLabel, 1, 2);

        return loginPane;
    }

    private GridPane footerBar(){                                                       //footer bar
        buyNowButton.setOnAction(new EventHandler<ActionEvent>() {                      //Buy Now button
            @Override
            public void handle(ActionEvent actionEvent) {
                Product product = productList.getSelectedProduct();
                boolean orderStatus = false;

                if(product != null && loggedInCustomer != null){                        //if customer signed in and product present in cart
                    orderStatus = order.placeOrder(loggedInCustomer, product);
                }

                if(orderStatus == true){
                    showDialogue("Order successful");
                }
                if(loggedInCustomer == null){
                    showDialogue("Order can't be placed. Please Sign In!!");
                }
            }
        });

        addToCartButton.setOnAction(new EventHandler<ActionEvent>() {                       //Add to cart button
            @Override
            public void handle(ActionEvent actionEvent) {
                Product product = productList.getSelectedProduct();
                addItemsToCart(product);
            }
        });

        placeOrderButton.setOnAction(new EventHandler<ActionEvent>() {                      //Place order button
            @Override
            public void handle(ActionEvent actionEvent) {
                int orderCount = 0;

                if(!cartItemList.isEmpty() && loggedInCustomer != null){
                    orderCount = order.placeMultipleOrderProducts(cartItemList, loggedInCustomer);
                    cartItemList.clear();
                }

                if(orderCount > 0){
                    showDialogue("Order for " + orderCount + " product(s) placed successfully!");
                }
                else{
                    showDialogue("Sorry, orders can't be placed right now! Try again!!");
                }
            }
        });

        signOutButton.setOnAction(new EventHandler<ActionEvent>() {                     // sign out button
            @Override
            public void handle(ActionEvent actionEvent) {
                loggedInCustomer = null;
                welcomeLabel.setText("Welcome customer");
                bodyPane.getChildren().clear();
                bodyPane.getChildren().add(loginPage());

            }
        });

        //placing components
        GridPane footer = new GridPane();
        footer.setHgap(10);
        footer.setTranslateY(headerLine + height + 10);
        footer.setTranslateX(10);
        footer.add(buyNowButton, 0, 0);
        footer.add(addToCartButton, 1, 0);
        footer.add(placeOrderButton, 2, 0);
        footer.add(signOutButton, 20,0);

        return footer;
    }

    private Pane createContent(){
        root.setPrefSize(width, height + 2*headerLine);

        bodyPane = new Pane();
        bodyPane.setPrefSize(width, height);
        bodyPane.setTranslateY(headerLine);
        bodyPane.setTranslateX(10);

        bodyPane.getChildren().add(loginPage());

        footerBar = footerBar();

        root.getChildren().addAll(headerBar(),
//                loginPage(),
//                productList.getAllProducts()
                bodyPane,
                footerBar
        );
        return root;
    }

    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(ECommerce.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(createContent());                                       //create scene
        stage.setTitle("ECommerce App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {                                            //launch app
        launch();
    }
}
