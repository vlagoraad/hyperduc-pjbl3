package com.example.projetoversaofinal.view;

import com.example.projetoversaofinal.Programa;
import com.example.projetoversaofinal.dao.*;
import com.example.projetoversaofinal.model.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ColaboradorInsercao {

    public void start(Stage stage) {
        Label lblTitulo = new Label("Inserir Colaborador");

        TextField txtId = new TextField();
        txtId.setPromptText("ID");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email");

        TextField txtFuncao = new TextField();
        txtFuncao.setPromptText("Função");

        Button btnSalvar = new Button("Salvar");
        Button btnVoltar = new Button("Voltar");

        btnSalvar.setOnAction(e -> {
            try {
                Colaborador c = new Colaborador(
                        txtId.getText(),
                        txtNome.getText(),
                        txtEmail.getText(),
                        txtFuncao.getText()
                );

                new ColaboradorDAO().adicionar(c);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText("Cadastro realizado");
                alert.setContentText("Colaborador salvo com sucesso!");
                alert.show();

            } catch (RuntimeException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Falha ao salvar");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });

        btnVoltar.setOnAction(e -> Telas.voltarAoMenu(stage));

        VBox vbox = new VBox(12, lblTitulo, txtId, txtNome, txtEmail, txtFuncao, btnSalvar, btnVoltar);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        stage.setScene(new Scene(vbox, 400, 400));
    }
}
