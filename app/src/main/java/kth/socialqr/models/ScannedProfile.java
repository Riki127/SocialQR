package kth.socialqr.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for a scanned profile
 */

public class ScannedProfile {
    private String profileName;
    private String description;
    private List<SocialLink> socialLinks;
    private static ScannedProfile scannedProfile = null;

    private ScannedProfile() {
        socialLinks = new ArrayList<>();
    }

    public static ScannedProfile getInstance(){
        if(scannedProfile == null){
            scannedProfile = new ScannedProfile() ;
        }
        return scannedProfile;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SocialLink> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(List<SocialLink> socialLinks) {
        this.socialLinks = socialLinks;
    }

}
