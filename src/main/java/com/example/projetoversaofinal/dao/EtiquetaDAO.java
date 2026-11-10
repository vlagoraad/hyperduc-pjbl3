package com.example.projetoversaofinal.dao;

import com.example.projetoversaofinal.model.Atividade;
import com.example.projetoversaofinal.model.Etiqueta;
import jakarta.persistence.EntityManager;

public class EtiquetaDAO extends DAOGenerico<Etiqueta, Integer> {

    public EtiquetaDAO() {
        super(Etiqueta.class);
    }

    @Override
    protected Integer getId(Etiqueta item) {
        return item.getId();
    }

    @Override
    protected void validar(Etiqueta item) {
        exigir(!vazio(item.getNome()), "O nome da etiqueta é obrigatório.");
    }

    // Etiqueta é o lado inverso (mappedBy) do N:N. A JPA só grava a tabela de
    // junção pelo lado dono (Atividade), então antes de excluir é preciso tirar a
    // etiqueta de cada atividade; senão a FK em atividade_etiqueta impede o DELETE.
    @Override
    protected void antesDeRemover(EntityManager em, Etiqueta item) {
        for (Atividade a : item.getAtividades()) {
            a.getEtiquetas().remove(item);
        }
        item.getAtividades().clear();
    }
}
