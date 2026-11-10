package com.example.projetoversaofinal.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// Comentário feito por um colaborador em uma atividade.
// É parte da atividade (composição): excluir a atividade exclui os comentários.
@Entity
@Table(name = "comentario")
public class Comentario implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    private int id;

    @Column(nullable = false, length = 1000)
    private String texto;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(optional = false)
    @JoinColumn(name = "atividade_id", nullable = false)
    private Atividade atividade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Colaborador autor;

    protected Comentario() {
    }

    public Comentario(int id, String texto, LocalDateTime dataHora, Atividade atividade, Colaborador autor) {
        this.id = id;
        this.texto = texto;
        this.dataHora = dataHora;
        this.atividade = atividade;
        this.autor = autor;
    }

    public int getId() { return id; }
    public String getTexto() { return texto; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Atividade getAtividade() { return atividade; }
    public Colaborador getAutor() { return autor; }

    public void setTexto(String texto) { this.texto = texto; }
    public void setAtividade(Atividade atividade) { this.atividade = atividade; }
    public void setAutor(Colaborador autor) { this.autor = autor; }

    public String getNomeAtividade() {
        return atividade == null ? "-" : atividade.getTitulo();
    }

    public String getNomeAutor() {
        return autor == null ? "-" : autor.getNome();
    }

    public String getDataHoraFormatada() {
        return dataHora == null ? "-" : dataHora.format(FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Comentario outro && outro.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " - " + getNomeAutor() + ": " + texto;
    }
}
