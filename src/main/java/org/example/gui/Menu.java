package org.example.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class Menu {
    private JButton cadastroDeProdutosButton;
    private JButton painelDeVendasButton;
    private JPanel principalPanel;
    private JButton historicoDeVendasButton;

    public Menu() {
        cadastroDeProdutosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame mainFrame = new JFrame("Cadastro de Produtos");
                mainFrame.setContentPane(new CadastroProdutos().getPrincipalProdutos());
                mainFrame.pack();
                mainFrame.setVisible(true);
            }
        });

        painelDeVendasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame mainFrame = new JFrame("Painel de Vendas");
                mainFrame.setContentPane(new PainelDeVendas().getPainelPrincipalVendas());
                mainFrame.pack();
                mainFrame.setVisible(true);
            }
        });
    }

    public JPanel getPrincipalPanel() {
        return principalPanel;
    }

}
