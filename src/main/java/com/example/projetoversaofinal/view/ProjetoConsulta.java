package com.example.projetoversaofinal.view;

import com.example.projetoversaofinal.Programa;
import com.example.projetoversaofinal.dao.*;
import com.example.projetoversaofinal.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class ProjetoConsulta {
    private Stage stage;
    private final ProjetoDAO repositorio = new ProjetoDAO();
    private TableView<Projeto> tabelaProjetos;
    private ObservableList<Projeto> listaProjetos;

    public ProjetoConsulta(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("Consultar Projetos");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label lblBusca = new Label("Buscar por ID:");
        TextField txtBuscaId = new TextField();
        txtBuscaId.setPromptText("Digite o ID do projeto");
        txtBuscaId.setPrefWidth(200);

        Button btnBuscar = new Button("Buscar");
        Button btnMostrarTodos = new Button("Mostrar Todos");
        Button btnVoltar = new Button("Voltar");

        btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnMostrarTodos.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoltar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        criarTabela();

        carregarTodosProjetos();

        btnBuscar.setOnAction(e -> {
            try {
                if (txtBuscaId.getText().trim().isEmpty()) {
                    mostrarAlerta("Aviso", "Digite um ID para buscar!", Alert.AlertType.WARNING);
                    return;
                }

                int id = Integer.parseInt(txtBuscaId.getText().trim());
                buscarPorId(id);

            } catch (NumberFormatException ex) {
                mostrarAlerta("Erro", "ID deve ser um número inteiro válido!", Alert.AlertType.ERROR);
            }
        });

        btnMostrarTodos.setOnAction(e -> {
            txtBuscaId.clear();
            carregarTodosProjetos();
        });

        btnVoltar.setOnAction(e -> {
            Programa app = new Programa();
            stage.setScene(app.criarMenuPrincipal(stage));
            stage.setTitle("HYPERDUC! - Sistema de Gerenciamento");
        });

        Label lblQuantidade = new Label();
        lblQuantidade.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
        atualizarQuantidade(lblQuantidade);

        HBox hboxBusca = new HBox(10);
        hboxBusca.setAlignment(Pos.CENTER_LEFT);
        hboxBusca.getChildren().addAll(lblBusca, txtBuscaId, btnBuscar, btnMostrarTodos);

        HBox hboxBotoes = new HBox(10);
        hboxBotoes.setAlignment(Pos.CENTER);
        hboxBotoes.getChildren().addAll(btnVoltar);

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.getChildren().addAll(lblTitulo, hboxBusca, tabelaProjetos, lblQuantidade, hboxBotoes);

        Scene scene = new Scene(vbox, 800, 600);
        return scene;
    }

    private void criarTabela() {
        tabelaProjetos = new TableView<>();

        TableColumn<Projeto, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaId.setPrefWidth(80);

        TableColumn<Projeto, String> colunaTitulo = new TableColumn<>("Título");
        colunaTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colunaTitulo.setPrefWidth(200);

        TableColumn<Projeto, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaDescricao.setPrefWidth(250);

        TableColumn<Projeto, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colunaStatus.setPrefWidth(120);

        TableColumn<Projeto, LocalDate> colunaData = new TableColumn<>("Data de Criação");
        colunaData.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
        colunaData.setPrefWidth(150);

        colunaData.setCellFactory(column -> new TableCell<Projeto, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Projeto projeto = getTableView().getItems().get(getIndex());
                    setText(projeto.getDataCriacaoFormatada());
                }
            }
        });

        tabelaProjetos.getColumns().addAll(colunaId, colunaTitulo, colunaDescricao, colunaStatus, colunaData);

        tabelaProjetos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tabelaProjetos.setPlaceholder(new Label("Nenhum projeto cadastrado"));

        tabelaProjetos.setPrefHeight(400);
    }

    private void carregarTodosProjetos() {
        try {
            ArrayList<Projeto> projetos = repositorio.listar();
            listaProjetos = FXCollections.observableArrayList(projetos);
            tabelaProjetos.setItems(listaProjetos);

            if (projetos.isEmpty()) {
                mostrarAlerta("Informação", "Nenhum projeto cadastrado ainda.", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao carregar projetos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void buscarPorId(int id) {
        try {
            ArrayList<Projeto> projetos = repositorio.listar();
            ArrayList<Projeto> resultado = new ArrayList<>();

            for (Projeto p : projetos) {
                if (p.getId() == id) {
                    resultado.add(p);
                    break;
                }
            }

            if (resultado.isEmpty()) {
                mostrarAlerta("Não Encontrado", "Nenhum projeto encontrado com o ID: " + id, Alert.AlertType.WARNING);
                listaProjetos = FXCollections.observableArrayList();
            } else {
                listaProjetos = FXCollections.observableArrayList(resultado);
                mostrarAlerta("Sucesso", "Projeto encontrado!", Alert.AlertType.INFORMATION);
            }

            tabelaProjetos.setItems(listaProjetos);

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao buscar projeto: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void atualizarQuantidade(Label lblQuantidade) {
        ArrayList<Projeto> projetos = repositorio.listar();
        lblQuantidade.setText("Total de projetos: " + projetos.size());
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
