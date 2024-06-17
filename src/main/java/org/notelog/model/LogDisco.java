package org.notelog.model;

import com.github.britooo.looca.api.core.Looca;

public class LogDisco extends LogAbstract {
    Looca looca = new Looca();
    private Integer fkDiscoRigido;
    private String usoDisco;

    public LogDisco(Integer fkDiscoRigido, String usoDisco) {

        this.fkDiscoRigido = fkDiscoRigido;
        this.usoDisco = usoDisco;
    }


    public Integer getFkDiscoRigido() {
        return fkDiscoRigido;
    }

    public void setFkDiscoRigido(Integer fkDiscoRigido) {
        this.fkDiscoRigido = fkDiscoRigido;
    }

    public String getUsoDisco() {
        return usoDisco;
    }

    public void setUsoDisco(String usoDisco) {
        this.usoDisco = usoDisco;
    }

    @Override
    public String toString() {
        return "LogDisco{" +
                "usoDisco='" + usoDisco + '\'' +
                ", fkDiscoRigido=" + fkDiscoRigido +
                '}';
    }
}