package com.example.projetoversaofinal;

import com.example.projetoversaofinal.dao.*;
import com.example.projetoversaofinal.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Popula o banco com um pequeno conjunto de dados relacionados entre si,
// para a apresentação. Só grava se o banco ainda estiver vazio.
public final class DadosExemplo {

    private DadosExemplo() {
    }

    public static String carregar() {
        ProjetoDAO projetoDAO = new ProjetoDAO();
        if (!projetoDAO.listar().isEmpty() || !new ColaboradorDAO().listar().isEmpty()) {
            return "O banco já tem dados; nada foi carregado.";
        }

        ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
        Colaborador amanda = new Colaborador("C01", "Amanda Freire", "amanda@hyperduc.com", "Desenvolvedora");
        Colaborador gabriella = new Colaborador("C02", "Gabriella Tavares", "gabriella@hyperduc.com", "Designer");
        Colaborador giovanna = new Colaborador("C03", "Giovanna Daniel", "giovanna@hyperduc.com", "Analista de QA");
        Colaborador joao = new Colaborador("C04", "João Lucas Ribeiro", "joao@hyperduc.com", "Desenvolvedor");
        Colaborador valentina = new Colaborador("C05", "Valentina Lago", "valentina@hyperduc.com", "Gerente de projeto");
        for (Colaborador c : new Colaborador[]{amanda, gabriella, giovanna, joao, valentina}) {
            colaboradorDAO.adicionar(c);
        }

        EtiquetaDAO etiquetaDAO = new EtiquetaDAO();
        Etiqueta urgente = new Etiqueta(1, "urgente", "vermelho");
        Etiqueta backend = new Etiqueta(2, "backend", "azul");
        Etiqueta frontend = new Etiqueta(3, "frontend", "verde");
        Etiqueta bug = new Etiqueta(4, "bug", "laranja");
        for (Etiqueta e : new Etiqueta[]{urgente, backend, frontend, bug}) {
            etiquetaDAO.adicionar(e);
        }

        Projeto app = new Projeto(1, "App de Monitoria", "Aplicativo para agendar monitorias da PUCPR",
                LocalDate.of(2026, 9, 1));
        app.iniciar();
        Projeto site = new Projeto(2, "Site do Centro Acadêmico", "Novo site com agenda de eventos",
                LocalDate.of(2026, 10, 15));
        projetoDAO.adicionar(app);
        projetoDAO.adicionar(site);

        ParticipacaoDAO participacaoDAO = new ParticipacaoDAO();
        participacaoDAO.adicionar(new Participacao(1, app, valentina, "Líder", LocalDate.of(2026, 9, 1)));
        participacaoDAO.adicionar(new Participacao(2, app, amanda, "Desenvolvedora", LocalDate.of(2026, 9, 2)));
        participacaoDAO.adicionar(new Participacao(3, app, joao, "Desenvolvedor", LocalDate.of(2026, 9, 2)));
        participacaoDAO.adicionar(new Participacao(4, site, gabriella, "Designer", LocalDate.of(2026, 10, 15)));
        participacaoDAO.adicionar(new Participacao(5, site, giovanna, "QA", LocalDate.of(2026, 10, 16)));

        AtividadeDAO atividadeDAO = new AtividadeDAO();
        Atividade login = new Atividade(1, "Tela de login", "Login com e-mail institucional",
                LocalDate.of(2026, 9, 3), app);
        login.setResponsavel(amanda);
        login.setEtiquetas(java.util.List.of(frontend, urgente));
        login.atribuir();
        login.enviarParaRevisao();

        Atividade api = new Atividade(2, "API de agendamentos", "Endpoints REST para criar e cancelar monitorias",
                LocalDate.of(2026, 9, 5), app);
        api.setResponsavel(joao);
        api.setEtiquetas(java.util.List.of(backend));
        api.atribuir();

        Atividade layout = new Atividade(3, "Protótipo do layout", "Wireframes da página inicial",
                LocalDate.of(2026, 10, 16), site);
        layout.setEtiquetas(java.util.List.of(frontend));

        atividadeDAO.adicionar(login);
        atividadeDAO.adicionar(api);
        atividadeDAO.adicionar(layout);

        ComentarioDAO comentarioDAO = new ComentarioDAO();
        comentarioDAO.adicionar(new Comentario(1, "Falta validar o domínio @pucpr.edu.br.",
                LocalDateTime.of(2026, 9, 10, 14, 30), login, giovanna));
        comentarioDAO.adicionar(new Comentario(2, "Corrigido, pode revisar de novo.",
                LocalDateTime.of(2026, 9, 11, 9, 15), login, amanda));

        Denuncia denuncia = new Denuncia(1, "Conteúdo inadequado", "Comentário ofensivo em atividade",
                "Atividade 1", LocalDate.of(2026, 10, 20), valentina);
        new DenunciaDAO().adicionar(denuncia);

        return "Dados de exemplo carregados: 5 colaboradores, 4 etiquetas, 2 projetos, "
                + "5 participações, 3 atividades, 2 comentários e 1 denúncia.";
    }
}
