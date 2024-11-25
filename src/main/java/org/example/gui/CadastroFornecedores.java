package org.example.gui;

import org.example.dao.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class CadastroFornecedores {
    private JTextField fldNome;
    private JTextField fldCNPJ;
    private JTextField fldEndereco;
    private JTextField fldTelefone;
    private JTextField fldEmail;
    private JButton salvarButton;
    private JButton deletarButton;
    private JTable tblFornecedores;
    private JPanel principalFornecedores;
    private JScrollPane spFornecedores;

    public CadastroFornecedores() {

        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection con = new DBConnection().getConnection();
                try {
                    PreparedStatement sql = con.prepareStatement("INSERT INTO FORNECEDORES (nome, cnpj, endereco, telefone, email) VALUES (?,?,?,?,?)");
                    sql.setString(1, fldNome.getText());
                    sql.setString(2, fldCNPJ.getText());
                    sql.setString(3, fldEndereco.getText());
                    sql.setString(4, fldTelefone.getText());
                    sql.setString(5, fldEmail.getText());
                    sql.execute();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                attFornecedoresTable();
                JOptionPane.showMessageDialog(null, "Cadastrado com Sucesso!");
            }
        });
        deletarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String sql = "DELETE FROM FORNECEDORES WHERE id_fornecedor = ?";
                try (Connection conn = new DBConnection().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    Arrays.stream(tblFornecedores.getSelectedRows()).forEach(i -> {
                        try {
                            stmt.setInt(1, (int) tblFornecedores.getValueAt(i,0));
                            stmt.execute();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao deletar fornecedor: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }

                attFornecedoresTable();
                JOptionPane.showMessageDialog(null, "Deletado com Sucesso!");
            }
        });

        attFornecedoresTable();
    }

    public JPanel getPrincipalFornecedores() {
        return principalFornecedores;
    }

    private void attFornecedoresTable() {
        String[] colunas = {"ID", "Nome", "CNPJ", "Endereco", "Telefone", "Email",};

        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);

        String sql = "SELECT id_fornecedor, nome, cnpj, endereco, telefone, email FROM Fornecedores";


        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> linha = new Vector<>();
                linha.add(rs.getInt("id_fornecedor"));
                linha.add(rs.getString("nome"));
                linha.add(rs.getString("cnpj"));
                linha.add(rs.getString("endereco"));
                linha.add(rs.getString("telefone"));
                linha.add(rs.getString("email"));
                modeloTabela.addRow(linha);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao buscar fornecedores: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        tblFornecedores.setModel(modeloTabela);
    }

}
