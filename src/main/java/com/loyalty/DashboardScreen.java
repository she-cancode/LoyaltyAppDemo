package com.loyalty;

import com.loyalty.model.Reward;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DashboardScreen {

    private final LoyaltyEngine engine;
    private final Stage stage;
    private BorderPane root;  // declared here so all methods can see it

    public DashboardScreen(Stage stage) {
        this.engine = MainApp.engine;
        this.stage  = stage;
    }

    public BorderPane getView() {
        root = new BorderPane();  // assigned FIRST before sidebar is built
        root.setStyle("-fx-background-color: #FFF8F8;");
        root.setLeft(buildSidebar());
        root.setCenter(buildDashboard());
        return root;
    }

    // ── Sidebar Navigation ───────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(28, 16, 28, 16));

        Label logo = new Label("🎀 Loyalty");
        logo.getStyleClass().add("sidebar-logo");

        Button dashBtn    = navButton("🏠  Dashboard");
        Button earnBtn    = navButton("⭐  Earn Points");
        Button historyBtn = navButton("📋  History");

        dashBtn.getStyleClass().add("nav-button-active");

        dashBtn.setOnAction(e -> {
            root.setCenter(buildDashboard());
            setActive(dashBtn, earnBtn, historyBtn);
        });

        earnBtn.setOnAction(e -> {
            root.setCenter(new EarnPointsScreen(stage).getView());
            setActive(earnBtn, dashBtn, historyBtn);
        });

        historyBtn.setOnAction(e -> {
            root.setCenter(new TransactionScreen(stage).getView());
            setActive(historyBtn, dashBtn, earnBtn);
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label userName = new Label(engine.getUser().getUsername());
        userName.getStyleClass().add("section-title");

        Label tierBadge = new Label(engine.getUser().getTierLevel());
        tierBadge.getStyleClass().add("tier-badge");

        VBox userInfo = new VBox(6, userName, tierBadge);
        userInfo.setPadding(new Insets(12, 0, 0, 4));

        sidebar.getChildren().addAll(logo, dashBtn, earnBtn, historyBtn, spacer, userInfo);
        return sidebar;
    }

    private VBox buildDashboard() {
        VBox content = new VBox(20);
        content.getStyleClass().add("app-container");
        content.setPadding(new Insets(28));
        content.getChildren().addAll(
            buildGreetingBanner(),
            buildStatCards(),
            buildProgressSection(),
            buildRewardsCatalog()
        );
        return content;
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        return btn;
    }

    private void setActive(Button active, Button... others) {
        active.getStyleClass().add("nav-button-active");
        for (Button b : others) b.getStyleClass().remove("nav-button-active");
    }

    private VBox buildGreetingBanner() {
        VBox banner = new VBox(6);
        banner.getStyleClass().add("greeting-banner");

        Label name = new Label("🎀 Welcome back, " + engine.getUser().getUsername() + "!");
        name.getStyleClass().add("greeting-name");

        Label sub = new Label("Here's your rewards summary for today.");
        sub.getStyleClass().add("greeting-subtitle");

        banner.getChildren().addAll(name, sub);
        return banner;
    }

    private HBox buildStatCards() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(
            createStatCard(String.valueOf((int) engine.getUser().getTotalPoints()),
                "Total Points", "stat-card"),
            createStatCard(engine.getUser().getTierLevel(),
                "Current Tier", "stat-card stat-card-lavender"),
            createStatCard((int) engine.getPointsToNextTier() + " pts",
                "To " + engine.getNextTierName(), "stat-card stat-card-mint")
        );
        return row;
    }

    private VBox createStatCard(String value, String label, String styleClasses) {
        VBox card = new VBox(6);
        for (String cls : styleClasses.split(" ")) card.getStyleClass().add(cls);

        Label valLabel = new Label(value);
        valLabel.getStyleClass().add("stat-card-value");

        Label lblLabel = new Label(label);
        lblLabel.getStyleClass().add("stat-card-label");

        card.getChildren().addAll(valLabel, lblLabel);
        return card;
    }

    private VBox buildProgressSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("progress-section");

        Label title = new Label("Progress to " + engine.getNextTierName());
        title.getStyleClass().add("progress-title");

        ProgressBar bar = new ProgressBar(engine.getProgressToNextTier());
        bar.getStyleClass().add("progress-bar");
        bar.setPrefWidth(Double.MAX_VALUE);

        Label hint = new Label(
            (int) engine.getPointsToNextTier() + " more points to reach " + engine.getNextTierName()
        );
        hint.getStyleClass().add("progress-label");

        section.getChildren().addAll(title, bar, hint);
        return section;
    }

    private VBox buildRewardsCatalog() {
        VBox section = new VBox(12);

        Label title = new Label("🎁 Rewards Catalog");
        title.getStyleClass().add("section-title");

        HBox cards = new HBox(14);
        cards.setAlignment(Pos.CENTER_LEFT);

        for (Reward reward : engine.getRewards()) {
            boolean canAfford = engine.getUser().getTotalPoints() >= reward.getPointCost();
            cards.getChildren().add(buildRewardCard(reward, canAfford));
        }

        section.getChildren().addAll(title, cards);
        return section;
    }

    private VBox buildRewardCard(Reward reward, boolean canAfford) {
        VBox card = new VBox(8);
        card.getStyleClass().add("reward-card");
        card.setAlignment(Pos.CENTER);
        if (!canAfford) card.getStyleClass().add("reward-card-locked");

        Label icon = new Label(reward.getIcon());
        icon.getStyleClass().add("reward-card-icon");

        Label name = new Label(reward.getName());
        name.getStyleClass().add("reward-card-name");

        Label cost = new Label((int) reward.getPointCost() + " pts");
        cost.getStyleClass().add("reward-card-cost");

        Button redeemBtn = new Button(canAfford ? "Redeem" : "🔒 Locked");
        redeemBtn.getStyleClass().add(canAfford ? "btn-primary" : "btn-ghost");
        redeemBtn.setDisable(!canAfford);

        redeemBtn.setOnAction(e -> {
            boolean success = engine.redeemReward(reward);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Redeemed! 🎉");
                alert.setHeaderText("You redeemed: " + reward.getName());
                alert.setContentText("Points remaining: " + (int) engine.getUser().getTotalPoints());
                alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/style.css").toExternalForm()
                );
                alert.showAndWait();
                root.setCenter(buildDashboard());
            }
        });

        card.getChildren().addAll(icon, name, cost, redeemBtn);
        return card;
    }
}
