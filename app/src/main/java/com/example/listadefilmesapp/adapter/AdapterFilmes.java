package com.example.listadefilmesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.listadefilmesapp.R;
import com.example.listadefilmesapp.model.Filmes;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Classe de adapter que extende a subclasse MyViewHolder
 **/
public class AdapterFilmes extends RecyclerView.Adapter<AdapterFilmes.MyViewHolder> {

    private static final String DEFAULT_IMAGE = "https://www.meirellesmilare.com.br/wp-content/themes/cardinal/images/default-thumb.png";

    private Context context;

    List<Filmes> filmeslista;

    /**
     * Construtor da classe AdapterFilmes que recebe uma lista do tipo filmes e o context
     **/
    public AdapterFilmes(List<Filmes> lista, Context c) {
        this.filmeslista = lista;
        this.context = c;
    }

    /**
     * Subclase MyViewHolder que cria o ViewHolder e retorna o layout do adapter_filmes
     **/
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_filmes, parent, false);

        return new MyViewHolder(layout);
    }

    /**
     * Ao exibir o recyclerView, cada item recebe o seu correspondente da lista, através do int position
     **/
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Filmes filmes = filmeslista.get(position);

        if (filmes.url == null || filmes.url.equals("")) {

            Picasso.get()
                    .load(DEFAULT_IMAGE)
                    .into(holder.capa);

        }

        Picasso.get().load(filmes.getUrl()).resize(864, 1280).into(holder.capa);
        holder.titulo.setText(filmes.getTitulo());
        holder.genero.setText(filmes.getGenero());
        holder.ano.setText(filmes.getAno());

    }

    /**
     * Retorna a quantidade de itens, pegando o tamanho da lista
     **/
    @Override
    public int getItemCount() {
        return this.filmeslista.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, ano, genero;
        ImageView capa;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            capa = itemView.findViewById(R.id.imgCapa);
            titulo = itemView.findViewById(R.id.txtTitulo);
            genero = itemView.findViewById(R.id.txtGen);
            ano = itemView.findViewById(R.id.txtAno);
        }
    }
}
