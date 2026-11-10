package com.example.projetoversaofinal.model;

import java.io.Serializable;

// State: cada classe interna é um status possível de atividade.
public abstract class EstadoAtividade implements Serializable {
    private static final long serialVersionUID = 1L;

    public abstract String getNome();

    public void atribuir(Atividade atividade)          { negar("atribuir"); }
    public void enviarParaRevisao(Atividade atividade) { negar("enviar para revisão"); }
    public void aprovar(Atividade atividade)           { negar("aprovar"); }
    public void reprovar(Atividade atividade)          { negar("reprovar"); }
    public void cancelar(Atividade atividade)          { negar("cancelar"); }

    protected void negar(String acao) {
        throw new IllegalStateException("Não é possível " + acao + " uma atividade no estado \"" + getNome() + "\".");
    }

    @Override
    public String toString() {
        return getNome();
    }

    // Estados concretos

    public static class Aberta extends EstadoAtividade {
        public String getNome() { return "Aberta"; }
        public void atribuir(Atividade a) {
            // Regra de negócio: só dá para atribuir se houver um responsável escolhido.
            if (a.getResponsavel() == null) {
                throw new IllegalStateException("Escolha um responsável e salve antes de atribuir a atividade.");
            }
            a.setEstado(new Atribuida());
        }
        public void cancelar(Atividade a) { a.setEstado(new Cancelada()); }
    }

    public static class Atribuida extends EstadoAtividade {
        public String getNome() { return "Atribuída"; }
        public void enviarParaRevisao(Atividade a) { a.setEstado(new Revisao()); }
        public void cancelar(Atividade a) { a.setEstado(new Cancelada()); }
    }

    public static class Revisao extends EstadoAtividade {
        public String getNome() { return "Revisão"; }
        public void aprovar(Atividade a) { a.setEstado(new Concluida()); }
        public void reprovar(Atividade a) { a.setEstado(new Atribuida()); }
        public void cancelar(Atividade a) { a.setEstado(new Cancelada()); }
    }

    public static class Concluida extends EstadoAtividade {
        public String getNome() { return "Concluída"; }
    }

    public static class Cancelada extends EstadoAtividade {
        public String getNome() { return "Cancelada"; }
    }
}