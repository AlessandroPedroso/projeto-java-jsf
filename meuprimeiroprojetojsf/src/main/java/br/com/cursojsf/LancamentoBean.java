package br.com.cursojsf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.dao.DaoGeneric;
import br.com.entidades.Lancamento;
import br.com.entidades.Pessoa;
import br.com.repository.IDaoLancamento;
import br.com.repository.IDaoLancamentoImpl;

@ViewScoped
@ManagedBean(name = "lancamentoBean")
public class LancamentoBean {

	private Lancamento lancamento = new Lancamento();
	private DaoGeneric<Lancamento> daoGeneric = new DaoGeneric<Lancamento>();
	private List<Lancamento> lancamentosList = new ArrayList<Lancamento>();
	private IDaoLancamento daoLancamento = new IDaoLancamentoImpl();
	
	public String salvar() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		
		HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		HttpSession session = req.getSession();
		
		Pessoa pessoaUser = (Pessoa) session.getAttribute("usuarioLogado");
		
		lancamento.setUsuario(pessoaUser); // salva com o usuario pessoa logado
		daoGeneric.salvar(lancamento);// salva no banco de dados referente a tabela de lançamentos
		
		carregarLancamentos();
		
		return""; // retorna para ficar na mesma tela, funciona com void também
	}
	
	@PostConstruct
	private void carregarLancamentos() {
		
		Long idLogado = usuarioLogadoId();
		
		lancamentosList = daoLancamento.consultar(idLogado);
		
		
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
