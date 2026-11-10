package com.example.projetoversaofinal.view;

import com.example.projetoversaofinal.dao.EtiquetaDAO;
import com.example.projetoversaofinal.model.Etiqueta;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// CRUD completo de Etiqueta numa tela só: formulário em cima, tabela embaixo.
// Clicar numa linha da tabela carrega o registro no formulário.
public class EtiquetaCrud {

    private final Stage stage;
    private final EtiquetaDAO dao = new EtiquetaDAO();

    private final TextField txtId = new TextField();
    private final TextField txtNome = new TextField();
    private final TextField txtCor = new TextField();
    private final TableView<Etiqueta> tabela = new TableView<>();

    public EtiquetaCrud(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("🏷️ Etiquetas");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        txtId.setPromptText("Ex.: 1");
        txtNome.setPromptText("Ex.: urgente, backend, bug");
        txtCor.setPromptText("Ex.: vermelho ou #FF0000");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("ID:"), txtId);
        form.addRow(1, new Label("Nome:"), txtNome);
        form.addRow(2, new Label("Cor:"), txtCor);

        Button btnInserir = botao("Inserir", "#4CAF50");
        Button btnAtualizar = botao("Atualizar", "#2196F3");
        Button btnExcluir = botao("Excluir", "#f44336");
        Button btnLimpar = botao("Limpar", "#FF9800");
        Button btnVoltar = botao("Voltar", "#9E9E9E");

        btnInserir.setOnAction(e -> executar(() -> {
            dao.adicionar(lerFormulario());
            return "Etiqueta inserida!";
        }));
        btnAtualizar.setOnAction(e -> executar(() -> {
            // Carrega a etiqueta do banco e altera só os campos do formulário,
            // para não mexer na lista de atividades dela.
            Etiqueta etiqueta = dao.buscarPorId(Telas.lerInteiro(txtId, "ID"));
            if (etiqueta == null) {
                throw new IllegalArgumentException("Não existe etiqueta com esse ID.");
            }
            etiqueta.setNome(txtNome.getText().trim());
            etiqueta.setCor(txtCor.getText().trim());
            dao.atualizar(etiqueta);
            return "Etiqueta atualizada!";
        }));
        btnExcluir.setOnAction(e -> executar(() -> {
            Integer id = Telas.lerInteiro(txtId, "ID");
            if (!Telas.confirmar("Excluir a etiqueta " + id + "?\nEla será retirada de todas as atividades.")) {
                return null;
            }
            if (!dao.remover(id)) {
                throw new IllegalArgumentException("Não existe etiqueta com esse ID.");
            }
            return "Etiqueta excluída!";
        }));
        btnLimpar.setOnAction(e -> limpar());
        btnVoltar.setOnAction(e -> Telas.voltarAoMenu(stage));

        tabela.getColumns().add(Telas.coluna("ID", "id", 60));
        tabela.getColumns().add(Telas.coluna("Nome", "nome", 200));
        tabela.getColumns().add(Telas.coluna("Cor", "cor", 140));
        tabela.getColumns().add(Telas.coluna("Nº de atividades", "quantidadeAtividades", 130));
        tabela.setPlaceholder(new Label("Nenhuma etiqueta cadastrada"));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                txtId.setText(String.valueOf(nova.getId()));
                txtNome.setText(nova.getNome());
                txtCor.setText(nova.getCor());
            }
        });
        recarregar();

        HBox botoes = new HBox(10, btnInserir, btnAtualizar, btnExcluir, btnLimpar, btnVoltar);
        botoes.setAlignment(Pos.CENTER);

        VBox raiz = new VBox(15, lblTitulo, form, botoes, tabela);
        raiz.setPadding(new Insets(20));
        return new Scene(raiz, 620, 560);
    }

    private Etiqueta lerFormulario() {
        return new Etiqueta(Telas.lerInteiro(txtId, "ID"), txtNome.getText().trim(), txtCor.getText().trim());
    }

    private void executar(java.util.function.Supplier<String> acao) {
        try {
            String mensagem = acao.get();
            if (mensagem != null) {
                Telas.alerta("Sucesso", mensagem, Alert.AlertType.INFORMATION);
                limpar();
            }
        } catch (IllegalArgumentException ex) {
            Telas.alerta("Erro", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void limpar() {
        txtId.clear();
        txtNome.clear();
        txtCor.clear();
        recarregar();
    }

    private void recarregar() {
        tabela.setItems(FXCollections.observableArrayList(dao.listar()));
    }

    static Button botao(String texto, String cor) {
        Button b = new Button(texto);
        b.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; -fx-font-weight: bold;");
        return b;
    }
}
