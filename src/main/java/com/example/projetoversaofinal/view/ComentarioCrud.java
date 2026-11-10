package com.example.projetoversaofinal.view;

import com.example.projetoversaofinal.dao.ComentarioDAO;
import com.example.projetoversaofinal.model.Atividade;
import com.example.projetoversaofinal.model.Colaborador;
import com.example.projetoversaofinal.model.Comentario;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.function.Supplier;

// CRUD de Comentario: um colaborador comenta uma atividade.
public class ComentarioCrud {

    private final Stage stage;
    private final ComentarioDAO dao = new ComentarioDAO();

    private final TextField txtId = new TextField();
    private final ComboBox<Atividade> cbAtividade = Telas.comboAtividades();
    private final ComboBox<Colaborador> cbAutor = Telas.comboColaboradores("Quem está comentando?");
    private final TextArea txtTexto = new TextArea();
    private final TableView<Comentario> tabela = new TableView<>();

    // Na atualização, a data/hora original do comentário é mantida.
    private LocalDateTime dataHoraCarregada;

    public ComentarioCrud(Stage stage) {
        this.stage = stage;
    }

    public Scene criarCena() {
        Label lblTitulo = new Label("💬 Comentários em Atividades");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        txtId.setPromptText("Ex.: 1");
        txtTexto.setPromptText("Escreva o comentário");
        txtTexto.setPrefRowCount(3);
        txtTexto.setWrapText(true);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("ID:"), txtId);
        form.addRow(1, new Label("Atividade:"), cbAtividade);
        form.addRow(2, new Label("Autor:"), cbAutor);
        form.addRow(3, new Label("Texto:"), txtTexto);

        Button btnInserir = EtiquetaCrud.botao("Inserir", "#4CAF50");
        Button btnAtualizar = EtiquetaCrud.botao("Atualizar", "#2196F3");
        Button btnExcluir = EtiquetaCrud.botao("Excluir", "#f44336");
        Button btnLimpar = EtiquetaCrud.botao("Limpar", "#FF9800");
        Button btnVoltar = EtiquetaCrud.botao("Voltar", "#9E9E9E");

        btnInserir.setOnAction(e -> executar(() -> {
            dao.adicionar(lerFormulario(LocalDateTime.now()));
            return "Comentário publicado!";
        }));
        btnAtualizar.setOnAction(e -> executar(() -> {
            LocalDateTime dataHora = dataHoraCarregada != null ? dataHoraCarregada : LocalDateTime.now();
            if (!dao.atualizar(lerFormulario(dataHora))) {
                throw new IllegalArgumentException("Não existe comentário com esse ID.");
            }
            return "Comentário atualizado!";
        }));
        btnExcluir.setOnAction(e -> executar(() -> {
            Integer id = Telas.lerInteiro(txtId, "ID");
            if (!Telas.confirmar("Excluir o comentário " + id + "?")) {
                return null;
            }
            if (!dao.remover(id)) {
                throw new IllegalArgumentException("Não existe comentário com esse ID.");
            }
            return "Comentário excluído!";
        }));
        btnLimpar.setOnAction(e -> limpar());
        btnVoltar.setOnAction(e -> Telas.voltarAoMenu(stage));

        tabela.getColumns().add(Telas.coluna("ID", "id", 50));
        tabela.getColumns().add(Telas.coluna("Atividade", "nomeAtividade", 170));
        tabela.getColumns().add(Telas.coluna("Autor", "nomeAutor", 140));
        tabela.getColumns().add(Telas.coluna("Comentário", "texto", 280));
        tabela.getColumns().add(Telas.coluna("Data/hora", "dataHoraFormatada", 120));
        tabela.setPlaceholder(new Label("Nenhum comentário cadastrado"));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                txtId.setText(String.valueOf(novo.getId()));
                cbAtividade.setValue(novo.getAtividade());
                cbAutor.setValue(novo.getAutor());
                txtTexto.setText(novo.getTexto());
                dataHoraCarregada = novo.getDataHora();
            }
        });
        recarregar();

        HBox botoes = new HBox(10, btnInserir, btnAtualizar, btnExcluir, btnLimpar, btnVoltar);
        botoes.setAlignment(Pos.CENTER);

        VBox raiz = new VBox(15, lblTitulo, form, botoes, tabela);
        raiz.setPadding(new Insets(20));
        return new Scene(raiz, 820, 640);
    }

    private Comentario lerFormulario(LocalDateTime dataHora) {
        return new Comentario(Telas.lerInteiro(txtId, "ID"), txtTexto.getText().trim(), dataHora,
                cbAtividade.getValue(), cbAutor.getValue());
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
        cbAtividade.setValue(null);
        cbAutor.setValue(null);
        txtTexto.clear();
        dataHoraCarregada = null;
        recarregar();
    }

    private void recarregar() {
        tabela.setItems(FXCollections.observableArrayList(dao.listar()));
    }
}
