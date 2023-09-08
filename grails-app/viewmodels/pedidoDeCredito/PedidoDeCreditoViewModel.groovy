package pedidoDeCredito

import grails.plugin.springsecurity.SpringSecurityService
import mz.maleyanga.ClienteService
import mz.maleyanga.ContadorService
import mz.maleyanga.CreditoService
import mz.maleyanga.SessionStorageService
import mz.maleyanga.SettingsService
import mz.maleyanga.cliente.Cliente
import mz.maleyanga.credito.Credito
import mz.maleyanga.pedidoDeCredito.ListaDeDesembolso
import mz.maleyanga.pedidoDeCredito.PedidoDeCredito
import mz.maleyanga.pedidoDeCredito.Penhora
import mz.maleyanga.security.Utilizador
import mz.maleyanga.settings.DefinicaoDeCredito
import grails.transaction.Transactional
import mz.maleyanga.settings.Settings
import org.springframework.stereotype.Service
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Label
import org.zkoss.zul.ListModelList
import java.sql.SQLException

@Transactional
@Service
class PedidoDeCreditoViewModel {
  @Wire Button bt_salvar
  @Wire Button bt_update
  @Wire Button bt_aprovar_pedido
  @Wire Button bt_limpar
  private boolean taxaManual = true
  SpringSecurityService springSecurityService
  private ListModelList<Cliente> clientes
  private ListModelList<PedidoDeCredito> pedidos
  private ListModelList<PedidoDeCredito> pedidosDoCliente
  private ListModelList<PedidoDeCredito> listaDePedidos
  private ListModelList<Penhora> penhoras
  private ListaDeDesembolso selectedLista
  private  Settings settings
  SessionStorageService sessionStorageService
  SettingsService settingsService
private PedidoDeCredito sPDC
  private String filter
  private String filterCliente
  private  Utilizador utilizador
  CreditoService creditoService
  ClienteService clienteService
  private ListModelList<DefinicaoDeCredito> definicoes
  private BigDecimal totalDesembolsado
  Cliente selectedCliente
  private  Date dataDeDesembolso
  private  String balcao
  private  String descricao
  private ListModelList<ListaDeDesembolso> listasDosDesembolsos
  private String filterLista
  ContadorService contadorService




  String getFilterLista() {
    return filterLista
  }

  void setFilterLista(String filterLista) {
    this.filterLista = filterLista
  }

  ListModelList<ListaDeDesembolso> getListasDosDesembolsos() {
    if(listasDosDesembolsos ==null){
      listasDosDesembolsos = new ListModelList<ListaDeDesembolso>()
    }
    return listasDosDesembolsos
  }

  ListaDeDesembolso getSelectedLista() {
    return selectedLista
  }

  @NotifyChange(["pedidos","selectedLista","penhoras"])
  void setSelectedLista(ListaDeDesembolso selectedLista) {
    this.selectedLista = selectedLista
    sessionStorageService.listaDeDesembolso = selectedLista
  }

  String getBalcao() {
    return balcao
  }

  void setBalcao(String balcao) {
    this.balcao = balcao
  }

  String getDescricao() {
    return descricao
  }

  void setDescricao(String descricao) {
    this.descricao = descricao
  }

  BigDecimal getTotalDesembolsado() {
    return totalDesembolsado
  }

  boolean getTaxaManual() {
    return taxaManual
  }

  @Command
  @NotifyChange(["totalDesembolsado"])
  def updateTotal(){
    totalDesembolsado = 0
    for(PedidoDeCredito pdc in listaDePedidos){
      totalDesembolsado+=pdc.valorDeCredito
    }
  }

  @Command
  @NotifyChange(["taxaManual","taxa"])
  def aplicarTaxaManual() {
    info.value = ""
    if(settings.taxaManual){
      taxaManual = !taxaManual
    }else if(!settings.taxaManual)

    {
      info.value="Altere as definições de credito de forma a poder usar a taxa manual!"
      info.style = "color:red;font-weight;font-size:16px;background:back"
    }
  }

  Date getDataDeDesembolso() {
    return dataDeDesembolso
  }

  void setDataDeDesembolso(Date dataDeDesembolso) {
    this.dataDeDesembolso = dataDeDesembolso
  }

  ListModelList<PedidoDeCredito> getListaDePedidos() {
    if(listaDePedidos==null){
      listaDePedidos = new  ListModelList<PedidoDeCredito>()
    }

    return listaDePedidos
  }

  @Command
  @NotifyChange(["listaDePedidos"])
  def addLPDC(){
  listaDePedidos.clear()
    getListaDePedidos()
  }
  Cliente getSelectedCliente() {
    return selectedCliente
  }

  @NotifyChange(["penhoras","pedidosDoCliente","pedidos"])
  void setSelectedCliente(Cliente selectedCliente) {
    penhoras.clear()
    this.selectedCliente = selectedCliente
    info.value = ""
    addPDC()
    }



  @Command
  @NotifyChange(["penhoras","clientes"])
  void doSearchCliente() {
    clientes.clear()
    penhoras.clear()
    List<Cliente> allItems = clienteService.findAllByName(filterCliente)
    if (filterCliente != null &&! "".equals(filterCliente))
    {
      for (Cliente item : allItems) {
        if (item.nome.toLowerCase().indexOf(filterCliente.toLowerCase()) >= 0 ||

                item.numeroDeIndentificao.indexOf(filterCliente) >= 0) {
          clientes.add(item)

        }
      }
    }
  }


  @Command
  void doSearchListas() {
    listasDosDesembolsos.clear()
    List<ListaDeDesembolso> allItems = ListaDeDesembolso.all
    if (filterLista != null &&! "".equals(filterLista))
    {
      for (ListaDeDesembolso item : allItems) {
        if (item.descricao?.toLowerCase()?.indexOf(filterLista?.toLowerCase()) >= 0 ||
        item?.balcao?.toLowerCase()?.indexOf(filterLista?.toLowerCase()) >= 0 ||
        item?.numeroDaLista?.toLowerCase()?.indexOf(filterLista?.toLowerCase()) >= 0 ||
         item?.dataDeDesembolso?.format("dd/MM/yy")?.toString()?.indexOf(filterLista) >= 0) {
          listasDosDesembolsos.add(item)

        }
      }
    }
  }
  String getFilterCliente() {
    return filterCliente
  }

  void setFilterCliente(String filterCliente) {
    this.filterCliente = filterCliente
  }

  ListModelList<DefinicaoDeCredito> getDefinicoes() {

    if(definicoes==null){
      definicoes = new ListModelList<>(DefinicaoDeCredito.all)
    }

    return definicoes
  }
  String getFilter() {
    return filter
  }

  void setFilter(String filter) {
    this.filter = filter
  }

  BigDecimal getValor() {
    return somar()
  }
  @NotifyChange(["pagamentos","info"])
  @Command
  void doSearch() {
    pedidos.clear()
    List<PedidoDeCredito> allItems = PedidoDeCredito.all
    if (filter == null || "".equals(filter)) {
      pedidos.addAll(allItems)
    } else {
      for (PedidoDeCredito item : allItems) {
        if (item.cliente.nome.toLowerCase().indexOf(filter.toLowerCase()) >= 0 ||
                item.motivo.toString().indexOf(filter) >= 0 ||
                item.valorDeCredito.toString().indexOf(filter) >= 0 ||
                item.dateCreated.format('dd/MM/yyyy').toString().indexOf(filter) >= 0 ||
                item.estado.indexOf(filter) >= 0) {
          pedidos.add(item)
        }
      }
    }
  }

  @NotifyChange(["pedidos"])
  ListModelList<PedidoDeCredito> getPedidos() {
    if(pedidos==null){
      pedidos = new ListModelList<PedidoDeCredito>()
    }
    if(selectedLista){
      pedidos = PedidoDeCredito.findAllByListaDeDesembolso(selectedLista)
    }else
    if(selectedCliente){
      def pedidoss = PedidoDeCredito.findAllByCliente(selectedCliente)
      if(pedidoss){
        return pedidoss
      }
    }
    return pedidos
  }

  @Command
  @NotifyChange
  ListModelList<PedidoDeCredito> getPedidosDoCliente() {
    if(pedidosDoCliente==null){
      pedidosDoCliente = new ListModelList<PedidoDeCredito>()
    }
    pedidosDoCliente.clear()
    if(selectedCliente!=null){
      pedidosDoCliente = PedidoDeCredito.findAllByCliente(selectedCliente)
    }
    return pedidosDoCliente
  }


  ListModelList<Penhora> getPenhoras() {
    if(penhoras==null){
      penhoras = new ListModelList<Penhora>()
    }
    if(sPDC?.id){
      penhoras = Penhora.findAllByPedidoDeCredito(sPDC)
      penhoras.add(new Penhora())
    }
    return penhoras
  }

  PedidoDeCredito getsPDC() {
    return sPDC
  }

  @NotifyChange(["penhoras"])
  void setsPDC(PedidoDeCredito sPDC) {
    this.sPDC = sPDC
    bt_salvar.visible = false
    bt_update.visible = true
    bt_aprovar_pedido.visible = true
    bt_limpar.visible = false
    getPenhoras()



  }

  ListModelList<Cliente> getClientes() {
    if(clientes==null){
      clientes = new ListModelList<Cliente>()
    }
    return clientes
  }
  @Wire Label info

    @Init init() {
      settings = settingsService.getSettings()

    }

  @Command
  @NotifyChange(['penhoras','sPDC'])
  def addPenhora(){
    info.value = ""
     penhoras.add(new Penhora(pedidoDeCredito: sPDC))
  }
  @Command
  @NotifyChange(["pedidos","sPDC"])
  def showPenhoras(){
    info.value = ""

  }
  @Command
  @NotifyChange(['penhoras','sPDC'])
  def addPDC(){
    info.value = ""
    sPDC = new PedidoDeCredito()
    penhoras.clear()
    penhoras.add(new Penhora())
    bt_salvar.visible = true
    bt_update.visible = false
    bt_aprovar_pedido.visible = false

  }

  @Command
  @NotifyChange([""])
  def informar(){
    info.value = "Faça double click para adicionar o PDC na lista!"
  }
  @Command
  @NotifyChange(["listaDePedidos","totalDesembolsado"])
  def addPedido(){
    info.value = ""
    if(!settings.permitirDesembolsoComDivida){
      if(Credito.findAllByClienteAndEmDivida(selectedCliente,true)){
        info.value = "Este cliente tem dívida por regularizar!"
        info.style = "color:red;font-size:14pt"
        return
      }
    }
    for(PedidoDeCredito pdc in listaDePedidos){
      if(pdc.cliente.id==selectedCliente.id){
      info.value = "este cliente já tem um PDC incluido na lista!"
        info.style = "color:blue;font-weight;font-size:14ptpt;background:back"
      return
      }
    }
    if(!listaDePedidos.contains(sPDC)){
      listaDePedidos.add(sPDC)
    }
    if(taxar()){
      sPDC.valorDaComissao = sPDC.valorDeCredito*sPDC.definicaoDeCredito.taxa.percentagem/100
     // sPDC.merge(flush: true)
    }
    info.value = "O pedido de crédito foi adicionado com sucesso!"
    info.style = "color:blue;font-size:14pt"
    updateTotal()
  }

  @Command
  @NotifyChange(["listaDePedidos","totalDesembolsado"])
  def salvarLista(){

    try {
      ListaDeDesembolso listaDeDesembolso = new ListaDeDesembolso()
      for(PedidoDeCredito pdc in listaDePedidos){
        if(Credito.findAllByClienteAndInvalido(selectedCliente,false)){
          pdc.frequencia = "renovação"
        }else {
          pdc.frequencia = "novo"
        }
        pdc.dataDeDesembolso = dataDeDesembolso
        pdc.listaDeDesembolso = listaDeDesembolso
        listaDeDesembolso.addToPedidosDeCredito(pdc)
      }
      listaDeDesembolso.descricao = descricao
      listaDeDesembolso.balcao = balcao
      listaDeDesembolso.dataDeDesembolso = dataDeDesembolso
      Utilizador user = springSecurityService.currentUser as Utilizador
      listaDeDesembolso.gerente = user
      listaDeDesembolso.setNumeroDaLista(contadorService.gerarNumeroDaLista())
      listaDeDesembolso.save(failOnError: true,flush: true)
      sessionStorageService.setListaDeDesembolso(listaDeDesembolso)
      info.value = "A lista dos candidatos a crédito foi criado com sucesso!"
      info.style= "color:blue;font-weight;font-size:14ptpt;background:back"
      listaDePedidos.clear()
    }catch(SQLException e){
      info.value = e.toString()
    }

  }

  @Command
  @NotifyChange(["listaDePedidos"])
  def removePedido(){
    if(listaDePedidos.contains(sPDC)){
      listaDePedidos.remove(sPDC)
    }
  }

    @Command
    @NotifyChange(["info"])
    def cleanInfo(){
        info.value = ""
    }

  @Command
  @NotifyChange(['penhoras','sPDC'])
  def addCredito(){
    if(sPDC.creditado){
      creditoService.pedidoDeCredito = sPDC
      Executions.sendRedirect("/credito/")
    }else {
      info.value="Este Pedido não foi aprovado!"
      info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }

  }
  @Command
  @NotifyChange(["penhoras"])
  def limpar(){
    info.value = ""
    penhoras.clear()
  }
  @Command
  @NotifyChange(["sPDC"])
  def aprovarPedido(){
    info.value=""
    sPDC.creditado = !sPDC.creditado
    if(sPDC.creditado){
      sPDC.estado="aprovado"
    }else {
      sPDC.estado="reprovado"
    }
  }
  @Command
  @NotifyChange(["sPDC","pedidos"])
  def updatePedido(){
    Utilizador user = springSecurityService.currentUser as Utilizador
    if (!user.authorities.any { it.authority == "PEDIDO_DE_CREDITO_UPDATE" }) {
      info.value="Este utilizador não tem permissão para executar esta acção !"
      info.style = "color:red;font-weight;font-size:14ptpt;background:back"
      return
    }
    if(sPDC.creditado){
      sPDC.dataDeAprovacao= new Date()
      }

    sPDC.merge(flush: true)
    for(Penhora p in penhoras){
      p.pedidoDeCredito = sPDC
      p.merge(flush: true)
    }
    info.value = "Pedido de crédito actualizado com sucesso!"
    info.style = "color:blue;font-weight;font-size:14ptpt;background:back"
  }
  @Command
  @NotifyChange(['penhoras','sPDC','pedidos'])
  def salvarPDC(){
    info.value = ""
    try {
      if(sPDC.id!=null){
        info.value = "Este Pedido de crédito já foi gravado na base de dados!"
        info.style = "color:blue;font-weight;font-size:14ptpt;background:back"
        return
      }
      if(penhoras.empty){
        info.value = "O pedido deve ter pelo menos uma penhora!"
        info.style = "color:blue;font-weight;font-size:14ptpt;background:back"
        return
      }
      if(somar()<sPDC.valorDeCredito){

        info.value = "A soma dos valores das penhoras deve ser superior ao valor pedido!"
        info.style = "color:blue;font-weight;font-size:14ptpt;background:back"
        return
      }
      utilizador = Utilizador.findById(springSecurityService.principal?.id)
      sPDC.utilizador = utilizador
      if(Credito.findAllByClienteAndInvalido(selectedCliente,false)){
        sPDC.frequencia = "renovação"
      }else {
        sPDC.frequencia = "novo"
      }
      sPDC.estado = "aberto"
      System.println(penhoras)
      sPDC.creditado = false
      sPDC.valorDaPenhora = somar()
      sPDC.cliente = selectedCliente
      sPDC.save(flush: true)
      def pedidoDB = PedidoDeCredito.findById(sPDC.id)
      System.println(pedidoDB.id)
      for(Penhora penhora in penhoras){
        penhora.pedidoDeCredito = pedidoDB
        penhora.cliente = selectedCliente
        penhora.save(flush: true)

      }




      if(pedidoDB!=null){
        pedidos.add(pedidoDB)
        info.value = "O Pedido de crédito foi criado com sucesso!"
        info.style = "color:blue;font-weight;font-size:14ptpt;background:back"
        pedidos.add(pedidoDB)

      }

    }catch(Exception e){
      System.println(e.toString())
    }

  }

  @Command
  @NotifyChange(['valor'])
  BigDecimal somar(){
    BigDecimal valor= 0.0
    penhoras.each {
      valor+=it.valor
    }
    return valor
  }

  boolean taxar(){
    if(settings.taxaManual){
      return taxaManual
    }else
    if (sPDC.definicaoDeCredito&&selectedCliente){
      //taxaService.calcularTaxas(selectedCliente,selectedDefinicaoDeCredito)
      def creditos = Credito.findAllByClienteAndPeriodicidadeAndInvalido(selectedCliente,sPDC.definicaoDeCredito.periodicidade,false) as List
      def reco_taxa = DefinicaoDeCredito.findById(sPDC.definicaoDeCredito.id).taxa.recorencia.toCharArray() as List
      def recorencia_taxa = new ArrayList<>()
      System.println(reco_taxa)
      if(creditos?.empty){
        return reco_taxa[0] != "0"

      }else {
        for(int x=0; x<creditos.size(); x++){
          recorencia_taxa.addAll(reco_taxa)
        }

        return recorencia_taxa[creditos.size()] != "0"

      }
    }
  }

  @Command
  @NotifyChange(["selectedLista"])
  def updateLista(){

    selectedLista.merge()
    info.value="Dados actualizados com sucesso!"
    info.style = "color:blue;font-weight;font-size:16px;background:back"
  }
}
