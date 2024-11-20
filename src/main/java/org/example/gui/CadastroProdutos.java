package org.example.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CadastroProdutos {

    private JPanel PrincipalProdutos;
    private JTextField fldNome;
    private JTextField fldCode;
    private JTextField fldPrecoCompra;
    private JTextField fldCategoria;
    private JButton salvarButton;
    private JTextField fldPrecoVenda;

    private Connection conexao;

    public CadastroProdutos() {
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection con = getConexao();
                try {
                    PreparedStatement sql = con.prepareStatement("INSERT INTO PRODUTOS (nome, codigo_barras, preco_venda, preco_compra, categoria, id_fornecedor) VALUES (?,?,?,?,?,1)");
                    sql.setString(1, fldNome.getText());
                    sql.setString(2, fldCode.getText());
                    sql.setString(3, fldPrecoVenda.getText());
                    sql.setString(4, fldPrecoCompra.getText());
                    sql.setString(5, fldCategoria.getText());
                    sql.execute();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(null, "Foi com Sucesso!");
            }
        });
    }

    public JPanel getPrincipalProdutos() {
        return PrincipalProdutos;
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
