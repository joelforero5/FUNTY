package net.electrosoftware.myapp2.activityes;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import net.electrosoftware.myapp2.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends Activity {
    public static final String SPLASH_SCREEN_OPTION_3 = "Option 3";

    private ImageView img_logo_splash;
    private TextView txt_splash_appname;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.READ_PHONE_STATE};

    private long splashDelay = 3500; // tiempo de duración de la pantalla en milisegunfos 1000ms = 1 seg
    Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        verifyStoragePermissions(this);
        img_logo_splash = (ImageView) findViewById(R.id.img_logo_splash);
        txt_splash_appname = (TextView) findViewById(R.id.txt_splash_appname);

        setAnimation(SPLASH_SCREEN_OPTION_3);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mainIntent = new Intent().setClass(SplashScreen.this, Login.class);
                startActivity(mainIntent);
                finish();
            }

        };
        Timer timer = new Timer();
        timer.schedule(task, splashDelay);

    }

    private void setAnimation(String category) {
        if (category.equals(SPLASH_SCREEN_OPTION_3)) {
            animation2();
            animation3();
        }
    }

    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(img_logo_splash, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(1200);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(img_logo_splash, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(1200);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(img_logo_splash, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    private void animation2() {
        img_logo_splash.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        img_logo_splash.startAnimation(anim);
    }

    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(txt_splash_appname, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    1001  // pass any request code
            );
    }

}
