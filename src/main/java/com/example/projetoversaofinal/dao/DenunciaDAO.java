package com.example.projetoversaofinal.dao;

import com.example.projetoversaofinal.model.Denuncia;

public class DenunciaDAO extends DAOGenerico<Denuncia, Integer> {

    public DenunciaDAO() {
        super(Denuncia.class);
    }

    @Override
    protected Integer getId(Denuncia item) {
        return item.getId();
    }

    @Override
    protected void validar(Denuncia item) {
        exigir(!vazio(item.getMotivo()), "O motivo da denúncia é obrigatório.");
        exigir(item.getAutor() != null, "Informe o colaborador autor da denúncia.");
    }
}
