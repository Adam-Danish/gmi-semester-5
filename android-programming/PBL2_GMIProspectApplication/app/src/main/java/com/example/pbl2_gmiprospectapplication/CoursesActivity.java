
package com.example.pbl2_gmiprospectapplication;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CoursesActivity extends AppCompatActivity {
    private static final String STATE_CATEGORY = "selected_category";
    private int category = 0;
    private TextView courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        courseList = findViewById(R.id.txtCourseList);

        findViewById(R.id.btnDiploma).setOnClickListener(v -> showCategory(0));
        findViewById(R.id.btnGAPP).setOnClickListener(v -> showCategory(1));
        findViewById(R.id.btnGUFP).setOnClickListener(v -> showCategory(2));
        findViewById(R.id.btnCourseEligibility).setOnClickListener(v -> {
            Intent intent = new Intent(this, EligibilityActivity.class);
            intent.putExtra(EligibilityActivity.EXTRA_CATEGORY, category);
            startActivity(intent);
        });
        findViewById(R.id.btnCourseSource).setOnClickListener(v ->
                GmiLinks.openWeb(this, category == 0 ? GmiLinks.DIPLOMA_PROGRAMMES
                        : category == 1 ? GmiLinks.GAPP_ADMISSION : GmiLinks.GUFP));
        findViewById(R.id.btnApply).setOnClickListener(v ->
                GmiLinks.openWeb(this, GmiLinks.APPLICATION));

        showCategory(savedInstanceState == null ? 0 :
                savedInstanceState.getInt(STATE_CATEGORY, 0));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CATEGORY, category);
        super.onSaveInstanceState(outState);
    }

    private void showCategory(int selected) {
        category = Math.max(0, Math.min(2, selected));
        int content = category == 0 ? R.string.diploma_list
                : category == 1 ? R.string.gapp_listing : R.string.gufp_listing;
        courseList.setText(content);
    }
}
