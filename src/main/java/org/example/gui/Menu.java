package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu {
    private JButton cadastroDeProdutosButton;
    private JButton painelDeVendasButton;
    private JPanel principalPanel;
    private JButton historicoDeVendasButton;
    private JButton cadastroDeFornecedoresButton;

    public Menu() {
        cadastroDeProdutosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyNextPanel("Cadastro de Produtos", new CadastroProdutos().getPrincipalProdutos());
            }
        });
        painelDeVendasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyNextPanel("Painel de Vendas", new PainelDeVendas().getPainelPrincipalVendas());
            }
        });
        cadastroDeFornecedoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyNextPanel("Cadastro de Fornecedores", new CadastroFornecedores().getPrincipalFornecedores());
            }
        });
        historicoDeVendasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyNextPanel("Historico de Vendas", new HistoricoDeVendas().getPrincipalHistorico());
            }
        });
    }

    public JPanel getPrincipalPanel() {
        return principalPanel;
    }

    private void applyNextPanel(String title, Container panel) {
        JFrame mainFrame = new JFrame(title);
        mainFrame.setContentPane(panel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

}
