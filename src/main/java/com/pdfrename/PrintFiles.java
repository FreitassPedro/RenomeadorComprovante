package com.pdfrename;

import static com.pdfrename.Types.fileInfos.directory;
import static com.pdfrename.Types.fileInfos.directoryRenomeados;

import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

public class PrintFiles {

    public static ArrayList<String> relatorioStrings;

    public PrintFiles() {
        relatorioStrings = new ArrayList<>();
    }

    public static void adicionarStrings(String novaString) {
        relatorioStrings.add(novaString);
    }

    public static void exibirRelatorio(short contadorComprovante) {
        SwingUtilities.invokeLater((Runnable) () -> {
            JFrame frame = new JFrame("Relatório de Mudanças");
            JTextArea textArea = new JTextArea();


            // Definindo a fonte e o tamanho
            Font novaFonte = new Font("Arial", Font.PLAIN, 14);
            textArea.setFont(novaFonte);
            
            boolean pastaVazia = abrirDiretorio(contadorComprovante);

            if (pastaVazia) {
                textArea.append(mensagemPastaVazia());
            } else {
                for (String str : relatorioStrings) {
                    textArea.append(str + "\n");
                }
                textArea.append("\n");
                textArea.append(contadorComprovante + " Comprovantes foram renomeados! Operação concluída.");
            }

            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane);

            frame.setSize(1000, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

        });
    }


    private static String mensagemPastaVazia() {
        return "Nenhum arquivo foi encontrado! "
                + "\nPor favor, adicione nesta pasta aberta."
                + "\n "
                + "\n" + directory();
    }

    // ABRE DIRETÓRIO ONDE PRECISA COLOCAR OS ARQUIVOS
    public static boolean abrirDiretorio(short contadorComprovante) {
        // Verifica se o Desktop é suportado
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                // Abre o Explorer na pasta especificada
                if (contadorComprovante != 0) {
                    desktop.open(new File(directoryRenomeados()));
                    return false;
                } else {
                    desktop.open(new File(directory()));
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop não é suportado.");
        }
        return true;
    }

}
