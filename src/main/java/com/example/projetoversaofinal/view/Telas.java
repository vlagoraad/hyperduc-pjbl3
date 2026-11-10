package com.example.projetoversaofinal.view;

import com.example.projetoversaofinal.Programa;
import com.example.projetoversaofinal.dao.*;
import com.example.projetoversaofinal.model.*;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Collection;

// Utilidades compartilhadas pelas telas: alertas, volta ao menu e os
// componentes de seleção usados para escolher o lado "um" de um relacionamento.
public final class Telas {

    private Telas() {
    }

    public static void alerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static boolean confirmar(String mensagem) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.OK, ButtonType.CANCEL);
        confirmacao.setHeaderText("Você tem certeza?");
        return confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static void voltarAoMenu(Stage stage) {
        stage.setScene(new Programa().criarMenuPrincipal(stage));
        stage.setTitle("HYPERDUC! - Sistema de Gerenciamento");
    }

    public static ComboBox<Projeto> comboProjetos() {
        return combo(new ProjetoDAO().listar(), "Escolha o projeto");
    }

    public static ComboBox<Colaborador> comboColaboradores(String dica) {
        return combo(new ColaboradorDAO().listar(), dica);
    }

    public static ComboBox<Atividade> comboAtividades() {
        return combo(new AtividadeDAO().listar(), "Escolha a atividade");
    }

    private static <T> ComboBox<T> combo(Collection<T> itens, String dica) {
        ComboBox<T> combo = new ComboBox<>(FXCollections.observableArrayList(itens));
        combo.setPromptText(dica);
        combo.setPrefWidth(300);
        return combo;
    }

    // Lista com seleção múltipla para o N:N Atividade <-> Etiqueta (Ctrl+clique).
    public static ListView<Etiqueta> listaEtiquetas() {
        ListView<Etiqueta> lista = new ListView<>(FXCollections.observableArrayList(new EtiquetaDAO().listar()));
        lista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lista.setPrefHeight(90);
        lista.setPlaceholder(new Label("Nenhuma etiqueta cadastrada"));
        return lista;
    }

    public static void selecionarEtiquetas(ListView<Etiqueta> lista, Collection<Etiqueta> selecionadas) {
        lista.getSelectionModel().clearSelection();
        for (Etiqueta e : selecionadas) {
            lista.getSelectionModel().select(e);
        }
    }

    public static <S> TableColumn<S, Object> coluna(String titulo, String propriedade, double largura) {
        TableColumn<S, Object> c = new TableColumn<>(titulo);
        c.setCellValueFactory(new PropertyValueFactory<>(propriedade));
        c.setPrefWidth(largura);
        return c;
    }

    public static Integer lerInteiro(TextField campo, String nomeCampo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo " + nomeCampo + " deve ser um número inteiro.");
        }
    }
}
