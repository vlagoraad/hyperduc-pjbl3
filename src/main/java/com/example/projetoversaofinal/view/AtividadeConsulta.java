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

public class AtividadeConsulta {
    private Stage stage;
    private final AtividadeDAO repositorio = new AtividadeDAO();
    private TableView<Atividade> tabelaAtividades;
    private ObservableList<Atividade> listaAtividades;

    public AtividadeConsulta(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("Consultar Atividades");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label lblBusca = new Label("Buscar por ID:");
        TextField txtBuscaId = new TextField();
        txtBuscaId.setPromptText("Digite o ID da atividade");
        txtBuscaId.setPrefWidth(200);

        Button btnBuscar = new Button("Buscar");
        Button btnMostrarTodos = new Button("Mostrar Todas");
        Button btnVoltar = new Button("Voltar");

        btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnMostrarTodos.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoltar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        criarTabela();

        // Filtro que usa uma consulta JPQL pelo relacionamento Atividade -> Projeto
        ComboBox<Projeto> cbFiltroProjeto = Telas.comboProjetos();
        cbFiltroProjeto.setPromptText("Filtrar por projeto");
        cbFiltroProjeto.setOnAction(e -> {
            Projeto p = cbFiltroProjeto.getValue();
            if (p != null) {
                tabelaAtividades.setItems(FXCollections.observableArrayList(repositorio.listarPorProjeto(p.getId())));
            }
        });

        carregarTodasAtividades();

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
            cbFiltroProjeto.setValue(null);
            carregarTodasAtividades();
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
        vbox.getChildren().addAll(lblTitulo, hboxBusca, cbFiltroProjeto, tabelaAtividades, lblQuantidade, hboxBotoes);

        return new Scene(vbox, 1150, 620);
    }

    private void criarTabela() {
        tabelaAtividades = new TableView<>();

        TableColumn<Atividade, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaId.setPrefWidth(80);

        TableColumn<Atividade, String> colunaTitulo = new TableColumn<>("Título");
        colunaTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colunaTitulo.setPrefWidth(180);

        TableColumn<Atividade, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaDescricao.setPrefWidth(180);

        TableColumn<Atividade, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colunaStatus.setPrefWidth(130);

        TableColumn<Atividade, LocalDate> colunaData = new TableColumn<>("Data Criação");
        colunaData.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
        colunaData.setPrefWidth(150);

        colunaData.setCellFactory(column -> new TableCell<Atividade, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Atividade atividade = getTableView().getItems().get(getIndex());
                    setText(atividade.getDataCriacaoFormatada());
                }
            }
        });

        tabelaAtividades.getColumns().addAll(colunaId, colunaTitulo, colunaDescricao, colunaStatus, colunaData);
        tabelaAtividades.getColumns().add(Telas.coluna("Projeto", "nomeProjeto", 150));
        tabelaAtividades.getColumns().add(Telas.coluna("Responsável", "nomeResponsavel", 140));
        tabelaAtividades.getColumns().add(Telas.coluna("Etiquetas", "etiquetasTexto", 140));

        tabelaAtividades.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tabelaAtividades.setPlaceholder(new Label("Nenhuma atividade cadastrada"));
        tabelaAtividades.setPrefHeight(400);
    }

    private void carregarTodasAtividades() {
        try {
            ArrayList<Atividade> atividades = repositorio.listar();
            listaAtividades = FXCollections.observableArrayList(atividades);
            tabelaAtividades.setItems(listaAtividades);

            if (atividades.isEmpty()) {
                mostrarAlerta("Informação", "Nenhuma atividade cadastrada ainda.", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao carregar atividades: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void buscarPorId(int id) {
        try {
            ArrayList<Atividade> atividades = repositorio.listar();
            ArrayList<Atividade> resultado = new ArrayList<>();

            for (Atividade a : atividades) {
                if (a.getId() == id) {
                    resultado.add(a);
                    break;
                }
            }

            if (resultado.isEmpty()) {
                mostrarAlerta("Não Encontrado", "Nenhuma atividade encontrada com o ID: " + id, Alert.AlertType.WARNING);
                listaAtividades = FXCollections.observableArrayList();
            } else {
                listaAtividades = FXCollections.observableArrayList(resultado);
                mostrarAlerta("Sucesso", "Atividade encontrada!", Alert.AlertType.INFORMATION);
            }

            tabelaAtividades.setItems(listaAtividades);

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao buscar atividade: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void atualizarQuantidade(Label lblQuantidade) {
        ArrayList<Atividade> atividades = repositorio.listar();
        lblQuantidade.setText("Total de atividades: " + atividades.size());
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
