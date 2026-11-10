package com.example.projetoversaofinal;

import com.example.projetoversaofinal.dao.JPAUtil;
import com.example.projetoversaofinal.view.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class Programa extends Application {

    @Override
    public void start(Stage stage) {
        // Antes de abrir o menu, confirma que o MySQL (MAMP) está ligado.
        // Na primeira conexão o EclipseLink também cria as tabelas.
        try {
            JPAUtil.getInstancia().testarConexao();
        } catch (RuntimeException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Banco de dados indisponível");
            alerta.setHeaderText("Não foi possível conectar ao MySQL.");
            alerta.setContentText("Verifique se o MAMP está com os servidores ligados e se a porta, "
                    + "o usuário e a senha em META-INF/persistence.xml estão corretos.\n\nDetalhe: " + e.getMessage());
            alerta.showAndWait();
            Platform.exit();
            return;
        }

        stage.setScene(criarMenuPrincipal(stage));
        stage.setTitle("HYPERDUC! - Sistema de Gerenciamento");
        stage.setResizable(true);
        stage.show();
    }

    @Override
    public void stop() {
        JPAUtil.fechar();
    }

    public Scene criarMenuPrincipal(Stage stage) {
        Label lblTitulo = new Label("HYPERDUC!");
        lblTitulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        Label lblSubtitulo = new Label("Plataforma de Gestão de Projetos Colaborativos");
        lblSubtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        GridPane grade = new GridPane();
        grade.setHgap(8);
        grade.setVgap(10);
        grade.setAlignment(Pos.CENTER);

        int linha = 0;
        // Entidades com quatro telas (inserir, consultar, atualizar, excluir)
        adicionarLinha(grade, linha++, "📁 Projetos", "#4CAF50", stage,
                () -> new ProjetoInsercao(stage).criarCena(),
                () -> new ProjetoConsulta(stage).criarCena(),
                () -> new ProjetoAtualizacao(stage).criarCena(),
                () -> new ProjetoExclusao(stage).criarCena());
        adicionarLinha(grade, linha++, "✅ Atividades", "#9C27B0", stage,
                () -> new AtividadeInsercao(stage).criarCena(),
                () -> new AtividadeConsulta(stage).criarCena(),
                () -> new AtividadeAtualizacao(stage).criarCena(),
                () -> new AtividadeExclusao(stage).criarCena());
        adicionarLinha(grade, linha++, "🚩 Denúncias", "#E91E63", stage,
                () -> new DenunciaInsercao(stage).criarCena(),
                () -> new DenunciaConsulta(stage).criarCena(),
                () -> new DenunciaAtualizacao(stage).criarCena(),
                () -> new DenunciaExclusao(stage).criarCena());

        // Colaborador: as telas antigas montam a cena sozinhas (método start)
        grade.add(rotulo("👥 Colaboradores"), 0, linha);
        Button[] botoesColab = {
                botao("Inserir", "#2196F3"), botao("Consultar", "#2196F3"),
                botao("Atualizar", "#2196F3"), botao("Excluir", "#2196F3")};
        botoesColab[0].setOnAction(e -> new ColaboradorInsercao().start(stage));
        botoesColab[1].setOnAction(e -> new ColaboradorConsulta().start(stage));
        botoesColab[2].setOnAction(e -> new ColaboradorAtualizacao().start(stage));
        botoesColab[3].setOnAction(e -> new ColaboradorExclusao().start(stage));
        for (int i = 0; i < 4; i++) {
            grade.add(botoesColab[i], i + 1, linha);
        }
        linha++;

        // Entidades novas do RA3: CRUD completo numa tela só
        adicionarLinhaCrud(grade, linha++, "🤝 Participações", "#00897B", stage, () -> new ParticipacaoCrud(stage).criarCena());
        adicionarLinhaCrud(grade, linha++, "💬 Comentários", "#3F51B5", stage, () -> new ComentarioCrud(stage).criarCena());
        adicionarLinhaCrud(grade, linha, "🏷️ Etiquetas", "#795548", stage, () -> new EtiquetaCrud(stage).criarCena());

        Button btnDemo = botao("Carregar dados de exemplo", "#607D8B");
        btnDemo.setPrefWidth(260);
        btnDemo.setOnAction(e -> {
            String resultado = DadosExemplo.carregar();
            new Alert(Alert.AlertType.INFORMATION, resultado).showAndWait();
        });

        Button btnSair = botao("❌ Sair", "#9E9E9E");
        btnSair.setPrefWidth(120);
        btnSair.setOnAction(e -> stage.close());

        Label lblInfo = new Label("Equipe: Amanda Freire, Gabriella Tavares Pedroso, Giovanna Rita Santos Daniel, "
                + "João Lucas do Prado Ribeiro e Valentina Lago");
        lblInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #999999; -fx-font-style: italic;");
        lblInfo.setWrapText(true);

        Label lblAno = new Label("PUCPR - Engenharia de Software - Design de Software (RA3) - 2026");
        lblAno.setStyle("-fx-font-size: 11px; -fx-text-fill: #999999; -fx-font-style: italic;");

        HBox rodapeBotoes = new HBox(10, btnDemo, btnSair);
        rodapeBotoes.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(14, lblTitulo, lblSubtitulo, new Separator(), grade, new Separator(),
                rodapeBotoes, lblInfo, lblAno);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25));
        return new Scene(vbox, 760, 620);
    }

    private void adicionarLinha(GridPane grade, int linha, String titulo, String cor, Stage stage,
                                Supplier<Scene> inserir, Supplier<Scene> consultar,
                                Supplier<Scene> atualizar, Supplier<Scene> excluir) {
        grade.add(rotulo(titulo), 0, linha);
        String[] nomes = {"Inserir", "Consultar", "Atualizar", "Excluir"};
        Supplier<?>[] telas = {inserir, consultar, atualizar, excluir};
        for (int i = 0; i < 4; i++) {
            Button b = botao(nomes[i], cor);
            Supplier<?> tela = telas[i];
            String tituloJanela = nomes[i] + " - " + titulo.substring(titulo.indexOf(' ') + 1);
            b.setOnAction(e -> trocarTela(stage, (Scene) tela.get(), tituloJanela));
            grade.add(b, i + 1, linha);
        }
    }

    private void adicionarLinhaCrud(GridPane grade, int linha, String titulo, String cor, Stage stage,
                                    Supplier<Scene> tela) {
        grade.add(rotulo(titulo), 0, linha);
        Button b = botao("Gerenciar (inserir, consultar, atualizar e excluir)", cor);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> trocarTela(stage, tela.get(), titulo.substring(titulo.indexOf(' ') + 1)));
        grade.add(b, 1, linha, 4, 1);
    }

    private static Label rotulo(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        l.setMinWidth(150);
        return l;
    }

    private static Button botao(String texto, String cor) {
        Button b = new Button(texto);
        b.setPrefSize(120, 36);
        b.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        return b;
    }

    private void trocarTela(Stage stage, Scene cena, String titulo) {
        stage.setScene(cena);
        stage.setTitle(titulo + " - HYPERDUC!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
