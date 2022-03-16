package kth.socialqr;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import kth.socialqr.models.Profile;
import kth.socialqr.utils.SavedProfile;
import kth.socialqr.ui.SocialLinkAdapter;
import kth.socialqr.utils.AlertUtil;
import kth.socialqr.utils.PlatformURL;
import kth.socialqr.utils.QRGenerator;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Profile profile;

    private SocialLinkAdapter socialLinkAdapter;
    private TextView profileName, description;
    private ImageView profileQR;
    private RecyclerView socialLinksRV;
    private DrawerLayout drawerLayout;

    private SharedPreferences sharedPreferences;
    private String sharedPrefFile = "kth.socialqr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //View init
        socialLinksRV = findViewById(R.id.social_links_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        socialLinksRV.setLayoutManager(linearLayoutManager);
        profileName = findViewById(R.id.profile_name);
        description = findViewById(R.id.profile_description);
        profileQR = findViewById(R.id.profile_qrcode);

        //Drawer layout init
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.app_toolbar);

        drawerLayout.bringToFront();
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        SavedProfile.readSavedProfile(ProfileActivity.this);

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    //Closes navigation drawer when back is pressed
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Send an implicit intent
    public void openSocialPlatform(String URL) {
        Uri webpage = Uri.parse(URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e("Intent", "Failed implicit intent");
        }
    }

    public void onScanClicked() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        barcodeLauncher.launch(options);
    }

    //Method for scanning a QR-code, library from journeyapps
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    AlertUtil.createAlert("No data from QR-Code scanner.", "QR-Scanner", ProfileActivity.this).show();
                } else {
                    getProfile(result.getContents());
                }
            });

    //Method for getting the scanned profile from firebase db
    private void getProfile(String profileID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("profiles");
        Intent intent = new Intent(this, ScannedProfileActivity.class);

        ref.child(profileID).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                AlertUtil.createAlert("Could not get the scanned profile.", "Error", ProfileActivity.this).show();
            } else {
                if (task.getResult() != null) {
                    HashMap dataHashMap = (HashMap) task.getResult().getValue();
                    intent.putExtra("data", dataHashMap);
                    ProfileActivity.this.startActivity(intent);
                } else {
                    AlertUtil.createAlert("The QR-Code provided no profile.", "Profile error", ProfileActivity.this).show();
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        new Handler().postDelayed(() -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_qr_scan:
                    onScanClicked();
                    break;
                case R.id.nav_settings:
                    Intent intent = new Intent(ProfileActivity.this, ProfileSettingsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_save_qrcode:
                    BitmapDrawable img = (BitmapDrawable) profileQR.getDrawable();
                    if (img != null) {
                        saveToGallery(img.getBitmap());
                    } else
                        AlertUtil.createAlert("You have no QR-code, make a profile.", "QR-code save error", ProfileActivity.this).show();
                    break;
                case R.id.nav_delete_profile:
                    String id = sharedPreferences.getString("id", null);
                    if (id != null) {
                        deleteProfile(id);
                    } else {
                        AlertUtil.createAlert("There is no profile to delete.", "No profile", ProfileActivity.this).show();
                    }
                    break;
            }
        }, 200);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void saveToGallery(Bitmap bitmap) {
        OutputStream outputStream = null;

        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "SocialQR-" + profile.getProfileName());
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures");
        }
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            outputStream = contentResolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            AlertUtil.createAlert("Your QR-code has been saved to your pictures folder.", "Saved QR-code.", ProfileActivity.this).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteProfile(String id) {
        Log.e("In delete", "Delete profile");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("profiles");

        ref.child(id).removeValue()
                .addOnSuccessListener(unused -> {
                    String filePathProfile = getApplicationContext().getFilesDir().getParent() + "/files/profile";
                    File deleteProfile = new File(filePathProfile);
                    boolean isProfileDeleted = deleteProfile.delete();
                    boolean isPrefDeleted = sharedPreferences.edit().clear().commit();
                    profile.resetProfile();
                    if (isProfileDeleted && isPrefDeleted) {
                        AlertUtil.createAlertRefresh("Profile has successfully been deleted.", "Delete profile", ProfileActivity.this, ProfileActivity.this).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("firebase", "deleting profile failure", e.getCause());
                    AlertUtil.createAlert("Something went wrong when deleting.", "Error", ProfileActivity.this).show();
                });
    }

    private void updateUI() {
        profile = Profile.getInstance();
        String id = sharedPreferences.getString("id", null);

        if (id != null) {
            Bitmap bitmap = QRGenerator.generate(ProfileActivity.this, id);
            profileQR.setImageBitmap(bitmap);
        }

        if (profile != null) {
            if (profile.getSocialLinks().size() >= 0) {
                if (socialLinkAdapter == null) {
                    socialLinkAdapter = new SocialLinkAdapter(position -> {
                        String URL = PlatformURL.PlatformString(profile.getSocialLinks().get(position).getSocialEnum()) +
                                profile.getSocialLinks().get(position).getSocialMediaName();
                        openSocialPlatform(URL);
                    });
                    socialLinksRV.setAdapter(socialLinkAdapter);
                } else {
                    socialLinkAdapter.notifyDataSetChanged();
                }
            }

            if (profile.getProfileName() != null) {
                profileName.setText(profile.getProfileName());
            }
            if (profile.getDescription() != null) {
                description.setText(profile.getDescription());
            }
        }
    }
}