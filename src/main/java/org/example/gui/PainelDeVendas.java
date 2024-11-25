package org.example.gui;

import org.example.dao.DBConnection;
import org.example.dto.FormaPagamentoDTO;
import org.example.dto.FornecedorCBDTO;
import org.example.dto.ItemsVendaDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private JComboBox cbFormasPago;
    private DefaultTableModel modelItemVenda = new DefaultTableModel(new String[]{"ID Produto", "Nome Produto", "Código de Barras", "Quantidade", "Preço Unitario"}, 0);


    public PainelDeVendas() {
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attTableProdutos();
            }
        });
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedLines = tblProdutos.getSelectedRows();

                for (int i = 0; i < selectedLines.length; i++) {
                    // Obter os dados da linha selecionada
                    int linha = selectedLines[i];
                    Object[] dadosSelecionados = new Object[modelItemVenda.getColumnCount()];

                    dadosSelecionados[0] = tblProdutos.getModel().getValueAt(linha, 0);
                    dadosSelecionados[1] = tblProdutos.getModel().getValueAt(linha, 1);
                    dadosSelecionados[2] = tblProdutos.getModel().getValueAt(linha, 2);
                    dadosSelecionados[3] = 1;
                    dadosSelecionados[4] = tblProdutos.getModel().getValueAt(linha, 3);

                    modelItemVenda.addRow(dadosSelecionados);
                }

                tblItensVenda.setModel(modelItemVenda);
                calculaTotal();
            }
        });

        //carrega forma de pagamentos
        List<FormaPagamentoDTO> listFormasPago = Arrays.asList(
            new FormaPagamentoDTO("Dinheiro"),
            new FormaPagamentoDTO("Cartão de Débito"),
            new FormaPagamentoDTO("Cartão de Crédito"),
            new FormaPagamentoDTO("PIX")
        );
        listFormasPago.forEach(fm -> cbFormasPago.addItem(fm));

        // inicia tabela de itens da venda
        tblItensVenda.setModel(modelItemVenda);

        //carrega tabela de produtos
        attTableProdutos();
        finalizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ItemsVendaDTO> itensVenda = new ArrayList<>();
                for (int i = 0; i < tblItensVenda.getRowCount(); i++) {
                    itensVenda.add(new ItemsVendaDTO(
                            (Integer) tblItensVenda.getModel().getValueAt(i,0),
                            (String) tblItensVenda.getModel().getValueAt(i,1),
                            Integer.valueOf((String) tblItensVenda.getModel().getValueAt(i,2)),
                            (Integer) tblItensVenda.getModel().getValueAt(i,3),
                            new BigDecimal(String.valueOf(tblItensVenda.getModel().getValueAt(i,    4)))
                    ));
                }

                Connection con = new DBConnection().getConnection();
                try {
                    PreparedStatement sql = con.prepareStatement("INSERT INTO VENDAS (data_venda, forma_pagamento, valor_total, id_cliente) VALUES (NOW(), ?, ?, 1)");
                    sql.setString(1, cbFormasPago.getSelectedItem().toString());
                    sql.setDouble(2, Double.valueOf(txtTotal.getText()));
                    sql.execute();

                    itensVenda.forEach(item -> {
                        try {
                            PreparedStatement sqlDetalheVenda = con.prepareStatement("INSERT INTO DETALHESVENDA (id_venda, id_produto, quantidade, preco_unitario) VALUES ((select max(id_venda) from vendas), ?, ?, ?)");
                            sqlDetalheVenda.setInt(1, item.getIdProduto());
                            sqlDetalheVenda.setInt(2, item.getQuantidade());
                            sqlDetalheVenda.setBigDecimal(3, item.getPrecoUnitario());
                            sqlDetalheVenda.execute();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(null, "Venda realizada com Sucesso!");
            }
        });
    }

    public JPanel getPainelPrincipalVendas() {
        return painelPrincipalVendas;
    }

    private void calculaTotal() {
        int qtdItens = tblItensVenda.getRowCount();
        Double totalvalue = 0.00D;
        for (int i = 0; i < qtdItens; i++) {
            totalvalue += (Double) tblItensVenda.getModel().getValueAt(i,4);
        }
        txtTotal.setText(totalvalue.toString());
    }

    private void attTableProdutos() {
        String[] colunas = {"ID", "Nome", "Código de Barras", "Preço Venda", "Preço Compra", "Categoria", "Estoque Mínimo"};

        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);

        String sql = "SELECT id_produto, nome, codigo_barras, preco_venda, preco_compra, categoria, estoque_minimo " +
                "FROM Produtos " +
                "WHERE nome LIKE ? OR codigo_barras LIKE ?";


        try (Connection conn = new DBConnection().getConnection();
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

}
