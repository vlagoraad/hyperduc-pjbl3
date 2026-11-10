package com.example.projetoversaofinal.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

// Memento, papel de zelador: guarda uma pilha de mementos por registro.
public class Historico<M> {

    private final Map<Object, Deque<M>> pilhas = new HashMap<>();

    public void salvar(Object id, M memento) {
        pilhas.computeIfAbsent(id, k -> new ArrayDeque<>()).push(memento);
    }

    // Devolve o último memento salvo para aquele ID, ou null se não houver.
    public M desfazer(Object id) {
        Deque<M> pilha = pilhas.get(id);
        return (pilha == null || pilha.isEmpty()) ? null : pilha.pop();
    }

    public boolean podeDesfazer(Object id) {
        Deque<M> pilha = pilhas.get(id);
        return pilha != null && !pilha.isEmpty();
    }
}