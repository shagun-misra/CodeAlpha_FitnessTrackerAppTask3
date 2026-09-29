package com.example.codealpha_fitnesstrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalSteps, tvTotalCalories, tvHistoryLog;
    private Spinner spinnerActivityType;
    private EditText etDuration, etStepsInput;
    private Button btnLogActivity;

    private int totalSteps = 0;
    private int totalCalories = 0;
    private StringBuilder historyBuilder;

    private SharedPreferences preferences;
    private static final String PREF_NAME = "FitnessData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalSteps = findViewById(R.id.tvTotalSteps);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvHistoryLog = findViewById(R.id.tvHistoryLog);
        spinnerActivityType = findViewById(R.id.spinnerActivityType);
        etDuration = findViewById(R.id.etDuration);
        etStepsInput = findViewById(R.id.etStepsInput);
        btnLogActivity = findViewById(R.id.btnLogActivity);

        historyBuilder = new StringBuilder();
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Populate Activity Spinner
        String[] activities = {"Walking (4 kcal/min)", "Running (10 kcal/min)", "Cycling (8 kcal/min)", "Yoga (3 kcal/min)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activities);
        spinnerActivityType.setAdapter(adapter);

        // Load saved values
        loadSavedData();

        btnLogActivity.setOnClickListener(v -> logNewActivity());
    }

    private void logNewActivity() {
        String durationStr = etDuration.getText().toString().trim();
        String stepsStr = etStepsInput.getText().toString().trim();

        if (durationStr.isEmpty()) {
            Toast.makeText(this, "Please enter duration in minutes", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = Integer.parseInt(durationStr);
        int steps = stepsStr.isEmpty() ? 0 : Integer.parseInt(stepsStr);

        int activityIndex = spinnerActivityType.getSelectedItemPosition();
        int caloriesPerMin;
        String activityName;

        switch (activityIndex) {
            case 1:
                activityName = "Running";
                caloriesPerMin = 10;
                break;
            case 2:
                activityName = "Cycling";
                caloriesPerMin = 8;
                break;
            case 3:
                activityName = "Yoga";
                caloriesPerMin = 3;
                break;
            default:
                activityName = "Walking";
                caloriesPerMin = 4;
                break;
        }

        int burnedCalories = duration * caloriesPerMin;
        totalCalories += burnedCalories;
        totalSteps += steps;

        String entry = "• " + activityName + ": " + duration + " mins | " + burnedCalories + " kcal"
                + (steps > 0 ? " | " + steps + " steps\n" : "\n");
        historyBuilder.insert(0, entry);

        updateUI();
        saveData();

        etDuration.setText("");
        etStepsInput.setText("");
        Toast.makeText(this, "Workout logged successfully!", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        tvTotalSteps.setText(String.valueOf(totalSteps));
        tvTotalCalories.setText(totalCalories + " kcal");
        tvHistoryLog.setText(historyBuilder.length() > 0 ? historyBuilder.toString() : "No workouts logged yet.");
    }

    private void saveData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("totalSteps", totalSteps);
        editor.putInt("totalCalories", totalCalories);
        editor.putString("history", historyBuilder.toString());
        editor.apply();
    }

    private void loadSavedData() {
        totalSteps = preferences.getInt("totalSteps", 0);
        totalCalories = preferences.getInt("totalCalories", 0);
        String savedHistory = preferences.getString("history", "");

        if (!savedHistory.isEmpty()) {
            historyBuilder.append(savedHistory);
        }
        updateUI();
    }
}
