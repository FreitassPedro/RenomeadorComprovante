package com.pdfrename.Types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Types {
    public static String pegarInfosTipo1(String caminhoArquivo) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] linhas = fileInfos.fileText(caminhoArquivo).split("\\r?\\n");

        String valor = "";
        String nomeDestinatario = "";
        String dataPagamento = "";
        String nomeDestinatarioAlt = "";

        for (String linha : linhas) {
            if (!linha.trim().isEmpty()) {
                lines.add(linha);
            }
        }
        String[] chavesDatas = {"Data do Pagamento:", "Data da Transação:"};
        String[] chavesPagamentos = {"Nome Destinatário:", "Razão Social do Beneficiário:", "Favorecido:", "Empresa:"};
        String[] chavesValores = {"Valor Total (R$):", "Valor Pago (R$):"};
        String[] chavesPagamentosAlt = {"Motivo Transferência:", "Descrição de Pagamento:", "Descrição do Pagamento:", "Tipo de Documento:"};
        while (dataPagamento.equals("") && valor.equals("") && nomeDestinatario.equals("")) {
            for (String line : lines) {
                String[] fields = line.split(": ");
                String prefixo = fields[0] + ":";

                for (String chave : chavesDatas) {
                    if (prefixo.equals(chave)) {
                        dataPagamento = fields[1].replace("/", ".");
                        break;
                    }
                }
                for (String chave : chavesPagamentos) {
                    for (String chaveAlt : chavesPagamentosAlt) {
                        if (prefixo.equals(chave)) {
                            nomeDestinatario = fields[1];
                            break;
                        }
                        if (prefixo.equals(chaveAlt)) {
                            nomeDestinatarioAlt = fields[1];
                        }
                    }
                    if (nomeDestinatario.equals("")) {
                        nomeDestinatario = nomeDestinatarioAlt;
                    }
                }
                for (String chave : chavesValores) {
                    if (prefixo.equals(chave)) {
                        valor = fields[1];
                        break;
                    }
                }
            }
        }
        return dataPagamento + " R$ " + valor + " - " + nomeDestinatario.toUpperCase().trim();
    }

    //EXTRAI AS INFORMAÇÕES SE FOR O TIPO 2
    public static String pegarInfosTipo2(String caminhoArquivo) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] linhas = fileInfos.fileText(caminhoArquivo).split("\\r?\\n");

        String valor = "";
        String nomeDestinatario = "";
        String nomeDestinatarioAlt = "";
        String dataPagamento = "";
        String numeroDocumento = "";

        for (String linha : linhas) {
            if (!linha.trim().isEmpty()) {
                lines.add(linha);
            }
        }
        String[] chavesPagamentos = {"Valor Pago (R$):", "Valor (R$):", "Valor Total (R$):", "Valor Transferido (R$):", "Valor a Transferir (R$):"};
        String[] chavesDatas = {"Data do Pagamento:", "Data da Transação:", "Data Transferência:"};
        String[] chavesNomes = {"Razão Social do Beneficiário:", "Favorecido:", "Empresa:"};
        String[] chavesNomesAlt = {"Motivo Transferência:", "Descrição de Pagamento:", "Descrição do Pagamento:"};
        while (dataPagamento.equals("") && valor.equals("") && nomeDestinatario.equals("")) {
            for (String line : lines) {
                for (String chave : chavesPagamentos) {
                    if (line.contains(chave)) {
                        int index = line.indexOf(chave);
                        valor = line.substring(0, index).trim();
                        break;
                    }
                }
                for (String chave : chavesDatas) {
                    if (line.contains(chave)) {
                        dataPagamento = line.substring(0, line.indexOf(chave));
                        dataPagamento = dataPagamento.replace("/", ".");
                        break;
                    }
                }

                for (String chave : chavesNomes) {
                    for (String chaveAlt : chavesNomesAlt) {
                        if (line.contains(chave)) {
                            nomeDestinatario = line.substring(0, line.indexOf(chave));
                            break;
                        }
                        if (line.contains(chaveAlt)) {
                            nomeDestinatarioAlt = line.substring(0, line.indexOf(chaveAlt));
                            break;
                        }
                    }
                    if (line.contains("Número do Documento:")) {
                        numeroDocumento = line.substring(0, line.indexOf("Número do Documento:"));
                    }
                }
                if (nomeDestinatario.equals("")) {
                    nomeDestinatario = nomeDestinatarioAlt;
                }
            }
            if (!numeroDocumento.equals("")) {
                return dataPagamento + " R$ " + valor + " - DARF " + numeroDocumento + ".pdf";
            }
        }
        return dataPagamento + " R$ " + valor + " - " + nomeDestinatario.toUpperCase().trim();
    }


    public static String pegarInfosTipo3(String caminhoArquivo) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] linhas = fileInfos.fileText(caminhoArquivo).split("\\r?\\n");
        String valor = "";
        String nomeDestinatarioFinal = "";
        String dataPagamento = "";

        String nomePagadorPara = "Nome do pagador:";
        String nomeDestinatarioDe = "Nome do destinatário:";

        String[] chavesDatas = {"Realizado em:"};
        String[] chavesPagamentos = {"Valor:"};

        boolean ehPagamento = false;
        for (String linha : linhas) {
            if (!linha.trim().isEmpty()) {
                lines.add(linha);
            }
        }
        while (dataPagamento.equals("") && valor.equals("") && nomeDestinatarioFinal.equals("")) {
            for (String line : lines) {
                if (line.contains("Comprovante de Pagamento PIX")) {
                    ehPagamento = true;
                }

                //SE FOR PAGAMENTO, SALVAR DESTINATARIO
                //SE FOR RECEBIMENTO, SALVAR PAGADOR
                if (line.contains(nomePagadorPara) && !ehPagamento) {
                    String[] fields = line.split(":");
                    nomeDestinatarioFinal = fields[1].trim();
                    continue;
                } else if (line.contains(nomeDestinatarioDe) && ehPagamento) {
                    String[] fields = line.split(":");
                    nomeDestinatarioFinal = fields[1];
                }

                for (String chave : chavesDatas) {
                    if (line.contains(chave)) {
                        String[] dataPagamentoSplit = line.split(": ");
                        dataPagamentoSplit = dataPagamentoSplit[1].split("-");
                        dataPagamento = dataPagamentoSplit[0];
                        dataPagamento = dataPagamento.replace("/", ".");
                    }
                }
                for (String chave : chavesPagamentos) {
                    if (line.contains(chave)) {
                        String[] valores = line.split(": ");
                        valor = valores[1];
                        break;
                    }
                }
            }
        }
        return dataPagamento + valor + " - " + nomeDestinatarioFinal.toUpperCase().trim();
    }
}