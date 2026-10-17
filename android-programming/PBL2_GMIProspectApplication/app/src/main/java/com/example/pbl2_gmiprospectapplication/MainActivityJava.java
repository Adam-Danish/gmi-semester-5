
package com.example.pbl2_gmiprospectapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnCourses).setOnClickListener(v ->
                startActivity(new Intent(this, CoursesActivity.class)));
        findViewById(R.id.btnEligibility).setOnClickListener(v ->
                startActivity(new Intent(this, EligibilityActivity.class)));
        findViewById(R.id.btnAbout).setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
    }
}
