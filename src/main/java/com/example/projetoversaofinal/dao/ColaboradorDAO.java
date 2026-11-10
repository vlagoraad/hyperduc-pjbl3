package com.example.projetoversaofinal.dao;

import com.example.projetoversaofinal.model.Colaborador;
import jakarta.persistence.EntityManager;

public class ColaboradorDAO extends DAOGenerico<Colaborador, String> {

    public ColaboradorDAO() {
        super(Colaborador.class);
    }

    @Override
    protected String getId(Colaborador item) {
        return item.getId();
    }

    @Override
    protected void validar(Colaborador item) {
        exigir(!vazio(item.getId()), "O ID do colaborador é obrigatório.");
        exigir(!vazio(item.getNome()), "O nome do colaborador é obrigatório.");
    }

    // Política de exclusão:
    //  - comentários e denúncias de autoria dele são histórico: bloqueiam a exclusão;
    //  - as atividades em que ele é responsável ficam sem responsável;
    //  - as participações dele em projetos são removidas.
    @Override
    protected void antesDeRemover(EntityManager em, Colaborador item) {
        long comentarios = em.createQuery("SELECT COUNT(c) FROM Comentario c WHERE c.autor = :col", Long.class)
                .setParameter("col", item).getSingleResult();
        long denuncias = em.createQuery("SELECT COUNT(d) FROM Denuncia d WHERE d.autor = :col", Long.class)
                .setParameter("col", item).getSingleResult();
        if (comentarios > 0 || denuncias > 0) {
            throw new IllegalArgumentException("Não é possível excluir " + item.getNome() + ": ele é autor de "
                    + comentarios + " comentário(s) e " + denuncias + " denúncia(s).");
        }
        em.createQuery("UPDATE Atividade a SET a.responsavel = NULL WHERE a.responsavel = :col")
                .setParameter("col", item).executeUpdate();
        em.createQuery("DELETE FROM Participacao p WHERE p.colaborador = :col")
                .setParameter("col", item).executeUpdate();
    }
}
