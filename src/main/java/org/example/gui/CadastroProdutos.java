package org.example.gui;

import org.example.dao.DBConnection;
import org.example.dto.FornecedorCBDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

public class CadastroProdutos {

    private JPanel PrincipalProdutos;
    private JTextField fldNome;
    private JTextField fldCode;
    private JTextField fldPrecoCompra;
    private JTextField fldCategoria;
    private JButton salvarButton;
    private JTextField fldPrecoVenda;
    private JButton deletarButton;
    private JTable tableProdutos;
    private JComboBox cbFornecedor;
    private JTextField fldQtdEstoque;

    public CadastroProdutos() {
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection con = new DBConnection().getConnection();
                try {
                    PreparedStatement sql = con.prepareStatement("INSERT INTO PRODUTOS (nome, codigo_barras, preco_venda, preco_compra, categoria, id_fornecedor) VALUES (?,?,?,?,?,?)");
                    sql.setString(1, fldNome.getText());
                    sql.setString(2, fldCode.getText());
                    sql.setBigDecimal(3,  new BigDecimal(fldPrecoVenda.getText().replace(",",".")));
                    sql.setBigDecimal(4, new BigDecimal(fldPrecoCompra.getText().replace(",",".")));
                    sql.setString(5, fldCategoria.getText());
                    sql.setInt(6, ((FornecedorCBDTO) cbFornecedor.getSelectedItem()).getId());
                    sql.execute();
                    PreparedStatement sqlEstoque = con.prepareStatement("INSERT INTO ESTOQUE (id_produto, quantidade, ultima_atualizacao) VALUES (LAST_INSERT_ID(), ?, NOW())");
                    sqlEstoque.setInt(1,Integer.parseInt(fldQtdEstoque.getText()));
                    sqlEstoque.execute();

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                attProductTable();
                JOptionPane.showMessageDialog(null, "Cadastrado com Sucesso!");
            }
        });
        deletarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = "DELETE FROM PRODUTOS WHERE id_produto = ?";
                String estoqueSql = "DELETE FROM ESTOQUE WHERE id_produto = ?";
                try (Connection conn = new DBConnection().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(estoqueSql)) {

                    Arrays.stream(tableProdutos.getSelectedRows()).forEach(i -> {
                        try {
                            stmt.setInt(1, (int) tableProdutos.getValueAt(i,0));
                            stmt.execute();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao deletar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
                try (Connection conn = new DBConnection().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    Arrays.stream(tableProdutos.getSelectedRows()).forEach(i -> {
                        try {
                            stmt.setInt(1, (int) tableProdutos.getValueAt(i,0));
                            stmt.execute();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao deletar produto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }

                attProductTable();
                JOptionPane.showMessageDialog(null, "Deletado com Sucesso!");
            }
        });
        fillFornecedorComboBox();
        attProductTable();
    }

    public JPanel getPrincipalProdutos() {
        return PrincipalProdutos;
    }

    private void fillFornecedorComboBox() {
        String sql = "SELECT id_fornecedor, nome FROM Fornecedores";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cbFornecedor.addItem(new FornecedorCBDTO(rs.getInt("id_fornecedor"), rs.getString("nome")));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao buscar fornecedores: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void attProductTable() {
        String[] colunas = {"ID", "Nome", "Código de Barras", "Preço Venda", "Preço Compra", "Categoria", "Estoque Mínimo"};

        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);

        String sql = "SELECT id_produto, nome, codigo_barras, preco_venda, preco_compra, categoria, estoque_minimo FROM Produtos";


        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

        tableProdutos.setModel(modeloTabela);
    }

}
