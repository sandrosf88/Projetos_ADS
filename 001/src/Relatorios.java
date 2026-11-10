import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Relatorios {

    private Conexao conexao = new Conexao();

    public void gerarRelC() {
        String sql = "SELECT id, nome, preco, quantidade FROM produtos";
        String rel = "RelatorioCompleto.csv";

        // Try-with-resources garante o fechamento automático de tudo
        try (Connection conex = conexao.conectar();
             PreparedStatement c1 = conex.prepareStatement(sql);
             ResultSet resultado = c1.executeQuery();
             BufferedWriter writer = new BufferedWriter(new FileWriter(rel, false))) {

            writer.write("id,nome,preco,quantidade");
            writer.newLine();

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nome = resultado.getString("nome");
                double preco = resultado.getDouble("preco");
                int quantidade = resultado.getInt("quantidade");

                String linhaCsv = id + "," + nome + "," + preco + "," + quantidade;
                writer.write(linhaCsv);
                writer.newLine();
            }

            System.out.println("\nRelatório completo gerado com sucesso em '" + rel + "'!\n");

        } catch (SQLException e) {
            System.err.println("Falha ao gerar relatório no banco: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Falha ao criar arquivo CSV: " + e.getMessage());
        }
    }

    public void gerarRelQ(int qtdC, String escolha) {
        String sql = "";

        // .equalsIgnoreCase aceita "acima", "Acima", "ACIMA", etc.
        if (escolha.equalsIgnoreCase("acima")) {
            sql = "SELECT id, nome, preco, quantidade FROM produtos WHERE quantidade > ?";
        } else {
            sql = "SELECT id, nome, preco, quantidade FROM produtos WHERE quantidade < ?";
        }

        String rel = "RelatorioPorQuantidade.csv";

        // Mudado para false para não duplicar cabeçalhos e dados velhos
        try (Connection conex = conexao.conectar();
             PreparedStatement c1 = conex.prepareStatement(sql)) {
             
            c1.setInt(1, qtdC);
            
            try (ResultSet resultado = c1.executeQuery();
                 BufferedWriter writer = new BufferedWriter(new FileWriter(rel, false))) {

                writer.write("id,nome,preco,quantidade");
                writer.newLine();

                while (resultado.next()) {
                    int id = resultado.getInt("id");
                    String nome = resultado.getString("nome");
                    double preco = resultado.getDouble("preco");
                    int quantidade = resultado.getInt("quantidade");

                    String linhaCsv = id + "," + nome + "," + preco + "," + quantidade;
                    writer.write(linhaCsv);
                    writer.newLine();
                }

                System.out.println("\nRelatório por quantidade gerado com sucesso em '" + rel + "'!\n");
            }

        } catch (SQLException e) {
            System.err.println("Falha ao gerar relatório no banco: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Falha ao criar arquivo CSV: " + e.getMessage());
        }
    }
}
