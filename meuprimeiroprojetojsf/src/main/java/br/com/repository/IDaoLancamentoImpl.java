package br.com.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Lancamento;

@Named
public class IDaoLancamentoImpl implements IDaoLancamento, Serializable {
	
	static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Lancamento> consultar(long codUser) {
		List<Lancamento> lista = null;
		
		//EntityManager entityManager = JPAUtil.getEntityManager();
		//EntityTransaction transaction = entityManager.getTransaction();
		//transaction.begin();
		
		lista = entityManager.createQuery(" from Lancamento where usuario.id = " + codUser).getResultList();
		
		//transaction.commit();
		//entityManager.close();
		
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Lancamento> consultarLimit10(long codUser) {
		List<Lancamento> lista = null;
	
		
		lista = entityManager.createQuery(" from Lancamento where usuario.id = " + codUser + "order by id desc")
				.setMaxResults(10)
				.getResultList();
		
		
		return lista;
	}


	@Override
	public List<Lancamento> relatorioLancamento(String numNota, Date dataIni, Date dataFim) {
		
		List<Lancamento> lancamentos = new ArrayList<Lancamento>();
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" select l from Lancamento l");
		
		if(dataIni == null && dataFim == null && numNota !=null && !numNota.isEmpty()) {
			
			sql.append(" where l.numeroNotaFiscal = '").append(numNota.trim()).append("'");
			
		}else if(numNota == null || (numNota !=null && numNota.isEmpty()) && dataIni != null && dataFim == null) {
			
			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			sql.append(" where l.dataInicial >= '").append(dataIniString).append("'");
			
		}else if(numNota == null || (numNota !=null && numNota.isEmpty()) && dataIni == null && dataFim != null) {
			
			String dataFimString = new SimpleDateFormat("yyyy-MM-dd").format(dataFim);
			sql.append(" where l.dataFinal <= '").append(dataFimString).append("'");
			
		}else if(numNota == null || (numNota !=null && numNota.isEmpty()) && dataIni!=null && dataFim!=null) {
			
			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			String dataFimString = new SimpleDateFormat("yyyy-MM-dd").format(dataFim);
			
			sql.append(" where l.dataInicial >= '").append(dataIniString).append("'");
			sql.append(" and l.dataFinal <= '").append(dataFimString).append("'");
			
		}else if(numNota !=null && !numNota.isEmpty() && dataIni!=null && dataFim!=null) {
			
			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			String dataFimString = new SimpleDateFormat("yyyy-MM-dd").format(dataFim);
			
			sql.append(" where l.dataInicial >= '").append(dataIniString).append("'");
			sql.append(" and l.dataFinal <= '").append(dataFimString).append("'");
			sql.append(" and l.numeroNotaFiscal = '").append(numNota.trim()).append("'");
			
		}
		
		lancamentos = entityManager.createQuery(sql.toString()).getResultList();
		
		return lancamentos;
	}
	
	
}
