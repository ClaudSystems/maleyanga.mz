package settings

import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import mz.maleyanga.SettingsService
import mz.maleyanga.Taxa.Taxa
import mz.maleyanga.security.Utilizador
import mz.maleyanga.settings.DefinicaoDeCredito
import mz.maleyanga.settings.Settings

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Div
import org.zkoss.zul.Grid
import org.zkoss.zul.Label
import org.zkoss.zul.ListModelList
import java.sql.SQLDataException
import java.sql.SQLException
@Transactional
class DefCreditoViewModel {
    SettingsService settingsService
    @Wire Button cfm
    @Wire Button bt_salvar
    @Wire Button cfq
    @Wire Button cfs
    @Wire Button cfd
    @Wire Button cfdd
    @Wire Label info
    @Wire Div dv_editor
    Settings settings
    String selecedFormaDeCalculo
    String selecedPeriodicidade
    private  String pagamentosEmOrdem
    DefinicaoDeCredito selectedDefinicaoDeCredito
    boolean def_fixa = true
    private ListModelList<DefinicaoDeCredito> definicoes
    private ListModelList<Taxa> taxas
    private  Taxa taxa
    boolean taxaManual
    boolean variavel = false
    String pdjda
    String taxaManua
    SpringSecurityService springSecurityService

    @Command
    @NotifyChange(["settings"])
    def ignorarPagDentroDoPrazo(){

        settings = Settings.findByNome("settings")
        settings.ignorarValorPagoNoPrazo= !settings.ignorarValorPagoNoPrazo
        settings.merge(flush: true)
        info.value ="Dados atualizados com sucesso!"+settings.ignorarValorPagoNoPrazo
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }

    @Command
    @NotifyChange(["settings"])
    def pagarEmSequencia(){
        settings = Settings.findByNome("settings")
        settings.pagarEmSequencia= !settings.pagarEmSequencia
        settings.merge(flush: true)
        info.value ="Dados atualizados com sucesso!"+settings.pagarEmSequencia
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }

    @Command
    @NotifyChange(["settings"])
    def altearDataDePagamento(){
        settings = Settings.findByNome("settings")
        settings.altearDataDePagamento= !settings.altearDataDePagamento
        settings.merge(flush: true)
        info.value ="Dados atualizados com sucesso!"+settings.altearDataDePagamento
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }
     boolean getVariavel() {
        return variavel
    }

    String getTaxaManua() {
        return settings.taxaManual.toString()
    }

    Taxa getTaxa() {
        return taxa
    }

    void setTaxa(Taxa taxa) {
        this.taxa = taxa
    }

    ListModelList<Taxa> getTaxas() {
        if(taxas==null){
            taxas = new ListModelList<Taxa>(Taxa.findAllByActivo(true))
        }
        return taxas
    }
    DefinicaoDeCredito definicaoDeCredito = new DefinicaoDeCredito()

    String getPagamentosEmOrdem() {
        return settings?.pagamentosEmOrdem?.toString()
    }

    void setPagamentosEmOrdem(String pagamentosEmOrdem) {

        this.pagamentosEmOrdem = pagamentosEmOrdem
    }

    String getCalcularAutomatico() {
        return settings?.calcularAutomatico?.toString()
    }

    String getPermitirDesembolsoComDivida() {
        return settings?.permitirDesembolsoComDivida?.toString()
    }

    DefinicaoDeCredito getSelectedDefinicaoDeCredito() {
        return selectedDefinicaoDeCredito
    }

    @NotifyChange(["definicaoDeCredito","selecedPeriodicidade","selecedFormaDeCalculo"])
    void setSelectedDefinicaoDeCredito(DefinicaoDeCredito selectedDefinicaoDeCredito) {
        this.selectedDefinicaoDeCredito = selectedDefinicaoDeCredito
        showItem()
    }

    String getSelecedPeriodicidade() {
        return selecedPeriodicidade
    }

    @NotifyChange(["variavel"])
    void setSelecedPeriodicidade(String selecedPeriodicidade) {
        variavel = selecedPeriodicidade == "variavel"
        this.selecedPeriodicidade = selecedPeriodicidade
    }

    DefinicaoDeCredito getDefinicaoDeCredito() {
        return definicaoDeCredito
    }

    void setDefinicaoDeCredito(DefinicaoDeCredito definicaoDeCredito) {
        this.definicaoDeCredito = definicaoDeCredito
    }

    @Command
    def fecharEditor(){
        dv_editor.visible = !dv_editor.visible
    }

    @Command
    @NotifyChange(["setting"])
    def changeViewGdPrestacoes(){
        setting.gd_prestacoes = !setting.gd_prestacoes
    }
    ListModelList<DefinicaoDeCredito> getDefinicoes() {

        if(definicoes==null){
            definicoes = new ListModelList<>()
        }
        definicoes.clear()
        definicoes= DefinicaoDeCredito.all
        definicoes.sort{it.id}
        return definicoes
    }

    @Command
    @NotifyChange(["definicoes"])
    def saveDefCredito(){
        if(definicaoDeCredito.descricao==""){
            info.value = "Preencha o campo 'Descrição'!"
            return
        }
        if(selecedPeriodicidade=="variavel"&&definicaoDeCredito.periodoVariavel.equals(null)){
            info.value = "Indique o Nº de dias' em 'Periocicidade==variavel'!"
            return
        }
        try {
                if(DefinicaoDeCredito.findByDescricao(definicaoDeCredito.descricao)){
                    info.value = "Já existe um valor com esta desrição"
                    return
                }

            definicaoDeCredito.periodicidade = selecedPeriodicidade
            definicaoDeCredito.formaDeCalculo = selecedFormaDeCalculo

            definicaoDeCredito.ativo=true

              definicaoDeCredito.save(flush: true)
            if(DefinicaoDeCredito.findById(definicaoDeCredito.id)){
                getDefinicoes()
                info.value= "A definição de crédito foi criado com sucesso"
                info.style= "color:blue;font-size:14pt"
                definicaoDeCredito = new DefinicaoDeCredito()
            }else {
                info.value= "Erro na gravação dos dados"
                info.style= "color:red;font-size:14pt"
            }


        }catch(SQLException e){
            System.println(e.toString())
        }
    }

    @Command
    @NotifyChange(["definicoes"])
    def deleteDefCredito(){
        if (selectedDefinicaoDeCredito==null){
            info.value = "Selecione o item que deseja eliminar!"
            info.style= "color:red;font-size:14pt"
        }
        try {

                selectedDefinicaoDeCredito.delete(flush: true)
            info.value = "A Definição de crédito "+selectedDefinicaoDeCredito.descricao+" foi eliminado com sucesso!"
            info.style= "color:blue;font-size:14pt"

            getDefinicoes()

        }catch(SQLDataException e){
            System.println(e.toString())
        }
    }

    @Command
   def showDelMessage(){
        info.value="Faça double click para eliminar o item "+selectedDefinicaoDeCredito.descricao

    }
    @Command
    @NotifyChange(["def_fixa","definicaoDeCredito"])
    def changeAtivo(){
        if(definicaoDeCredito.ativo){
            definicaoDeCredito.ativo=false
        }
        else{
            definicaoDeCredito.ativo=true
        }

    }
    @Command
    @NotifyChange(["definicoes","selectedDefinicaoDeCredito","def_fixa","definicaoDeCredito","def_fixa","selecedPeriodicidade","selecedFormaDeCalculo"])
    def showItem(){
        definicaoDeCredito= DefinicaoDeCredito.findById(selectedDefinicaoDeCredito.id)
        selecedPeriodicidade=definicaoDeCredito.periodicidade
        selecedFormaDeCalculo = definicaoDeCredito.formaDeCalculo
        info.value = "O item "+selectedDefinicaoDeCredito.descricao +" foi selecionado!"
    }
    @Command
    @NotifyChange(["def_fixa","definicaoDeCredito","definicoes"])
    def addDef(){
        definicaoDeCredito = new DefinicaoDeCredito()
        definicaoDeCredito.ativo =true

        if(def_fixa){
            def_fixa=false
        }else {def_fixa=true}
       definicaoDeCredito = new DefinicaoDeCredito()
        dv_editor.visible = true
    }
    String getSelecedFormaDeCalculo() {
        return selecedFormaDeCalculo
    }

    void setSelecedFormaDeCalculo(String selecedFormaDeCalculo) {
        this.selecedFormaDeCalculo = selecedFormaDeCalculo
    }
    boolean getDef_fixa() {
        return def_fixa
    }


    @Init init() {
        settings = Settings.findByNome("settings")
        getPagamentosEmOrdem()
    }

    @Command
    @NotifyChange(["definicaoDeCredito","definicoes"])
    def salvarSettings(){
        definicaoDeCredito.merge(flush: true)
        info.value = "Definições de crédito actualizados com sucesso!"

    }


    @NotifyChange(["info",'cfm','cfq','cfs','cfd'])
    Settings getSettings() {
        return settingsService.getSettings()
    }

    @NotifyChange(["settings"])
    @Command
    void setSettings(Settings settings) {
        this.settings = settings
      }

    @Command
    @NotifyChange(["settings"])
    def changeValueCfm(){
        if(settings.creditoFixoMensal){
            settings.creditoFixoMensal = false
            System.println(settings.creditoFixoMensal)
        }else {
            settings.creditoFixoMensal= true
            System.println(settings.creditoFixoMensal)
        }
    }
    @NotifyChange(["settings","info",'cfm','cfq','cfs','cfd','cfdd'])
    @Command
    def changeValueCfq(){
        if(settings.creditoFixoQuinzenal){
            settings.creditoFixoQuinzenal = false
        }else {
            settings.creditoFixoQuinzenal= true
        }
    }
    @NotifyChange(["settings","info",'cfm','cfq','cfs','cfd','cfdd'])
    @Command
    def changeValueCfs(){
        if(settings.creditoFixoSemanal){
            settings.creditoFixoSemanal = false
        }else {
            settings.creditoFixoSemanal= true
        }
    }
    @NotifyChange(["settings","info",'cfm','cfq','cfs','cfd','cfdd'])
    @Command
    def changeValueCfdd(){
        if(settings.creditoFixoDoisDias){
            settings.creditoFixoDoisDias = false
        }else {
            settings.creditoFixoDoisDias= true
        }
    }
    @NotifyChange(["settings","info",'cfm','cfq','cfs','cfd','cfdd'])
    @Command
    def changeValueCfd(){
        if(settings.creditoFixoDiario){
            settings.creditoFixoDiario = false
        }else {
            settings.creditoFixoDiario= true
        }
    }

    @Command
    @NotifyChange(["settings","calcularAutomatico"])
    def calcularMorasAutomatico(){
        settings = Settings.findByNome("settings")
        settings.calcularAutomatico= !settings.calcularAutomatico
        settings.merge(flush: true)
        info.value ="Dados atualizados com sucesso!"+settings.calcularAutomatico
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }


    @NotifyChange(["settings","permitirDesembolsoComDivida"])
    @Command
    def desembolsarComDividas(){
        try{
            Utilizador user = springSecurityService.currentUser as Utilizador
            if (!user.authorities.any { it.authority == "SETTINGS_UPDATE" }) {
                info.value = "Este utilizador não tem permissão para executar esta acção !"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                return
            }
            settings = Settings.findByNome("settings")
            if(settings.permitirDesembolsoComDivida){
                settings.permitirDesembolsoComDivida=false
            }else {
                settings.permitirDesembolsoComDivida=true
            }

            settings.merge(flush: true)
            info.value ="Dados atualizados com sucesso! "+settings.permitirDesembolsoComDivida
            info.style = "color:red;font-weight;font-size:14ptpt;background:back"
        }catch(Exception e){
            info.value = e.toString()
        }

    }

    Settings getSetting() {
        return Settings.findByNome("settings")
    }

    void setSetting(Settings setting) {
        this.setting = setting
    }

    @NotifyChange(["setting","definicaoDeCredito"])
    @Command
    def excluirSabados(){

        definicaoDeCredito.excluirSabados = !definicaoDeCredito.excluirSabados
        info.value ="Excluir sábados ! == "+definicaoDeCredito.excluirSabados
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }

    @NotifyChange(["setting","definicaoDeCredito"])
    @Command
    def excluirPagNosSabados(){

        definicaoDeCredito.excluirDiaDePagNoSabado = !definicaoDeCredito.excluirDiaDePagNoSabado
        info.value ="Excluir Pagamentos nos sábados == "+definicaoDeCredito.excluirDiaDePagNoSabado
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }

    @NotifyChange(["setting","definicaoDeCredito"])
    @Command
    def excluirDomingos(){
        definicaoDeCredito.excluirDomingos = !definicaoDeCredito.excluirDomingos
        info.value ="Excluir Domingos == "+definicaoDeCredito.excluirDomingos
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }
    @NotifyChange(["setting","definicaoDeCredito"])
    @Command
    def excluirPagNosDomingos(){
        definicaoDeCredito.excluirDiaDePagNoDomingo = !definicaoDeCredito.excluirDiaDePagNoDomingo
        info.value ="Excluir pagamentos nos domingos =="+definicaoDeCredito.excluirDiaDePagNoDomingo
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }

         @NotifyChange(["definicaoDeCredito","definicoes"])
         @Command
        def updateDados (){
             settings = Settings.findByNome("settings")
             settings.conta1 = settings.conta1
             settings.conta2 = settings.conta2
             settings.pagamentosEmOrdem = settings.pagamentosEmOrdem
            settings.merge(flush: true)
        info.value ="Dados actualizados com sucesso! "
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
        }

    @Command
    @NotifyChange(["settings","pagamentosEmOrdem"])
    def ordenarPagamentos(){
        settings = Settings.findByNome("settings")
        settings.pagamentosEmOrdem =!settings.pagamentosEmOrdem
        settings.merge(flush: true)
        info.value ="Dados actualizados com sucesso!"+settings.pagamentosEmOrdem
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"


    }

    @Command
    @NotifyChange (["taxaManual","taxaManua","settings"])
    def taxarManualmente(){
        taxaManual =!taxaManual
        settings = Settings.findByNome("settings")
        settings.taxaManual =!settings.taxaManual
        settings.merge(flush: true)
        info.value ="Dados atualizados com sucesso!"+settings.taxaManual
        info.style = "color:red;font-weight;font-size:14ptpt;background:back"
    }
}
