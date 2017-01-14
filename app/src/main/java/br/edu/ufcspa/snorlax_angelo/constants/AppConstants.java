package br.edu.ufcspa.snorlax_angelo.constants;

/**
 * Created by Arun on 06-09-2015.
 */
public class AppConstants {

    public enum SharedPreferenceKeys {
        USER_NAME("userName"),
        USER_EMAIL("userEmail"),
        USER_IMAGE_URL("userImageUrl");


        private String value;

        SharedPreferenceKeys(String value) {
            this.value = value;
        }

        public String getKey() {
            return value;
        }
    }


    public static final String GOOGLE_CLIENT_ID = "AIzaSyB57PDWPwjE6Rm6rjq2f7wZZk72Cn1f1T4";
}
