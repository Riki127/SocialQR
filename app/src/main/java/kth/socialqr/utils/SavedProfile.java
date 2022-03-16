package kth.socialqr.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import kth.socialqr.models.Profile;

/**
 * Utility class for saving and reading user's profile.
 * Saves the singleton class as serialized object and when needed reads the saved serialized object.
 * Primary use of this class is to have data locally stored to not force the need for internet connection just to show user's profile.
 */

public class SavedProfile {

    private static String FILE_NAME = "profile";

    public static void storeCurrentProfile(Context context) {
        FileOutputStream writer = null;
        try {
            writer = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(writer);
            oos.writeObject(Profile.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readSavedProfile(Context context) {
        Profile profile = Profile.getInstance();
        FileInputStream reader = null;
        try {
            reader = context.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(reader);
            Profile tmp = (Profile) ois.readObject();

            profile.setProfileName(tmp.getProfileName());
            profile.setDescription(tmp.getDescription());
            profile.setSocialLinks(tmp.getSocialLinks());



        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
