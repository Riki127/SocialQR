package kth.socialqr.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

/**
 * Utility class for building alerts
 */

public class AlertUtil {
    public static Dialog createAlert(String message, String title, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(" Ok", (dialog, id) -> { });
        return builder.create();
    }

    public static Dialog createAlertRefresh(String message, String title, Context context, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(" Ok", (dialog, id) -> activity.recreate());
        return builder.create();
    }
}
