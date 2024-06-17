package org.notelog.dao;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;
import org.notelog.model.DiscoRigido;
import org.notelog.model.LogDisco;
import org.notelog.util.database.ConexaoMySQL;
import org.notelog.util.database.ConexaoSQLServer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static org.notelog.dao.DiscoRigidoDAO.*;
import static org.notelog.model.LogAbstract.dataHoraAtual;

public class LogDiscoDAO {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = Logger.getLogger(LogDiscoDAO.class.getName());

    public LogDiscoDAO() {
        ConexaoMySQL conexaoMySQL = new ConexaoMySQL();
        this.jdbcTemplate = conexaoMySQL.getConexaoDoBanco();
    }

    public void adicionarLogDisco(LogDisco logDisco, Integer fkDiscoRigido) throws InterruptedException {
        ConexaoMySQL conexaoMySQL = new ConexaoMySQL();
        JdbcTemplate conmysql = conexaoMySQL.getConexaoDoBanco();

        ConexaoSQLServer conSQLServer = new ConexaoSQLServer();
        JdbcTemplate consqlserver = conSQLServer.getConexaoDoBanco();

        // SQL SERVER

        String sql = """
                INSERT INTO LogDisco (fkDiscoRigido, usoDisco, dataLog)
                VALUES (?, ?, ?)
                """;

        Object[] params = {
                fkDiscoRigido,
                logDisco.getUsoDisco(),
                dataHoraAtual()
        };

        consqlserver.update(sql, params);

        Thread.sleep(5000);

        String sqlSelectSQLServer = "SELECT TOP 1 id FROM LogDisco WHERE fkDiscoRigido = ? ORDER BY id DESC";

        Integer id = consqlserver.queryForObject(sqlSelectSQLServer, Integer.class, fkDiscoRigido);

        // MY SQL

        String mysql = """
                INSERT INTO LogDisco (id, fkDiscoRigido, usoDisco, dataLog)
                VALUES (?, ?, ?, ?)
                """;

        Object[] myparams = {
                id,
                fkDiscoRigido,
                logDisco.getUsoDisco(),
                dataHoraAtual()
        };

        conmysql.update(mysql, myparams);


    }


    private Boolean logDiscoExiste(LogDisco logDisco, Integer fkDiscoRigido) {
        ConexaoSQLServer conSQLServer = new ConexaoSQLServer();
        JdbcTemplate consqlserver = conSQLServer.getConexaoDoBanco();

        String sql = "SELECT COUNT(*) FROM LogDisco WHERE fkDiscoRigido = ? AND datalog = ?";
        Object[] params = {fkDiscoRigido, logDisco.getDataLog()};
        Integer count = null;

        count = consqlserver.queryForObject(sql, params, Integer.class);

        return count != null && count > 0;
    }


    public void adicionarNovoLogDisco(Integer idNotebook) throws InterruptedException, IOException {
        Looca looca = new Looca();
        DiscoGrupo grupoDeDiscos = looca.getGrupoDeDiscos();
        List<Disco> discos = grupoDeDiscos.getDiscos();
        List<Volume> volumes = grupoDeDiscos.getVolumes();

        ConexaoMySQL conexaoMySQL = new ConexaoMySQL();
        JdbcTemplate conmysql = conexaoMySQL.getConexaoDoBanco();

        ConexaoSQLServer conSQLServer = new ConexaoSQLServer();
        JdbcTemplate consqlserver = conSQLServer.getConexaoDoBanco();

        for (Disco disco : discos) {
            Integer fkDiscoRigido = null;

            if (disco.getSerial() == "unknown" || disco.getSerial() == "" || disco.getSerial() == null && disco.getModelo() == "unknown" || disco.getModelo() == "" || disco.getModelo() == null) {
                String instanceId = getInstanceMetadata("instance-id");

                try {

                    fkDiscoRigido = consqlserver.queryForObject("SELECT DiscoRigido.id FROM DiscoRigido WHERE DiscoRigido.serial = ?;", Integer.class, instanceId);


                } catch (EmptyResultDataAccessException e) {
                    logger.warning("Nenhum disco rígido encontrado para o serial " + disco.getSerial());
                    continue;  // Pular para o próximo disco se não encontrar nenhum resultado
                }

                Long usoDisco = 0L;
                for (Volume volume : volumes) {
                    usoDisco += ((volume.getTotal() - volume.getDisponivel()) / volumes.size());
                }
                LogDisco novoLogDiscoRigido = new LogDisco(null, usoDisco.toString());

                if (!logDiscoExiste(novoLogDiscoRigido, fkDiscoRigido)) {
                    adicionarLogDisco(novoLogDiscoRigido, fkDiscoRigido);
                } else {
                    logger.info("LogDisco já existe: " + novoLogDiscoRigido);
                }


            } else {
                try {

                    fkDiscoRigido = consqlserver.queryForObject("SELECT DiscoRigido.id FROM DiscoRigido WHERE DiscoRigido.serial = ?;", Integer.class, disco.getSerial());

                } catch (EmptyResultDataAccessException e) {
                    logger.warning("Nenhum disco rígido encontrado para o serial " + disco.getSerial());
                    continue;  // Pular para o próximo disco se não encontrar nenhum resultado
                }

                Long usoDisco = 0L;
                for (Volume volume : volumes) {
                    if (volume.getVolume().startsWith(disco.getNome())) {
                        usoDisco += (volume.getTotal() - volume.getDisponivel());
                    }
                }
                LogDisco novoLogDiscoRigido = new LogDisco(null, usoDisco.toString());

                if (!logDiscoExiste(novoLogDiscoRigido, fkDiscoRigido)) {
                    adicionarLogDisco(novoLogDiscoRigido, fkDiscoRigido);
                } else {
                    logger.info("LogDisco já existe: " + novoLogDiscoRigido);
                }
            }
        }

    }

}