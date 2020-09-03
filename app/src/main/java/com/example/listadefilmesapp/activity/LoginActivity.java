package com.example.listadefilmesapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listadefilmesapp.R;
import com.example.listadefilmesapp.firebase.ConfigDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {

    ConfigDatabase configDatabase;

    private TextInputEditText inputEmail, inputSenha;
    private Button btLog;
    private TextView txtCad;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(R.string.login);

        btLog = findViewById(R.id.btLog);
        inputSenha = findViewById(R.id.inputSenhaLogin);
        inputEmail = findViewById(R.id.inputEmailLogin);
        txtCad = findViewById(R.id.txtCad);



        btLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** Chama o método de login, passando o email e a senha digitados **/
                login(inputEmail.getText().toString(), inputSenha.getText().toString());
            }
        });


        txtCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /** Ao tocar no textView, é chamada a activity de cadastro **/
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });
    }

    /** Método para realzar o login, recebendo email e senha digitados com parâmetros **/
    public void login(String email, String senha) {

        configDatabase = new ConfigDatabase();

        /** Realiza o login com email e senha **/
        configDatabase.AUTH.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                /** Verifica se a tarefa foi bem sucedida
                 * Caso tenha sido, a MainActivity é chamada
                 * Caso haja falha, é exibido um toast
                 * **/
                if (task.isSuccessful()) {


                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "Falha ao logar", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
