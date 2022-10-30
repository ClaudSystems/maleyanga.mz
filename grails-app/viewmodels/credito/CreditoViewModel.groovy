package credito

import mz.maleyanga.ClienteService
import org.springframework.stereotype.Service
import org.zkoss.zul.Label
import grails.plugin.springsecurity.SpringSecurityService
import mz.maleyanga.ContadorService
import mz.maleyanga.CreditoService
import mz.maleyanga.PagamentoService
import mz.maleyanga.SessionStorageService
import mz.maleyanga.SettingsService
import mz.maleyanga.cliente.Cliente
import mz.maleyanga.conta.Conta
import mz.maleyanga.ContaService
import mz.maleyanga.credito.Credito
import mz.maleyanga.pagamento.Pagamento
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.pedidoDeCredito.PedidoDeCredito
import mz.maleyanga.security.Utilizador
import mz.maleyanga.settings.DefinicaoDeCredito
import mz.maleyanga.settings.Settings
import mz.maleyanga.transacao.Transacao
import grails.transaction.Transactional
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Grid
import org.zkoss.zul.Hbox
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Tab

import java.sql.SQLException

@Transactional
@Service

class CreditoViewModel {

    @Wire Label info
    @Wire Hbox hb_editor
    @Wire Button bt_fechar

    private  boolean  allCreditos
    private  boolean  vw_bt_novo_credito = false
    String red = "color:red;font-size:18px;font-weight;background:back"
    String blue = "color:blue;font-size:18px;font-weight;background:back"
    ClienteService clienteService
    Utilizador utilizador
    private  boolean  allPagamentos
    ListModelList<Credito> creditos
    ListModelList<Credito> emPagamento
    ListModelList<Credito> pendentes
    ListModelList<Credito> fechados
    ListModelList<Credito> invalidos
    ListModelList<Pagamento> pagamentos
    String cliente_style
    Pagamento sPagamento
    Conta contaCapital
    Conta contaCliente
    private dataInicial
    private dataFinal
    @Wire Tab tb_abertos
    @Wire Grid gd_pagamento
    @Wire Button bt_salvar
    @Wire Hbox hb_novo_credito
    private PedidoDeCredito pedidoDeCredito
    private  Cliente selectedCliente
    ContadorService contadorService
    int numeroDePrestacoes
    private    String selecedPeriodicidade
    private String filterCliente
    private String filterCredito
    ContaService contaService
    PagamentoService pagamentoService
    CreditoService creditoService
    SessionStorageService sessionStorageService
    SpringSecurityService springSecurityService
    boolean mostrarCreditos = false
    private  Credito credito = new Credito()
    SettingsService settingsService
    private  Settings settings
    private  String juros
    private  String jurosDeMora
    private  String formaDeCalculo
    private  DefinicaoDeCredito selectedDefinicaoDeCredito
    private BigDecimal v_prestacao =0.0
    private BigDecimal v_divida = 0.0
    private  BigDecimal v_mora= 0.0
    private  BigDecimal v_pago=0.0
    private  BigDecimal v_juro=0.0
    private  BigDecimal v_amo=0.0
    private ListModelList<DefinicaoDeCredito> definicoes
    private Double totalPrestacoes = 0
    private Double totalamortizacao=0
    private Double totaljuros = 0
    private boolean taxaManual = true
    private boolean db_data = false
    private  String creditosDe = "Créditos"
    private Pagamento pagamento

    boolean getVw_bt_novo_credito() {
       return vw_bt_novo_credito
    }

    boolean getEditor() {
        return editor
    }

    @Command
    @NotifyChange(["pagamentos"])
    def salvarPagamento(){
        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "PAGAMENTO_CREATE" }) {
            info.value="Este utilizador não tem permissão para executar esta acção !"
            info.style = red
        }
        try {
           pagamento.save(flush: true)
            info.value="Operação feita com sucesso!"
            info.style = red
        }catch(SQLException e){
            info.value="Erro na gravação dos dados!"
            info.style = red
            System.println(e.toString())
        }
    }

    @Command
    @NotifyChange(["pagamentos","credito","sPagamento"])
    def deletePagamento(){
        try {
            Utilizador user = springSecurityService.currentUser as Utilizador
            if (!user.authorities.any { it.authority == "PAGAMENTO_DELETE" }) {
                info.value="Este utilizador não tem permissão para executar esta acção !"
                info.style = red
            }
           sPagamento.delete(flush: true)
            info.value="A Parcela Nº" +sPagamento.numeroDePagamento+" foi eliminada com sucesso!"
            info.style = blue
            getPagamentos()
        }catch(SQLException e){
            info.value="Erro na remoção da Parcela! detais:"+e.toString()
            info.style = red
        }
    }
    @Command
    @NotifyChange(["pagamento"])
    def addPagamento(){
        pagamento = new  Pagamento()
        pagamento.recorenciaDeMoras = credito.recorenciaDeMoras
        pagamento.setCredito(credito)
        pagamento.valorDeAmortizacao = 0.0
        pagamento.valorDeJuros = 0.0
        pagamento.saldoDevedor = 0.0
        pagamentos.sort{it.id}
        def num = pagamentos.last().numeroDePagamento as Integer
        num++
        pagamento.numeroDePagamento=num
    }
    Pagamento getPagamento() {
        return  pagamento
    }

    void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento
    }

    String getCliente_style() {
        cliente_style="background:#69F4AF;font-weight:bold;font-size:14ptpt"
        if(selectedCliente){
            if(Credito.findAllByClienteAndEmDivida(selectedCliente,true)){
                cliente_style="background:red;font-weight:bold;font-size:14ptpt"
            }else {
                selectedCliente.emDivida = false
                selectedCliente.merge()
            }

        }
        return cliente_style
    }

    boolean getAllCreditos() {
        return allCreditos
    }



    void setAllCreditos(boolean allCreditos) {
        this.allCreditos = allCreditos

    }
    boolean getDb_data() {
        return db_data
    }


    void setDb_data(boolean db_data) {
        this.db_data = db_data
    }
    @Command
    @NotifyChange(["db_data"])
    def showDataBox(){
        db_data = !db_data
    }

    String getCreditosDe() {
        if(selectedCliente){
            return "Créditos do(a): "+selectedCliente?.nome
        }

    }


    @NotifyChange(["taxa"])
    boolean getTaxa() {
        if(taxaManual){
            return true
        }else
            return taxar()
    }

    boolean getTaxaManual() {
        return taxaManual
    }

    @Command
    @NotifyChange(["taxaManual","taxa"])
    def aplicarTaxaManual() {
        if(settings.taxaManual){
            taxaManual = !taxaManual
        }else

        {
            info.value="Altere as definições de credito de forma a poder usar a taxa manual!"
            info.style = red
        }
    }


    def getDataInicial() {

        Calendar c = Calendar.getInstance()
        c.add(Calendar.MONTH,-1)
        dataInicial = c.getTime()
        return dataInicial
    }

    void setDataInicial(dataInicial) {
        this.dataInicial = dataInicial
    }

    def getDataFinal() {
        Calendar c = Calendar.getInstance()
        dataFinal = c.getTime()
        return dataFinal
    }

    void setDataFinal(dataFinal) {
        this.dataFinal = dataFinal
    }


    String getFilterCredito() {
        return filterCredito
    }

    void setFilterCredito(String filterCredito) {
        this.filterCredito = filterCredito
    }

    String getFilterCliente() {
        return filterCliente
    }

    @NotifyChange(["creditos","mostrarCreditos"])
    @Command
    def showCreditos(){
        if(selectedCliente==null){
            info.value = "Selecione um cliente!"
            return
        }
        tb_abertos.value = "Creditos do(a) "+selectedCliente.nome
        mostrarCreditos=true

    }
    @Command
    def alertDelete(){

        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "CREDITO_DELETE" }) {
            info.value="Este utilizador não tem permissão para executar esta acção !"
            info.style = red
        }
        if(credito.invalido){
            info.value="Este credito já foi invalidado!"
            info.style = red

        }
       /* if(credito.estado=="Pendente"||credito.estado=="Fechado"||credito.estado=="EmProgresso"){
            info.value="Este credito não pode ser invalidade!"
            info.style = "color:red;font-weight;font-size:16px;background:back"
        }*/

        else {
            info.value="Double Click para eliminar este item!"
            info.style = red
        }
    }
    @Command
    @NotifyChange(["creditos"])
    def delete(){

        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "CREDITO_DELETE" }) {
            info.value="Este utilizador não tem permissão para executar esta acção !"
            info.style = red
            return
        }
        if (credito.id == null) {
            info.value="Seleccione um credito!"
            info.style = red
            return
        }
        try {
            def pagamentos = Pagamento.findAllByCredito(credito)
            for(Pagamento p in pagamentos){
                for(Parcela parcela in p.parcelas){
                    if(parcela.valorPago>0.0||parcela.valorParcial>0.0){
                        info.value="Este credito não pode ser invalidado pois tem pagamentos efetivados!"
                        info.style = red
                        return
                    }
                }
            }
           /* for(Pagamento p in pagamentos){
                for(Parcela parcela in p.parcelas){
                    if(parcela.valorPago==0.0&&parcela.valorParcial==0.0){
                        parcela.pagamento==null
                        parcela.merge()
                        parcela.delete(flush: true)
                    }
                }
            }*/
            Credito creditoDb = Credito.findById(credito.id)
            Conta contaDb = Conta.findById(contaCapital.id)
            Transacao tCredito = new Transacao()
            Transacao tDebito = new Transacao()
            tCredito.setValor(creditoDb.valorCreditado)
            tDebito.setValor(creditoDb.valorCreditado)
            tCredito.descricao = "estorno do crédito Nº:"+creditoDb.numeroDoCredito
            tDebito.descricao =  "estorno do crédito Nº:"+creditoDb.numeroDoCredito
            System.println("tCredito"+tCredito.valor)
            System.println("tDebito"+tDebito.valor)
            tCredito.credito = true
            tDebito.credito = false
            tCredito.save(flush: true)
            tDebito.save(flush: true)
            if (contaDb.transacoes == null) {
                contaDb.transacoes = new LinkedHashSet<Transacao>()
            }
            if (contaCliente.transacoes == null) {
                contaCliente.transacoes = new LinkedHashSet<Transacao>()
            }
            contaDb.transacoes.add(tDebito)
            contaCliente.transacoes.add(tCredito)
            contaDb.merge(flush: true)
            contaCliente.merge(flush: true)

            for (Pagamento p in pagamentos) {

                p.valorDaPrestacao = 0.0
                p.valorDeJuros =0.0
                p.valorDeJurosDeDemora =0.0
                p.valorDeAmortizacao = 0.0
                p.valorDeJurosDeDemora =0.0
                p.merge(flash: true)
            }
            creditoDb.invalido = true


            creditoDb.valorCreditado=0.0
            creditoDb.merge(flush: true)
            info.value="Crédito invalidado com sucesso!"
            info.style = red

        }catch(Exception e){
            System.println(e.toString())
            info.value = "Erro na eliminação do crédito!"
            info.style = red
        }

    }
    BigDecimal getV_prestacao() {
        return v_prestacao
    }

    BigDecimal getV_divida() {
        return v_divida
    }

    BigDecimal getV_mora() {
        return v_mora
    }

    BigDecimal getV_pago() {
        return v_pago
    }

    BigDecimal getV_juro() {
        return v_juro
    }

    BigDecimal getV_amo() {
        return v_amo
    }

    def soma(){
        v_prestacao=0
        v_divida=0
        v_mora=0
        v_pago=0
        v_juro=0
        v_amo=0


        if(credito!=null)
            for (Pagamento p in credito.pagamentos){

                v_prestacao+=p.valorDaPrestacao
                v_divida+=p.totalEmDivida
                v_mora+=p.valorDeJurosDeDemora
                v_pago+=p.valorPago
                v_juro+=p.valorDeJuros
                v_amo+=p.valorDaRemissao
            }
    }




    void setFilterCliente(String filterCliente) {
        this.filterCliente = filterCliente
    }
    private ListModelList<Cliente> clientes
    private ListModelList<Pagamento> prestacoes


    @NotifyChange(["creditos","credito","vw_bt_novo_credito","selectedCliente"])
    @Command
    void doSearchCredito(){
        info.value=""
        if(!filterCredito?.empty){
            credito = Credito?.findByNumeroDoCredito(filterCredito)
        }
        if(credito?.invalido){
            info.value = "O Credito No."+credito.id+" é inválido!"
            info.style = red
            credito = null
            return
        }
        //  credito?.pagamentos?.each {pagamentoService.calcularJurosDeDemora(it)}
        soma()
        hb_editor.visible = false
        creditoService.credito = credito
        sessionStorageService.credito = credito


    }



    @Command
    @NotifyChange(["credito","pagamentos","v_amo","v_juro","v_pago","v_divida","v_mora","v_prestacao","hb_editor","clientes"])
    void doSearchCliente() {
      //  bt_add_novo_credito.visible = false

        info.value = ""
        clientes.clear()
        List<Cliente> allItems = clienteService.findAllByName(filterCliente)
        if (filterCliente != null &&! "".equals(filterCliente))
         {
            for (Cliente item : allItems) {
                clientes.add(item)

            }
             if(clientes.empty||clientes.empty){
                 info.value+="Cliente não indentificado !"
                 info.style = red
             }
        }
    }

    ListModelList<Pagamento> getPagamentos() {
        if(pagamentos==null){
            pagamentos = new ListModelList<Pagamento>()
        }
        pagamentos.clear()
        if(credito!=null){
            pagamentos = credito.pagamentos
        }
        soma()
        return pagamentos
    }

    @Command
    @NotifyChange(["creditos","allCreditos","info"])
    void setAllC() {
        allCreditos = !allCreditos
        getCreditos()
    }
    Pagamento getsPagamento() {
        return sPagamento
    }

    void setsPagamento(Pagamento sPagamento) {
        this.sPagamento = sPagamento
    }

    @Command
    @NotifyChange(["pagamentos","v_amo","v_juro","v_pago","v_divida","v_mora","v_prestacao",'credito','hb_editor',"db_data"])
    def showPagamentos(){
           for(Pagamento p in pagamentos.sort{it.dataPrevistoDePagamento}){
              for(Parcela parcela in p.parcelas){
                  pagamentoService.calcularMoras(p,parcela)
              }
           }

        hb_editor.visible = false
    }
    @Command
    @NotifyChange(["prestacoes","credito","editor"])
    def fecharEditor(){
        hb_novo_credito.visible=!hb_novo_credito.visible
        def creditos = Credito.findAllByClienteAndEmDivida(selectedCliente,true)
        if(!settings.permitirDesembolsoComDivida){
            if(selectedCliente){
                if(!creditos.empty){
                    hb_editor.visible=false
                    bt_fechar.label="Não é permitido desembolso de clientes com dívidas!"
                }

            }
        }
    }
    @Command
    def showCredito(){
        creditoService.credito = credito
        Executions.sendRedirect("/credito/show/"+credito.id)
    }
    ListModelList<Credito> getEmPagamento() {
        if(emPagamento ==null){
            emPagamento= new ListModelList<Credito>()
        }
        emPagamento.clear()
        emPagamento = Credito.findAllByEstado('EmProgresso')
        return emPagamento
    }

    ListModelList<Credito> getPendentes() {
        if(pendentes ==null){
            pendentes= new ListModelList<Credito>()
        }
        pendentes.clear()
        pendentes = Credito.findAllByEstado('Pendente')
        return pendentes
    }

    ListModelList<Credito> getFechados() {
        if(fechados ==null){
            fechados= new ListModelList<Credito>()
        }
        fechados.clear()
        fechados = Credito.findAllByEstadoAndInvalido('Fechado',false)
        return fechados
    }
    ListModelList<Credito> getInvalidos() {
        if(invalidos ==null){
            invalidos= new ListModelList<Credito>()
        }
        invalidos.clear()
        invalidos = Credito.findAllByInvalido(true)
        return invalidos
    }

    Conta getContaCliente() {
        if(selectedCliente==null){
            return new Conta()
        }
        if(selectedCliente?.id!=null){
            contaCliente = Conta.findByNumeroDaConta(selectedCliente.id.toString())
        }
        if(contaCliente==null){
            Conta conta = new Conta()
            conta.numeroDaConta = selectedCliente.id
            Integer cod = selectedCliente.id.toInteger()
            String str = String.format("%04d", cod)
            def contaDb = Conta.findByFinalidadeAndDesignacaoDaConta("conta_integradora", "CLIENTES")
            conta.codigo = contaDb.codigo + "." + str
            conta.designacaoDaConta = "conta_cliente" + '_' + conta.codigo
            conta.finalidade = 'conta_cliente'
            conta.conta = contaDb
            conta.ativo = contaDb.ativo
            conta.cliente = selectedCliente
            conta.save(flush: true)
            def  contaDB = Conta.findById(conta.id)
            if(contaDb){
                return contaDB
            }
        }
     return contaCliente
    }
    Conta getContaCapital() {
        return contaCapital
    }

    @NotifyChange(['contaCapital'])
    def geTContaCapital(){
        for(Conta c in utilizador.contas){
            if(c.finalidade=="conta_capital"){
                contaCapital= c
            }
        }
    }

    Utilizador getUtilizador() {
        return utilizador
    }

    @Command
    ListModelList<Credito> getCreditos() {
        if(creditos==null){
            creditos = new ListModelList<Credito>()
        }
        creditos.clear()
       if(mostrarCreditos){
           if(selectedCliente?.id!=null){
               if(allCreditos){
                   creditos = creditoService.findAllByCliente(selectedCliente)
               }else{
                   creditos = creditoService.findAllByClienteAndEmDivida(selectedCliente)
               }
           }
       }

        return creditos
    }
    void setNumeroDePrestacoes(int numeroDePrestacoes) {
        this.numeroDePrestacoes = numeroDePrestacoes
    }

    String getSelecedPeriodicidade() {
        return selecedPeriodicidade
    }

    @NotifyChange(["selectedDefinicaoDeCredito"])
    void setSelecedPeriodicidade(String selecedPeriodicidade) {
        cb_def.children.clear()

        selectedDefinicaoDeCredito = null

        this.selecedPeriodicidade = selecedPeriodicidade
    }

    ListModelList<DefinicaoDeCredito> getDefinicoes() {

        if(definicoes==null){
            definicoes = new ListModelList<>(DefinicaoDeCredito.findAllByAtivo(true))
        }

        return definicoes
    }

    @Command
    @NotifyChange(["contaCliente","contaCapital"])
    def showDefinicoesDeCredito(){
         if(definicoes?.empty){
            Executions.sendRedirect("/settings/defCredito/")
        }

    }
    @NotifyChange(["definicaoDeCredito"])
    DefinicaoDeCredito getSelectedDefinicaoDeCredito() {
        if(creditoService?.pedidoDeCredito!=null){
            return creditoService?.pedidoDeCredito?.definicaoDeCredito
        }
        return selectedDefinicaoDeCredito
    }

    @NotifyChange(["selecedPeriodicidade","selectedDefinicaoDeCredito"])
    void setSelectedDefinicaoDeCredito(DefinicaoDeCredito selectedDefinicaoDeCredito) {
        this.selectedDefinicaoDeCredito = selectedDefinicaoDeCredito

    }

    Double getTotalPrestacoes() {

        return totalPrestacoes
    }

    Double getTotalamortizacao() {
        return totalamortizacao
    }

    Double getTotaljuros() {
        return totaljuros
    }

    ListModelList<Pagamento> getPrestacoes() {
        prestacoes=null
        if(prestacoes==null){
            prestacoes = new ListModelList<Pagamento>()
        }
        if(credito!=null){
            if (credito.periodicidade!=""&&credito.valorCreditado>0&&credito.numeroDePrestacoes){
                def prestacoes = pagamentoService.simuladorDeCredito(credito)
                totalPrestacoes = 0
                totaljuros = 0
                totalamortizacao = 0
                for(Pagamento p in prestacoes){
                    totaljuros +=p.valorDeJuros
                    totalamortizacao+=p.valorDeAmortizacao
                    totalPrestacoes+=p.valorDaPrestacao
                }
                return  prestacoes
            }
        }

        return prestacoes

    }

    Cliente getSelectedCliente() {
        /*def cliente = sessionStorageService.getCliente() as Cliente
        if(cliente){
            selectedCliente = cliente
        }
        if(sessionStorageService)*/
        return selectedCliente
    }

    @NotifyChange(["creditosDe","pedidos","selectedCliente","contaCliente","clientes","cliente_style","credito","vw_bt_novo_credito"])
    void setSelectedCliente(Cliente selectedCliente) {
        info.value = ""
        sessionStorageService.credito = null
        credito =null
        this.selectedCliente = selectedCliente
        sessionStorageService.cliente = selectedCliente
        clientes.clear()
        clientes.add(selectedCliente)
        addCredito()

    }
    @NotifyChange(['creditos',"credito"])
    @Init init() {
        sessionStorageService.credito = null
        utilizador = Utilizador.findById(springSecurityService.principal?.id)
        settings = settingsService.getSettings()
        creditoService.pagamentos = null
        creditoService.credito = null
        geTContaCapital()

        if(creditoService?.pedidoDeCredito?.id){
            credito = new Credito()
            credito.valorCreditado = creditoService.pedidoDeCredito.valorDeCredito
            selectedCliente = Cliente.findById(creditoService.pedidoDeCredito.cliente.id)
            pedidoDeCredito = creditoService.pedidoDeCredito

            selectedDefinicaoDeCredito = creditoService.pedidoDeCredito.definicaoDeCredito
            creditoService.pedidoDeCredito = null
        }
    }
    ListModelList<Cliente> getClientes() {
        if(clientes==null){
            clientes = new ListModelList<Cliente>()
        }
        return clientes
    }
    Credito getCredito() {
        return credito
    }

    @Command
    @NotifyChange(["credito","pagamentos"])
    def capitalizar(){
        try {
            Utilizador user = springSecurityService.currentUser as Utilizador
            if (!user.authorities.any { it.authority == "PAGAMENTO_CREATE" }) {
                info.value = "Este utilizador não tem permissão para executar esta acção !"
                info.style =red
                return
            }
            pagamentoService.calcularMoraCaPital(credito)
            info.value = "Capitalização feita com sucesso!"
            getPagamentos()
        }catch(Exception e){
            info.value = "Erro na capitalização, por favor faz o refresh da pagina e inicie de novo o processo!"
            System.println(e.toString())
        }

    }

    @Command
    def updateDatas(){
        info.value= ""
        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "PAGAMENTO_EDIT" }) {
            info.value="Este utilizador não tem permissão para executar esta acção !"
            info.style = red

        }else {
           try {

               pagamentoService.udateDatas(credito)
               info.value = "Datas actualizadas com sucesso!"
           }catch(Exception e){
               info.style= red
               info.value = "Erro na atualização dos dados, por favor faz o refresh da pagina e inicie de novo o processo!"
               System.println(e.toString())
           }
        }

    }

    @Command
    @NotifyChange(["credito","pagamentos"])
    def removerCapitalizacoes(){
        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "PAGAMENTO_DELETE" }) {
            info.value="Este utilizador não tem permissão para executar esta acção !"
            info.style = red
            return
        }
        try {
            pagamentoService.eliminarCapitalizacoes(credito)
            info.value = "Capitalização removidas com sucesso!"

        }catch(Exception e){
            info.value = "Erro na eliminação dos dados, por favor faz o refresh da pagina e inicie de novo o processo!"
            System.println(e.toString())
        }

    }

    @NotifyChange(["pagamentos","credito"])
    void setCredito(Credito credito) {
        info.value = ""
        this.credito = credito
        creditoService.credito = credito
        sessionStorageService.setCredito(credito)
        getPagamentos()
        soma()
      }

    Settings getSettings() {
        return settings
    }

    String getFormaDeCalculo() {
        return formaDeCalculo
    }

    @Command
    def verificarData(){
        info.value = ""
           Credito lastCredito = Credito.last()
           if(lastCredito.cliente==selectedCliente&&!lastCredito.invalido){
               info.value = "Este CLiente ja foi desembolsado!"
               hb_novo_credito.visible = false
           }
    }

    @Command
    @NotifyChange(["juros","jurosDeMora","formaDeCalculo","info","prestacoes","totalPrestacoes","totaljuros","totalamortizacao","credito","contaCapital","contaCliente","taxa"])
    def showDetails(){
        info.value= ""

        geTContaCapital()
       // getClientes()
        if(definicoes?.empty){
            Executions.sendRedirect("/settings/defCredito/")
        }

       // gd_parcelas.visible=true
        credito.numeroDePrestacoes = numeroDePrestacoes
        if(selectedDefinicaoDeCredito!=null){
            credito.periodicidade = selectedDefinicaoDeCredito.periodicidade
            credito.recorenciaDeMoras = selectedDefinicaoDeCredito.recorenciaDeMoras
            credito.formaDeCalculo = selectedDefinicaoDeCredito.formaDeCalculo
            credito.percentualJurosDeDemora = selectedDefinicaoDeCredito.percentualJurosDeDemora
            credito.percentualDejuros = selectedDefinicaoDeCredito.percentualDejuros
            juros=selectedDefinicaoDeCredito.percentualDejuros
            jurosDeMora=selectedDefinicaoDeCredito.percentualJurosDeDemora
            formaDeCalculo = selectedDefinicaoDeCredito.formaDeCalculo


            if (selectedDefinicaoDeCredito.numeroDePrestacoes<credito.numeroDePrestacoes){
                info.value = "O número de prestações não permitido"
                info.style = red
                credito.numeroDePrestacoes = selectedDefinicaoDeCredito.numeroDePrestacoes
            }
         //   getPrestacoes()
        }
        bt_salvar.visible=true
        bt_salvar.focus()
    }
    boolean taxar(){
        if(settings.taxaManual){
            return taxaManual
        }else
        if (selectedDefinicaoDeCredito&&selectedCliente){
            //taxaService.calcularTaxas(selectedCliente,selectedDefinicaoDeCredito)
            def creditos = Credito.findAllByClienteAndPeriodicidadeAndInvalido(selectedCliente,selectedDefinicaoDeCredito.periodicidade,false) as List
            def reco_taxa = DefinicaoDeCredito.findById(selectedDefinicaoDeCredito.id).taxa.recorencia.toCharArray() as List
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
    String getJuros() {
        return juros
    }

    String getJurosDeMora() {
        return jurosDeMora
    }
    @Command
    @NotifyChange(['credito',"creditos",'contaCapital','contaCliente'])
    def salvarCredito(){
        info.value = ""
        if(selectedCliente==null){
            System.println("selectedCliente.id")
            info.value = "Selecione um cliente!"
            info.style = red
            return
        }

        if(selectedCliente.id==Credito.last().cliente.id){
            info.value = "ESTE CLIENTE JÁ FOI DESEMBOLSADO!"
            hb_editor.visible = false
            return
        }
        info.value=""
        if(contaCliente==null){
            getContaCliente()
        }
        if(contaCliente==null){
            info.value = "A conta CLIENTE não foi detectado!"
            info.style = "color:red;font-weight;font-size:16px;background:back"
            return
        }
        if(credito.dateConcecao>new Date()){
            info.value = "Data de conceção é inválida!"
            info.style = red
            credito.dateConcecao = new Date()
        }
        if(credito.dateConcecao>new Date()){
            info.value = "Data de conceção é inválida!"
            info.style = red
            credito.dateConcecao = new Date()
        }
        if(contaCapital==null){
            info.value = "Este Utilizador não tem conta capital para gerar créditos!"
            info.style = red
            return
        }
        if(contaCapital.saldo<credito.valorCreditado){
            info.value = "O seu saldo não cobre o valor do crédito!"
            info.style = red
            return
        }
        if(selectedCliente==null){
            info.value = "Selecione um cliente!"
            info.style = red
            return
        }
        if(!settings.permitirDesembolsoComDivida){
              if(selectedCliente.emDivida){
                    info.value = "Este cliente tem dívida!"

                    info.style = red
                  return
              }
        }
        if(credito.valorCreditado<=0){
            info.value = "O valor do crédito é inválido"
            info.style = red
            return

        }
        try {


            credito.cliente=selectedCliente

            credito.utilizador = utilizador
            def agora = new Date()
            Calendar c = Calendar.getInstance()
            c.setTime(agora)
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
            if (!settings.domingo&&dayOfWeek==1){
                info.value = "Dia não permitido!"
                info.style = red
                return
            }
            if (!settings.segunda&&dayOfWeek==2){
                info.value = "Dia não permitido!"
                info.style = red
                return
            }
            if (!settings.terca&&dayOfWeek==3){
                info.value = "Dia não permitido!"
                info.style = red
                return
            }
            if (!settings.quarta&&dayOfWeek==4){
                info.value = "Dia não permitido!"
                info.style = red
                return
            }
            if (!settings.quinta&&dayOfWeek==5){
                info.value = "Dia não permitido!"
                info.style = red
                return
            }
            if (!settings.sexta&&dayOfWeek==6){
                info.value = "Dia não permitido!"
                info.style = red
                return
            }
            if (!settings.sabado&&dayOfWeek==7){
                info.value = "Dia não permitido!"
                info.style = red
                return
            }
            if (!settings.permitirDesembolsoComDivida){
                if(selectedCliente.emDivida){
                    info.value = "Este cliente tem dívida!"
                    info.style = red
                    return
                }

            }
            if (!selectedCliente.ativo){
                info.value ="Este cliente esta impedido de ter novos crédito!"
                info.style = red
                return
            }

            if(settings.atualizarDadosDoCliente){
                if (credito.cliente.dataDeExpiracao<agora){
                    info.value ="Este cliente esta com documento fora do prazo!"
                    info.style = red
                    return
                }
            }

            credito.emDivida=true
            credito.invalido=false
            if(taxar()){
                credito.taxas = credito.valorCreditado*DefinicaoDeCredito.findById(selectedDefinicaoDeCredito.id).taxa.percentagem/100
            }

            credito.setNumeroDoCredito(contadorService.gerarNumeroDoCredito())
            credito.periodoVariavel = selectedDefinicaoDeCredito.periodoVariavel
            credito.estado ="Aberto"
            credito.ignorarValorPagoNoPrazo = settings.ignorarValorPagoNoPrazo
            credito.save(failOnError: true,flush: true)
            pagamentoService.criarPagamentos(credito,selectedDefinicaoDeCredito)
/*

            Transacao tCredito = new Transacao()
            Transacao tDebito = new Transacao()
            tCredito.setValor(credito.valorCreditado)
            tDebito.setValor(credito.valorCreditado)
            tCredito.descricao = "debito a conta:"+contaCliente.codigo
            tDebito.descricao = "credito da conta:"+contaCapital.codigo
            System.println("tCredito"+tCredito.valor)
            System.println("tDebito"+tDebito.valor)
            tCredito.credito = true
            tDebito.credito = false
            tCredito.save(flush: true,failOnError: true)
            tDebito.save(flush: true,failOnError: true)

            contaCapital = Conta.findById(contaCapital.id)
            if (contaCapital.transacoes == null) {
                contaCapital.transacoes = new LinkedHashSet<Transacao>()
            }
            if (contaCliente.transacoes == null) {
                contaCliente.transacoes = new LinkedHashSet<Transacao>()
            }
            contaCapital.transacoes.add(tCredito)
            contaCliente.transacoes.add(tDebito)
            contaCapital.merge(flush: true)
            contaCliente.merge(flush: true)
*/

            info.value = "O Credito No."+credito.numeroDoCredito+" do(a) "+selectedCliente.nome+" foi criado com sucesso!"
            info.style = blue
            bt_salvar.visible = false

          /*  geTContaCapital()
            getClientes()
            if(pedidoDeCredito?.id){
                PedidoDeCredito pdcDB = PedidoDeCredito.findById(pedidoDeCredito?.id)
                pdcDB.creditado = credito
                pdcDB.merge(flush: true)
            }*/

            sessionStorageService.credito = credito


        }catch (Exception e){
            info.value = e.toString()
            info.style= red

        }
        bt_salvar.visible = false
    }
    @Command
    @NotifyChange(["credito","prestacoes","editor","creditos","numeroDePrestacoes"])
    def addCredito(){
        info.value = ""
        if(selectedCliente==null){
            info.value = "Selecione Um Cliente"
        }
        credito = new Credito()
        credito.dateConcecao = new Date()
        numeroDePrestacoes = 0
        creditos.clear()


       // prestacoes.clear()

    }
    /*@Command
    def printExtrato(){
        if(credito.invalido){
            info.value = "O Credito No."+credito.id+" é inválido!"
            info.style = red
            return
        }
       // reCapitalizar()
        creditoService.credito = credito
        creditoService.pagamentos = getPagamentos()
        //  Executions.sendRedirect("/credito/printExtratoDeCredito")
    }*/

    @Command
    @NotifyChange(["credito"])
    def reCapitalizar(){
        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "PAGAMENTO_DELETE" }) {
            info.value="Este utilizador não tem permissão para executar esta acção !"
            info.style = red
            return
        }
        try {
            if(credito.id!=null){
                List<Pagamento> pagamentos = Pagamento.findAllByCredito(credito)
                System.println(credito.pagamentos)
                for(Pagamento pagamento in pagamentos){
                    if(pagamento.descricao=="CAPITALIZACAO"){
                        credito.pagamentos.remove(pagamento)
                        pagamento.save()
                        credito.merge(flush: true)

                    }
                }
                List<Pagamento> pagaments = Pagamento.findAllByCredito(credito)
                /*for(Pagamento pag in pagaments){
                    pagamentoService.calcularMoras(pag)

                }*/
                pagamentoService.calcularMoraCaPital(credito)
            }

            // info.value = "Recapitalização feita com sucesso!"
            info.style = red
        }catch(Exception e){
            info.value = "Erro na Recapitalização!, por favor, faça refresh e tente novamente!"
            info.style = red
        }


    }
    @Command
    def editPagamento(){
        if(credito.invalido){
            return
        }
        pagamentoService.pagamentoInstance =sPagamento
        Executions.sendRedirect("/pagamento/pagamentos")
    }
    @Command
    def printPlanoDePagamento(){
        if(credito.invalido){
            info.value = "O Credito No."+credito.id+" é inválido!"
            info.style = red
            return
        }
        creditoService.credito = credito
        sessionStorageService.credito = credito
        creditoService.pagamentos = getPagamentos()
        //  Executions.sendRedirect("/credito/printPlano")
    }

    @Command
    @NotifyChange(['items',"novaConta"])
    def printExtratoCliente (){
        def rconta = Conta.findByNumeroDaConta(credito.id.toString())
        if(rconta==null){
            info.value = "Selecione uma conta!"
            info.style =red
            return
        }
        if(dataFinal==null){
            info.value = "Selecione uma data!"
            info.style = red
            return
        }
        if(dataInicial==null){
            info.value = "Selecione uma data!"
            info.style = red
            return
        }
        contaService.conta = rconta
        contaService.credito = credito
        contaService.dataInicial = dataInicial
        contaService.dataFinal = dataFinal
        Executions.sendRedirect("/conta/printExtratoDeConta/")
    }


    /*Pedidos de credito*/
    private   PedidoDeCredito sPDC
    private ListModelList<PedidoDeCredito> pedidos
    PedidoDeCredito getsPDC() {
        return sPDC
    }

    void setsPDC(PedidoDeCredito sPDC) {
        this.sPDC = sPDC
    }


    ListModelList<PedidoDeCredito> getPedidos() {
        if(pedidos==null){
            pedidos = new ListModelList<PedidoDeCredito>()
        }
        if(selectedCliente){
            pedidos = PedidoDeCredito.findAllByCliente(selectedCliente)
        }
        return pedidos
    }
    /* fim do pedido de credito*/

}
