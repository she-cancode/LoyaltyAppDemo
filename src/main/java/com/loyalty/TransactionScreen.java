package com.loyalty;
import com.loyalty.model.Transaction;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
public class TransactionScreen {
    private final LoyaltyEngine engine;

    public TransactionScreen(Stage stage) {
        this.engine = MainApp.engine;
    }

    public VBox getView() {
        VBox root = new VBox(20);
        root.getStyleClass().add("app-container");
        root.setPadding(new Insets(28));

        // ── Header ──────────────────────────────────────────
        Label title = new Label("📋 Transaction History");
        title.getStyleClass().add("heading-1");

        Label sub = new Label("All your points earned and redeemed");
        sub.getStyleClass().add("subtext");

        VBox header = new VBox(4, title, sub);

        // ── Summary chips ────────────────────────────────────
        double totalEarned   = engine.getTransactions().stream()
            .filter(t -> t.getType() == Transaction.Type.EARNED)
            .mapToDouble(Transaction::getPoints).sum();

        double totalRedeemed = engine.getTransactions().stream()
            .filter(t -> t.getType() == Transaction.Type.REDEEMED)
            .mapToDouble(Transaction::getPoints).sum();

        Label earnedChip   = new Label("✅  Earned: " + (int) totalEarned + " pts");
        earnedChip.getStyleClass().addAll("btn-pill", "label-mint");
        earnedChip.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;");

        Label redeemedChip = new Label("🎁  Redeemed: " + (int) totalRedeemed + " pts");
        redeemedChip.getStyleClass().add("btn-pill");

        HBox chips = new HBox(12, earnedChip, redeemedChip);

        // ── Table ────────────────────────────────────────────
        TableView<Transaction> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);
        table.setFixedCellSize(45);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getFormattedDate()));
        dateCol.setMinWidth(160);

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getDescription()));
        descCol.setMinWidth(200);


        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getType().toString()));
        typeCol.setPrefWidth(100);

        TableColumn<Transaction, String> ptsCol = new TableColumn<>("Points");
        ptsCol.setCellValueFactory(d -> {
            Transaction t = d.getValue();
            String prefix = t.getType() == Transaction.Type.EARNED ? "+" : "-";
            return new SimpleStringProperty(prefix + (int) t.getPoints() + " pts");
        });
        ptsCol.setPrefWidth(100);

        // Color-code the points column
        ptsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().removeAll("points-earned", "points-redeemed");
                    if (item.startsWith("+")) getStyleClass().add("points-earned");
                    else                      getStyleClass().add("points-redeemed");
                }
            }
        });

        table.getColumns().addAll(dateCol, descCol, typeCol, ptsCol);
        table.setItems(FXCollections.observableArrayList(engine.getTransactions()));

        if (engine.getTransactions().isEmpty()) {
            table.setPlaceholder(new Label("No transactions yet — start earning! 🎀"));
        }

        root.getChildren().addAll(header, chips, table);
        return root;
    }
}
