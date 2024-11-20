package org.example.gui;

import com.mysql.cj.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class PainelDeVendas {
    private JTextField fldBuscaProduto;
    private JTable tblItensVenda;
    private JLabel txtTotal;
    private JTable tblProdutos;
    private JButton adicionarButton;
    private JButton finalizarButton;
    private JButton buscarButton;
    private JPanel painelPrincipalVendas;
    private JComboBox comboBox1;

    private Connection conexao;

    public PainelDeVendas() {
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] colunas = {"ID", "Nome", "Código de Barras", "Preço Venda", "Preço Compra", "Categoria", "Estoque Mínimo"};

                DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);

                String sql = "SELECT id_produto, nome, codigo_barras, preco_venda, preco_compra, categoria, estoque_minimo " +
                        "FROM Produtos " +
                        "WHERE nome LIKE ? OR codigo_barras LIKE ?";


                try (Connection conn = getConexao();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    String parametroFormatado = "%" + fldBuscaProduto.getText() + "%";
                    stmt.setString(1, parametroFormatado); // Para o nome
                    stmt.setString(2, parametroFormatado); // Para o código de barras

                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        Vector<Object> linha = new Vector<>();
                        linha.add(rs.getInt("id_produto"));       // ID
                        linha.add(rs.getString("nome"));          // Nome
                        linha.add(rs.getString("codigo_barras")); // Código de Barras
                        linha.add(rs.getDouble("preco_venda"));   // Preço Venda
                        linha.add(rs.getDouble("preco_compra"));  // Preço Compra
                        linha.add(rs.getString("categoria"));     // Categoria
                        linha.add(rs.getInt("estoque_minimo"));   // Estoque Mínimo
                        modeloTabela.addRow(linha);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao buscar produtos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }

                tblProdutos.setModel(modeloTabela);
            }
        });
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public JPanel getPainelPrincipalVendas() {
        return painelPrincipalVendas;
    }

    public Connection getConexao() {
        Connection con;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermercado", "super", "super");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return con;
    }

}
