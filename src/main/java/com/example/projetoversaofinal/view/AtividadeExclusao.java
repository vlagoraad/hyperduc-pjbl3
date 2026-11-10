package com.example.projetoversaofinal.view;

import com.example.projetoversaofinal.Programa;
import com.example.projetoversaofinal.dao.*;
import com.example.projetoversaofinal.model.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class AtividadeExclusao {
    private Stage stage;
    private final AtividadeDAO repositorio = new AtividadeDAO();
    private TextField txtId;
    private TextField txtTitulo;
    private TextArea txtDescricao;
    private TextField txtData;
    private TextField txtStatus;
    private TextField txtProjeto;
    private int comentarios;
    private Button btnExcluir;

    public AtividadeExclusao(Stage stage){
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("Excluir Atividade");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f44336;");

        Label lblBuscaId = new Label("ID da Atividade:");
        TextField txtBuscaId = new TextField();
        txtBuscaId.setPromptText("Digite o ID para buscar");
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

        Separator separador = new Separator();

        Label lblId = new Label("ID:");
        txtId = new TextField();
        txtId.setEditable(false);
        txtId.setStyle("-fx-background-color: #f0f0f0;");

        Label lblTituloAtv = new Label("Título:");
        txtTitulo = new TextField();
        txtTitulo.setEditable(false);
        txtTitulo.setStyle("-fx-background-color: #f0f0f0;");

        Label lblDescricao = new Label("Descrição:");
        txtDescricao = new TextArea();
        txtDescricao.setEditable(false);
        txtDescricao.setPrefRowCount(4);
        txtDescricao.setWrapText(true);
        txtDescricao.setStyle("-fx-background-color: #f0f0f0;");

        Label lblStatus = new Label("Status:");
        txtStatus = new TextField();
        txtStatus.setEditable(false);
        txtStatus.setStyle("-fx-background-color: #f0f0f0;");

        Label lblProjeto = new Label("Projeto:");
        txtProjeto = new TextField();
        txtProjeto.setEditable(false);
        txtProjeto.setStyle("-fx-background-color: #f0f0f0;");

        Label lblData = new Label("Data de Criação:");
        txtData = new TextField();
        txtData.setEditable(false);
        txtData.setStyle("-fx-background-color: #f0f0f0;");

        Label lblAviso = new Label("⚠️ ATENÇÃO: Esta ação não pode ser desfeita!");
        lblAviso.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold; -fx-font-size: 12px;");
        lblAviso.setVisible(false);

        btnExcluir = new Button("Excluir Atividade");
        Button btnLimpar = new Button("Limpar");
        Button btnVoltar = new Button("Voltar");

        btnExcluir.setDisable(true);

        btnExcluir.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLimpar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoltar.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-weight: bold;");

        
        btnBuscar.setOnAction(e -> {
            try {
                if (txtBuscaId.getText().trim().isEmpty()) {
                    mostrarAlerta("Aviso", "Digite um ID para buscar!", Alert.AlertType.WARNING);
                    return;
                }

                int id = Integer.parseInt(txtBuscaId.getText().trim());
                buscarECarregarAtividade(id, lblAviso);

            } catch (NumberFormatException ex) {
                mostrarAlerta("Erro", "ID deve ser um número inteiro válido!", Alert.AlertType.ERROR);
            }
        });

        
        btnExcluir.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());

                Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacao.setTitle("Confirmação de Exclusão");
                confirmacao.setHeaderText("Você tem certeza?");
                confirmacao.setContentText(
                        "Deseja realmente excluir a atividade \"" + txtTitulo.getText() + "\"?\n\n"
                                + "Os " + comentarios + " comentário(s) dela também serão excluídos.\n"
                                + "Esta ação não pode ser desfeita!"
                );

                ButtonType btnSim = new ButtonType("Sim, Excluir", ButtonBar.ButtonData.OK_DONE);
                ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                confirmacao.getButtonTypes().setAll(btnSim, btnCancelar);

                Optional<ButtonType> resultado = confirmacao.showAndWait();

                if (resultado.isPresent() && resultado.get() == btnSim) {
                    boolean sucesso = repositorio.remover(id);

                    if (sucesso) {
                        mostrarAlerta("Sucesso", "Atividade excluída com sucesso!", Alert.AlertType.INFORMATION);
                        limparCampos(txtBuscaId, lblAviso);
                    } else {
                        mostrarAlerta("Erro", "Falha ao excluir atividade!", Alert.AlertType.ERROR);
                    }
                }

            } catch (Exception ex) {
                mostrarAlerta("Erro", "Erro ao excluir atividade: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnLimpar.setOnAction(e -> limparCampos(txtBuscaId, lblAviso));

        btnVoltar.setOnAction(e -> {
            Programa app = new Programa();
            stage.setScene(app.criarMenuPrincipal(stage));
            stage.setTitle("Sistema de Gerenciamento");
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(lblTitulo, 0, 0, 2, 1);

        grid.add(lblBuscaId, 0, 1);
        HBox hboxBusca = new HBox(10);
        hboxBusca.getChildren().addAll(txtBuscaId, btnBuscar);
        grid.add(hboxBusca, 1, 1);

        grid.add(separador, 0, 2, 2, 1);

        grid.add(lblId, 0, 3);
        grid.add(txtId, 1, 3);

        grid.add(lblTituloAtv, 0, 4);
        grid.add(txtTitulo, 1, 4);

        grid.add(lblDescricao, 0, 5);
        grid.add(txtDescricao, 1, 5);

        grid.add(lblStatus, 0, 6);
        grid.add(txtStatus, 1, 6);

        grid.add(lblData, 0, 7);
        grid.add(txtData, 1, 7);

        grid.add(lblProjeto, 0, 8);
        grid.add(txtProjeto, 1, 8);

        grid.add(lblAviso, 0, 9, 2, 1);

        HBox hboxBotoes = new HBox(10);
        hboxBotoes.setAlignment(Pos.CENTER);
        hboxBotoes.getChildren().addAll(btnExcluir, btnLimpar, btnVoltar);
        grid.add(hboxBotoes, 0, 10, 2, 1);

        return new Scene(grid, 650, 640);
    }

    private void buscarECarregarAtividade(int id, Label lblAviso) {
        try {
            Atividade atv = repositorio.buscarPorId(id);

            if (atv != null) {
                txtId.setText(String.valueOf(atv.getId()));
                txtTitulo.setText(atv.getTitulo());
                txtDescricao.setText(atv.getDescricao());
                txtStatus.setText(atv.getStatus());
                txtData.setText(atv.getDataCriacaoFormatada());
                txtProjeto.setText(atv.getNomeProjeto());
                comentarios = (int) repositorio.contarComentarios(id);

                btnExcluir.setDisable(false);
                lblAviso.setVisible(true);

                mostrarAlerta("Sucesso", "Atividade encontrada! Verifique antes de excluir.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Não Encontrada", "Nenhuma atividade com ID: " + id, Alert.AlertType.WARNING);
            }

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao buscar atividade: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void limparCampos(TextField txtBuscaId, Label lblAviso) {
        txtBuscaId.clear();
        txtId.clear();
        txtTitulo.clear();
        txtDescricao.clear();
        txtStatus.clear();
        txtData.clear();
        txtProjeto.clear();

        btnExcluir.setDisable(true);
        lblAviso.setVisible(false);
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
