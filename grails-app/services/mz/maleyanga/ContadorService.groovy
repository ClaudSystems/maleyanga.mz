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

        def pagamentos = Pagamento.list()
        if (!pagamentos) {
            return "$year/1"
        }

        for (Pagamento pagamento in pagamentos) {
            if (!pagamento.numeroDePagamento) {
                continue
            }

            try {
                def numero = pagamento.numeroDePagamento.split("/")
                if (numero.size() >= 2) {
                    Integer sub = numero[1].toInteger()
                    Integer ano = numero[0].toInteger()
                    if (ano == year && sub >= num) {
                        num = sub
                    }
                }
            } catch (NumberFormatException e) {
                // Ignorar números mal formatados e continuar
                continue
            } catch (Exception e) {
                // Ignorar outros erros e continuar
                continue
            }
        }

        num++
        return "$year/$num"
    }

    String gerarNumeroDaParcela() {
        Integer num = 0
        Date date = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)

        def parcelas = Parcela.list()
        if (!parcelas) {
            return "$num_ano/1"
        }

        for (Parcela parcela in parcelas) {
            if (!parcela.numeroDoRecibo) {
                continue
            }

            try {
                def numero = parcela.numeroDoRecibo.split("/")
                if (numero.size() >= 2) {
                    Integer sub = numero[1].toInteger()
                    def ano = numero[0]
                    if (ano == num_ano && sub >= num) {
                        num = sub
                    }
                }
            } catch (NumberFormatException e) {
                continue
            } catch (Exception e) {
                continue
            }
        }

        num++
        return "$num_ano/$num"
    }

    String gerarNumeroDoCredito() {
        Integer num = 0
        Date date = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)

        def creditos = Credito.list()
        if (!creditos) {
            return "$num_ano/1"
        }

        for (Credito credito in creditos) {
            if (!credito.numeroDoCredito) {
                continue
            }

            try {
                def numero = credito.numeroDoCredito.split("/")
                if (numero.size() >= 2) {
                    Integer sub = numero[1].toInteger()
                    def ano = numero[0]
                    if (ano == num_ano && sub >= num) {
                        num = sub
                    }
                }
            } catch (NumberFormatException e) {
                continue
            } catch (Exception e) {
                continue
            }
        }

        num++
        return "$num_ano/$num"
    }

    String gerarNumeroDaLista() {
        Integer num = 0
        Date date = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)

        def ldds = ListaDeDesembolso.list()
        if (!ldds) {
            return "$num_ano/1"
        }

        for (ListaDeDesembolso lista in ldds) {
            if (!lista.numeroDaLista) {
                continue
            }

            try {
                def numero = lista.numeroDaLista.split("/")
                if (numero.size() >= 2) {
                    Integer sub = numero[1].toInteger()
                    def ano = numero[0]
                    if (ano == num_ano && sub >= num) {
                        num = sub
                    }
                }
            } catch (NumberFormatException e) {
                continue
            } catch (Exception e) {
                continue
            }
        }

        num++
        return "$num_ano/$num"
    }

    String gerarNumeroDoDiario() {
        Integer num = 0
        Date date = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)

        def diarios = Diario.list()
        if (!diarios) {
            return "$num_ano/1"
        }

        for (Diario diario in diarios) {
            if (!diario.numeroDoDiario) {
                continue
            }

            try {
                def numero = diario.numeroDoDiario.split("/")
                if (numero.size() >= 2) {
                    Integer sub = numero[1].toInteger()
                    def ano = numero[0]
                    if (ano == num_ano && sub >= num) {
                        num = sub
                    }
                }
            } catch (NumberFormatException e) {
                continue
            } catch (Exception e) {
                continue
            }
        }

        num++
        return "$num_ano/$num"
    }

    String gerarNumeroDaSaida(Saida saida) {
        if (!saida?.dateCreated) {
            Calendar cal = Calendar.getInstance()
            cal.setTime(new Date())
            int year = cal.get(Calendar.YEAR)
            def num_ano = year.toString().substring(2, 4)
            return "$num_ano/0"
        }

        Calendar cal = Calendar.getInstance()
        cal.setTime(saida.dateCreated)
        int year = cal.get(Calendar.YEAR)
        def num_ano = year.toString().substring(2, 4)
        return "$num_ano/$saida.id"
    }

    Conta getByNumeroDacoonta(String numeroDaConta) {
        Conta.findByNumeroDaConta(numeroDaConta)
    }
}