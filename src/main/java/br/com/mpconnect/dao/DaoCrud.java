package br.com.mpconnect.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import br.com.mpconnect.model.Persistente;

public interface DaoCrud<P extends Persistente> {
    public Long recuperaPorParamsQteReg(String var1, Map<String, Object> var2) throws DaoException;

    public P merge(P var1) throws DaoException;

    public void gravar(P var1) throws DaoException;

    public void gravar(List<P> var1) throws DaoException;

    public void alterar(P var1) throws DaoException;

    public void deletar(P var1) throws DaoException;

    public void excluirTodos() throws DaoException;

    public P recuperaUm(Serializable var1) throws DaoException;

    public Long recuperaTotalRegistros() throws DaoException;
    
    public Long recuperaTotalRegistros(Map<String, Object> filters) throws DaoException;
    
    public List<P> recuperaTodos() throws DaoException;
    
    public List<P> recuperaTodosPorIntervalo(int first, int max, Map<String,Object> filters) throws DaoException;

    public List<P> recuperaTodosOrderBy(String var1) throws DaoException;

    public List<P> recuperaTodosOrderByCriteria(String var1) throws DaoException;

    public List<P> recuperaPorParams(String var1, Map<String, Object> var2) throws DaoException;

    public List<P> recuperaPorParams(String var1, Map<String, Object> var2, int var3) throws DaoException;

    public List<P> recuperaPorParams(String var1, Map<String, Object> var2, int var3, int var4) throws DaoException;

    public List<P> recuperaPorQuery(String var1) throws DaoException;

    public Object recuperaUmPorQuery(String var1) throws DaoException;

    public P recuperaUmPorParams(String var1, Map<String, Object> var2) throws DaoException;

    public List<P> paginacaoBaseSerializableDataModel(String var1, Map<String, Object> var2) throws DaoException;

    public P recuperaUmPorParamsTrataNull(String var1, Map<String, Object> var2) throws DaoException;

    public Object recuperaUmCampoPorParams(String var1, Map<String, Object> var2) throws DaoException;

    public int executaUpdateOuDelete(String var1) throws DaoException;

    public int executaUpdateOuDelete(String var1, Map<String, Object> var2) throws DaoException;

    public List<P> recuperaPorParamsSql(String var1, Map<String, Object> var2, Class<P> var3) throws DaoException;

    public List<P> recuperaPorParamsSql(String var1, Map<String, Object> var2, Class<P> var3, int var4, int var5) throws DaoException;

    public Session getSession();

    public P refresh(P var1) throws DaoException;

    public void refreshRefresh(P var1) throws DaoException;

    public void flush() throws DaoException;
}
