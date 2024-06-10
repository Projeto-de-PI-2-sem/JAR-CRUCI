package org.notelog;

import com.github.britooo.looca.api.core.Looca;
import org.notelog.dao.*;
import org.notelog.model.*;
import org.notelog.service.ASCIIService;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

import static org.notelog.service.LoginService.vincularFuncionario;

public class Main {
    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public static void main(String[] args)  {
        try {
            SimpleLogger logger = new SimpleLogger("application.log");
            ASCIIService ascii = new ASCIIService();
            ascii.NotelogASCII();

            Thread.sleep(3000);
            Scanner scanner = new Scanner(System.in);
            // Loop de login
            boolean loginValido = false;
            FuncionarioDAO userDAO = new FuncionarioDAO();
            Funcionario usuario = null;

            while (!loginValido) {
                Console console = System.console();
                String senha = "";
                System.out.println(ANSI_BLUE + "Por favor, faça login para continuar..." + ANSI_RESET);
                System.out.print(ANSI_YELLOW + "Email: " + ANSI_RESET);
                String email = scanner.nextLine();
                if (console == null) {
                    System.out.print(ANSI_YELLOW + "Senha: " + ANSI_RESET);
                    senha = scanner.nextLine();
                } else {
                    char[] senhaArray = console.readPassword(ANSI_PURPLE + "Senha: " + ANSI_RESET);
                    senha = new String(senhaArray);
                    java.util.Arrays.fill(senhaArray, ' ');
                }

                // Verifica se o usuário existe no banco de dados
                usuario = userDAO.verificaUsuario(email, senha);
                if (usuario != null) {
                    loginValido = true;
                    System.out.println();
                    System.out.println(ANSI_GREEN + "Login bem-sucedido! Bem-vindo, " + usuario.getNome() + "!" + ANSI_RESET);
                    logger.info("Login bem-sucedido para o usuário:" + usuario.getNome());
                    Thread.sleep(3000);
                    vincularFuncionario(usuario);
                } else {
                    System.out.println();
                    System.out.println(ANSI_RED + "Email ou senha incorretos. Tente novamente." + ANSI_RESET);
                    logger.error("Login mal-sucedido!");
                }
            }
            logger.fechar();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
