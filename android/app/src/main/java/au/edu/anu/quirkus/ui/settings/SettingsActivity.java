package au.edu.anu.quirkus.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import au.edu.anu.quirkus.ActiveUser;
import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.ui.home.HomeModel;
import au.edu.anu.quirkus.ui.loginSignup.LoginActivity;

/**
 * @author Arjun Raj (u7526852)
 * <p>
 * Activity class for Settings page.
 * Contains setup of all UI components and workflow of processes
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        SettingsModel settings = new ViewModelProvider(this).get(SettingsModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.profile_photo_frame, ProfilePhotoFragment.class, null)
                    .add(R.id.settings2, new SettingsFragment(), null)
                    .commit();
        }

        // Sign out button listener
        findViewById(R.id.sign_out_button).setOnClickListener(view -> {
            ActiveUser.logout();
            launchMain();
        });

        setupToolBar();
    }

    private void launchMain() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void setupToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }


    /**
     * @author Arjun Raj (u7526852)
     * <p>
     * Activity class for Settings page.
     * Contains setup of all UI components and workflow of processes
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        private SettingsModel settings;
        private EditTextPreference name;
        private EditTextPreference email;
        private EditTextPreference password;
        private ListPreference theme;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            initialiseFields();
            initialiseTextFields();
            setUpListeners();
        }

        private void initialiseFields(){
            name = (EditTextPreference) findPreference("name_edit_text");
            email = (EditTextPreference) findPreference("email_edit_text");
            password = (EditTextPreference) findPreference("password_edit_text");
            theme = (ListPreference) findPreference("theme_preference");

            settings = new ViewModelProvider(this).get(SettingsModel.class);
        }

        private void initialiseTextFields(){
            settings.getActiveUser().observe(this, user -> {
                name.setText(user.getName());
                email.setText(user.getEmail());
                password.setText(" ");
            });
        }

        private void setUpListeners(){
            name.setOnPreferenceChangeListener((preference, newValue) -> {
                settings.setUserName((String) newValue, result -> {
                    if (result) {
                        Toast.makeText(preference.getContext(), "Change Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(preference.getContext(), "Change Unsuccessful", Toast.LENGTH_SHORT).show();
                        initialiseTextFields();
                    }
                });
                return true;
            });

            email.setOnPreferenceChangeListener((preference, newValue) -> {
                settings.setEmail((String) newValue,
                        result -> {
                            if (result) {
                                Toast.makeText(preference.getContext(), "Change Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(preference.getContext(), "Change Unsuccessful", Toast.LENGTH_SHORT).show();
                                initialiseTextFields();
                            }
                        });
                return true;
            });

            password.setOnPreferenceChangeListener((preference, newValue) -> {
                settings.setPassword((String) newValue,
                        result -> {
                            if (result) {
                                Toast.makeText(preference.getContext(), "Change Successful", Toast.LENGTH_SHORT).show();
                                password.setText("");
                            } else {
                                Toast.makeText(preference.getContext(), "Change Unsuccessful", Toast.LENGTH_SHORT).show();
                                password.setText("");
                            }
                        });
                return true;
            });

            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    switch ((String) newValue) {
                        case "light":
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                        case "dark":
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                    }
                    return true;
                }
            });
        }
    }
}