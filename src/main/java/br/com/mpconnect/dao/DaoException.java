/*
 * Decompiled with CFR 0_102.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityTransaction
 */
package br.com.mpconnect.dao;

import javax.persistence.EntityTransaction;

public class DaoException
extends Throwable {
    private EntityTransaction transacao;
    private static final long serialVersionUID = 1;

    public DaoException() {
    }

    public DaoException(String message, Throwable cause, String string) {
    }

    public EntityTransaction getTransacao() {
        return this.transacao;
    }

    public void setTransacao(EntityTransaction transacao) {
        this.transacao = transacao;
    }
}
