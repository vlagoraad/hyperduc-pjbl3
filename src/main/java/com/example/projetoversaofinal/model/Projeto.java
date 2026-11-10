package com.example.projetoversaofinal.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Entidade JPA. Também é o contexto do State e o originador do Memento.
@Entity
@Table(name = "projeto")
public class Projeto implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    private int id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 500)
    private String descricao;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    // O estado (State) não é uma entidade: é gravado como texto na coluna "status"
    // e reconstruído pelo conversor.
    @Convert(converter = EstadoProjetoConverter.class)
    @Column(name = "status", nullable = false, length = 30)
    private EstadoProjeto estado;

    // 1 projeto -> N atividades. Excluir o projeto exclui as atividades dele.
    @OneToMany(mappedBy = "projeto", cascade = CascadeType.REMOVE)
    private List<Atividade> atividades = new ArrayList<>();

    // 1 projeto -> N participações (entidade associativa Projeto x Colaborador).
    @OneToMany(mappedBy = "projeto", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Participacao> participacoes = new ArrayList<>();

    // Construtor sem argumentos exigido pela JPA.
    protected Projeto() {
    }

    public Projeto(int id, String titulo, String descricao, LocalDate dataCriacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.estado = new EstadoProjeto.Planejamento();
    }

    public void iniciar()  { getEstado().iniciar(this); }
    public void concluir() { getEstado().concluir(this); }
    public void cancelar() { getEstado().cancelar(this); }

    void setEstado(EstadoProjeto estado) {
        this.estado = estado;
    }

    public EstadoProjeto getEstado() {
        if (estado == null) {
            estado = new EstadoProjeto.Planejamento();
        }
        return estado;
    }

    public String getStatus() {
        return getEstado().getNome();
    }

    public Memento salvarEstado() {
        return new Memento(titulo, descricao, dataCriacao, getEstado());
    }

    public void restaurarEstado(Memento m) {
        this.titulo = m.titulo;
        this.descricao = m.descricao;
        this.dataCriacao = m.dataCriacao;
        this.estado = m.estado;
    }

    public static final class Memento {
        private final String titulo;
        private final String descricao;
        private final LocalDate dataCriacao;
        private final EstadoProjeto estado;

        private Memento(String titulo, String descricao, LocalDate dataCriacao, EstadoProjeto estado) {
            this.titulo = titulo;
            this.descricao = descricao;
            this.dataCriacao = dataCriacao;
            this.estado = estado;
        }
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public List<Atividade> getAtividades() { return atividades; }
    public List<Participacao> getParticipacoes() { return participacoes; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getDataCriacaoFormatada() {
        return dataCriacao == null ? "Data não encontrada" : dataCriacao.format(FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Projeto outro && outro.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Usado nas ComboBox das telas.
    @Override
    public String toString() {
        return id + " - " + titulo;
    }
}
