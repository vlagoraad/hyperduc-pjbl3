package com.example.projetoversaofinal.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Entidade JPA. Também é o contexto do State (delega as transições ao estado atual)
// e a originadora do Memento (sabe tirar e restaurar o próprio retrato).
@Entity
@Table(name = "atividade")
public class Atividade implements Serializable {

    private static final long serialVersionUID = 2L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    private int id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 500)
    private String descricao;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    @Convert(converter = EstadoAtividadeConverter.class)
    @Column(name = "status", nullable = false, length = 30)
    private EstadoAtividade estado;

    // N atividades -> 1 projeto (obrigatório). Lado dono da relação: tem a FK projeto_id.
    @ManyToOne(optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    // N atividades -> 0..1 colaborador responsável.
    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Colaborador responsavel;

    // Composição: comentários não existem sem a atividade.
    @OneToMany(mappedBy = "atividade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    // N atividades <-> N etiquetas, com tabela de junção atividade_etiqueta.
    @ManyToMany
    @JoinTable(name = "atividade_etiqueta",
            joinColumns = @JoinColumn(name = "atividade_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id"))
    private List<Etiqueta> etiquetas = new ArrayList<>();

    protected Atividade() {
    }

    public Atividade(int id, String titulo, String descricao, LocalDate dataCriacao, Projeto projeto) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.projeto = projeto;
        this.estado = new EstadoAtividade.Aberta();
    }

    public void atribuir()          { estado.atribuir(this); }
    public void enviarParaRevisao() { estado.enviarParaRevisao(this); }
    public void aprovar()           { estado.aprovar(this); }
    public void reprovar()          { estado.reprovar(this); }
    public void cancelar()          { estado.cancelar(this); }

    void setEstado(EstadoAtividade estado) {
        this.estado = estado;
    }

    public EstadoAtividade getEstado() {
        return estado;
    }

    public String getStatus() {
        return estado.getNome();
    }

    public Memento salvarEstado() {
        return new Memento(titulo, descricao, dataCriacao, estado, projeto, responsavel, new ArrayList<>(etiquetas));
    }

    public void restaurarEstado(Memento m) {
        this.titulo = m.titulo;
        this.descricao = m.descricao;
        this.dataCriacao = m.dataCriacao;
        this.estado = m.estado;
        this.projeto = m.projeto;
        this.responsavel = m.responsavel;
        this.etiquetas = new ArrayList<>(m.etiquetas);
    }

    public static final class Memento {
        private final String titulo;
        private final String descricao;
        private final LocalDate dataCriacao;
        private final EstadoAtividade estado;
        private final Projeto projeto;
        private final Colaborador responsavel;
        private final List<Etiqueta> etiquetas;

        private Memento(String titulo, String descricao, LocalDate dataCriacao, EstadoAtividade estado,
                        Projeto projeto, Colaborador responsavel, List<Etiqueta> etiquetas) {
            this.titulo = titulo;
            this.descricao = descricao;
            this.dataCriacao = dataCriacao;
            this.estado = estado;
            this.projeto = projeto;
            this.responsavel = responsavel;
            this.etiquetas = etiquetas;
        }
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public Projeto getProjeto() { return projeto; }
    public Colaborador getResponsavel() { return responsavel; }
    public List<Comentario> getComentarios() { return comentarios; }
    public List<Etiqueta> getEtiquetas() { return etiquetas; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public void setResponsavel(Colaborador responsavel) { this.responsavel = responsavel; }
    public void setEtiquetas(List<Etiqueta> etiquetas) { this.etiquetas = new ArrayList<>(etiquetas); }

    // Propriedades "de exibição" usadas pelas colunas das tabelas JavaFX.
    public String getNomeProjeto() {
        return projeto == null ? "-" : projeto.getTitulo();
    }

    public String getNomeResponsavel() {
        return responsavel == null ? "(sem responsável)" : responsavel.getNome();
    }

    public String getEtiquetasTexto() {
        return etiquetas.stream().map(Etiqueta::getNome).collect(Collectors.joining(", "));
    }

    public String getDataCriacaoFormatada() {
        return dataCriacao == null ? "Data não encontrada" : dataCriacao.format(FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Atividade outra && outra.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " - " + titulo;
    }
}
