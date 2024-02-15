package ch.zhaw.init.orwell_a.common;

public class AppPreferences {
    private static final String PREFERENCES_NAME = "spy_preferences";
    private static final String PREFERENCE_CAMERA = "spy_camera";
    private static final String PREFERENCES_MICROPHONE = "spy_microphone";
    private static final String PREFERENCES_LOCATION = "spy_location";


    public static String getPreferencesName() {
        return PREFERENCES_NAME;
    }

    public static String getPreferenceCamera() {
        return PREFERENCE_CAMERA;
    }

    public static String getPreferencesMicrophone() {
        return PREFERENCES_MICROPHONE;
    }

    public static String getPreferencesLocation() {
        return PREFERENCES_LOCATION;
    }
}
