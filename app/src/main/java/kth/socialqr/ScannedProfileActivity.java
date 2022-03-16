package kth.socialqr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

import kth.socialqr.models.Profile;
import kth.socialqr.models.ScannedProfile;
import kth.socialqr.ui.SocialLinkAdapter;
import kth.socialqr.ui.SocialScannedAdapter;
import kth.socialqr.utils.ParseUtil;
import kth.socialqr.utils.PlatformURL;

public class ScannedProfileActivity extends AppCompatActivity {

    private ScannedProfile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_profile);
        Intent intent = getIntent();
        HashMap scannedProfile = (HashMap) intent.getSerializableExtra("data");
        profile = ParseUtil.parseProfile(scannedProfile);

        TextView name = findViewById(R.id.scanned_profile_name);
        TextView description = findViewById(R.id.scanned_description);

        name.setText(profile.getProfileName());
        description.setText(profile.getDescription());

        RecyclerView socialLinksRV = findViewById(R.id.scanned_social_links);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        socialLinksRV.setLayoutManager(linearLayoutManager);

        SocialScannedAdapter socialLinkAdapter = new SocialScannedAdapter(position -> {
            String URL = PlatformURL.PlatformString(profile.getSocialLinks().get(position).getSocialEnum()) +
                    profile.getSocialLinks().get(position).getSocialMediaName();
            openSocialPlatform(URL);
        });
        socialLinksRV.setAdapter(socialLinkAdapter);

        Toolbar toolbar = findViewById(R.id.scanned_profile_toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void openSocialPlatform(String URL) {
        Uri webpage = Uri.parse(URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e("Intent", "Failed implicit intent");
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
