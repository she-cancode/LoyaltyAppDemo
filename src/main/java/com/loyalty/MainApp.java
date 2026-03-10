package com.loyalty;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class MainApp extends Application{
     public static LoyaltyEngine engine = new LoyaltyEngine("Teri");

    @Override
    public void start(Stage stage) {
        // Add some sample data so the dashboard isn't empty
        engine.earnPoints("Welcome Bonus", 500);
        engine.earnPoints("Coffee Purchase", 45);
        engine.earnPoints("Lunch Order", 120);

        DashboardScreen dashboard = new DashboardScreen(stage);

        Scene scene = new Scene(dashboard.getView(), 960, 640);
        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );

        stage.setTitle("🎀 Loyalty Rewards");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
