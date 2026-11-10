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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ProjetoInsercao {
    private Stage stage;
    private final ProjetoDAO repositorio = new ProjetoDAO();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProjetoInsercao(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("Inserir Novo Projeto");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label lblId = new Label("ID:");
        TextField txtId = new TextField();
        txtId.setPromptText("Digite o ID do projeto");

        Label lblTituloProjeto = new Label("Título:");
        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Digite o título do projeto");

        Label lblDescricao = new Label("Descrição:");
        TextArea txtDescricao = new TextArea();
        txtDescricao.setPromptText("Digite a descrição do projeto");
        txtDescricao.setPrefRowCount(4);
        txtDescricao.setWrapText(true);

        Label lblData = new Label("Data de Criação:");
        TextField txtData = new TextField();
        txtData.setPromptText("dd/MM/yyyy");

        Button btnDataAtual = new Button("Usar Data Atual");
        btnDataAtual.setOnAction(e -> {
            txtData.setText(LocalDate.now().format(formatter));
        });

        Button btnSalvar = new Button("Salvar");
        Button btnLimpar = new Button("Limpar");
        Button btnVoltar = new Button("Voltar");

        btnSalvar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLimpar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoltar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSalvar.setOnAction(e -> {
            try {
                if (txtId.getText().trim().isEmpty()) {
                    mostrarAlerta("Erro", "O campo ID não pode estar vazio!", Alert.AlertType.ERROR);
                    return;
                }

                int id = Integer.parseInt(txtId.getText().trim());

                if (txtTitulo.getText().trim().isEmpty()) {
                    mostrarAlerta("Erro", "O campo Título não pode estar vazio!", Alert.AlertType.ERROR);
                    return;
                }

                if (txtDescricao.getText().trim().isEmpty()) {
                    mostrarAlerta("Erro", "O campo Descrição não pode estar vazio!", Alert.AlertType.ERROR);
                    return;
                }

                if (txtData.getText().trim().isEmpty()) {
                    mostrarAlerta("Erro", "O campo Data não pode estar vazio!", Alert.AlertType.ERROR);
                    return;
                }

                LocalDate data = LocalDate.parse(txtData.getText().trim(), formatter);

                for (Projeto p : repositorio.listar()) {
                    if (p.getId() == id) {
                        mostrarAlerta("Erro", "Já existe um projeto com este ID!", Alert.AlertType.ERROR);
                        return;
                    }
                }

                Projeto novoProjeto = new Projeto(id, txtTitulo.getText().trim(),
                        txtDescricao.getText().trim(), data);
                repositorio.adicionar(novoProjeto);

                mostrarAlerta("Sucesso", "Projeto adicionado com sucesso!", Alert.AlertType.INFORMATION);

                limparCampos(txtId, txtTitulo, txtDescricao, txtData);

            } catch (NumberFormatException ex) {
                mostrarAlerta("Erro", "ID deve ser um número inteiro válido!", Alert.AlertType.ERROR);
            } catch (DateTimeParseException ex) {
                mostrarAlerta("Erro", "Data inválida! Use o formato dd/MM/yyyy", Alert.AlertType.ERROR);
            } catch (Exception ex) {
                mostrarAlerta("Erro", "Erro ao salvar projeto: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnLimpar.setOnAction(e -> limparCampos(txtId, txtTitulo, txtDescricao, txtData));

        btnVoltar.setOnAction(e -> {
            Programa app = new Programa();
            stage.setScene(app.criarMenuPrincipal(stage));
            stage.setTitle("HYPERDUC! - Sistema de Gerenciamento");
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(lblTitulo, 0, 0, 2, 1);
        grid.add(lblId, 0, 1);
        grid.add(txtId, 1, 1);
        grid.add(lblTituloProjeto, 0, 2);
        grid.add(txtTitulo, 1, 2);
        grid.add(lblDescricao, 0, 3);
        grid.add(txtDescricao, 1, 3);
        grid.add(lblData, 0, 4);

        HBox hboxData = new HBox(10);
        hboxData.getChildren().addAll(txtData, btnDataAtual);
        grid.add(hboxData, 1, 4);

        HBox hboxBotoes = new HBox(10);
        hboxBotoes.setAlignment(Pos.CENTER);
        hboxBotoes.getChildren().addAll(btnSalvar, btnLimpar, btnVoltar);
        grid.add(hboxBotoes, 0, 5, 2, 1);

        Scene scene = new Scene(grid, 600, 500);
        return scene;
    }

    private void limparCampos(TextField txtId, TextField txtTitulo, TextArea txtDescricao, TextField txtData) {
        txtId.clear();
        txtTitulo.clear();
        txtDescricao.clear();
        txtData.clear();
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}