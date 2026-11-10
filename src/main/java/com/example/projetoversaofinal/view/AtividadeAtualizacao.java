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
import java.util.function.Consumer;


public class AtividadeAtualizacao {


    private static final Historico<Atividade.Memento> HISTORICO = new Historico<>();

    private final Stage stage;
    private final AtividadeDAO repositorio = new AtividadeDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Atividade atual;
    private TextField txtId;
    private TextField txtTitulo;
    private TextArea txtDescricao;
    private TextField txtData;
    private Label lblEstado;
    private ComboBox<Projeto> cbProjeto;
    private ComboBox<Colaborador> cbResponsavel;
    private ListView<Etiqueta> lstEtiquetas;

    public AtividadeAtualizacao(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("Atualizar Atividade");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField txtBuscaId = new TextField();
        txtBuscaId.setPromptText("Digite o ID para buscar");
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

        txtId = new TextField();
        txtId.setEditable(false);
        txtId.setStyle("-fx-background-color: #f0f0f0;");
        txtTitulo = new TextField();
        txtDescricao = new TextArea();
        txtDescricao.setWrapText(true);
        txtDescricao.setPrefRowCount(4);
        txtData = new TextField();
        txtData.setPromptText("dd/MM/yyyy");
        lblEstado = new Label("-");
        lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: #9C27B0;");
        cbProjeto = Telas.comboProjetos();
        cbResponsavel = Telas.comboColaboradores("(sem responsável)");
        lstEtiquetas = Telas.listaEtiquetas();
        Button btnSemResponsavel = new Button("Remover responsável");
        btnSemResponsavel.setOnAction(e -> cbResponsavel.setValue(null));


        Button btnAtribuir = new Button("Atribuir");
        Button btnRevisao = new Button("Enviar p/ Revisão");
        Button btnAprovar = new Button("Aprovar");
        Button btnReprovar = new Button("Reprovar");
        Button btnCancelar = new Button("Cancelar");
        btnAtribuir.setOnAction(e -> aplicarTransicao(Atividade::atribuir));
        btnRevisao.setOnAction(e -> aplicarTransicao(Atividade::enviarParaRevisao));
        btnAprovar.setOnAction(e -> aplicarTransicao(Atividade::aprovar));
        btnReprovar.setOnAction(e -> aplicarTransicao(Atividade::reprovar));
        btnCancelar.setOnAction(e -> aplicarTransicao(Atividade::cancelar));

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
                mostrarAlerta("Erro", "ID inválido!", Alert.AlertType.ERROR);
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
        grid.add(new Label("ID da Atividade:"), 0, 1);
        grid.add(new HBox(10, txtBuscaId, btnBuscar), 1, 1);
        grid.add(new Separator(), 0, 2, 2, 1);
        grid.add(new Label("ID:"), 0, 3);
        grid.add(txtId, 1, 3);
        grid.add(new Label("Título:"), 0, 4);
        grid.add(txtTitulo, 1, 4);
        grid.add(new Label("Descrição:"), 0, 5);
        grid.add(txtDescricao, 1, 5);
        grid.add(new Label("Data de Criação:"), 0, 6);
        grid.add(txtData, 1, 6);
        grid.add(new Label("Projeto:"), 0, 7);
        grid.add(cbProjeto, 1, 7);
        grid.add(new Label("Responsável:"), 0, 8);
        grid.add(new HBox(10, cbResponsavel, btnSemResponsavel), 1, 8);
        grid.add(new Label("Etiquetas (Ctrl+clique):"), 0, 9);
        grid.add(lstEtiquetas, 1, 9);
        grid.add(new Label("Status atual:"), 0, 10);
        grid.add(lblEstado, 1, 10);
        grid.add(new Label("Mudar status:"), 0, 11);
        grid.add(new HBox(6, btnAtribuir, btnRevisao, btnAprovar, btnReprovar, btnCancelar), 1, 11);
        grid.add(new HBox(10, btnSalvar, btnDesfazer, btnVoltar), 0, 12, 2, 1);

        return new Scene(grid, 780, 720);
    }

    private void carregar(int id) {
        atual = repositorio.buscarPorId(id);
        if (atual == null) {
            mostrarAlerta("Não encontrado", "Nenhuma atividade com ID " + id, Alert.AlertType.WARNING);
            return;
        }
        preencherCampos();
    }

    private void preencherCampos() {
        txtId.setText(String.valueOf(atual.getId()));
        txtTitulo.setText(atual.getTitulo());
        txtDescricao.setText(atual.getDescricao());
        txtData.setText(atual.getDataCriacaoFormatada());
        lblEstado.setText(atual.getStatus());
        cbProjeto.setValue(atual.getProjeto());
        cbResponsavel.setValue(atual.getResponsavel());
        Telas.selecionarEtiquetas(lstEtiquetas, atual.getEtiquetas());
    }

    // Grava as alterações de título, descrição e data.
    // O memento é tirado antes de mexer na atividade: ele serve tanto para o botão
    // "Desfazer" quanto para voltar atrás se a gravação no banco falhar.
    private void salvarDados() {
        if (!temAtividadeCarregada()) return;

        try {
            LocalDate data = LocalDate.parse(txtData.getText().trim(), formatter);

            if (txtTitulo.getText().trim().isEmpty() || txtDescricao.getText().trim().isEmpty()
                    || cbProjeto.getValue() == null) {
                mostrarAlerta("Erro", "Título, descrição e projeto são obrigatórios!", Alert.AlertType.ERROR);
                return;
            }

            Atividade.Memento anterior = atual.salvarEstado();

            atual.setTitulo(txtTitulo.getText().trim());
            atual.setDescricao(txtDescricao.getText().trim());
            atual.setDataCriacao(data);
            atual.setProjeto(cbProjeto.getValue());
            atual.setResponsavel(cbResponsavel.getValue());
            atual.setEtiquetas(lstEtiquetas.getSelectionModel().getSelectedItems());

            if (!atualizarNoBanco()) {
                // Não gravou (a atividade pode ter sido excluída em outra tela):
                // a própria atividade se restaura a partir do memento.
                atual.restaurarEstado(anterior);
                preencherCampos();
                mostrarAlerta("Erro", "Não foi possível atualizar a atividade.", Alert.AlertType.ERROR);
                return;
            }

            // Só entra no histórico o que de fato foi salvo.
            HISTORICO.salvar(atual.getId(), anterior);
            preencherCampos();
            mostrarAlerta("Sucesso", "Atividade atualizada!", Alert.AlertType.INFORMATION);

        } catch (DateTimeParseException ex) {
            mostrarAlerta("Erro", "Data inválida! Use dd/MM/yyyy", Alert.AlertType.ERROR);
        }
    }

    // Muda o status da atividade (Atribuir, Aprovar etc.).
    // Quem decide se a mudança é permitida é o estado atual (padrão State);
    // se não for, ele lança IllegalStateException e nada é alterado.
    private void aplicarTransicao(Consumer<Atividade> transicao) {
        if (!temAtividadeCarregada()) return;

        Atividade.Memento antes = atual.salvarEstado();

        try {
            transicao.accept(atual);

            // Sem esta gravação o novo status aparece na tela mas some ao reabrir.
            if (!atualizarNoBanco()) {
                atual.restaurarEstado(antes);
                preencherCampos();
                mostrarAlerta("Erro", "Não foi possível salvar o novo status.", Alert.AlertType.ERROR);
                return;
            }

            HISTORICO.salvar(atual.getId(), antes);
            preencherCampos();

        } catch (IllegalStateException ex) {
            mostrarAlerta("Transição inválida", ex.getMessage(), Alert.AlertType.WARNING);
        }
    }


    private void desfazer() {
        if (!temAtividadeCarregada()) return;
        Atividade.Memento anterior = HISTORICO.desfazer(atual.getId());
        if (anterior == null) {
            mostrarAlerta("Aviso", "Não há alterações para desfazer.", Alert.AlertType.INFORMATION);
            return;
        }
        atual.restaurarEstado(anterior);
        atualizarNoBanco();
        preencherCampos();
    }

    // Grava no banco; se o banco recusar (ex.: projeto excluído em outra tela), avisa e devolve false.
    private boolean atualizarNoBanco() {
        try {
            return repositorio.atualizar(atual);
        } catch (IllegalArgumentException ex) {
            mostrarAlerta("Erro", ex.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    private boolean temAtividadeCarregada() {
        if (atual == null) {
            mostrarAlerta("Aviso", "Busque uma atividade primeiro.", Alert.AlertType.WARNING);
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