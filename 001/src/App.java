import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Optional;

public class App extends Application {

    // Instanciação das classes de controle e banco
    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private Relatorios relatorios = new Relatorios();

    // Componentes da Interface Gráfica (Nodes)
    private TextField txtId = new TextField();
    private TextField txtNome = new TextField();
    private TextField txtPreco = new TextField();
    private TextField txtQuantidade = new TextField();
    private TextArea txtConsoleLog = new TextArea();

    public static void main(String[] args) {
        launch(args); 
    }

    @Override
    public void start(Stage palcoPrincipal) {
        palcoPrincipal.setTitle("Gerenciamento de Estoque - JavaFX");

        // 1. FORMULÁRIO DE ENTRADA (GridPane para alinhar Labels e Fields)
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(15));

        // Configuração dos campos
        txtId.setPromptText("Gerado automaticamente / Necessário para Atualizar ou Excluir");
        txtId.setPrefWidth(300);

        formulario.add(new Label("ID do Produto:"), 0, 0);
        formulario.add(txtId, 1, 0);
        formulario.add(new Label("Nome:"), 0, 1);
        formulario.add(txtNome, 1, 1);
        formulario.add(new Label("Preço R$:"), 0, 2);
        formulario.add(txtPreco, 1, 2);
        formulario.add(new Label("Quantidade:"), 0, 3);
        formulario.add(txtQuantidade, 1, 3);

        // 2. BOTÕES DE OPERAÇÃO DO ESTOQUE (HBox horizontal)
        Button btnInserir = new Button("Inserir");
        Button btnBuscar = new Button("Buscar por Nome");
        Button btnAtualizar = new Button("Atualizar Estoque");
        Button btnExcluir = new Button("Excluir");

        HBox linhaBotoesAcoes = new HBox(10, btnInserir, btnBuscar, btnAtualizar, btnExcluir);
        linhaBotoesAcoes.setAlignment(Pos.CENTER);
        linhaBotoesAcoes.setPadding(new Insets(10, 0, 10, 0));

        // 3. SEÇÃO DE RELATÓRIOS (HBox horizontal)
        Button btnRelCompleto = new Button("Relatório Completo (CSV)");
        Button btnRelQuantidade = new Button("Relatório por Qtd (CSV)");
        ComboBox<String> cbFiltroQtd = new ComboBox<>();
        cbFiltroQtd.getItems().addAll("Acima", "Abaixo");
        cbFiltroQtd.setValue("Acima");

        HBox linhaBotoesRelatorios = new HBox(10, btnRelCompleto, cbFiltroQtd, btnRelQuantidade);
        linhaBotoesRelatorios.setAlignment(Pos.CENTER);
        linhaBotoesRelatorios.setPadding(new Insets(0, 0, 15, 0));

        // 4. ÁREA DE TEXTO/CONSOLE (Para exibir resultados das buscas)
        txtConsoleLog.setEditable(false);
        txtConsoleLog.setPromptText("Os resultados das buscas e logs do sistema aparecerão aqui...");
        txtConsoleLog.setPrefHeight(200);

        // 5. LAYOUT PRINCIPAL (VBox para empilhar tudo na vertical)
        VBox layoutPrincipal = new VBox(10, formulario, linhaBotoesAcoes, linhaBotoesRelatorios, new Label("Resultados / Logs:"), txtConsoleLog);
        layoutPrincipal.setPadding(new Insets(15));

        // --- CONFIGURAÇÃO DOS EVENTOS (Ações dos Botões via Expressões Lambda) ---

        // Ação: Inserir Produto
        btnInserir.setOnAction(event -> {
            try {
                String nome = txtNome.getText();
                double preco = Double.parseDouble(txtPreco.getText());
                int qtd = Integer.parseInt(txtQuantidade.getText());

                Produto p = new Produto(nome, preco, qtd);
                produtoDAO.inserir(p);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto inserido com sucesso!");
                limparCampos();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Preço e Quantidade devem ser valores numéricos válidos.");
            }
        });

        // Ação: Buscar por Nome
        btnBuscar.setOnAction(event -> {
            String nomeBusca = txtNome.getText();
            ArrayList<Produto> lista = produtoDAO.buscarPorNome(nomeBusca);
            
            if (lista.isEmpty()) {
                txtConsoleLog.setText("Nenhum produto encontrado com o nome: " + nomeBusca);
            } else {
                StringBuilder sb = new StringBuilder("--- PRODUTOS ENCONTRADOS ---\n\n");
                for (Produto p : lista) {
                    sb.append("ID: ").append(p.getId()).append(" | ")
                      .append("Nome: ").append(p.getNome()).append(" | ")
                      .append("Preço: R$ ").append(String.format("%.2f", p.getPreco())).append(" | ")
                      .append("Qtd: ").append(p.getQuantidade()).append(" unidades\n");
                }
                txtConsoleLog.setText(sb.toString());
            }
        });

        // Ação: Atualizar Estoque (Requer ID e Quantidade)
        btnAtualizar.setOnAction(event -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                int novaQtd = Integer.parseInt(txtQuantidade.getText());

                boolean atualizou = produtoDAO.atualizarEstoque(id, novaQtd);
                if (atualizou) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Estoque do ID " + id + " atualizado!");
                    limparCampos();
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Nenhum produto encontrado com o ID informado.");
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Campos Inválidos", "Preencha os campos 'ID do Produto' e 'Quantidade' com números.");
            }
        });

        // Ação: Excluir Produto (Com confirmação de janela pop-up)
        btnExcluir.setOnAction(event -> {
            try {
                int id = Integer.parseInt(txtId.getText());

                // Alerta de confirmação conforme o slide 9 do material
                Alert alertaConf = new Alert(Alert.AlertType.CONFIRMATION);
                alertaConf.setTitle("Confirmar Exclusão");
                alertaConf.setHeaderText("Excluir produto permanentemente?");
                alertaConf.setContentText("Deseja mesmo apagar o registro de ID: " + id + "?");

                Optional<ButtonType> resposta = alertaConf.showAndWait();
                if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
                    boolean deletou = produtoDAO.deletar(id);
                    if (deletou) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto removido com sucesso.");
                        limparCampos();
                    } else {
                        mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Nenhum produto localizado com o ID: " + id);
                    }
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "ID Inválido", "Insira um número de ID válido para realizar a exclusão.");
            }
        });

        // Ação: Gerar Relatório Completo
        btnRelCompleto.setOnAction(event -> {
            relatorios.gerarRelC();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Relatório Exportado", "Arquivo 'RelatorioCompleto.csv' gerado na pasta raiz do projeto.");
        });

        // Ação: Gerar Relatório por Filtro de Quantidade
        btnRelQuantidade.setOnAction(event -> {
            try {
                int qtdLimite = Integer.parseInt(txtQuantidade.getText());
                String filtro = cbFiltroQtd.getValue().toLowerCase(); // "acima" ou "abaixo"

                relatorios.gerarRelQ(qtdLimite, filtro);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Relatório Exportado", "Arquivo 'RelatorioPorQuantidade.csv' gerado com sucesso.");
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Quantidade Ausente", "Digite um valor numérico no campo 'Quantidade' para servir de limite ao relatório.");
            }
        });

        // Montagem do Cenário e exibição da Janela
        Scene cena = new Scene(layoutPrincipal, 520, 560);
        palcoPrincipal.setScene(cena);
        palcoPrincipal.show();
    }

    // Método utilitário para criar caixas de diálogo rápidas (Slides 5 a 8)
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    // Limpa as caixas de texto após operações bem-sucedidas
    private void limparCampos() {
        txtId.clear();
        txtNome.clear();
        txtPreco.clear();
        txtQuantidade.clear();
    }
}
