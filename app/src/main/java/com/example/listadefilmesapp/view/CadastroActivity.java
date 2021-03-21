package com.example.listadefilmesapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.listadefilmesapp.R;
import com.example.listadefilmesapp.database.ConfigDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText inputSenha, inputEmail, inputConfirmar;
    private Button btCad;
    private ConfigDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle(this.getResources().getString(R.string.cadastro));

        inputEmail = findViewById(R.id.inputEmailCadastro);
        inputSenha = findViewById(R.id.inputSenhaCadastro);
        inputConfirmar = findViewById(R.id.inputConfirmarCadastro);
        btCad = findViewById(R.id.btCad);

        btCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** Strings que recebem o texto dos EditText convertidos para String **/
                String email = inputEmail.getText().toString();
                String senha = inputSenha.getText().toString();
                String confirmar = inputConfirmar.getText().toString();

                /** Verifica se os campos estão vazios **/
                if (email.isEmpty() || senha.isEmpty() || confirmar.isEmpty()) {

                    Toast.makeText(getApplicationContext(),
                            "Digite todos os campos",
                            Toast.LENGTH_SHORT).show();
                    inputEmail.setText("");
                    inputSenha.setText("");
                    inputConfirmar.setText("");

                } else {

                    /** Verifica se a senha digitada é igual à senha confirmada
                     * Caso seja, é chamado o método de cadastrar
                     * caso não seja, é exibido um toast
                     * **/
                    if (senha.equals(confirmar)){

                        /** Método que passa o email e a senha digitados como parâmetros **/
                        cadastrar(email, senha);

                    }else{

                        Toast.makeText(CadastroActivity.this,
                                "As senhas estão diferentes",
                                Toast.LENGTH_SHORT).show();
                        inputSenha.setText("");
                        inputConfirmar.setText("");

                    }

                }


            }
        });

    }

    /** Método de cadastro com email e senha como parâmetros **/
    public void cadastrar(String email, String senha) {

        /** Instância da classe ConfigDatabase **/
        database = new ConfigDatabase();

        /** Cria um usuário com email e senha**/
        database.AUTH.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        /** Verifica se a tarefa foi bem sucedida **/
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Cadastro realizado com sucesso",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Falha ao realizar cadastro",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}
