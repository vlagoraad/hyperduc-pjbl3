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

import java.util.function.Consumer;

public class DenunciaAtualizacao {

    private static final Historico<Denuncia.Memento> HISTORICO = new Historico<>();

    private final Stage stage;
    private final DenunciaDAO repositorio = new DenunciaDAO();

    private Denuncia atual;

    private TextField txtMotivo;
    private TextField txtAlvo;
    private TextArea txtDescricao;
    private Label lblEstado;
    private Label lblAutor;

    public DenunciaAtualizacao(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("Analisar Denúncia");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField txtBuscaId = new TextField();
        txtBuscaId.setPromptText("Número da denúncia");
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

        txtMotivo = new TextField();
        txtAlvo = new TextField();
        txtDescricao = new TextArea();
        txtDescricao.setWrapText(true);
        txtDescricao.setPrefRowCount(4);
        lblEstado = new Label("-");
        lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: #E91E63;");
        lblAutor = new Label("-");

        Button btnAnalisar = new Button("Iniciar Análise");
        Button btnProcedente = new Button("Julgar Procedente");
        Button btnArquivar = new Button("Arquivar");
        btnAnalisar.setOnAction(e -> aplicarTransicao(Denuncia::analisar));
        btnProcedente.setOnAction(e -> aplicarTransicao(Denuncia::julgarProcedente));
        btnArquivar.setOnAction(e -> aplicarTransicao(Denuncia::arquivar));

        Button btnSalvar = new Button("Salvar Dados");
        Button btnDesfazer = new Button("↶ Desfazer Última Alteração");
        Button btnVoltar = new Button("Voltar");
        btnSalvar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDesfazer.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoltar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        btnBuscar.setOnAction(e -> {
            try {
                carregar(Integer.parseInt(txtBuscaId.getText().trim()));
            } catch (NumberFormatException ex) {
                mostrarAlerta("Erro", "Número inválido!", Alert.AlertType.ERROR);
            }
        });
        btnSalvar.setOnAction(e -> salvarDados());
        btnDesfazer.setOnAction(e -> desfazer());
        btnVoltar.setOnAction(e -> {
            stage.setScene(new Programa().criarMenuPrincipal(stage));
            stage.setTitle("HYPERDUC! - Sistema de Gerenciamento");
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(lblTitulo, 0, 0, 2, 1);
        grid.add(new Label("Número:"), 0, 1);
        grid.add(new HBox(10, txtBuscaId, btnBuscar), 1, 1);
        grid.add(new Separator(), 0, 2, 2, 1);
        grid.add(new Label("Motivo:"), 0, 3);
        grid.add(txtMotivo, 1, 3);
        grid.add(new Label("Alvo:"), 0, 4);
        grid.add(txtAlvo, 1, 4);
        grid.add(new Label("Descrição:"), 0, 5);
        grid.add(txtDescricao, 1, 5);
        grid.add(new Label("Autor:"), 0, 6);
        grid.add(lblAutor, 1, 6);
        grid.add(new Label("Status atual:"), 0, 7);
        grid.add(lblEstado, 1, 7);
        grid.add(new Label("Mudar status:"), 0, 8);
        grid.add(new HBox(6, btnAnalisar, btnProcedente, btnArquivar), 1, 8);
        grid.add(new HBox(10, btnSalvar, btnDesfazer, btnVoltar), 0, 9, 2, 1);

        return new Scene(grid, 680, 520);
    }

    private void carregar(int id) {
        atual = repositorio.buscarPorId(id);
        if (atual == null) {
            mostrarAlerta("Não encontrada", "Nenhuma denúncia com número " + id, Alert.AlertType.WARNING);
            return;
        }
        preencherCampos();
    }

    private void preencherCampos() {
        txtMotivo.setText(atual.getMotivo());
        txtAlvo.setText(atual.getAlvo());
        txtDescricao.setText(atual.getDescricao());
        lblEstado.setText(atual.getStatus());
        lblAutor.setText(atual.getNomeAutor());
    }

    private void salvarDados() {
        if (!temDenunciaCarregada()) return;
        Denuncia.Memento antes = atual.salvarEstado();
        atual.setMotivo(txtMotivo.getText().trim());
        atual.setAlvo(txtAlvo.getText().trim());
        atual.setDescricao(txtDescricao.getText().trim());
        try {
            repositorio.atualizar(atual);
            HISTORICO.salvar(atual.getId(), antes);
            mostrarAlerta("Sucesso", "Denúncia atualizada!", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException ex) {
            atual.restaurarEstado(antes);
            preencherCampos();
            mostrarAlerta("Erro", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    
private void aplicarTransicao(Consumer<Denuncia> transicao) {
    if (!temDenunciaCarregada()) return;

    Denuncia.Memento antes = atual.salvarEstado();

    try {
        transicao.accept(atual);
        repositorio.atualizar(atual);
        HISTORICO.salvar(atual.getId(), antes);
        preencherCampos();

    } catch (IllegalStateException ex) {
        mostrarAlerta(
            "Transição inválida",
            ex.getMessage(),
            Alert.AlertType.WARNING
        );
    } catch (IllegalArgumentException ex) {
        atual.restaurarEstado(antes);
        preencherCampos();
        mostrarAlerta("Erro", ex.getMessage(), Alert.AlertType.ERROR);
    }
}


    private void desfazer() {
        if (!temDenunciaCarregada()) return;
        Denuncia.Memento anterior = HISTORICO.desfazer(atual.getId());
        if (anterior == null) {
            mostrarAlerta("Aviso", "Não há alterações para desfazer.", Alert.AlertType.INFORMATION);
            return;
        }
        atual.restaurarEstado(anterior);
        repositorio.atualizar(atual);
        preencherCampos();
    }

    private boolean temDenunciaCarregada() {
        if (atual == null) {
            mostrarAlerta("Aviso", "Busque uma denúncia primeiro.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
