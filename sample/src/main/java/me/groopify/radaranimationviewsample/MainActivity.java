package me.groopify.radaranimationviewsample;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import me.groopify.radaranimationview.R;
import me.groopify.radaranimationview.RadarAnimationView;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    RadarAnimationView radarAnimationView =
        (RadarAnimationView) findViewById(R.id.radar_animation_view);
    radarAnimationView.setNumberOfItemsToDiscover(7);
    radarAnimationView.setCounterTextSizeDp(40);
    radarAnimationView.beginAnimation(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {

      }

      @Override public void onAnimationEnd(Animator animator) {
        Toast.makeText(MainActivity.this, "Discovered 7 items!", Toast.LENGTH_SHORT).show();
      }

      @Override public void onAnimationCancel(Animator animator) {

      }

      @Override public void onAnimationRepeat(Animator animator) {

      }
    });
  }
}
