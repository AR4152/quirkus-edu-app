package au.edu.anu.quirkus.ui.loginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.ui.home.Home;

/**
 * @author Arjun Raj (u7526852), Oanh Pham (u7281948) and Daniel Herlad (u7480080)
 * <p>
 * Activity class for Login page.
 * Contains setup of all UI components and workflow of processes
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is logged in
        if (ActiveUser.isLoggedIn()) {
            launchDashboard();
        }

        EditText email = findViewById(R.id.register_email);
        EditText password = findViewById(R.id.register_password);
        Button register = findViewById(R.id.go_to_login);
        findViewById(R.id.signup_button).setOnClickListener(e -> {
            ActiveUser.login(email.getText().toString(), password.getText().toString(), result -> {
                if (result) {
                    Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
                    launchDashboard();
                } else {
                    Toast.makeText(this, "Either username or password is invalid",
                                    Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Register Button at Bottom
        findViewById(R.id.go_to_login).setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterAccountActivity.class));
        });

        // Make "Register" in the button text blue
        String fullText = "Don't have an account? Register";
        SpannableString spannableString = new                 SpannableString(fullText);
        int startIndex = fullText.indexOf("Register");
        int endIndex = startIndex + "Register".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0D3B66")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        register.setText(spannableString);
    }

    protected void launchDashboard() {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
        finish();
    }
}