package br.com.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.jpautil.JPAUtil;

public class DaoGeneric<E> {
	
	public void salvar(E entidade) {
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		
		entityManager.persist(entidade);
		
		transaction.commit();
		entityManager.close();	
	}
			

}
