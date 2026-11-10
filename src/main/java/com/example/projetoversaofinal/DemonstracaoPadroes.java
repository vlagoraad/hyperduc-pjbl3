package com.example.projetoversaofinal;

import com.example.projetoversaofinal.dao.*;
import com.example.projetoversaofinal.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Roda no console (sem abrir a interface) o CRUD das 7 entidades e os padrões
// de projeto, usando IDs 900+ que são apagados no final. Útil na apresentação.
// No IntelliJ: botão direito na classe -> Run 'DemonstracaoPadroes.main()'.
public class DemonstracaoPadroes {

    private static int falhas = 0;

    public static void main(String[] args) {
        try {
            executar();
        } finally {
            JPAUtil.fechar();
        }
        System.out.println(falhas == 0 ? "\nTODAS AS VERIFICAÇÕES PASSARAM" : "\nFALHAS: " + falhas);
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void executar() {
        ProjetoDAO projetoDAO = new ProjetoDAO();
        AtividadeDAO atividadeDAO = new AtividadeDAO();
        ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
        DenunciaDAO denunciaDAO = new DenunciaDAO();
        ParticipacaoDAO participacaoDAO = new ParticipacaoDAO();
        ComentarioDAO comentarioDAO = new ComentarioDAO();
        EtiquetaDAO etiquetaDAO = new EtiquetaDAO();
        limpar();

        titulo("SINGLETON (JPAUtil)");
        verificar("mesma instância", JPAUtil.getInstancia() == JPAUtil.getInstancia());

        titulo("CREATE das 7 entidades");
        Colaborador ana = new Colaborador("C900", "Ana Teste", "ana@teste.com", "Dev");
        Colaborador bia = new Colaborador("C901", "Bia Teste", "bia@teste.com", "QA");
        colaboradorDAO.adicionar(ana);
        colaboradorDAO.adicionar(bia);
        Etiqueta urgente = new Etiqueta(900, "teste-urgente", "vermelho");
        Etiqueta backend = new Etiqueta(901, "teste-backend", "azul");
        etiquetaDAO.adicionar(urgente);
        etiquetaDAO.adicionar(backend);
        Projeto projeto = new Projeto(900, "Projeto Teste", "Criado pela demonstração", LocalDate.now());
        projetoDAO.adicionar(projeto);
        participacaoDAO.adicionar(new Participacao(900, projeto, ana, "Líder", LocalDate.now()));
        Atividade atv = new Atividade(900, "Atividade Teste", "Testar o ORM", LocalDate.now(), projeto);
        atv.setResponsavel(ana);
        atv.setEtiquetas(List.of(urgente, backend));
        atividadeDAO.adicionar(atv);
        comentarioDAO.adicionar(new Comentario(900, "Primeiro comentário", LocalDateTime.now(), atv, bia));
        denunciaDAO.adicionar(new Denuncia(900, "Spam", "Teste", "Atividade 900", LocalDate.now(), bia));
        System.out.println("Inseridos: 2 colaboradores, 2 etiquetas, 1 projeto, 1 participação, "
                + "1 atividade, 1 comentário, 1 denúncia");

        titulo("READ e relacionamentos");
        Atividade lida = atividadeDAO.buscarPorId(900);
        System.out.println("Atividade lida: " + lida + " | projeto=" + lida.getNomeProjeto()
                + " | responsável=" + lida.getNomeResponsavel() + " | etiquetas=" + lida.getEtiquetasTexto());
        verificar("N:1 Atividade -> Projeto", lida.getProjeto().getId() == 900);
        verificar("N:1 Atividade -> Colaborador", "C900".equals(lida.getResponsavel().getId()));
        verificar("N:N Atividade <-> Etiqueta", lida.getEtiquetas().size() == 2);
        verificar("1:N Atividade -> Comentário", lida.getComentarios().size() == 1);
        verificar("1:N Projeto -> Atividade", projetoDAO.buscarPorId(900).getAtividades().size() == 1);
        verificar("lado inverso Etiqueta -> Atividades", etiquetaDAO.buscarPorId(900).getQuantidadeAtividades() == 1);
        verificar("N:N via Participacao", colaboradorDAO.buscarPorId("C900").getParticipacoes().size() == 1);
        verificar("JPQL listarPorProjeto", atividadeDAO.listarPorProjeto(900).size() == 1);
        verificar("status gravado pelo conversor", "Aberta".equals(lida.getStatus()));

        titulo("Regras e validações");
        esperarErro("ID duplicado", () -> projetoDAO.adicionar(new Projeto(900, "Dup", "-", LocalDate.now())));
        esperarErro("participação repetida",
                () -> participacaoDAO.adicionar(new Participacao(901, projeto, ana, "Outro", LocalDate.now())));
        esperarErro("atividade sem projeto",
                () -> atividadeDAO.adicionar(new Atividade(901, "Sem projeto", "-", LocalDate.now(), null)));
        esperarErro("excluir colaborador autor de comentário", () -> colaboradorDAO.remover("C901"));

        titulo("STATE + MEMENTO + UPDATE");
        Historico<Atividade.Memento> historico = new Historico<>();
        historico.salvar(lida.getId(), lida.salvarEstado());
        lida.atribuir();
        lida.setTitulo("Título alterado");
        lida.setEtiquetas(List.of(backend));
        atividadeDAO.atualizar(lida);
        Atividade aposUpdate = atividadeDAO.buscarPorId(900);
        System.out.println("Depois do update: " + aposUpdate + " | " + aposUpdate.getStatus()
                + " | etiquetas=" + aposUpdate.getEtiquetasTexto());
        verificar("update de campo", "Título alterado".equals(aposUpdate.getTitulo()));
        verificar("update de status", "Atribuída".equals(aposUpdate.getStatus()));
        verificar("update do N:N", aposUpdate.getEtiquetas().size() == 1);
        esperarErroEstado("transição inválida Atribuída -> Concluída", aposUpdate::aprovar);
        lida.restaurarEstado(historico.desfazer(lida.getId()));
        atividadeDAO.atualizar(lida);
        Atividade desfeita = atividadeDAO.buscarPorId(900);
        System.out.println("Depois do desfazer: " + desfeita + " | " + desfeita.getStatus()
                + " | etiquetas=" + desfeita.getEtiquetasTexto());
        verificar("memento restaurou título e status",
                "Atividade Teste".equals(desfeita.getTitulo()) && "Aberta".equals(desfeita.getStatus()));
        verificar("memento restaurou etiquetas", desfeita.getEtiquetas().size() == 2);

        Colaborador anaLida = colaboradorDAO.buscarPorId("C900");
        anaLida.setFuncao("Tech Lead");
        colaboradorDAO.atualizar(anaLida);
        verificar("update colaborador", "Tech Lead".equals(colaboradorDAO.buscarPorId("C900").getFuncao()));
        Participacao part = participacaoDAO.buscarPorId(900);
        part.setPapel("Gerente");
        participacaoDAO.atualizar(part);
        verificar("update participação", "Gerente".equals(participacaoDAO.buscarPorId(900).getPapel()));
        Comentario com = comentarioDAO.buscarPorId(900);
        com.setTexto("Comentário editado");
        comentarioDAO.atualizar(com);
        verificar("update comentário", "Comentário editado".equals(comentarioDAO.buscarPorId(900).getTexto()));
        Etiqueta et = etiquetaDAO.buscarPorId(901);
        et.setCor("roxo");
        etiquetaDAO.atualizar(et);
        verificar("update etiqueta", "roxo".equals(etiquetaDAO.buscarPorId(901).getCor()));
        Denuncia den = denunciaDAO.buscarPorId(900);
        den.analisar();
        denunciaDAO.atualizar(den);
        verificar("update denúncia (State)", "Em análise".equals(denunciaDAO.buscarPorId(900).getStatus()));
        Projeto proj = projetoDAO.buscarPorId(900);
        proj.iniciar();
        projetoDAO.atualizar(proj);
        verificar("update projeto (State)", "Em andamento".equals(projetoDAO.buscarPorId(900).getStatus()));

        titulo("DELETE e políticas de exclusão");
        verificar("remover etiqueta em uso", etiquetaDAO.remover(901));
        verificar("etiqueta saiu da atividade", atividadeDAO.buscarPorId(900).getEtiquetas().size() == 1);
        verificar("remover denúncia", denunciaDAO.remover(900));
        verificar("remover comentário", comentarioDAO.remover(900));
        verificar("remover colaborador (agora sem autoria)", colaboradorDAO.remover("C901"));
        verificar("remover colaborador responsável", colaboradorDAO.remover("C900"));
        verificar("atividade ficou sem responsável", atividadeDAO.buscarPorId(900).getResponsavel() == null);
        verificar("participação removida junto", participacaoDAO.buscarPorId(900) == null);
        Colaborador caio = new Colaborador("C902", "Caio Teste", "caio@teste.com", "Dev");
        colaboradorDAO.adicionar(caio);
        comentarioDAO.adicionar(new Comentario(901, "Vai sumir em cascata", LocalDateTime.now(),
                atividadeDAO.buscarPorId(900), caio));
        verificar("remover projeto (cascade)", projetoDAO.remover(900));
        verificar("atividade removida em cascata", atividadeDAO.buscarPorId(900) == null);
        verificar("comentário removido em cascata", comentarioDAO.buscarPorId(901) == null);
        verificar("remover atividade inexistente devolve false", !atividadeDAO.remover(900));
        verificar("remover participação inexistente devolve false", !participacaoDAO.remover(900));
        verificar("remover etiqueta", etiquetaDAO.remover(900));
        verificar("remover colaborador", colaboradorDAO.remover("C902"));
        limpar();
    }

    // Apaga restos de execuções anteriores (IDs de teste).
    private static void limpar() {
        new ComentarioDAO().remover(900);
        new ComentarioDAO().remover(901);
        new DenunciaDAO().remover(900);
        new ProjetoDAO().remover(900);
        new EtiquetaDAO().remover(900);
        new EtiquetaDAO().remover(901);
        for (String id : new String[]{"C900", "C901", "C902"}) {
            try {
                new ColaboradorDAO().remover(id);
            } catch (IllegalArgumentException ignorada) {
                // ainda tem autoria; some depois que os comentários forem apagados
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println("\n=== " + texto + " ===");
    }

    private static void verificar(String descricao, boolean ok) {
        System.out.println((ok ? "  [OK]    " : "  [FALHA] ") + descricao);
        if (!ok) falhas++;
    }

    private static void esperarErro(String descricao, Runnable acao) {
        try {
            acao.run();
            verificar(descricao + " deveria ser recusado", false);
        } catch (IllegalArgumentException e) {
            verificar(descricao + " recusado: " + e.getMessage(), true);
        }
    }

    private static void esperarErroEstado(String descricao, Runnable acao) {
        try {
            acao.run();
            verificar(descricao + " deveria ser bloqueada", false);
        } catch (IllegalStateException e) {
            verificar(descricao + " bloqueada: " + e.getMessage(), true);
        }
    }
}
