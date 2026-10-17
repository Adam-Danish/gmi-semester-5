package com.example.pbl2_gmiprospectapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EligibilityActivity extends AppCompatActivity {
    public static final String EXTRA_CATEGORY = "category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligibility);

        EditText creditsInput = findViewById(R.id.inputCredits);
        CheckBox spm = findViewById(R.id.checkSpm);
        CheckBox bm = findViewById(R.id.checkBm);
        CheckBox english = findViewById(R.id.checkEnglish);
        CheckBox englishPass = findViewById(R.id.checkEnglishPass);
        CheckBox math = findViewById(R.id.checkMath);
        CheckBox addMath = findViewById(R.id.checkAddMath);
        CheckBox physics = findViewById(R.id.checkPhysics);
        CheckBox chemistry = findViewById(R.id.checkChemistry);
        CheckBox science = findViewById(R.id.checkScience);
        TextView result = findViewById(R.id.txtEligibility);

        findViewById(R.id.btnCheck).setOnClickListener(v -> {
            int credits;

            try {
                credits = Integer.parseInt(
                        creditsInput.getText().toString().trim());
            } catch (NumberFormatException e) {
                creditsInput.setError("Enter your number of SPM credits");
                return;
            }

            if (credits < 0) {
                creditsInput.setError("Credits cannot be negative");
                return;
            }

            int checkedCredits = 0;
            for (CheckBox subject : new CheckBox[]{
                    bm, english, math, addMath,
                    physics, chemistry, science}) {
                if (subject.isChecked()) checkedCredits++;
            }

            if (credits < checkedCredits) {
                creditsInput.setError(
                        "Total credits cannot be less than the subjects ticked");
                return;
            }

            if (!spm.isChecked()) {
                result.setText("This checker covers applicants who passed SPM. "
                        + "Ask GMI about other qualifications.");
                return;
            }

            StringBuilder matches = new StringBuilder();

            if (credits >= 3) {
                matches.append("• Diploma in Cyber Security Technology\n");
                matches.append("• Diploma in Creative Multimedia\n");
            }

            if (credits >= 3 && math.isChecked()) {
                matches.append("• Diploma in Software Engineering\n");
            }

            boolean engineeringDiploma =
                    credits >= 3
                            && math.isChecked()
                            && (physics.isChecked()
                            || chemistry.isChecked()
                            || science.isChecked())
                            && (english.isChecked()
                            || englishPass.isChecked());

            if (engineeringDiploma) {
                matches.append("• Diploma of Mechatronics Engineering Technology\n");
                matches.append("• Diploma of Engineering Technology "
                        + "(Instrumentation & Control)\n");
                matches.append("• Diploma of Electronics Engineering Technology "
                        + "(Computer)\n");
                matches.append("• Autotronics Engineering Technology\n");
                matches.append("• Diploma in Engineering Technology "
                        + "(Sustainable Energy & Power Distribution)\n");
                matches.append("• Diploma in Industrial Quality "
                        + "Engineering Technology\n");
                matches.append("• Diploma in Innovative Product Design "
                        + "Engineering Technology\n");
                matches.append("• Diploma of Mechanical Engineering Technology "
                        + "(CNC Precision)\n");
                matches.append("• Diploma of Mechanical Engineering Technology "
                        + "(Manufacturing)\n");
            }

            boolean gapp =
                    english.isChecked()
                            && math.isChecked()
                            && addMath.isChecked()
                            && physics.isChecked()
                            && chemistry.isChecked();

            if (gapp) {
                matches.append("• GAPP\n");
            }

            boolean gufp = bm.isChecked() && gapp;

            if (gufp) {
                matches.append("• GUFP\n");
            }

            if (matches.length() == 0) {
                result.setText("No programme matched the SPM results entered. "
                        + "Check GMI's official requirements for other routes.");
                return;
            }

            String message = "Possible matches:\n\n"
                    + matches
                    + "\nGMI makes the final admission decision.";

            result.setText(message);

            new AlertDialog.Builder(this)
                    .setTitle("Possible matches")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(
                            "Continue to GMI admission",
                            (dialog, which) ->
                                    GmiLinks.openWeb(
                                            this, GmiLinks.APPLICATION))
                    .show();
        });
    }
}