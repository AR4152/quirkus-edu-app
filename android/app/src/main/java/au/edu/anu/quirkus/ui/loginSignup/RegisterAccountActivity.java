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
 * Activity class for Register Account page.
 * Contains setup of all UI components and workflow of processes
 */
public class RegisterAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        EditText name = findViewById(R.id.register_name);
        EditText email = findViewById(R.id.register_email);
        EditText password = findViewById(R.id.register_password);
        EditText confirmPassword = findViewById(R.id.register_confirm_password);

        Button register = findViewById(R.id.signup_button);

        Button login = findViewById(R.id.go_to_login);

        register.setOnClickListener(e -> {
            String p = String.valueOf(password.getText());
            String p2 = String.valueOf(confirmPassword.getText());

            if (!p.equals(p2)) {
                Toast.makeText(this, "The passwords don't match", Toast.LENGTH_SHORT).show();
                return;
            }

            ActiveUser.signup(String.valueOf(email.getText()), p, String.valueOf(name.getText()), result -> {
                if (result) {
                    Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show();
                    launchDashboard();
                } else {
                    Toast.makeText(this, "An error occured",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });


        // Sign In Button at Bottom
        findViewById(R.id.go_to_login).setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        });

        // Make "Register" in the button text blue
        String fullText = "Already have an account? Login";
        SpannableString spannableString = new SpannableString(fullText);
        int startIndex = fullText.indexOf("Login");
        int endIndex = startIndex + "Login".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0D3B66")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(spannableString);
    }

    protected void launchDashboard() {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
        finish();
    }
}