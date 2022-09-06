package parcela

import mz.maleyanga.ClienteService
import mz.maleyanga.CurrencyWriter
import mz.maleyanga.SessionStorageService
import mz.maleyanga.cliente.Cliente
import mz.maleyanga.credito.Credito
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.ParcelaService
import mz.maleyanga.security.Utilizador
import org.springframework.stereotype.Service
import org.zkoss.zk.grails.*

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.select.annotation.Wire
@Service
class PrintParcelaViewModel {
    SessionStorageService sessionStorageService
    Parcela parcelaInstance
    String username
    Credito creditoInstance
    List<Parcela> parcelas
    BigDecimal total =0.0
    String valorPorExtenso
    Cliente cliente
    BigDecimal totalEmDivida
    CurrencyWriter currencyWriter = CurrencyWriter.getInstance()

    BigDecimal getTotalEmDivida() {
        return creditoInstance.valorEmDivida
    }

    String getCliente() {

        return cliente
    }

    String getUsername() {

        return username
    }

    String getValorPorExtenso() {
        return currencyWriter.write(total)
    }

    List<Parcela> getParcelas() {
        if(parcelas==null){
            parcelas = new LinkedList<Parcela>(Parcela.findAllByNumeroDoRecibo(parcelaInstance.numeroDoRecibo))
        }

        return parcelas
    }

    BigDecimal getTotal() {
       return total
    }

    Parcela getParcelaInstance() {
        return parcelaInstance
    }



    @Init init() {
        // initialzation code here
      //  parcelaInstance = Parcela.findById(parcelaService.parcelaInstance.id)
        parcelaInstance = sessionStorageService.parcela as Parcela
        Utilizador utilizador = Utilizador.findById(parcelaInstance.utilizador.id)
        username = utilizador.username
        if(parcelaInstance.pagamento!=null){
          //  creditoInstance = Credito.findById(parcelaService.parcelaInstance.pagamento.credito.id)
            creditoInstance = sessionStorageService.credito as Credito
            creditoInstance = Credito.findById(creditoInstance.id)
            cliente = Cliente.findByNome(creditoInstance.cliente.nome)

        }
              total = parcelaInstance.valorPago

    }

    Credito getCreditoInstance() {
        return creditoInstance
    }
}
