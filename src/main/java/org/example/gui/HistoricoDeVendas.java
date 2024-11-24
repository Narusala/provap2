package org.example.gui;

import com.mysql.cj.util.StringUtils;
import org.example.dao.DBConnection;
import org.example.dto.ClienteCBDTO;
import org.example.dto.ProdutoDBDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class HistoricoDeVendas {
    private JPanel principalHistorico;
    private JTextField fldData1;
    private JTextField fldData2;
    private JComboBox cbCliente;
    private JComboBox cbProduto;
    private JButton filtrarButton;
    private JTable tblHistorico;
    private JScrollPane scrollPane1;

    public HistoricoDeVendas() {
        filtrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attTableProdutos();
            }
        });

        attTableProdutos();
        fillClienteComboBox();
        fillProdutosComboBox();
    }

    public JPanel getPrincipalHistorico() {
        return principalHistorico;
    }

    private void fillClienteComboBox() {
        String sql = "SELECT id_cliente, nome FROM CLIENTES";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            cbCliente.addItem(new ClienteCBDTO(null, null));
            while (rs.next()) {
                cbCliente.addItem(new ClienteCBDTO(rs.getInt("id_cliente"), rs.getString("nome")));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao buscar clientes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void fillProdutosComboBox() {
        String sql = "SELECT id_produto, nome FROM Produtos";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            cbProduto.addItem(new ProdutoDBDTO(null, null));
            while (rs.next()) {
                cbProduto.addItem(new ProdutoDBDTO(rs.getInt("id_produto"), rs.getString("nome")));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao buscar produtos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void attTableProdutos() {
        String[] colunas = {"ID Venda", "Forma de Pagamento", "Data da Venda", "Quantidade de Produtos", "Valor Total"};

        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);

        String sql = "select " +
                "v.id_venda, " +
                "v.forma_pagamento, " +
                "v.data_venda, " +
                "count(d.id_produto) qtd_produtos, " +
                "v.valor_total " +
                "from vendas v " +
                "inner join detalhesvenda d on v.id_venda = d.id_venda " +
                "where (? is null or exists(select 1 from detalhesvenda d2 where d2.id_produto = ?)) " +
                "and (? is null or (v.id_cliente = ?)) " +
                "and (? is null or (v.data_venda between ? and ?)) " +
                "group by v.id_venda, v.forma_pagamento, v.data_venda, v.valor_total";


        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cbProduto.getSelectedItem() == null || cbProduto.getSelectedItem().equals(new ProdutoDBDTO()) ? null : ((ProdutoDBDTO) cbProduto.getSelectedItem()).getId().toString());
            stmt.setString(2, cbProduto.getSelectedItem() == null || cbProduto.getSelectedItem().equals(new ProdutoDBDTO()) ? null : ((ProdutoDBDTO) cbProduto.getSelectedItem()).getId().toString());
            stmt.setString(3, cbCliente.getSelectedItem() == null || cbCliente.getSelectedItem().equals(new ClienteCBDTO()) ? null : ((ClienteCBDTO) cbCliente.getSelectedItem()).getId().toString());
            stmt.setString(4, cbCliente.getSelectedItem() == null || cbCliente.getSelectedItem().equals(new ClienteCBDTO()) ? null : ((ClienteCBDTO) cbCliente.getSelectedItem()).getId().toString());
            stmt.setDate(5, StringUtils.isEmptyOrWhitespaceOnly(fldData1.getText()) ? null : Date.valueOf(fldData1.getText()));
            stmt.setDate(6, StringUtils.isEmptyOrWhitespaceOnly(fldData1.getText()) ? null : Date.valueOf(fldData1.getText()));
            stmt.setDate(7, StringUtils.isEmptyOrWhitespaceOnly(fldData2.getText()) ? null : Date.valueOf(fldData2.getText()));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<Object> linha = new Vector<>();
                linha.add(rs.getInt("id_venda"));
                linha.add(rs.getString("forma_pagamento"));
                linha.add(rs.getString("data_venda"));
                linha.add(rs.getInt("qtd_produtos"));
                linha.add(rs.getDouble("valor_total"));
                modeloTabela.addRow(linha);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao buscar historico: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        tblHistorico.setModel(modeloTabela);
    }

}
