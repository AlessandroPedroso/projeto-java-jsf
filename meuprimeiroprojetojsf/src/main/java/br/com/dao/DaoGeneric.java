package br.com.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;


import br.com.jpautil.JPAUtil;

@Named
public class DaoGeneric<E> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager entityManager;
	
	@Inject
	private JPAUtil jpaUtil;
	
	public void salvar(E entidade) {
		
		//EntityManager entityManager = JPAUtil.getEntityManager(); essa linha não precisa mais ser usado, por que ela está sendo injetado
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		
		entityManager.persist(entidade);
		
		transaction.commit();
		//entityManager.close(); framework vai fazer o controle
	}
	
	public E merge(E entidade) {
		
		//EntityManager entityManager = JPAUtil.getEntityManager(); essa linha não precisa mais ser usado, por que ela está sendo injetado
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		
		E retorno = entityManager.merge(entidade);
		
		transaction.commit();

		
		return retorno;
	}
	
	public void Delete(E entidade) {
		
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		
		entityManager.remove(entidade);
		
		transaction.commit();
	}
	
	public void DeletePorId(E entidade) {
		
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		
		Object id = jpaUtil.getPrimaryKey(entidade);
		
		//entityManager.createNativeQuery("delete from " + entidade.getClass().getSimpleName().toLowerCase() + " where id =" + id).executeUpdate();
		entityManager.createQuery("delete from " + entidade.getClass().getCanonicalName() + " where id =" + id).executeUpdate();
		
		transaction.commit();
	}
	
	public List<E> getListEntity(Class<E> entidade){
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<E> retorno = entityManager.createQuery("from " + entidade.getName()).getResultList();
		
		transaction.commit();
		
		return retorno;
		
	}
	
	public E consultar(Class<E> entidade, String codigo) {
		
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		E objeto = (E) entityManager.find(entidade, Long.parseLong(codigo));
		
		transaction.commit();
		
		return objeto;
		
	}
			

}
