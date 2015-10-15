package com.permutassep.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lalongooo.permutassep.R;
import com.permutassep.PermutasSEPApplication;
import com.permutassep.ui.ActivityLogin;
import com.permutassep.ui.ActivityMain;
import com.permutassep.ui.ActivitySignUp;

public class ActivityLoginSignUp extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ca_fragment_login_signup);
        setUI();
    }

    private void setUI() {

        TextView btnRegister;
        TextView btnLogin;
        TextView tvContinue;

        btnRegister = (TextView) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityLoginSignUp.this, ActivitySignUp.class));
            }
        });

        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityLoginSignUp.this, ActivityLogin.class));
            }
        });

        tvContinue = (TextView) findViewById(R.id.tvContinue);
        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Tracker t = ((PermutasSEPApplication) getApplication()).getTracker();
                t.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_event_category_ux))
                        .setAction(getString(R.string.ga_event_action_click))
                        .setLabel(getString(R.string.ga_login_action_anonymous))
                        .build());

                Intent i = new Intent().setClass(ActivityLoginSignUp.this, ActivityMain.class);
                startActivity(i);
            }
        });
    }
}