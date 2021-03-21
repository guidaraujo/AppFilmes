package com.example.listadefilmesapp.viewmodel;

import android.app.Application;

import com.example.listadefilmesapp.model.Filmes;
import com.example.listadefilmesapp.repo.InserirRepository;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class InserirViewModel extends ViewModel {
    InserirRepository repository;
    public InserirViewModel(InserirRepository repository) {
    this.repository = repository;
    }

    public void salvarFilme(final FirebaseUser userId, final Filmes filme, Boolean isEdit){
        repository.recuperarCapa(userId, filme, isEdit);
    }
}

