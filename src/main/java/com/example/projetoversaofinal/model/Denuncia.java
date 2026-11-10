package com.example.projetoversaofinal.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// Entidade JPA. Também é o contexto do State e a originadora do Memento.
@Entity
@Table(name = "denuncia")
public class Denuncia implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    private int id;

    @Column(nullable = false, length = 100)
    private String motivo;

    @Column(length = 500)
    private String descricao;

    @Column(length = 100)
    private String alvo;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    @Convert(converter = EstadoDenunciaConverter.class)
    @Column(name = "status", nullable = false, length = 30)
    private EstadoDenuncia estado;

    // N denúncias -> 1 colaborador autor.
    @ManyToOne(optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Colaborador autor;

    protected Denuncia() {
    }

    public Denuncia(int id, String motivo, String descricao, String alvo, LocalDate dataCriacao, Colaborador autor) {
        this.id = id;
        this.motivo = motivo;
        this.descricao = descricao;
        this.alvo = alvo;
        this.dataCriacao = dataCriacao;
        this.autor = autor;
        this.estado = new EstadoDenuncia.Aberta();
    }

    public void analisar()         { estado.analisar(this); }
    public void julgarProcedente() { estado.julgarProcedente(this); }
    public void arquivar()         { estado.arquivar(this); }

    void setEstado(EstadoDenuncia estado) {
        this.estado = estado;
    }

    public EstadoDenuncia getEstado() {
        return estado;
    }

    public String getStatus() {
        return estado.getNome();
    }

    public Memento salvarEstado() {
        return new Memento(motivo, descricao, alvo, estado);
    }

    public void restaurarEstado(Memento m) {
        this.motivo = m.motivo;
        this.descricao = m.descricao;
        this.alvo = m.alvo;
        this.estado = m.estado;
    }

    public static final class Memento {
        private final String motivo;
        private final String descricao;
        private final String alvo;
        private final EstadoDenuncia estado;

        private Memento(String motivo, String descricao, String alvo, EstadoDenuncia estado) {
            this.motivo = motivo;
            this.descricao = descricao;
            this.alvo = alvo;
            this.estado = estado;
        }
    }

    public int getId() { return id; }
    public String getMotivo() { return motivo; }
    public String getDescricao() { return descricao; }
    public String getAlvo() { return alvo; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public Colaborador getAutor() { return autor; }

    public void setMotivo(String motivo) { this.motivo = motivo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setAlvo(String alvo) { this.alvo = alvo; }
    public void setAutor(Colaborador autor) { this.autor = autor; }

    public String getNomeAutor() {
        return autor == null ? "-" : autor.getNome();
    }

    public String getDataCriacaoFormatada() {
        return dataCriacao == null ? "Data não encontrada" : dataCriacao.format(FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Denuncia outra && outra.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Denuncia{id=" + id + ", motivo='" + motivo + "', alvo='" + alvo + "', estado=" + getStatus() + "}";
    }
}
