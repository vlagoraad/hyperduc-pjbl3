package com.example.projetoversaofinal.dao;

import com.example.projetoversaofinal.model.Comentario;

public class ComentarioDAO extends DAOGenerico<Comentario, Integer> {

    public ComentarioDAO() {
        super(Comentario.class);
    }

    @Override
    protected Integer getId(Comentario item) {
        return item.getId();
    }

    @Override
    protected void validar(Comentario item) {
        exigir(!vazio(item.getTexto()), "O texto do comentário é obrigatório.");
        exigir(item.getAtividade() != null, "Escolha a atividade comentada.");
        exigir(item.getAutor() != null, "Escolha o autor do comentário.");
    }
}
