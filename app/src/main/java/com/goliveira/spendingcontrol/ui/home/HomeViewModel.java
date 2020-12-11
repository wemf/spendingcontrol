package com.goliveira.spendingcontrol.ui.home;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.goliveira.spendingcontrol.R;
import com.goliveira.spendingcontrol.model.Expense;
import com.goliveira.spendingcontrol.model.Income;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final String currentUserUid = FirebaseAuth.getInstance().getUid();
    private PieChart pieChartHome;


    public HomeViewModel() {

    }


    public void DisplayFirebaseData (View root, Fragment fragment) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double totalExpense = calculateExpense(dataSnapshot);
                TextView expenseTodayValue =  root.findViewById(R.id.expenseTodayValue);
                expenseTodayValue.setText( "$ " + totalExpense);

                double totalIncome = calculateIncome(dataSnapshot);
                TextView todayIncomeValue =  root.findViewById(R.id.todayIncomeValue);
                todayIncomeValue.setText( "$ " + totalIncome);

                DrawChart(root, fragment, totalIncome, totalExpense);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("HomeFragment", "Error fetching database");
            }
        });
    }

    private double calculateExpense (DataSnapshot dataSnapshot) {
        double totalExpense = 0.00;
        for (DataSnapshot ds : dataSnapshot.child("expenses").getChildren()) {
            Expense expense = ds.getValue(Expense.class);
            totalExpense = totalExpense + expense.getAmount();
        }
        return totalExpense;
    }

    private double calculateIncome(DataSnapshot dataSnapshot) {
        double totalIncome = 0.00;
        for (DataSnapshot ds : dataSnapshot.child("incomes").getChildren()) {
            Income income = ds.getValue(Income.class);
            totalIncome = totalIncome + income.getAmount();
        }
        return totalIncome;
    }

    public ArrayList<Integer> ConfigurePieChartColors(Fragment fragment)
    {
        ArrayList<Integer> chartColors = new ArrayList<>();

        chartColors.add(ContextCompat.getColor(fragment.getContext(), R.color.income_color));
        chartColors.add(ContextCompat.getColor(fragment.getContext(), R.color.expense_color));

        return chartColors;
    }

    public void DrawChart(View root, Fragment fragment, double totalIncome, double totalExpense)
    {
        pieChartHome = root.findViewById(R.id.pieChartHome);
        List<PieEntry> chartData = new ArrayList<>();

        chartData.add(new PieEntry((int) totalIncome, "Income"));
        chartData.add(new PieEntry((int) totalExpense, "Expense"));

        PieDataSet pieDataSet = new PieDataSet(chartData, "Budget");

        pieDataSet.setColors(ConfigurePieChartColors(fragment));
        PieData pieData = new PieData(pieDataSet);

        pieChartHome.setData(pieData);
        pieChartHome.invalidate();
    }

}