package app.geochat.ui.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import app.geochat.LoginActivity;
import app.geochat.R;
import app.geochat.beans.SharedPreferences;
import app.geochat.util.Utils;


public class SplashScreenActivity extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;
    private RelativeLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        mSharedPreferences = new SharedPreferences(this);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        setUpAnimation();
        setupGCMDeviceToken();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mSharedPreferences.getLoggedIn())
                    Utils.startHomeActivity(SplashScreenActivity.this);
                else
                    showLoginScreen();
            }
        }, 1500);
    }

    private void setUpAnimation() {
    }

    private void setupGCMDeviceToken() {
        if (Utils.checkPlayServices(SplashScreenActivity.this)) {
            if (mSharedPreferences.getDeviceToken().isEmpty() || mSharedPreferences.getDeviceToken() == null)
                Utils.registerInBackground(SplashScreenActivity.this, mSharedPreferences);
            else
                Log.e("Token", mSharedPreferences.getDeviceToken());
        }
    }

    public void showLoginScreen() {
        Intent intent = new Intent(SplashScreenActivity.this,LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_up_out);
    }
}
