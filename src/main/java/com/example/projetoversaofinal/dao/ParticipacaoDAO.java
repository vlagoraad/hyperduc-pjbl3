package com.example.projetoversaofinal.dao;

import com.example.projetoversaofinal.model.Participacao;

public class ParticipacaoDAO extends DAOGenerico<Participacao, Integer> {

    public ParticipacaoDAO() {
        super(Participacao.class);
    }

    @Override
    protected Integer getId(Participacao item) {
        return item.getId();
    }

    @Override
    protected void validar(Participacao item) {
        exigir(item.getProjeto() != null, "Escolha o projeto.");
        exigir(item.getColaborador() != null, "Escolha o colaborador.");
        exigir(!vazio(item.getPapel()), "Informe o papel do colaborador no projeto.");

        // Um colaborador só pode participar uma vez de cada projeto
        // (também garantido pela UNIQUE(projeto_id, colaborador_id) no banco).
        long repetidas = consultar(em -> em.createQuery(
                        "SELECT COUNT(p) FROM Participacao p WHERE p.projeto = :proj "
                                + "AND p.colaborador = :col AND p.id <> :id", Long.class)
                .setParameter("proj", item.getProjeto())
                .setParameter("col", item.getColaborador())
                .setParameter("id", item.getId())
                .getSingleResult());
        exigir(repetidas == 0, item.getColaborador().getNome() + " já participa do projeto \""
                + item.getProjeto().getTitulo() + "\".");
    }
}
