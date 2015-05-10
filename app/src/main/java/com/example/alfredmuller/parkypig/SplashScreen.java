package com.example.alfredmuller.parkypig;



        import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * SplashScreen is a class that creates an activity window that displays our splash screen on startup.
 * The duration of the screen is set at 6 seconds on a timer.
 * @author Alf Muller
 */
public class SplashScreen extends Activity {

    /**
     * Default constructor for SplashScreen. Not used.
     */
    public SplashScreen(){}


    // Splash screen timer constant
    private static int SPLASH_TIME_OUT = 6000;

    /**
     * onCreate is the first method called when the app starts.
     * @param savedInstanceState
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        // Showing splash screen with a timer.
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Starts the main activity, MapsActivity class
                Intent i = new Intent(SplashScreen.this, MapsActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}