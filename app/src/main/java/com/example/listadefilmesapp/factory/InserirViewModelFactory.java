package com.example.listadefilmesapp.factory;

import com.example.listadefilmesapp.repo.InserirRepository;
import com.example.listadefilmesapp.viewmodel.InserirViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class InserirViewModelFactory implements ViewModelProvider.Factory {
    InserirRepository repository;
    public InserirViewModelFactory(InserirRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(InserirViewModel.class)){

            return (T) new InserirViewModel(repository);

        }else {
            throw new IllegalArgumentException("Classe desconhecida");
        }
    }
}
