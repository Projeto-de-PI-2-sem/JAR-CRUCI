package org.notelog.service;

import org.notelog.dao.FuncionarioDAO;
import org.notelog.dao.NotebookDAO;
import org.notelog.model.Funcionario;
import org.notelog.model.Notebook;

import java.util.Scanner;

import static org.notelog.app.system.MonitoramentoSystem.escolherMonitoramento;
import static org.notelog.model.Notebook.pegarNumeroSerial;

public class LoginService {
    public static void vincularFuncionario(Funcionario usuarioMaster) {
        FuncionarioDAO userDAO = new FuncionarioDAO();
        NotebookDAO notebookDAO = new NotebookDAO();


        if (userDAO.temVinculo(pegarNumeroSerial())){
            Notebook notebookJaCadastrado = notebookDAO.consultaNotebook(userDAO.pegaFuncionarioPeloNumeroSerial().getId());
            try {
                escolherMonitoramento(userDAO.pegaFuncionarioPeloNumeroSerial(), notebookJaCadastrado);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } else{

            Scanner scanner = new Scanner(System.in);

            ASCIIService ascii = new ASCIIService();
            ascii.VinculoFuncionarioASCII();

            for (Funcionario funcionario : userDAO.buscarFuncionarios(usuarioMaster.getFkEmpresa())) {
                System.out.println(funcionario);
            }

            Notebook notebook = new Notebook(scanner.nextInt(), usuarioMaster.getFkEmpresa());

            if (notebookDAO.adicionarNotebook(notebook)){
                Funcionario usuario = userDAO.pegaFuncionarioPeloNumeroSerial();
                try {
                    escolherMonitoramento(usuario, notebook);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else System.out.println("ERRO!!! Funcionário inexistente vinculado a empresa");
        };
    }
}
