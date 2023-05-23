package credito

import mz.maleyanga.CurrencyWriter
import mz.maleyanga.PenhoraService
import mz.maleyanga.SessionStorageService
import mz.maleyanga.SettingsService
import mz.maleyanga.credito.Credito
import mz.maleyanga.entidade.Entidade
import mz.maleyanga.pedidoDeCredito.PedidoDeCredito
import mz.maleyanga.pedidoDeCredito.Penhora
import mz.maleyanga.settings.Settings
import org.springframework.stereotype.Service
import org.zkoss.bind.annotation.Init
import org.zkoss.zul.ListModelList


@Service
class PrintContratoDeCreditoViewModel {
    Settings settings
    SettingsService settingsService
    SessionStorageService sessionStorageService
    private  Credito credito
    String valorCreditadoPorExtenso
    String percentualPorExtenso
    String prestacao
    String representante
    String numeroDeRegisto
    private  PedidoDeCredito pedidoDeCredito
    BigDecimal valorDaPrestacao
    BigDecimal valorAPagar
    String valorAPagarExt
    String valorDaPrestacaoExt
    String percentualDeMorasExt
    BigDecimal valorDaComissao
    String valorDaComissaoExt
    String valorDeJurosExt
    PenhoraService penhoraService

    BigDecimal getValorDaComissao() {

        return credito.valorCreditado*0.02
    }

    String getValorDeJurosExt() {
        return currencyWriter.write(credito.valorDeJuros)
    }

    String getValorDaComissaoExt() {
        return currencyWriter.write(getValorDaComissao())
    }
    CurrencyWriter currencyWriter = CurrencyWriter.getInstance()

    Settings getSettings() {
        return settings
    }

    String getRepresentante() {
        if(entidade.genero=="femenino"){
            representante=", neste acto representada pela Senhora "
        }else {
            representante  = ", neste acto representado pela Senhor "
        }
        return representante
    }

    String getNumeroDeRegisto() {
        if(entidade.numeroDeRegisto.empty){
            return ""
        }else
        return ""
    }

    String getPrestacao() {
        if(credito.numeroDePrestacoes==1){
            prestacao = " prestação"
        }else {
            prestacao = " prestações"
        }
        return prestacao
    }

    String getValorAPagarExt() {
        return currencyWriter.write(getValorAPagar())
    }

    BigDecimal getValorAPagar() {
        return getValorDaPrestacao()*credito.numeroDePrestacoes
    }
    ListModelList<Penhora> penhoras

    String getPercentualPorExtenso() {
        return currencyWriter.escreve(credito.percentualDejuros)
    }

    String getPercentualDeMorasExt() {
        return currencyWriter.escreve(credito.percentualJurosDeDemora)
    }

    String getValorDaPrestacaoExt() {
        return currencyWriter.write(getValorDaPrestacao())
    }

    BigDecimal getValorDaPrestacao() {
        return credito.pagamentos.first().valorDaPrestacao*(-1)
    }

    PedidoDeCredito getPedidoDeCredito() {
        pedidoDeCredito = PedidoDeCredito?.findByCredito(credito)
        if(pedidoDeCredito==null){
            pedidoDeCredito = PedidoDeCredito.findByCliente(credito.cliente)
        }
        return pedidoDeCredito
    }

    String getValorCreditadoPorExtenso() {
        return currencyWriter.write(credito.valorCreditado)
    }
    private  Entidade entidade = Entidade.all.first()

    Entidade getEntidade() {
        return entidade
    }

    ListModelList<Penhora> getPenhoras() {
        if(penhoras==null){
            penhoras = penhoraService.getPenhoras(credito.cliente)
        }
        return penhoras
    }

    Credito getCredito() {
        return credito
    }

    @Init init() {
        credito = Credito.findById(sessionStorageService?.credito?.id)
        settings = settingsService.getSettings()

    }



}
