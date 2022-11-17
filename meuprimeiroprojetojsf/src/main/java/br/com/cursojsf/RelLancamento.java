package br.com.cursojsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.dao.DaoGeneric;
import br.com.entidades.Lancamento;
import br.com.repository.IDaoLancamento;

@ViewScoped
@Named(value = "relLacamento")
public class RelLancamento implements Serializable {

	private static final long serialVersionUID = 1L;

		private List<Lancamento> listLancamento = new ArrayList<Lancamento>();
		
		@Inject
		private IDaoLancamento daoLancamento;
		
		@Inject
		private DaoGeneric<Lancamento> daoGeneric;
		
		public void setListLancamento(List<Lancamento> listLancamento) {
			this.listLancamento = listLancamento;
		}
		
		public List<Lancamento> getListLancamento() {
			return listLancamento;
		}
		
		public void buscarLancamento() {
			
			listLancamento = daoGeneric.getListEntity(Lancamento.class);
			
		}
}
