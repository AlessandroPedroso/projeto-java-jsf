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
	
	public E merge(E entidade) {
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		
		E retorno = entityManager.merge(entidade);
		
		transaction.commit();
		entityManager.close();
		
		return retorno;
	}
	
	public void Delete(E entidade) {
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		
		entityManager.remove(entidade);
		
		transaction.commit();
		entityManager.close();	
	}
	
	public void DeletePorId(E entidade) {
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		
		Object id = JPAUtil.getPrimaryKey(entidade);
		
		//entityManager.createNativeQuery("delete from " + entidade.getClass().getSimpleName().toLowerCase() + " where id =" + id).executeUpdate();
		entityManager.createQuery("delete from " + entidade.getClass().getCanonicalName() + " where id =" + id).executeUpdate();
		
		transaction.commit();
		entityManager.close();	
	}
			

}
