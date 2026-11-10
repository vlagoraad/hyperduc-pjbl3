package com.example.projetoversaofinal.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Etiqueta (tag) para classificar atividades, ex.: "urgente", "backend".
@Entity
@Table(name = "etiqueta")
public class Etiqueta implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private int id;

    @Column(nullable = false, unique = true, length = 40)
    private String nome;

    @Column(length = 20)
    private String cor;

    // Lado inverso do N:N. Quem controla a tabela atividade_etiqueta é Atividade.
    @ManyToMany(mappedBy = "etiquetas")
    private List<Atividade> atividades = new ArrayList<>();

    protected Etiqueta() {
    }

    public Etiqueta(int id, String nome, String cor) {
        this.id = id;
        this.nome = nome;
        this.cor = cor;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCor() { return cor; }
    public List<Atividade> getAtividades() { return atividades; }

    public void setNome(String nome) { this.nome = nome; }
    public void setCor(String cor) { this.cor = cor; }

    public int getQuantidadeAtividades() {
        return atividades.size();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Etiqueta outra && outra.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nome;
    }
}
