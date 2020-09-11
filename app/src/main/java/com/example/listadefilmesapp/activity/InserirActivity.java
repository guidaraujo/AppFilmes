package com.example.listadefilmesapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listadefilmesapp.R;
import com.example.listadefilmesapp.api.DataService;
import com.example.listadefilmesapp.firebase.ConfigDatabase;
import com.example.listadefilmesapp.model.Filmes;
import com.example.listadefilmesapp.model.Resultado;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class InserirActivity extends AppCompatActivity {

    /**
     * Instacia a classe com atributos do Firebase
     **/
    ConfigDatabase database = new ConfigDatabase();

    private Retrofit retrofit;

    /** Constantes com dados para query da API de pesquisa **/
    private static final String BASE_URL = "https://www.googleapis.com/customsearch/";
    private static final String KEY = "AIzaSyCKM2xSlNUpybQeS_Hx97-IUTOm7G_t8fw";
    private static final String CX = "585630a4f613ff306";

    private TextInputEditText nome, genero, ano;

    private String sNome, sGenero, sAno, sUrl;

    static String urlResultadoFinal;

    /**
     * Instancia a classe modelo para os filmes
     **/
    Filmes filme = new Filmes();

    private Filmes filmeAtual;

    /**
     * Referência para o nó "filmes no firebase
     **/
    private DatabaseReference reference = database.REFERENCE.child("filmes");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Adicionar filme");
        setContentView(R.layout.activity_inserir);
        nome = findViewById(R.id.inputNomeFilme);
        genero = findViewById(R.id.inputGenero);
        ano = findViewById(R.id.inputAno);

        retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


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
        ano.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean test = false;
                if (i== EditorInfo.IME_ACTION_DONE){

                    sNome = nome.getText().toString();

                    /** Chama o método para buscar url da imagem de capa (poster) com base no titulo do filme
                     * Passa o id do usuário logado e o titulo do filme que o usuario digitou **/
                    recuperarCapa(database.AUTH.getCurrentUser(), sNome);
                    test = true;

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.confirm:

                sNome = nome.getText().toString();

                /** Chama o método para buscar url da imagem de capa (poster) com base no titulo do filme
                 * Passa o id do usuário logado e o titulo do filme que o usuario digitou **/
                recuperarCapa(database.AUTH.getCurrentUser(), sNome);

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /** Método que recupera a capa do filme com base no titulo que o usuario digitou **/
    public void recuperarCapa(final FirebaseUser userId, String nome) {

        /** String que recebe o nome do filme, mas com "+" no lugar dos espaços, para pesquisa na WEB **/
        String nomeNoSpaces = nome.replace(" ", "+");

        DataService dataService = retrofit.create(DataService.class);

        /** Objeto call do tipo Resultado que recebe o método de pesquisa @GET que recupera a capa
         * passando como parâmetros de pesquisa o nome do filme sem espaços e com "+official+poster"
         * para melhorar os resultados da pesquisa, a KEY da API de pesquisa do Google, o código CX
         * do Motor de Pesquisa programável (CSE), e o númmero máximo de resultados
         * **/
        Call<Resultado> call = dataService.recuperarCapa(nomeNoSpaces + "+official+poster", KEY, CX, "1");

        call.enqueue(new Callback<Resultado>() {
            @Override
            public void onResponse(Call<Resultado> call, Response<Resultado> response) {
                /** Objeto do tipo Resultado que recebe o resultado da pesquisa **/
                Resultado resultado = response.body();

                /** String urlResultado que recebe somente a url da imagem **/
                String urlResultado = resultado.items.get(0).pagemap.cse_image.get(0).src;

                /**Chama o método salvar passando o id do usuário e a url da imagem**/
                salvar(userId, urlResultado);
            }

            @Override
            public void onFailure(Call<Resultado> call, Throwable t) {

            }
        });
    }

    /**
     * Método para salvar(ou editar) que recebe o id do usuário ativo e a url da capa como parâmetro
     **/
    public void salvar(FirebaseUser user, String urlResultado) {

        sNome = nome.getText().toString();
        sGenero = genero.getText().toString();
        sAno = ano.getText().toString();
        sUrl = urlResultado;

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
                filme.setUrl(sUrl);

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
                filme.setUrl(sUrl);

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
