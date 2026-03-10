package com.loyalty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
public class EarnPointsScreen {
    private final LoyaltyEngine engine;
    private final Stage stage;

    public EarnPointsScreen(Stage stage) {
        this.engine = MainApp.engine;
        this.stage  = stage;
    }

    public VBox getView() {
        VBox root = new VBox(24);
        root.getStyleClass().add("app-container");
        root.setPadding(new Insets(28));

        // ── Header ───────────────────────────────────────────
        Label title = new Label("⭐ Earn Points");
        title.getStyleClass().add("heading-1");

        Label sub = new Label("Log a purchase to earn loyalty points");
        sub.getStyleClass().add("subtext");

        VBox header = new VBox(4, title, sub);

        // ── Current balance card ─────────────────────────────
        VBox balanceCard = new VBox(6);
        balanceCard.getStyleClass().add("greeting-banner");
        balanceCard.setAlignment(Pos.CENTER);

        Label balanceVal = new Label((int) engine.getUser().getTotalPoints() + " pts");
        balanceVal.getStyleClass().add("greeting-name");

        Label balanceLbl = new Label("Current Balance");
        balanceLbl.getStyleClass().add("greeting-subtitle");

        balanceCard.getChildren().addAll(balanceVal, balanceLbl);

        // ── Input form ───────────────────────────────────────
        VBox form = new VBox(16);
        form.getStyleClass().add("progress-section");
        form.setMaxWidth(480);

        Label formTitle = new Label("Log a Purchase");
        formTitle.getStyleClass().add("section-title");

        // Description field
        Label descLabel = new Label("Where did you shop?");
        descLabel.getStyleClass().add("subtext");
        TextField descField = new TextField();
        descField.setPromptText("e.g. Coffee Shop, Grocery Store...");
        descField.getStyleClass().add("text-field");

        // Amount field
        Label amountLabel = new Label("How much did you spend? (R)");
        amountLabel.getStyleClass().add("subtext");
        TextField amountField = new TextField();
        amountField.setPromptText("e.g. 150.00");
        amountField.getStyleClass().add("text-field");

        // Points preview label
        Label previewLabel = new Label("You'll earn: 0 pts");
        previewLabel.getStyleClass().add("label-pink");

        // Live preview as user types amount
        amountField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double amount  = Double.parseDouble(newVal);
                int    preview = (int) (amount * 0.1);
                previewLabel.setText("You'll earn: " + preview + " pts  🎀");
            } catch (NumberFormatException e) {
                previewLabel.setText("You'll earn: 0 pts");
            }
        });

        // Result message label
        Label resultLabel = new Label("");
        resultLabel.getStyleClass().add("subtext");

        // Submit button
        Button earnBtn = new Button("✅  Log Purchase & Earn Points");
        earnBtn.getStyleClass().add("btn-primary");
        earnBtn.setPrefWidth(300);

        earnBtn.setOnAction(e -> {
            String desc   = descField.getText().trim();
            String amtStr = amountField.getText().trim();

            if (desc.isEmpty() || amtStr.isEmpty()) {
                resultLabel.setText("⚠️  Please fill in both fields.");
                return;
            }

            try {
                double amount = Double.parseDouble(amtStr);
                if (amount <= 0) {
                    resultLabel.setText("⚠️  Amount must be greater than 0.");
                    return;
                }

                engine.earnPoints(desc, amount);
                int earned = (int) (amount * 0.1);

                // Update balance display
                balanceVal.setText((int) engine.getUser().getTotalPoints() + " pts");

                // Success feedback
                resultLabel.setText("🎉  +" + earned + " points added! Keep going!");
                resultLabel.setStyle("-fx-text-fill: #43A047; -fx-font-weight: bold;");

                // Clear fields
                descField.clear();
                amountField.clear();
                previewLabel.setText("You'll earn: 0 pts");

            } catch (NumberFormatException ex) {
                resultLabel.setText("⚠️  Please enter a valid number for the amount.");
            }
        });

        form.getChildren().addAll(
            formTitle,
            descLabel, descField,
            amountLabel, amountField,
            previewLabel,
            earnBtn,
            resultLabel
        );

        // ── Quick earn buttons ───────────────────────────────
        VBox quickSection = new VBox(12);
        Label quickTitle = new Label("⚡ Quick Add");
        quickTitle.getStyleClass().add("section-title");

        HBox quickBtns = new HBox(12);
        quickBtns.setAlignment(Pos.CENTER_LEFT);

        String[][] quickOptions = {
            {"☕ Coffee",   "45"},
            {"🍔 Lunch",   "120"},
            {"🛒 Groceries","350"},
            {"⛽ Fuel",    "600"}
        };

        for (String[] option : quickOptions) {
            Button btn = new Button(option[0] + "  R" + option[1]);
            btn.getStyleClass().add("btn-secondary");
            btn.setOnAction(e -> {
                engine.earnPoints(option[0], Double.parseDouble(option[1]));
                int earned = (int)(Double.parseDouble(option[1]) * 0.1);
                balanceVal.setText((int) engine.getUser().getTotalPoints() + " pts");
                resultLabel.setText("🎉  +" + earned + " points from " + option[0] + "!");
                resultLabel.setStyle("-fx-text-fill: #43A047; -fx-font-weight: bold;");
            });
            quickBtns.getChildren().add(btn);
        }

        quickSection.getChildren().addAll(quickTitle, quickBtns);
        root.getChildren().addAll(header, balanceCard, form, quickSection);
        return root;
    } 
}
