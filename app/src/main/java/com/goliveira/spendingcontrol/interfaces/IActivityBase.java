package com.goliveira.spendingcontrol.interfaces;

public interface IActivityBase {

    String getCategory();
    void setCategory(String category);

    String getDescription();
    void setDescription(String category);

    int getAmount();
    void setAmount(int category);

    String getRecyclerViewDescription();

    void save();

}