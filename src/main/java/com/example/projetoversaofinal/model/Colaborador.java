package com.example.projetoversaofinal.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "colaborador")
public class Colaborador implements Serializable {
    private static final long serialVersionUID = 1L;

    // A chave do colaborador é um código digitado (ex.: C01), por isso String.
    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 120)
    private String email;

    @Column(length = 60)
    private String funcao;

    // Lado inverso da associação com Projeto (via Participacao).
    @OneToMany(mappedBy = "colaborador")
    private List<Participacao> participacoes = new ArrayList<>();

    protected Colaborador() {
    }

    public Colaborador(String id, String nome, String email, String funcao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.funcao = funcao;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getFuncao() { return funcao; }
    public List<Participacao> getParticipacoes() { return participacoes; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setFuncao(String funcao) { this.funcao = funcao; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Colaborador outro && Objects.equals(outro.id, id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " - " + nome + (funcao == null || funcao.isBlank() ? "" : " (" + funcao + ")");
    }
}
