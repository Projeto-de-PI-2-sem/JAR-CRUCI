package org.notelog.dao;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import org.notelog.SimpleLogger;
import org.notelog.util.database.ConexaoMySQL;
import com.github.britooo.looca.api.group.discos.Disco;
import org.notelog.model.DiscoRigido;
import org.notelog.util.database.ConexaoSQLServer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiscoRigidoDAO {

    SimpleLogger logger;

    {
        try {
            logger = new SimpleLogger("application.log");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarDisco(DiscoRigido discoRigido) {
        ConexaoMySQL conexaoMySQL = new ConexaoMySQL();
        JdbcTemplate conmysql = conexaoMySQL.getConexaoDoBanco();

        ConexaoSQLServer conSQLServer = new ConexaoSQLServer();
        JdbcTemplate consqlserver = conSQLServer.getConexaoDoBanco();

        //SQL SERVER

        String sql = "INSERT INTO DiscoRigido (modelo, serial, tamanho, fkNotebook) VALUES (?, ?, ?, ?)";
        Object[] params = {discoRigido.getModelo(), discoRigido.getSerial(), discoRigido.getTamanho(), discoRigido.getFkNotebook()};


        logger.info("Inserindo informações de Disco no Banco de dados (SQL Server)");
        consqlserver.update(sql, params);

        // Obter ID do último disco inserido
        String selectSQLServer = "SELECT TOP 1 id FROM DiscoRigido WHERE fkNotebook = ? ORDER BY id DESC";
        Integer id = null;
        id = consqlserver.queryForObject(selectSQLServer, Integer.class, discoRigido.getFkNotebook());


        // MYSQL

        String mysql = "INSERT INTO DiscoRigido (id, modelo, serial, tamanho, fkNotebook) VALUES (?, ?, ?, ?, ?)";
        Object[] myparams = {id,discoRigido.getModelo(), discoRigido.getSerial(), discoRigido.getTamanho(), discoRigido.getFkNotebook()};


        logger.info("Inserindo informações de Disco no Banco de dados (MySQL)");
        conmysql.update(mysql, myparams);


        if (id != null) {
            discoRigido.setId(id);
        } else {
            System.err.println("Não foi possível obter o ID do disco inserido.");
        }
    }



    private boolean discoExiste(DiscoRigido disco) {
        ConexaoSQLServer conSQLServer = new ConexaoSQLServer();
        JdbcTemplate consqlserver = conSQLServer.getConexaoDoBanco();

        String sql = "SELECT COUNT(*) FROM DiscoRigido WHERE modelo = ? AND serial = ?";
        Object[] params = {disco.getModelo(), disco.getSerial()};
        Integer quantidade = null;

        quantidade = consqlserver.queryForObject(sql, params, Integer.class);

        return quantidade != null && quantidade > 0;
    }


    public static List<DiscoRigido> buscarTodosOsDiscos() {
        ConexaoSQLServer conSQLServer = new ConexaoSQLServer();
        JdbcTemplate consqlserver = conSQLServer.getConexaoDoBanco();

        String sql = "SELECT * FROM DiscoRigido";
        List<DiscoRigido> discos = new ArrayList<>();

        discos.addAll(consqlserver.query(sql, new BeanPropertyRowMapper<>(DiscoRigido.class)));

        return discos;
    }


    private DiscoRigido buscarDiscoPeloSerial(String serial) {
        ConexaoSQLServer conSQLServer = new ConexaoSQLServer();
        JdbcTemplate consqlserver = conSQLServer.getConexaoDoBanco();

        String sql = "SELECT * FROM DiscoRigido WHERE serial = ?";
        Object[] params = {serial};
        DiscoRigido disco = null;

            try {
                disco = consqlserver.queryForObject(sql, params, new BeanPropertyRowMapper<>(DiscoRigido.class));
            } catch (EmptyResultDataAccessException e) {
                // Nenhum disco encontrado no SQL Server
            }


        return disco;
    }


    public void exibirTodosOsDiscos() {
        for (DiscoRigido discoRigido : buscarTodosOsDiscos()) {
            System.out.println(discoRigido);
        }
    }

    public void adiconarNovoDisco(Integer fkNotebook) {

        Looca looca = new Looca();
        DiscoGrupo grupoDeDiscos = looca.getGrupoDeDiscos();

        List<Disco> discos = grupoDeDiscos.getDiscos();

        for (Disco disco : discos) {
            DiscoRigido novoDiscoRigido = new DiscoRigido(null, disco.getModelo(), disco.getSerial(), grupoDeDiscos.getTamanhoTotal().toString(), fkNotebook);

            if (!discoExiste(novoDiscoRigido)) {
                adicionarDisco(novoDiscoRigido);
            }
        }
    }
}
