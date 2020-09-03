package com.example.listadefilmesapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.listadefilmesapp.R;
import com.example.listadefilmesapp.adapter.AdapterFilmes;
import com.example.listadefilmesapp.firebase.ConfigDatabase;
import com.example.listadefilmesapp.model.Filmes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ConfigDatabase database = new ConfigDatabase();

    private DatabaseReference reference = database.REFERENCE.child("filmes");
    private DatabaseReference referenciaUid;
    private ChildEventListener childEventListener;
    private Filmes filmeSelecionado;
    private RecyclerView recFilmes;
    private AdapterFilmes adapterFilmes;
    public List<Filmes> lista = new ArrayList<>();
    public String id;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** Variável do tipo FirebaseUser que recebe o id do usuário **/
        FirebaseUser user = database.AUTH.getCurrentUser();

        /** String que recebe o id do usuário **/
        id = user.getUid();

        /** Referência para o nó específico do usuário logado **/
        referenciaUid = reference.child(id);

        recFilmes = findViewById(R.id.recyclerFilmes);

        recFilmes.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recFilmes, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                /** Objeto do tipo Filmes que recebe os dados do filme selecionado **/
                filmeSelecionado = lista.get(position);
                Intent intent = new Intent(MainActivity.this, InserirActivity.class);

                /** Passa os dados do filmeSelecionado para a intent **/
                intent.putExtra("filmeSelec", filmeSelecionado);


                /** Chama a activity passando os dados na intent **/
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

                filmeSelecionado = lista.get(position);
                String id = filmeSelecionado.getId();
                alertDialogExcluir(id);


            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

        /** FAB que chama a atividade de adicionar um filme **/
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InserirActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        /** Chama o método de carregar a lista toda vez que o onStart é chamado **/
        carregarLista();


    }

    @Override
    protected void onStop() {
        super.onStop();

        /** Remove o listener quando a activity é pausada **/
        referenciaUid.removeEventListener(childEventListener);

    }

    /** Método para carregar a lista no recyclerView **/
    public void carregarLista() {

        /** Lista que vai ser exibida no recycler recebe o método que
         *  lista os dados do Firebase e retorna uma lista **/
        lista = listar();
        adapterFilmes = new AdapterFilmes(lista, getApplicationContext());

        /** Configura o recyclerView **/
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recFilmes.setLayoutManager(layoutManager);
        recFilmes.setHasFixedSize(true);
        recFilmes.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recFilmes.setAdapter(adapterFilmes);

    }

    /** Método que retorna a lista com dados do Firebase **/
    public List<Filmes> listar() {

        final List<Filmes> filmesLista = new ArrayList<>();

        /** Listener para recuperar os dados do Firebase **/
        childEventListener = referenciaUid.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                /** Cria um objeto do tipo filmes que recebe os dados do Firebase **/
                Filmes filmes = dataSnapshot.getValue(Filmes.class);

                /** Seta as chaves dos nós do Firebase em uma String em Filmes **/
                filmes.setId(dataSnapshot.getKey());

                /** Adiciona os dados do tipo Filmes na lista **/
                filmesLista.add(filmes);
                adapterFilmes.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return filmesLista;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case R.id.sair:
                /** Chama o método que cria o AlertDialog **/
                alertDialogSair();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    /** Método que criar o popup para confirmar o signOut **/
    public void alertDialogSair() {


        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Confirmar logout");

        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.AUTH.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.setNegativeButton("Não", null);
        dialog.create();
        dialog.show();

    }

    public void alertDialogExcluir(final String idChild){

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Confirmar exclusão");

        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                reference.child(id).child(idChild).removeValue();
                carregarLista();

            }
        });
        dialog.setNegativeButton("Não", null);
        dialog.create();
        dialog.show();

    }
}
