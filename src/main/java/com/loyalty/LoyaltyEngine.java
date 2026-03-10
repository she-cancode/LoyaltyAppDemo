package com.loyalty;
import com.loyalty.model.Reward;
import com.loyalty.model.Transaction;
import com.loyalty.model.User;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyEngine {
private User user;
    private List<Reward> rewards;
    private List<Transaction> transactions;

    public LoyaltyEngine(String username) {
        this.user         = new User(username);
        this.rewards      = new ArrayList<>();
        this.transactions = new ArrayList<>();
        loadDefaultRewards();
    }

    // ── Earn points based on amount spent ──────────────────
    public void earnPoints(String description, double amountSpent) {
        double points = amountSpent * 0.1; // 10% of spend = points
        user.addPoints(points);
        transactions.add(new Transaction(description, points, Transaction.Type.EARNED));
    }

    // ── Redeem a reward ────────────────────────────────────
    public boolean redeemReward(Reward reward) {
        if (!reward.isAvailable()) return false;

        boolean success = user.deductPoints(reward.getPointCost());
        if (success) {
            transactions.add(new Transaction(
                "Redeemed: " + reward.getName(),
                reward.getPointCost(),
                Transaction.Type.REDEEMED
            ));
        }
        return success;
    }

    // ── How far to the next reward tier ────────────────────
    public double getProgressToNextTier() {
        double points = user.getTotalPoints();
        if      (points < 500)  return points / 500.0;
        else if (points < 1000) return (points - 500) / 500.0;
        else                    return 1.0; // max tier reached
    }

    public String getNextTierName() {
        return switch (user.getTierLevel()) {
            case "Bronze" -> "Silver";
            case "Silver" -> "Gold";
            default       -> "Max Tier Reached! 🎉";
        };
    }

    public double getPointsToNextTier() {
        double points = user.getTotalPoints();
        if      (points < 500)  return 500  - points;
        else if (points < 1000) return 1000 - points;
        else                    return 0;
    }

    // ── Default rewards catalog ─────────────────────────────
    private void loadDefaultRewards() {
        rewards.add(new Reward("Free Coffee",       100,  "☕"));
        rewards.add(new Reward("10% Discount",      200,  "🏷️"));
        rewards.add(new Reward("Free Dessert",       350,  "🍰"));
        rewards.add(new Reward("Free Meal",          500,  "🍽️"));
        rewards.add(new Reward("VIP Experience",    1000, "⭐"));
    }

    // ── Getters ─────────────────────────────────────────────
    public User              getUser()         { return user; }
    public List<Reward>      getRewards()      { return rewards; }
    public List<Transaction> getTransactions() { return transactions; }
}
