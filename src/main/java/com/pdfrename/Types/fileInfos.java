package com.pdfrename.Types;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.pdfrename.PrintFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static com.pdfrename.PrintFiles.*;
import static com.pdfrename.Types.Types.*;

public class fileInfos {
    public static String directory() {
        String username = System.getProperty("user.name");
        return "C:\\Users\\" + username + "\\Desktop\\Comprovantes\\";
    }

    public static String directoryRenomeados() {
        String username = System.getProperty("user.name");
        return "C:\\Users\\" + username + "\\Desktop\\Comprovantes\\Renomeados\\";
    }

    public static String fileText(String path) throws IOException {
        try (PDDocument document = PDDocument.load(new File(path))) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
            document.close();
            return null;
        }
    }

    // APARTIR DO FILETEXT, COLETA QUAL O TIPO DO COMPROVANTE, POSSUINDO APENAS 2
    // TIPOS
    public static Integer fileType(String path) throws IOException {
        String text = fileText(path);
        assert text != null;
        if (text.startsWith("Ass")) {
            return 1;
        } else if (text.startsWith("Comprovante de Recebimento") || text.startsWith("Comprovante de Pagamento")) {
            return 3;
        } else {
            return 2;
        }
    }

    public static void renameFiles() {
        criadorRepositorio();

        // VARIAVEL "ARMAZENAMENTO" PRECISA PARA EVITAR NULLPOINTEREXEPCETION
        PrintFiles armazenamento = new PrintFiles();
        File diretorio = new File(directory());
        File[] arquivos = diretorio.listFiles();
        short contadorComprovante = 0;
        if (arquivos != null) {
            for (int i = 0; i < arquivos.length; i++) {
                String resultadoRenomeacao = "";
                File arquivo = arquivos[i];
                if (arquivo.isFile()) {
                    String nomeTemporario = "comprovante " + (i + 1) + ".pdf";
                    File fileTemporario = new File(arquivo.getParent(), nomeTemporario);

                    boolean renomeadoComSucesso = arquivo.renameTo(fileTemporario);

                    if (renomeadoComSucesso) {
                        String nomeComInfos = directory() + nomeTemporario;
                        String dadosExtraidos = "";
                        try {
                            switch (fileInfos.fileType(fileTemporario.getPath())) {
                                case 1 -> dadosExtraidos = pegarInfosTipo1(nomeComInfos);
                                case 2 -> dadosExtraidos = pegarInfosTipo2(nomeComInfos);
                                case 3 -> dadosExtraidos = pegarInfosTipo3(nomeComInfos);
                            }

                            resultadoRenomeacao = renomeacaoFinal(fileTemporario, dadosExtraidos, nomeTemporario);
                            contadorComprovante++;

                        } catch (Exception e) {
                            resultadoRenomeacao = "Algo deu errado" + nomeTemporario + ": " + e.getMessage();
                        }
                    } else {
                        resultadoRenomeacao = "Falha ao renomear o arquivo " + arquivo.getName();
                    }
                }
                if (!resultadoRenomeacao.equals("")) {
                    PrintFiles.adicionarStrings(resultadoRenomeacao);
                }
            }
        }
        exibirRelatorio(contadorComprovante);
        abrirDiretorio(contadorComprovante);
    }

    // CLASSE QUE RENOMEIA O ARQUIVO COM AS INFORMAÇÕES EXTRAIDAS
    private static String renomeacaoFinal(File fileTemporario, String dadosExtraidos, String nomeTemporario) {
        File arquivoFinal = new File(directoryRenomeados(), dadosExtraidos + ".pdf");
        String respostaReturn = dadosExtraidos;

        if (arquivoFinal.exists()) {
            // Se o arquivo final já existe, adicione ' (i)' ao nome
            int contador = 1;
            do {
                respostaReturn = dadosExtraidos + " (" + contador + ").pdf";
                arquivoFinal = new File(directoryRenomeados(), respostaReturn);
                contador++;
            } while (arquivoFinal.exists());
        }

        if (fileTemporario.renameTo(arquivoFinal)) {
            respostaReturn = "Arquivo " + nomeTemporario + " renomeado com sucesso para " + respostaReturn;
        } else {
            respostaReturn = "Erro ao renomear o arquivo " + nomeTemporario;
        }
        return respostaReturn;

    }

    // CRIA UM DIRETÓRIO NO WINDOWS DO USUÁRIO PARA SALVAR O TXT
    private static void criadorRepositorio() {
        Path directoryPath = Paths.get(directoryRenomeados());

        // Verifica se o diretório já existe
        if (!Files.exists(directoryPath)) {
            try {
                // Cria o diretório
                Files.createDirectories(directoryPath);
                System.out.println("Diretório criado:  " + directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
