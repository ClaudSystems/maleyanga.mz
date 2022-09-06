package parcela

import grails.plugin.springsecurity.SpringSecurityService
import mz.maleyanga.CurrencyWriter
import mz.maleyanga.SessionStorageService
import mz.maleyanga.credito.Credito
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.ParcelaService
import mz.maleyanga.security.Utilizador
import org.springframework.stereotype.Service
import org.zkoss.bind.annotation.Init

@Service
class PrintEntradaViewModel {
    SessionStorageService sessionStorageService
    Parcela parcelaInstance
    ParcelaService parcelaService
    List<Parcela> parcelas
    String valorPorExtenso
    Utilizador utilizador
    SpringSecurityService springSecurityService

    Utilizador getUtilizador() {
        return springSecurityService.currentUser as Utilizador
    }
    CurrencyWriter currencyWriter = CurrencyWriter.getInstance()

    String getValorPorExtenso() {
        return currencyWriter.write(parcelaInstance.valorPago)
    }

    List<Parcela> getParcelas() {
        if(parcelas==null){
            parcelas = new LinkedList<Parcela>(Parcela.findAllByNumeroDoRecibo(parcelaInstance.numeroDoRecibo))
        }

        return parcelas
    }



    Parcela getParcelaInstance() {
        return parcelaInstance
    }



    @Init init() {
        // initialzation code here
      //  parcelaInstance = Parcela.findById(parcelaService.entrada.id)
        parcelaInstance = sessionStorageService.entrada as Parcela

    }



}
