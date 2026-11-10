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


public class ColaboradorAtualizacao {

    public void start(Stage stage) {
        Label lblTitulo = new Label("Atualizar Colaborador");

        TextField txtIdBusca = new TextField();
        txtIdBusca.setPromptText("ID do colaborador");

        TextField txtNome = new TextField();
        TextField txtEmail = new TextField();
        TextField txtFuncao = new TextField();

        Button btnBuscar = new Button("Buscar");
        Button btnSalvar = new Button("Salvar Alterações");
        Button btnVoltar = new Button("Voltar");

        ColaboradorDAO repositorio = new ColaboradorDAO();

        btnBuscar.setOnAction(e -> {
            Colaborador c = repositorio.buscarPorId(txtIdBusca.getText().trim());
            if (c == null) {
                alert("Não encontrado");
                return;
            }

            txtNome.setText(c.getNome());
            txtEmail.setText(c.getEmail());
            txtFuncao.setText(c.getFuncao());
        });

        btnSalvar.setOnAction(e -> {
            Colaborador c = repositorio.buscarPorId(txtIdBusca.getText().trim());
            if (c == null) {
                alert("Não encontrado");
                return;
            }
            c.setNome(txtNome.getText());
            c.setEmail(txtEmail.getText());
            c.setFuncao(txtFuncao.getText());
            try {
                repositorio.atualizar(c);
                alert("Atualizado");
            } catch (IllegalArgumentException ex) {
                alert(ex.getMessage());
            }
        });

        btnVoltar.setOnAction(e -> Telas.voltarAoMenu(stage));

        VBox vbox = new VBox(12, lblTitulo, txtIdBusca, btnBuscar, txtNome, txtEmail, txtFuncao, btnSalvar, btnVoltar);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        stage.setScene(new Scene(vbox, 400, 500));
    }

    private void alert(String txt) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(txt);
        a.show();
    }
}
