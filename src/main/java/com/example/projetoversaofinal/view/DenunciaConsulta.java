package com.example.projetoversaofinal.view;

import com.example.projetoversaofinal.Programa;
import com.example.projetoversaofinal.dao.*;
import com.example.projetoversaofinal.model.*;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DenunciaConsulta {
    private final Stage stage;
    private final DenunciaDAO repositorio = new DenunciaDAO();

    public DenunciaConsulta(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("Consultar Denúncias");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Denuncia> tabela = new TableView<>();
        tabela.getColumns().add(coluna("Nº", "id", 50));
        tabela.getColumns().add(coluna("Motivo", "motivo", 170));
        tabela.getColumns().add(coluna("Alvo", "alvo", 130));
        tabela.getColumns().add(coluna("Autor", "nomeAutor", 130));
        tabela.getColumns().add(coluna("Descrição", "descricao", 230));
        tabela.getColumns().add(coluna("Status", "status", 110));
        tabela.getColumns().add(coluna("Data", "dataCriacaoFormatada", 100));
        tabela.setPlaceholder(new Label("Nenhuma denúncia registrada"));
        tabela.setItems(FXCollections.observableArrayList(repositorio.listar()));

        Button btnVoltar = new Button("Voltar");
        btnVoltar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoltar.setOnAction(e -> {
            stage.setScene(new Programa().criarMenuPrincipal(stage));
            stage.setTitle("HYPERDUC! - Sistema de Gerenciamento");
        });

        VBox vbox = new VBox(15, lblTitulo, tabela, btnVoltar);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(20));
        return new Scene(vbox, 1000, 550);
    }

    private TableColumn<Denuncia, Object> coluna(String titulo, String propriedade, double largura) {
        TableColumn<Denuncia, Object> c = new TableColumn<>(titulo);
        c.setCellValueFactory(new PropertyValueFactory<>(propriedade));
        c.setPrefWidth(largura);
        return c;
    }
}
