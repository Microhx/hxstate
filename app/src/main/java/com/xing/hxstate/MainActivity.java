package com.xing.hxstate;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.xing.viewstate.HxStateView;
import com.xing.viewstate.OnLayoutRetryListener;

public class MainActivity extends AppCompatActivity implements OnLayoutRetryListener {

  private HxStateView stateView1;
  private HxStateView stateView2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    stateView1 = findViewById(R.id.id_state_1);
    stateView1.registerRetryId(R.id.id_state_error, this);

    stateView2 = findViewById(R.id.id_state_2);

    stateView1.showLoading();
    stateView2.showLoading();

    stateView1.postDelayed(new Runnable() {
      @Override public void run() {
        stateView1.showError();
        stateView2.addContentView(R.layout.content_layout);

      }
    },4000);

  }

  @Override public void onRetry() {
    stateView1.showLoading();
    stateView1.postDelayed(new Runnable() {
      @Override public void run() {
        stateView1.showEmpty();
      }
    },3000);

  }
}
