package tk.iobserver.bjutloginapp.ui;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import tk.iobserver.bjutloginapp.R;
import tk.iobserver.bjutloginapp.BuildConfig;

/**
 * Created by ZeroGo on 2017.2.22.
 */


public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.settings_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        getFragmentManager().beginTransaction().replace(R.id.content, new SettingsFragment()).commit();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }

    public static class SettingsFragment extends PreferenceFragment {
        EditTextPreference userNamePreference;
        EditTextPreference psdNamePreference;
        Preference versionPreference;
        private static Preference.OnPreferenceChangeListener onPreferenceChangeListener = (preference, newValue) -> {
            String value = newValue.toString();
            if (value.isEmpty()) {
                preference.setSummary("Nothing");
            } else if (preference.getKey().equals("password")) {
                preference.setSummary("********");
            } else {
                preference.setSummary(value);
            }
            return true;
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs_settings);

            userNamePreference = (EditTextPreference) findPreference("user");
            psdNamePreference = (EditTextPreference) findPreference("password");
            versionPreference = findPreference("version");

            versionPreference.setSummary(BuildConfig.VERSION_NAME);

            bindPreferenceSummaryToValue(userNamePreference);
            bindPreferenceSummaryToValue(psdNamePreference);
        }

        private static void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(onPreferenceChangeListener);
            onPreferenceChangeListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }
}