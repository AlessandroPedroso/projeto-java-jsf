package br.com.cursojsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.dao.DaoGeneric;
import br.com.entidades.Lancamento;
import br.com.entidades.Pessoa;
import br.com.repository.IDaoLancamento;

//@ViewScoped
@javax.faces.view.ViewScoped //do cdi
@Named(value ="lancamentoBean")
 public class LancamentoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Lancamento lancamento = new Lancamento();
	private List<Lancamento> lancamentosList = new ArrayList<Lancamento>();
	
	@Inject
	private DaoGeneric<Lancamento> daoGeneric;
	
	@Inject
	private IDaoLancamento daoLancamento;
	
	public String salvar() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		
		HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		HttpSession session = req.getSession();
		
		Pessoa pessoaUser = (Pessoa) session.getAttribute("usuarioLogado");
		
		lancamento.setUsuario(pessoaUser); // salva com o usuario pessoa logado
		lancamento = daoGeneric.merge(lancamento);// salva no banco de dados referente a tabela de lançamentos
		
		carregarLancamentos();
		
		FacesContext.getCurrentInstance().addMessage("msg", new FacesMessage("Salvo com sucesso."));
		
		return""; // retorna para ficar na mesma tela, funciona com void também
	}
	
	@PostConstruct
	private void carregarLancamentos() {
		
		Long idLogado = usuarioLogadoId();
		
		lancamentosList = daoLancamento.consultarLimit10(idLogado);
	}
	
	private Long usuarioLogadoId() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		
		HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		HttpSession session = req.getSession();
		
		Pessoa pessoaUser = (Pessoa) session.getAttribute("usuarioLogado");
		
		return pessoaUser.getId();
		
	}

	public String novo() {
		
		lancamento = new Lancamento();
		return "";
	}
	
	public String remover() {
		daoGeneric.DeletePorId(lancamento);
		lancamento = new Lancamento();//limpa o objeto
		carregarLancamentos();
		FacesContext.getCurrentInstance().addMessage("msg", new FacesMessage("Excluído com sucesso."));
		return ""; 
	}

	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}

	public DaoGeneric<Lancamento> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Lancamento> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public List<Lancamento> getLancamentosList() {
		return lancamentosList;
	}

	public void setLancamentosList(List<Lancamento> lancamentosList) {
		this.lancamentosList = lancamentosList;
	}

}
