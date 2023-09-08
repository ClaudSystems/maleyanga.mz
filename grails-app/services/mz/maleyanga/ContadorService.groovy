package mz.maleyanga

import grails.transaction.Transactional
import mz.maleyanga.conta.Conta
import mz.maleyanga.credito.Credito
import mz.maleyanga.diario.Diario
import mz.maleyanga.pagamento.Pagamento
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.pedidoDeCredito.ListaDeDesembolso
import mz.maleyanga.saidas.Saida

/**
 * ContadorService
 * A service class encapsulates the core business logic of a Grails application
 */
@Transactional
class ContadorService {
    String gerarNumeroDoPagamento() {
        Integer num = 0
        Date date = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        int year = cal.get(Calendar.YEAR)

        def pagamentos = Pagamento.all
        if (pagamentos == null) {

            return year + "/" + 1
        } else {
            for (Pagamento pagamento in pagamentos) {

                def numero = pagamento.numeroDePagamento.split("/")
                Integer sub = numero[1].toInteger()
                Integer ano = numero[0].toInteger()
                if (ano == year) {
                    if (sub >= num) {
                        num = sub
                    }
                }

            }


            num++

            return year + "/" + num

        }

    }


    String gerarNumeroDaParcela() {
        Integer num = 0
        Date date = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)
        def parcelas = Parcela.all
        if (parcelas == null) {
            return num_ano + "/" + 1
        } else {
            for (Parcela edp in parcelas) {
                def numero = edp.numeroDoRecibo.split("/")
                Integer sub = numero[1].toInteger()
                def ano = numero[0]
                if (ano == num_ano) {
                    if (sub >= num) {
                        num = sub
                    }
                }

            }
            num++
            return num_ano + "/" + num
        }
    }

    String gerarNumeroDoCredito() {
        Integer num = 0
        Date date = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)
        def creditos = Credito.all
        if (creditos == null) {
            return num_ano + "/" + 1
        } else {
            for (Credito edp in creditos) {
                def numero = edp.numeroDoCredito.split("/")
                Integer sub = numero[1].toInteger()
                def ano = numero[0]

                if (ano == num_ano) {
                    if (sub >= num) {
                        num = sub
                    }
                }
            }
            num++
            return num_ano + "/" + num
        }

    }

    String gerarNumeroDaLista() {
        Integer num = 0
        Date date = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)
        def ldds = ListaDeDesembolso.all
        if (ldds == null) {
            return num_ano + "/" + 1
        } else {
            for (ListaDeDesembolso edp in ldds) {
                def numero = edp.numeroDaLista.split("/")
                Integer sub = numero[1].toInteger()
                def ano = numero[0]

                if (ano == num_ano) {
                    if (sub >= num) {
                        num = sub
                    }
                }
            }
            num++
            return num_ano + "/" + num
        }

    }


    String gerarNumeroDoDiario() {
        Integer num = 0
        Date date = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)
        def diarios = Diario.all
        if (diarios == null) {
            return num_ano + "/" + 1
        } else {
            for (Diario edp in diarios) {
                def numero = edp.numeroDoDiario.split("/")
                Integer sub = numero[1].toInteger()
                def ano = numero[0]
                if (ano == num_ano) {
                    if (sub >= num) {
                        num = sub
                    }
                }

            }
            num++
            return num_ano + "/" + num
        }


    }
    String gerarNumeroDaSaida(Saida saida) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(saida.dateCreated)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)
        return num_ano + "/" + saida.id

    }

    Conta getByNumeroDacoonta(String numeroDaConta) {
        Conta.findByNumeroDaConta(numeroDaConta)
    }

}
