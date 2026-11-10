package com.example.projetoversaofinal.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// Entidade associativa entre Projeto e Colaborador (N:N com atributos próprios).
// Foi usada no lugar de um @ManyToMany direto porque a relação tem dados
// (papel e data de entrada) que não caberiam numa tabela de junção simples.
@Entity
@Table(name = "participacao",
        uniqueConstraints = @UniqueConstraint(columnNames = {"projeto_id", "colaborador_id"}))
public class Participacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Colaborador colaborador;

    @Column(nullable = false, length = 60)
    private String papel;

    @Column(name = "data_entrada")
    private LocalDate dataEntrada;

    protected Participacao() {
    }

    public Participacao(int id, Projeto projeto, Colaborador colaborador, String papel, LocalDate dataEntrada) {
        this.id = id;
        this.projeto = projeto;
        this.colaborador = colaborador;
        this.papel = papel;
        this.dataEntrada = dataEntrada;
    }

    public int getId() { return id; }
    public Projeto getProjeto() { return projeto; }
    public Colaborador getColaborador() { return colaborador; }
    public String getPapel() { return papel; }
    public LocalDate getDataEntrada() { return dataEntrada; }

    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public void setPapel(String papel) { this.papel = papel; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }

    public String getNomeProjeto() {
        return projeto == null ? "-" : projeto.getTitulo();
    }

    public String getNomeColaborador() {
        return colaborador == null ? "-" : colaborador.getNome();
    }

    public String getDataEntradaFormatada() {
        return dataEntrada == null ? "-" : dataEntrada.format(FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Participacao outra && outra.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " - " + getNomeColaborador() + " em " + getNomeProjeto() + " (" + papel + ")";
    }
}
