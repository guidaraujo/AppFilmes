package com.example.listadefilmesapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.example.listadefilmesapp.R;
import com.example.listadefilmesapp.firebase.ConfigDatabase;

public class SplashActivity extends AppCompatActivity {
    ConfigDatabase configDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    /** Instancia a classe com objetos do Firebase **/
                    configDatabase = new ConfigDatabase();

                    /**
                     * Verifica se o usuário está logado
                     * Caso esteja, ele chama a MainActivity
                     * Caso não esteja, chama a LoginActivity
                     **/
                    if (configDatabase.auth.getCurrentUser()!=null){

                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }, 2000);



    }
}
