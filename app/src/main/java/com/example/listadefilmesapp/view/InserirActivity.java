package com.example.listadefilmesapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listadefilmesapp.R;
import com.example.listadefilmesapp.api.DataService;
import com.example.listadefilmesapp.database.ConfigDatabase;
import com.example.listadefilmesapp.databinding.ActivityInserirBinding;
import com.example.listadefilmesapp.factory.InserirViewModelFactory;
import com.example.listadefilmesapp.model.Filmes;
import com.example.listadefilmesapp.model.Resultado;
import com.example.listadefilmesapp.repo.InserirRepository;
import com.example.listadefilmesapp.viewmodel.InserirViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class InserirActivity extends AppCompatActivity {


    InserirViewModel viewModel;
    ActivityInserirBinding bind;

    private TextInputEditText nome, genero, ano;

    private Boolean isEdit = false;

    /**
     * Instancia a classe modelo para os filmes
     **/
    Filmes filme = new Filmes();

    private Filmes filmeAtual;

    /**
     * Referência para o nó "filmes no firebase
     **/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Adicionar filme");
        View view = LayoutInflater.from(this).inflate(R.layout.activity_inserir, null, true);
        setContentView(view);

        bind = ActivityInserirBinding.bind(view);
        viewModel = new ViewModelProvider(this,
                new InserirViewModelFactory(new InserirRepository(this, this)))
                .get(InserirViewModel.class);

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
            isEdit = true;
            nome.setText(filmeAtual.getTitulo());
            genero.setText(filmeAtual.getGenero());
            ano.setText(filmeAtual.getAno());
        }
        ano.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {

                    //sNome = nome.getText().toString();

                    if (!isEdit) {
                        filmeAtual = new Filmes();
                        filmeAtual.genero = bind.inputGenero.getText().toString();
                        filmeAtual.titulo = bind.inputNomeFilme.getText().toString();
                        filmeAtual.ano = bind.inputAno.getText().toString();
                    }

                    viewModel.salvarFilme(ConfigDatabase.AUTH.getCurrentUser(), filmeAtual, isEdit);

                    /** Chama o método para buscar url da imagem de capa (poster) com base no titulo do filme
                     * Passa o id do usuário logado e o titulo do filme que o usuario digitou **/
                    //recuperarCapa(ConfigDatabase.AUTH.getCurrentUser(), sNome);

                }
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inserir, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.confirm) {
            if (!isEdit) {
                filmeAtual = new Filmes();
                filmeAtual.genero = bind.inputGenero.getText().toString();
                filmeAtual.titulo = bind.inputNomeFilme.getText().toString();
                filmeAtual.ano = bind.inputAno.getText().toString();
            }

            viewModel.salvarFilme(ConfigDatabase.AUTH.getCurrentUser(), filmeAtual, isEdit);

            /** Chama o método para buscar url da imagem de capa (poster) com base no titulo do filme
             * Passa o id do usuário logado e o titulo do filme que o usuario digitou **/
            //recuperarCapa(ConfigDatabase.AUTH.getCurrentUser(), sNome);
        }
        return super.onOptionsItemSelected(item);
    }
}
