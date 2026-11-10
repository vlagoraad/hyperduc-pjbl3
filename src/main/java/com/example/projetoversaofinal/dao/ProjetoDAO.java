package com.example.projetoversaofinal.dao;

import com.example.projetoversaofinal.model.Projeto;

public class ProjetoDAO extends DAOGenerico<Projeto, Integer> {

    public ProjetoDAO() {
        super(Projeto.class);
    }

    @Override
    protected Integer getId(Projeto item) {
        return item.getId();
    }

    @Override
    protected void validar(Projeto item) {
        exigir(!vazio(item.getTitulo()), "O título do projeto é obrigatório.");
    }

    // Excluir um projeto apaga em cascata as atividades (e os comentários delas)
    // e as participações. Estes contadores servem para avisar o usuário antes.
    public long contarAtividades(int projetoId) {
        return consultar(em -> em.createQuery(
                        "SELECT COUNT(a) FROM Atividade a WHERE a.projeto.id = :id", Long.class)
                .setParameter("id", projetoId).getSingleResult());
    }

    public long contarParticipacoes(int projetoId) {
        return consultar(em -> em.createQuery(
                        "SELECT COUNT(p) FROM Participacao p WHERE p.projeto.id = :id", Long.class)
                .setParameter("id", projetoId).getSingleResult());
    }
}
