package com.example.listadefilmesapp.repo;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.listadefilmesapp.api.DataService;
import com.example.listadefilmesapp.database.ConfigDatabase;
import com.example.listadefilmesapp.model.Filmes;
import com.example.listadefilmesapp.model.Resultado;
import com.example.listadefilmesapp.view.InserirActivity;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InserirRepository {
    private Retrofit retrofit;
    private Context context;
    private Activity activity;

    /**
     * Constantes com dados para query da API de pesquisa
     **/
    private static final String BASE_URL = "https://www.googleapis.com/customsearch/";
    private static final String KEY = "AIzaSyCKM2xSlNUpybQeS_Hx97-IUTOm7G_t8fw";
    private static final String CX = "585630a4f613ff306";

    public InserirRepository(Context context, Activity activity) {
        this.activity = activity;
        this.context = context;
        retrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Método que recupera a capa do filme com base no titulo que o usuario digitou
     **/
    public void recuperarCapa(final FirebaseUser userId, final Filmes filme, final Boolean isEdit) {

        /** String que recebe o nome do filme, mas com "+" no lugar dos espaços, para pesquisa na WEB **/
        String nomeNoSpaces = filme.titulo.replace(" ", "+");

        DataService dataService = retrofit.create(DataService.class);

        /** Objeto call do tipo Resultado que recebe o método de pesquisa @GET que recupera a capa
         * passando como parâmetros de pesquisa o nome do filme sem espaços e com "+official+poster"
         * para melhorar os resultados da pesquisa, a KEY da API de pesquisa do Google, o código CX
         * do Motor de Pesquisa programável (CSE), e o número máximo de resultados
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
                salvar(userId, urlResultado, filme, isEdit);
            }

            @Override
            public void onFailure(Call<Resultado> call, Throwable t) {

            }
        });
    }

    /**
     * Método para salvar(ou editar) que recebe o id do usuário ativo e a url da capa como parâmetro
     **/
    public void salvar(FirebaseUser user, String urlResultado, Filmes filme, boolean isEdit) {

        String sNome = filme.titulo;
        String sGenero = filme.genero;
        String sAno = filme.ano;


        /** String que recebe o id do usuário **/
        String userId = user.getUid();

        /**
         * Verifica se o método foi chamado para edição ou salvamento de novo filme
         * Caso filmeAtual seja nulo, é para salvar
         * Caso não seja nulo, é para editar
         * **/
        if (!isEdit) {/** SALVAR **/
            /** Verifica se os campos foram preenchidos **/
            if (sNome != null && sGenero != null && sAno != null) {

                filme.setTitulo(sNome);
                filme.setGenero(sGenero);
                filme.setAno(sAno);
                filme.setUrl(urlResultado);

                ConfigDatabase.REFERENCE.child("filmes").child(userId).push().setValue(filme);

                Toast.makeText(context, "Filme salvo com sucesso", Toast.LENGTH_SHORT).show();
                activity.finish();
            } else {
                Toast.makeText(context, "Preencha todos", Toast.LENGTH_SHORT).show();
            }
        } else {/** EDIÇÃO **/
            /** Verifica se os campos foram preenchidos **/
            if (sNome != null && sGenero != null && sAno != null) {

                /** String que recebe a key do nó sendo editado **/
                String id = filme.getId();

                filme.setTitulo(sNome);
                filme.setGenero(sGenero);
                filme.setAno(sAno);
                filme.setUrl(urlResultado);

                /** Salva os dados editados no nó do usuário logado **/
                ConfigDatabase.REFERENCE.child("filmes").child(userId).child(id).setValue(filme);

                Toast.makeText(context, "Filme editado com sucesso", Toast.LENGTH_SHORT).show();
                activity.finish();

            } else {
                Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }


        }
    }

}
