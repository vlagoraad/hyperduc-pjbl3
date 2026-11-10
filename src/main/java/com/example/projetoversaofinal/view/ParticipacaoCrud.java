package com.example.projetoversaofinal.view;

import com.example.projetoversaofinal.dao.ParticipacaoDAO;
import com.example.projetoversaofinal.model.Colaborador;
import com.example.projetoversaofinal.model.Participacao;
import com.example.projetoversaofinal.model.Projeto;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.function.Supplier;

// CRUD da entidade associativa Participacao (qual colaborador está em qual projeto e com que papel).
public class ParticipacaoCrud {

    private final Stage stage;
    private final ParticipacaoDAO dao = new ParticipacaoDAO();

    private final TextField txtId = new TextField();
    private final ComboBox<Projeto> cbProjeto = Telas.comboProjetos();
    private final ComboBox<Colaborador> cbColaborador = Telas.comboColaboradores("Escolha o colaborador");
    private final TextField txtPapel = new TextField();
    private final DatePicker dpEntrada = new DatePicker(LocalDate.now());
    private final TableView<Participacao> tabela = new TableView<>();

    public ParticipacaoCrud(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("🤝 Participações em Projetos");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        txtId.setPromptText("Ex.: 1");
        txtPapel.setPromptText("Ex.: líder, desenvolvedora, designer");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("ID:"), txtId);
        form.addRow(1, new Label("Projeto:"), cbProjeto);
        form.addRow(2, new Label("Colaborador:"), cbColaborador);
        form.addRow(3, new Label("Papel:"), txtPapel);
        form.addRow(4, new Label("Data de entrada:"), dpEntrada);

        Button btnInserir = EtiquetaCrud.botao("Inserir", "#4CAF50");
        Button btnAtualizar = EtiquetaCrud.botao("Atualizar", "#2196F3");
        Button btnExcluir = EtiquetaCrud.botao("Excluir", "#f44336");
        Button btnLimpar = EtiquetaCrud.botao("Limpar", "#FF9800");
        Button btnVoltar = EtiquetaCrud.botao("Voltar", "#9E9E9E");

        btnInserir.setOnAction(e -> executar(() -> {
            dao.adicionar(lerFormulario());
            return "Participação registrada!";
        }));
        btnAtualizar.setOnAction(e -> executar(() -> {
            if (!dao.atualizar(lerFormulario())) {
                throw new IllegalArgumentException("Não existe participação com esse ID.");
            }
            return "Participação atualizada!";
        }));
        btnExcluir.setOnAction(e -> executar(() -> {
            Integer id = Telas.lerInteiro(txtId, "ID");
            if (!Telas.confirmar("Excluir a participação " + id + "?")) {
                return null;
            }
            if (!dao.remover(id)) {
                throw new IllegalArgumentException("Não existe participação com esse ID.");
            }
            return "Participação excluída!";
        }));
        btnLimpar.setOnAction(e -> limpar());
        btnVoltar.setOnAction(e -> Telas.voltarAoMenu(stage));

        tabela.getColumns().add(Telas.coluna("ID", "id", 50));
        tabela.getColumns().add(Telas.coluna("Projeto", "nomeProjeto", 200));
        tabela.getColumns().add(Telas.coluna("Colaborador", "nomeColaborador", 180));
        tabela.getColumns().add(Telas.coluna("Papel", "papel", 150));
        tabela.getColumns().add(Telas.coluna("Entrada", "dataEntradaFormatada", 100));
        tabela.setPlaceholder(new Label("Nenhuma participação cadastrada"));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                txtId.setText(String.valueOf(nova.getId()));
                cbProjeto.setValue(nova.getProjeto());
                cbColaborador.setValue(nova.getColaborador());
                txtPapel.setText(nova.getPapel());
                dpEntrada.setValue(nova.getDataEntrada());
            }
        });
        recarregar();

        HBox botoes = new HBox(10, btnInserir, btnAtualizar, btnExcluir, btnLimpar, btnVoltar);
        botoes.setAlignment(Pos.CENTER);

        VBox raiz = new VBox(15, lblTitulo, form, botoes, tabela);
        raiz.setPadding(new Insets(20));
        return new Scene(raiz, 760, 620);
    }

    private Participacao lerFormulario() {
        return new Participacao(Telas.lerInteiro(txtId, "ID"), cbProjeto.getValue(), cbColaborador.getValue(),
                txtPapel.getText().trim(), dpEntrada.getValue());
    }

    private void executar(Supplier<String> acao) {
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
        cbProjeto.setValue(null);
        cbColaborador.setValue(null);
        txtPapel.clear();
        dpEntrada.setValue(LocalDate.now());
        recarregar();
    }

    private void recarregar() {
        tabela.setItems(FXCollections.observableArrayList(dao.listar()));
    }
}
