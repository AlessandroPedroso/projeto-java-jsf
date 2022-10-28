package br.com.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Lancamento;
import br.com.jpautil.JPAUtil;

public class IDaoLancamentoImpl implements IDaoLancamento {

	@Override
	public List<Lancamento> consultar(long codUser) {
		List<Lancamento> lista = null;
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		lista = entityManager.createQuery("from lancamento where usuario.id = " + codUser).getResultList();
		
		transaction.commit();
		entityManager.close();
		
		return lista;
	}
	
	
}
