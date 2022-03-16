package kth.socialqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import kth.socialqr.models.Profile;
import kth.socialqr.models.SocialLink;
import kth.socialqr.models.SocialMediaEnums;
import kth.socialqr.utils.SavedProfile;
import kth.socialqr.ui.SocialSettingsAdapter;
import kth.socialqr.ui.SpinnerActivity;
import kth.socialqr.utils.AlertUtil;

public class ProfileSettingsActivity extends AppCompatActivity {
    private Profile profile;

    private Spinner spinner;
    private EditText profileName;
    private EditText description;
    private EditText username;
    private SocialSettingsAdapter adapter;

    private SharedPreferences sharedPreferences;
    private String sharedPrefFile = "kth.socialqr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        profileName = findViewById(R.id.profilename_edittext);
        description = findViewById(R.id.description_edittext);
        username = findViewById(R.id.fill_username_edittext);

        profile = Profile.getInstance();

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        initializeSpinner();

        RecyclerView settingsRecyclerView = findViewById(R.id.settings_recyclerview);

        adapter = new SocialSettingsAdapter();
        settingsRecyclerView.setAdapter(adapter);
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        profileName.setText(profile.getProfileName());
        description.setText(profile.getDescription());
    }

    public void saveProfileSettings(View view) {
        if(profileName.getText().length() > 2){
            profile.setProfileName(profileName.getText().toString());
            profile.setDescription(description.getText().toString());
            saveProfile();
        }
        else AlertUtil.createAlert("Profile name needs to be atleast 3 characters.","Too short profile name", ProfileSettingsActivity.this).show();
    }

    public void addSocialMedia(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) ProfileSettingsActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(username.getText().length() > 0){
            profile.getSocialLinks().add(new SocialLink(SocialMediaEnums.valueOf(
                    spinner.getSelectedItem().toString().toUpperCase()), username.getText().toString()));
            adapter.notifyDataSetChanged();
        }
        else AlertUtil.createAlert("Please fill in username.","Add social media error", ProfileSettingsActivity.this).show();
    }

    private void initializeSpinner() {
        spinner = findViewById(R.id.platform_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.platform_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new SpinnerActivity());
    }

    public void saveProfile() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("profiles");

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            String id = sharedPreferences.getString("id", null);
            if (id == null) {
                id = database.getReference("profiles").push().getKey();
            }
            String finalId = id;
            if (id != null) {
                ref.child(id).setValue(profile)
                        .addOnSuccessListener(unused -> {
                            AlertUtil.createAlert("Profile has been saved.", "Save successful", ProfileSettingsActivity.this).show();
                            SavedProfile.storeCurrentProfile(ProfileSettingsActivity.this);
                            SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                            preferencesEditor.putString("id", finalId);
                            preferencesEditor.apply();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("firebase", "saving data failure", e.getCause());
                            AlertUtil.createAlert("Something went wrong.", "Error", ProfileSettingsActivity.this).show();
                        });
            }
        } else {
            AlertUtil.createAlert("Check your internet connection.", "Network error", ProfileSettingsActivity.this).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}