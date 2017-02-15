package classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMRegistration {
    private GoogleCloudMessaging GCM;
    private Context context;
    private SharedPreferences villaprefs;
    private SharedPreferences.Editor prefsedit;
    private static String PREFS_NAME = "villaprefs";
    private static String PROPERTY_CLIENT = "client";
    private static String PROPERTY_PREFS = "preferences";
    private static String SERVER_API = "AIzaSyCajUoDQk1GCTAer02ARHbjw9I_vkpgrMI";
    private static String PROJECT_NUMBER = "954887413355";

}
