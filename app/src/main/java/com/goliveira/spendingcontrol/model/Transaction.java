package com.goliveira.spendingcontrol.model;

import com.goliveira.spendingcontrol.interfaces.IExpenditure;
import com.goliveira.spendingcontrol.notification.PushNotificationAsync;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements IExpenditure {

    private TransactionType type;
    private double amount = 0.00;
    private String date = "";
    private Long dateUnix;
    private String category = "";
    private String description = "";
    private String wallet = "";
    // timestamps
    private String createdAt = "";
    private final String createdBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final String createdByName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

    public Transaction(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createdAt = dateFormat.format(new Date());
    }

    public TransactionType getType() { return type; }

    public void setType(TransactionType type) { this.type = type; }

    public double getAmount() { return amount; }

    public void setAmount(double amount) { this.amount = amount; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public Long getDateUnix() { return dateUnix; }

    public void setDateUnix(Long dateUnix) { this.dateUnix = dateUnix; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getWallet() { return wallet; }

    public void setWallet(String wallet) { this.wallet = wallet; }

    public String getCreatedAt() { return createdAt; }

    public String getCreatedBy() { return createdBy; }

    public String getCreatedByName() { return createdByName; }

    public String getMessageForBuddyNotification(){
        if(getType() == TransactionType.EXPENSE){
            return createdByName + " just expent " + getAmount() + " with " + getCategory() + ".";
        }
        else{
            return createdByName + " received " + getAmount() + " with " + getCategory() + ".";
        }
    }

    public void save() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference();
        String localWallet;
        if(!getWallet().equals("")){
            localWallet = getWallet();
        } else{
            localWallet = getCreatedBy();
        }
        databaseRef.child("users").child(localWallet).child("transactions").push().setValue(this);
    }

    public static double calculateTotal(@NotNull DataSnapshot dataSnapshot, TransactionType type) {
        double total = 0.00;
        for (DataSnapshot ds : dataSnapshot.child("transactions").getChildren()) {
            Transaction transaction =  ds.getValue(Transaction.class);
            if(transaction.getType() == type){
                total = total + transaction.getAmount();
            }
        }
        return total;
    }
}
