package com.example.projetoversaofinal.dao;

import com.example.projetoversaofinal.model.Atividade;

import java.util.ArrayList;

public class AtividadeDAO extends DAOGenerico<Atividade, Integer> {

    public AtividadeDAO() {
        super(Atividade.class);
    }

    @Override
    protected Integer getId(Atividade item) {
        return item.getId();
    }

    @Override
    protected void validar(Atividade item) {
        exigir(!vazio(item.getTitulo()), "O título da atividade é obrigatório.");
        exigir(item.getProjeto() != null, "Toda atividade precisa pertencer a um projeto.");
    }

    // Consulta JPQL navegando pelo relacionamento Atividade -> Projeto.
    public ArrayList<Atividade> listarPorProjeto(int projetoId) {
        return consultar(em -> new ArrayList<>(em.createQuery(
                        "SELECT a FROM Atividade a WHERE a.projeto.id = :id ORDER BY a.id", Atividade.class)
                .setParameter("id", projetoId).getResultList()));
    }

    public long contarComentarios(int atividadeId) {
        return consultar(em -> em.createQuery(
                        "SELECT COUNT(c) FROM Comentario c WHERE c.atividade.id = :id", Long.class)
                .setParameter("id", atividadeId).getSingleResult());
    }
}
