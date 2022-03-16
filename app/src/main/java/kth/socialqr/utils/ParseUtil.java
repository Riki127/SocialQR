package kth.socialqr.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kth.socialqr.models.ScannedProfile;
import kth.socialqr.models.SocialLink;
import kth.socialqr.models.SocialMediaEnums;

/**
 * Utility class for parsing a scanned profile from the firebase db
 */

public class ParseUtil {
    public static ScannedProfile parseProfile(HashMap hashMap) {
        ScannedProfile profile = ScannedProfile.getInstance();

        profile.setProfileName(hashMap.get("profileName").toString());
        profile.setDescription(hashMap.get("description").toString());

        List tmp = (List) hashMap.get("socialLinks");
        List<SocialLink> socialLinks = new ArrayList<>();
        if (tmp != null) {
            for (int i = 0; i < tmp.size(); i++) {
                HashMap socialLink = (HashMap) tmp.get(i);
                SocialMediaEnums enumInsert = SocialMediaEnums.valueOf(socialLink.get("socialEnum").toString());
                socialLinks.add(new SocialLink(enumInsert,socialLink.get("socialMediaName").toString()));
            }
        }

        profile.setSocialLinks(socialLinks);
        return profile;
    }
}
