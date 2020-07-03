package com.example.listadefilmesapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.listadefilmesapp.R;
import com.example.listadefilmesapp.firebase.ConfigDatabase;
import com.example.listadefilmesapp.model.Filmes;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class InserirActivity extends AppCompatActivity {

    /** Instacia a classe com atributos do Firebase **/
    ConfigDatabase database = new ConfigDatabase();

    private TextInputEditText nome, genero, ano;

    private String sNome, sGenero, sAno;

    /** Instancia a classe modelo para os filmes **/
    Filmes filme = new Filmes();

    private Filmes filmeAtual;

    /** Referência para o nó "filmes no firebase **/
    private DatabaseReference reference = database.reference.child("filmes");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Adicionar filme");
        setContentView(R.layout.activity_inserir);
        nome = findViewById(R.id.inputNomeFilme);
        genero = findViewById(R.id.inputGenero);
        ano = findViewById(R.id.inputAno);

        /** Objeto do tipo Filmes recebe os dados(filmeSelecionado) passados através da intent **/
        filmeAtual = (Filmes) getIntent().getSerializableExtra("filmeSelec");

        /** Verifica se filmeAtual é vazio
         * Caso seja vazio, não faz nada
         * Caso não seja vazio, seta os dados do filmeSelecionado(titulo, genero e ano) nos campos
         * **/
        if (filmeAtual != null) {
            nome.setText(filmeAtual.getTitulo());
            genero.setText(filmeAtual.getGenero());
            ano.setText(filmeAtual.getAno());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inserir, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.confirm:
                /** Chama o método para salvar (ou editar) passando o usuário ativo do app **/
                salvar(database.auth.getCurrentUser());

                break;

        }


        return super.onOptionsItemSelected(item);
    }

    /** Método para salvar(ou editar) que recebe o id do usuário ativo como parâmetro **/
    public void salvar(FirebaseUser user) {

        sNome = nome.getText().toString();
        sGenero = genero.getText().toString();
        sAno = ano.getText().toString();

        /** String que recebe o id do usuário **/
        String userId = user.getUid();

        /**
         * Verifica se o método foi chamado para edição ou salvamento de novo filme
         * Caso filmeAtual seja nulo, é para salvar
         * Caso não seja nulo, é para editar
         * **/
        if (filmeAtual == null) {/** SALVAR **/
            /** Verifica se os campos foram preenchidos **/
            if (sNome != null && sGenero != null && sAno != null) {


                filme.setTitulo(sNome);
                filme.setGenero(sGenero);
                filme.setAno(sAno);

                reference.child(userId).push().setValue(filme);

                Toast.makeText(this, "Filme salvo com sucesso", Toast.LENGTH_SHORT).show();

                finish();
            } else {
                Toast.makeText(this, "Preencha todos", Toast.LENGTH_SHORT).show();
            }
        } else {/** EDIÇÃO **/
            /** Verifica se os campos foram preenchidos **/
            if (sNome != null && sGenero != null && sAno != null) {

                /** String que recebe a key do nó sendo editado **/
                String id = filmeAtual.getId();

                filme.setTitulo(sNome);
                filme.setGenero(sGenero);
                filme.setAno(sAno);

                /** Salva os dados editados no nó do usuário logado **/
                reference.child(userId).child(id).setValue(filme);

                Toast.makeText(this, "Filme editado com sucesso", Toast.LENGTH_SHORT).show();

                finish();
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
