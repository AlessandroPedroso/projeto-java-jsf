package br.com.cursojsf;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import br.com.dao.DaoGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImpl;

@ViewScoped
//@SessionScoped
//@ApplicationScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	
	private List<SelectItem> estados;
	
	private List<SelectItem> cidades;
	
	public void salvar() {
		
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		mostrarMsg("Cadastrado com sucesso!");// mostrar mensagem por ultimo SEMPRE!
		/*
		daoGeneric.salvar(pessoa);
		pessoa = new Pessoa();
		*/
		
	}
	
	private void mostrarMsg(String msg) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
	}

	public void novo() {
		/*executa algum processo antes de novo*/
		pessoa = new Pessoa();
	}
	
	public void limpar() {
		/*executa algum processo antes de limpar*/
		pessoa = new Pessoa();
	}
	
	public void remove() {
		daoGeneric.DeletePorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		mostrarMsg("Removido com sucesso!");
	}
	
	@PostConstruct //carrega a lista quando abrir a pagina
	public void carregarPessoas() {
		pessoas = daoGeneric.getListEntity(Pessoa.class);
	}
	
	public void pesquisaCep(AjaxBehaviorEvent event) {
		//System.out.println("Metodo pesquisa cep chamado CEP: " + pessoa.getCep());
		
		try {
			
			URL url = new URL("https://viacep.com.br/ws/"+  pessoa.getCep() +"/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			
			String cep = "";
			StringBuilder jsoncep = new StringBuilder();
			
			while((cep = br.readLine()) != null) { //enquanto tiver linhas e não vazias
				
				jsoncep.append(cep);
			}
			
			Pessoa gson = new Gson().fromJson(jsoncep.toString(),Pessoa.class);
			
			pessoa.setCep(gson.getCep());
			pessoa.setLogradouro(gson.getLogradouro());
			pessoa.setComplemento(gson.getComplemento());
			pessoa.setBairro(gson.getBairro());
			pessoa.setLocalidade(gson.getLocalidade());
			pessoa.setUf(gson.getUf());
			pessoa.setBairro(gson.getBairro());
			pessoa.setIbge(gson.getIbge());
			pessoa.setDdd(gson.getDdd());
			
			System.out.println(gson);
			
		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("Erro ao consultar o cep");
		}
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}


	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}


	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	
	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}
	
	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	
	public String deslogar() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		
		HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		HttpSession session = req.getSession();
		
		session.removeAttribute("usuarioLogado");
		
		req.getSession().invalidate();
		
		return "index.jsf?faces-redirect=true";
	}
	
	
	public String logar() {
		
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		
		if(pessoaUser!=null) { //achou usuario
			
			/*
			//adicionar o usuário na sessão - usuarioLogado
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser.getLogin());
			*/
			
			//adicionar o usuário na sessão - usuarioLogado
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			
			HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
			HttpSession session = req.getSession();
			
			session.setAttribute("usuarioLogado", pessoaUser);
			
			return "minhaprimeirapagina.jsf?faces-redirect=true";
			
		}
		
		//System.out.println(pessoa.getLogin() + " - " + pessoa.getSenha());
		
		return "index.jsf";
	}
	
	
	public Boolean permiteAcesso(String acesso) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		
		HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		HttpSession session = req.getSession();
		
		Pessoa pessoaUser = (Pessoa) session.getAttribute("usuarioLogado");
		
		return pessoaUser.getPerfilUser().equals(acesso);
		
	}
	
	public void setEstados(List<SelectItem> estados) {
		this.estados = estados;
	}
	
	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}
	
	public void carregaCidades(AjaxBehaviorEvent event) {
		
		//String codigoEstado = (String)event.getComponent().getAttributes().get("submittedValue");
		
		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue(); //pega o obejto inteiro selecionado no comboBox
			
			//Estados estado = JPAUtil.getEntityManager().find(Estados.class, Long.parseLong(codigoEstado));
			
			if(estado !=null) {
				
				pessoa.setEstados(estado);
				List<Cidades> cidades = JPAUtil.getEntityManager().createQuery("from Cidades where estados.id = " +  estado.getId()).getResultList();
				
				List<SelectItem> selectItemsCidades = new ArrayList<SelectItem>();
				
				for (Cidades cidade : cidades) {
					selectItemsCidades.add(new SelectItem(cidade, cidade.getNome()));
				}
				
				setCidades(selectItemsCidades);
				
			}
			//System.out.println(codigoEstado);
		
	}
	
	public void editar() {
		
		if(pessoa.getCidades() != null) {
			
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);
			
			List<Cidades> cidades = JPAUtil.getEntityManager().createQuery("from Cidades where estados.id = " +  estado.getId()).getResultList();
			
			List<SelectItem> selectItemsCidades = new ArrayList<SelectItem>();
			
			for (Cidades cidade : cidades) {
				selectItemsCidades.add(new SelectItem(cidade, cidade.getNome()));
			}
			
			setCidades(selectItemsCidades);
			
		}
		
		//System.out.println(pessoa);
	}
	
	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}
	
	public List<SelectItem> getCidades() {
		return cidades;
	}
	
	
	/*
	private String nome;
	private String senha;
	private String texto;

	private HtmlCommandButton commandButton;

	private List<String> nomes = new ArrayList<String>();

	public String addNome() {

		nomes.add(nome);

		if (nomes.size() > 3) {
			commandButton.setDisabled(true);
			return "paginanavegada?faces-redirect=true";
		}

		return ""; // null ou vazio retorna na mesma pagina -> outcome

	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public void setCommandButton(HtmlCommandButton commandButton) {
		this.commandButton = commandButton;
	}

	public HtmlCommandButton getCommandButton() {
		return commandButton;
	}

	public void setNomes(List<String> nomes) {
		this.nomes = nomes;
	}

	public List<String> getNomes() {
		return nomes;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
*/
}
