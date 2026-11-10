package com.example.projetoversaofinal.view;

import com.example.projetoversaofinal.Programa;
import com.example.projetoversaofinal.dao.*;
import com.example.projetoversaofinal.model.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DenunciaExclusao {
    private final Stage stage;
    private final DenunciaDAO repositorio = new DenunciaDAO();

    public DenunciaExclusao(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("Excluir Denúncia");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField txtId = new TextField();
        txtId.setPromptText("Número da denúncia");

        Button btnExcluir = new Button("Excluir");
        Button btnVoltar = new Button("Voltar");
        btnExcluir.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoltar.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-weight: bold;");

        btnExcluir.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Excluir a denúncia " + id + "?");
                if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

                boolean removido = repositorio.remover(id);
                Alert alert = new Alert(removido ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                        removido ? "Denúncia removida." : "Nenhuma denúncia com esse número.");
                alert.showAndWait();
                txtId.clear();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Número inválido!").showAndWait();
            } catch (IllegalArgumentException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        btnVoltar.setOnAction(e -> {
            stage.setScene(new Programa().criarMenuPrincipal(stage));
            stage.setTitle("HYPERDUC! - Sistema de Gerenciamento");
        });

        HBox botoes = new HBox(10, btnExcluir, btnVoltar);
        botoes.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(15, lblTitulo, txtId, botoes);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25));
        return new Scene(vbox, 400, 250);
    }
}
