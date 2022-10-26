package mz.maleyanga


import mz.maleyanga.credito.Credito
import mz.maleyanga.documento.Nota
import mz.maleyanga.feriado.Feriado
import mz.maleyanga.pagamento.Pagamento
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.pagamento.Remissao
import mz.maleyanga.settings.DefinicaoDeCredito
import mz.maleyanga.settings.Settings
import org.springframework.transaction.annotation.Transactional
import org.zkoss.zul.ListModelList

import java.math.RoundingMode
import java.sql.SQLDataException


/**
 * PagamentoService
 * A service class encapsulates the core business logic of a Grails application
 */
@Transactional
class PagamentoService {

    Credito credito
    SimuladorService simuladorService
    Pagamento pagamentoInstance
     def settingsService


    def criarPagamentos(Credito creditoInstance, DefinicaoDeCredito definicaoDeCredito) {
        def pagamentos = new ArrayList<Pagamento>()
        Integer dias = 0
        Calendar c = Calendar.getInstance()
        def valorDaPrestacao = 0.0
        def feriados = Feriado.all


        def r = creditoInstance.percentualDejuros / 100
        if (creditoInstance.formaDeCalculo == "pmt") {
            valorDaPrestacao = pmt(r, creditoInstance.numeroDePrestacoes, creditoInstance.valorCreditado, 0, 0)
        } else if (creditoInstance.formaDeCalculo == "taxafixa") {
            valorDaPrestacao = taxafixa(r, creditoInstance.numeroDePrestacoes, creditoInstance.valorCreditado, 0, 0)
        }
        c.setTime(creditoInstance.dateConcecao)

        1.upto(creditoInstance.numeroDePrestacoes) {
            //  System.println('antes do incremento' + c.getTime())
            if (creditoInstance.periodicidade == "mensal") {
                c.add(Calendar.MONTH, 1)
            }
            if (creditoInstance.periodicidade == "quinzenal") {
                c.add(Calendar.DATE, 15)
            }
            if (creditoInstance.periodicidade == "semanal") {
                c.add(Calendar.DATE, 7)
            }
            if (creditoInstance.periodicidade == "diario") {
                c.add(Calendar.DATE, 1)
            }
            if (creditoInstance.periodicidade == "doisdias") {
                c.add(Calendar.DATE, 2)
            }
            if (creditoInstance.periodicidade == "variavel") {
                int variavel = definicaoDeCredito.periodoVariavel as int
                creditoInstance.periodoVariavel = variavel as Integer
                c.add(Calendar.DATE, variavel)
            }
            //   System.println('depois do incremento' + c.getTime())
            def pagamento = new Pagamento()
            def num = creditoInstance.numeroDePrestacoes
            pagamento.recorenciaDeMoras = definicaoDeCredito.recorenciaDeMoras
            pagamento.setCredito(creditoInstance)
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek == 7) {
                if (definicaoDeCredito.excluirSabados) {
                    pagamento.descricao += "Sabado!"
                    //  System.println('sabado' + c.getTime())
                    c.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            int dayOfWee = c.get(Calendar.DAY_OF_WEEK)
            if (dayOfWee == 1) {
                if (definicaoDeCredito.excluirDomingos) {
                    pagamento.descricao += "Domingo"
                    dias = dias + 1
                    //  System.println('domingo' + c.getTime())
                    c.add(Calendar.DAY_OF_MONTH, 1)
                }


            }

            for (Iterator<Feriado> i = feriados.iterator(); i.hasNext();) {
                Feriado feriado = i.next()
                if (Objects.equals(c.getTime().format("dd/MM/yyyy"), feriado.data.format("dd/MM/yyyy"))) {
                    c.add(Calendar.DAY_OF_MONTH, 1)
                    int sabado = c.get(Calendar.DAY_OF_WEEK)
                    if (definicaoDeCredito.excluirDiaDePagNoSabado && sabado == 7) {
                        c.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    int domingo = c.get(Calendar.DAY_OF_WEEK)
                    if (definicaoDeCredito.excluirDiaDePagNoSabado && domingo == 1) {
                        c.add(Calendar.DAY_OF_MONTH, 1)
                    }

                }
            }
            int dayOf = c.get(Calendar.DAY_OF_WEEK)
            if (dayOf == 7) {
                if (definicaoDeCredito.excluirSabados) {
                    pagamento.descricao += "Sabado!"
                    //  System.println('sabado' + c.getTime())
                    c.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            int dayO = c.get(Calendar.DAY_OF_WEEK)
            if (dayO == 1) {
                if (definicaoDeCredito.excluirDomingos) {
                    pagamento.descricao += "Domingo"
                    dias = dias + 1
                    // System.println('domingo' + c.getTime())
                    c.add(Calendar.DAY_OF_MONTH, 1)
                }


            }

            pagamento.setDataPrevistoDePagamento(c.getTime())
            pagamento.setValorDaPrestacao(valorDaPrestacao)

            pagamento.setDescricao("${it}º- Prestação")
            def numeroDoCredito = creditoInstance.numeroDoCredito.split('/')
            pagamento.setNumeroDePagamento(numeroDoCredito[0] + numeroDoCredito[1] + it.toString())
            if (num.equals(it)) {
                if (creditoInstance.reterCapital) {
                    //   System.println("reterCapital")
                    pagamento.setValorDaPrestacao(valorDaPrestacao + creditoInstance.valorCreditado)
                }


            }
            pagamentos.add(pagamento)
            // pagamento.save flush:true

        }

        for (Pagamento p in pagamentos) {
            Calendar cs = Calendar.getInstance()
            Calendar cd = Calendar.getInstance()
            cs.setTime(p.dataPrevistoDePagamento)
            int sabado = cs.get(Calendar.DAY_OF_WEEK)
            if (definicaoDeCredito.excluirDiaDePagNoSabado && sabado == 7) {
                cs.add(Calendar.DAY_OF_MONTH, 1)
                p.dataPrevistoDePagamento = cs.getTime()
            }
            cd.setTime(p.dataPrevistoDePagamento)
            int domingo = cd.get(Calendar.DAY_OF_WEEK)
            if (definicaoDeCredito.excluirDiaDePagNoDomingo && domingo == 1) {
                cd.add(Calendar.DAY_OF_MONTH, 1)
                p.dataPrevistoDePagamento = cd.getTime()
            }
        }
        if (creditoInstance.formaDeCalculo == "pmt") {
            ArrayList its = simuladorService.gerarExtrato(creditoInstance, valorDaPrestacao)

            for (int x = 1; x < its.size(); x++) {

                pagamentos[x - 1].saldoDevedor = its[x].saldoDevedor.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                pagamentos[x - 1].valorDeJuros = its[x].juros.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                pagamentos[x - 1].valorDeAmortizacao = its[x].amortizacao.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                pagamentos[x - 1].save(failOnError: true, flush: true)

            }
        } else if (creditoInstance.formaDeCalculo == "taxafixa") {
            BigDecimal amortizacao = creditoInstance.valorCreditado / creditoInstance.numeroDePrestacoes
            BigDecimal valorDeJuros = (creditoInstance.valorCreditado * creditoInstance.percentualDejuros / 100) / creditoInstance.numeroDePrestacoes
            BigDecimal base = creditoInstance.valorCreditado
            BigDecimal v_amortizacao = 0
            for (Pagamento pagamento in pagamentos) {
                v_amortizacao += amortizacao
                //  System.println(base)
                pagamento.valorDeAmortizacao = amortizacao
                pagamento.valorDeJuros = valorDeJuros
                pagamento.saldoDevedor = base - v_amortizacao
                pagamento.save(failOnError: true, flush: true)

            }

        }





    }

    def actualizarEstadoDeCredito(Credito creditoInstance) {
        def pagamentosNaoPagos = creditoInstance.pagamentos.findAll { !it.pago }
        if (pagamentosNaoPagos.empty) {
            creditoInstance.emDivida = false
            creditoInstance.estado = "Fechado"
        }
        for (Pagamento p in pagamentosNaoPagos) {
            if (p.diasDeMora > 0) {
                p.credito.estado = "Pendente"
            } else p.credito.estado = "EmProgresso"
        }
        if (!creditoInstance.emDivida) {
            creditoInstance.estado = "Fechado"
        }


        creditoInstance.merge(flush: true)
    }

    def eliminarPagamentos(Credito creditoInstance) {
        for (Pagamento p in creditoInstance.pagamentos) {
            creditoInstance.pagamentos.remove(p)
            p.delete(flush: true)
        }
        creditoInstance
    }

    def eliminarCapitalizacoes(Credito creditoInstance) {
        try {
            def pagamentos = Pagamento.findAllByDescricaoAndCredito("CAPITALIZACAO", creditoInstance)
            for (Pagamento pagamento in pagamentos) {
                pagamento.credito = null
                for (Nota nota in pagamento.notas) {
                    nota.pagamento = null
                    nota.merge(flush: true)
                }
                for (Parcela parcela in pagamento.parcelas) {
                    parcela.pagamento = null
                    parcela.merge(flush: true)
                }
                for (Remissao remissao in pagamento.remissoes) {
                    remissao.pagamento = null
                    remissao.merge(flush: true)
                }
                pagamento.parcelas = null
                pagamento.remissoes = null
                pagamento.notas = null
                pagamento.merge(flush: true)
                pagamento.delete(flush: true)
                return true
            }
        } catch (SQLDataException e) {
            System.println(e.toString())
            return false
        }


    }

    def calcularMoras(Pagamento pagamentoInstance) {
        /*   if (pagamentoInstance?.credito == null) {
               return
           }
           System.println("calcularMoras.pagamentoInstance" + pagamentoInstance?.descricao)
           def agora = new Date()
           def dias = 0
           int periodo = 0
           if (pagamentoInstance?.credito?.periodicidade == "mensal") {
               dias = 15
               periodo = 31
           }
           if (pagamentoInstance?.credito?.periodicidade == "quinzenal") {
               dias = 7
               periodo = 15
           }
           if (pagamentoInstance?.credito?.periodicidade == "semanal") {
               dias = 3
               periodo = 7
           }
           if (pagamentoInstance?.credito?.periodicidade == "diario") {
               dias = 1
               periodo = 2
           }
           if (pagamentoInstance?.credito?.periodicidade == "doisdias") {
               dias = 1
               periodo = 3
           }
           if (!pagamentoInstance.pago) {
               if (pagamentoInstance.parcelas.empty) {

                   dias += agora - pagamentoInstance.dataPrevistoDePagamento
                   if (dias < 0) {
                       dias = 0
                   }
                   if (dias > 0) {
                       def moras = dias / periodo
                       if (moras.setScale(0, RoundingMode.HALF_DOWN).toInteger() >= 1) {
                           moras = 1
                       }
                       pagamentoInstance.diasDeMora = moras
                   } else {
                       pagamentoInstance.diasDeMora = 0
                   }
                   pagamentoInstance.save()
               } else {
                   dias += agora - pagamentoInstance.dataPrevistoDePagamento
                   System.println("dias de mora com uma parte paga" + dias)
                   if (dias > 0) {
                       def moras = dias / periodo
                       if (moras.setScale(0, RoundingMode.HALF_DOWN).toInteger() >= 1) {
                           moras = 1
                       }
                       pagamentoInstance.diasDeMora = moras
                       pagamentoInstance.credito.estado = "Pendente"
                       pagamentoInstance.credito.save()
                       pagamentoInstance.save()
                   } else {
                       pagamentoInstance.diasDeMora = 0
                   }


               }

           }

           pagamentoInstance.merge()
           verificarPagamentos(pagamentoInstance.credito)*/
        if (!pagamentoInstance.parcelas.empty) {
            pagamentoInstance.parcelas.sort { it.dataDePagamento }
            BigDecimal vp = 0.0
            BigDecimal vem = 0.0


            for (Parcela parcela in pagamentoInstance.parcelas) {
                vp += parcela.valorParcial
                vem += parcela.vdjdm
                if (vp + vem >= pagamentoInstance.valorDaPrestacao * (-1)) {
                    pagamentoInstance.diasDeMora = parcela.dem

                }

            }
        } else {
            Date date = new Date()
            if (date.after(pagamentoInstance.dataPrevistoDePagamento)) {
                pagamentoInstance.diasDeMora = date - pagamentoInstance.dataPrevistoDePagamento
                if (pagamentoInstance.recorenciaDeMoras > pagamentoInstance.diasDeMora) {
                    pagamentoInstance.diasDeMora = pagamentoInstance.recorenciaDeMoras
                }
            } else {
                pagamentoInstance.diasDeMora = 0
            }
        }
        try {
            pagamentoInstance.merge(flush: true)
        } catch (SQLDataException e) {
            System.println(e.toString())
        }

    }

    def calcularMoraCaPital(Credito creditoInstance) {
        try {

            def agora = new Date()
            def dias = 0
            int periodo = 30

            if (creditoInstance.periodicidade == "mensal") {
                dias = 15

            }
            if (creditoInstance.periodicidade == "quinzenal") {
                dias = 15

            }
            if (creditoInstance.periodicidade == "semanal") {
                dias = 23

            }
            if (creditoInstance.periodicidade == "diario") {
                dias = 29

            }
            if (creditoInstance.periodicidade == "doisdias") {
                dias = 28

            }
            //  System.println("calcularMoraCaPital" + " time==" + agora + " Dias==" + dias)

            List<Pagamento> pagamentos = new ArrayList<Pagamento>(Pagamento.findAllByCredito(creditoInstance))
            pagamentos.sort { it.id }
            Calendar c = Calendar.getInstance()
            c.setTime(pagamentos.last().dataPrevistoDePagamento)
            c.add(Calendar.DAY_OF_MONTH, 1)
            c.add(Calendar.MONTH, 1)
            creditoInstance.validade = c.getTime()
            dias += agora - c.getTime()
            if (dias < 0) {
                dias = 0
            }
                if (dias > 0) {
                    def moras = dias / periodo

                    //  System.println("moras==" + moras)
                    creditoInstance.setMoras(moras.setScale(0, RoundingMode.HALF_DOWN).toInteger())
                    if (creditoInstance.moras > 0) {
                        for (int m = 1; m < creditoInstance.moras; m++) {
                            String numro = creditoInstance.id + "-" + m + 1
                            Pagamento pgmt = Pagamento.findByNumeroDePagamento(numro)
                            if (pgmt == null) {
                                Pagamento paga = new Pagamento()
                                Calendar cal = Calendar.getInstance()
                                cal.setTime(creditoInstance.validade)
                                cal.add(Calendar.MONTH, 1)
                                paga.dataPrevistoDePagamento = cal.getTime()
                                cal.setTime(paga.dataPrevistoDePagamento)
                                Calendar call = Calendar.getInstance()
                                call.setTime(creditoInstance.validade)
                                call.add(Calendar.MONTH, m)
                                //  System.println(call.getTime())
                                paga.dataDaCriacao = call.getTime()
                                BigDecimal divida = getSaldo(pagamentos, creditoInstance, paga.dataDaCriacao)
                                def pdjdd = creditoInstance.percentualJurosDeDemora
                                if (settingsService.settings.pdjda == "pdj") {
                                    pdjdd = creditoInstance.percentualDejuros
                                }
                                if (settingsService.settings.pdjda == "pdjdm") {
                                    pdjdd = creditoInstance.percentualJurosDeDemora
                                }

                                def valorDeMoras = divida * pdjdd / 100
                                paga.valorDaPrestacao = valorDeMoras
                                paga.numeroDePagamento = numro
                                paga.pago = false
                                paga.descricao = "CAPITALIZACAO"
                                paga.credito = creditoInstance
                                if (paga.valorDaPrestacao < 0) {
                                    paga.save(flush: true)
                                } else {
                                    System.println("paga.valorDaPrestacao==" + paga.valorDaPrestacao)
                                }
                                pagamentos.clear()
                                pagamentos = Pagamento.findAllByCredito(creditoInstance)
                            }
                        }
                    }

                } else {
                    creditoInstance.moras = 0
                }

            creditoInstance.merge(failOnError: true)

            //  pagamentoInstance.merge(flush: true)

            verificarPagamentos(creditoInstance)
        } catch (Exception e) {
            System.println(e.toString())
        }

    }

    BigDecimal getSaldo(List<Pagamento> pagamentos, Credito creditoInstance, Date data) {

        List<ExtratoDeCredito> extratoDeCreditos = new ArrayList<ExtratoDeCredito>()
        List<ExtratoDeCredito> extratos = new ArrayList<ExtratoDeCredito>()

        BigDecimal juros = 0.0
        for (Pagamento pagamento1 in pagamentos.sort { it.id }) {

            juros += pagamento1.valorDeJuros
        }
        BigDecimal saldo = creditoInstance.valorCreditado + juros
        ExtratoDeCredito extrato = new ExtratoDeCredito()
        extrato.debito = saldo
        extrato.credito = 0.0
        extrato.descricao = "EMPRESTIMO C/ JUROS"
        extrato.jurosDeMora = 0.0
        extrato.juros = 0.0
        extrato.data = creditoInstance.dateConcecao
        extrato.saldo = 0.0
        extratoDeCreditos.add(extrato)


        for (Pagamento pagamento in pagamentos.sort { it.id }) {

            for (Parcela parcela in pagamento?.parcelas?.sort { it.id }) {
                if (parcela.valorPago > 0.0) {
                    ExtratoDeCredito extratoDeCredito = new ExtratoDeCredito()
                    extratoDeCredito.data = parcela.dataDePagamento
                    extratoDeCredito.descricao = parcela.descricao
                    extratoDeCredito.debito = 0.0
                    extratoDeCredito.credito = parcela.valorPago * (-1)
                    extratoDeCredito.jurosDeMora = 0.0
                    extratoDeCredito.saldo = 0.0
                    extratoDeCreditos.add(extratoDeCredito)

                }

            }
            if (pagamento.valorDeJurosDeDemora * (-1) > 0.0) {
                Calendar c = Calendar.getInstance()
                c.setTime(pagamento.dataPrevistoDePagamento)
                c.add(Calendar.DAY_OF_MONTH, 1)
                ExtratoDeCredito extratoDeCredito = new ExtratoDeCredito()
                extratoDeCredito.data = c.getTime()
                extratoDeCredito.descricao = "JUROS DE MORA da " + pagamento.descricao
                extratoDeCredito.debito = 0.0
                extratoDeCredito.credito = 0.0
                extratoDeCredito.jurosDeMora = pagamento.valorDeJurosDeDemora * (-1)
                extratoDeCredito.saldo = 0.0
                extratoDeCreditos.add(extratoDeCredito)
            }
            if (pagamento.descricao == "CAPITALIZACAO") {

                ExtratoDeCredito extratoDeCredito = new ExtratoDeCredito()
                extratoDeCredito.data = pagamento.dataDaCriacao
                extratoDeCredito.descricao = pagamento.descricao
                extratoDeCredito.debito = 0.0
                extratoDeCredito.credito = 0.0
                extratoDeCredito.jurosDeMora = pagamento.valorDaPrestacao * (-1)
                extratoDeCredito.saldo = 0.0
                extratoDeCreditos.add(extratoDeCredito)
            }

        }
        extratoDeCreditos.each {
            if (it.data < data) {
                extratos.add(it)
            }
        }
        extratos.sort { it.data }

        BigDecimal sal = 0.0
        for (ExtratoDeCredito extratoDeCredito in extratos) {

            sal += extratoDeCredito.credito
            sal += extratoDeCredito.debito
            sal += extratoDeCredito.jurosDeMora
            extratoDeCredito.saldo = sal

        }

        return extratos.last().saldo * (-1)
    }
    def calcularMoras(Pagamento pagamentoInstance, Parcela parcela) {
        try {
            def agora = parcela.dataDePagamento
            def dias = 0

            if (!pagamentoInstance.pago) {
                if (pagamentoInstance.parcelas.empty) {

                    dias += agora - pagamentoInstance.dataPrevistoDePagamento
                    if (dias < 0) {
                        dias = 0
                    }

                    if (dias > 0) {

                        if (pagamentoInstance.recorenciaDeMoras > dias) {
                            dias = pagamentoInstance.recorenciaDeMoras
                        }
                        pagamentoInstance.diasDeMora = dias


                    } else {
                        pagamentoInstance.diasDeMora = 0
                    }

                } else {

                    dias = pagamentoInstance.parcelas.sort { it.dataDePagamento }.last().dataDePagamento - pagamentoInstance.dataPrevistoDePagamento
                    if (dias > 0) {
                        if (pagamentoInstance.recorenciaDeMoras > dias) {
                            pagamentoInstance.diasDeMora = dias
                        }
                        pagamentoInstance.credito.estado = "Pendente"

                    } else {
                        pagamentoInstance.diasDeMora = 0
                    }


                }

            }
        } catch (Exception e) {
            System.println(e.toString())
        }


    }

    def calcularJurosDeDemora(Parcela parcelaInstance) {
        int periodo = 0
        def dias = 0
        if (pagamentoInstance.credito.periodicidade == "mensal") {
            dias = 15
            periodo = 30
        }
        if (pagamentoInstance.credito.periodicidade == "quinzenal") {
            dias = 7
            periodo = 15
        }
        if (pagamentoInstance.credito.periodicidade == "semanal") {
            dias = 3
            periodo = 7
        }
        if (pagamentoInstance.credito.periodicidade == "diario") {
            periodo = 1
        }
        if (pagamentoInstance.credito.periodicidade == "doisdias") {
            dias = 1
            periodo = 2
        }


        dias += parcelaInstance.dataDePagamento - parcelaInstance.pagamento.dataPrevistoDePagamento
        //  System.println("dias de demora =" + dias)
        if (dias > 0) {

            //  System.println(dias + " > 0")
            def moras = dias / periodo
            if (moras.setScale(0, RoundingMode.HALF_DOWN).toInteger() >= 1) {
                moras = 1
            }
            pagamentoInstance.diasDeMora = moras
            parcelaInstance.pagamento.credito.estado = "Pendente"
            parcelaInstance.pagamento.credito.save(flush: true)
            pagamentoInstance.save(flush: true)
        } else {
            parcelaInstance.pagamento.diasDeMora = 0
            parcelaInstance.pagamento.valorDeJurosDeDemora = 0
        }
        parcelaInstance.pagamento.save(flush: true)

    }


    def verificarPagamentos(Credito creditoInstance) {
        try {
            def allNaoPago = creditoInstance.pagamentos.findAll { !it.pago }

            if (allNaoPago.empty) {
                creditoInstance.estado = "Fechado"
                creditoInstance.emDivida = false
                creditoInstance.merge(flush: true)
            } else {
                for (Pagamento p in allNaoPago) {
                    if (p.diasDeMora > 0) {
                        creditoInstance.estado = "Pendente"
                        creditoInstance.merge(flush: true)
                    }
                }
            }
        } catch (Exception e) {
            System.println(e.toString())
        }


    }


    /* def calcularPagamentosVencidos() {

         def pagamentos = Pagamento.findAllByPago(false)
         DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy")
         Calendar c = Calendar.getInstance()
         //c.add(Calendar.DATE, 30)
         System.println(c.getTime())

         for (Pagamento p : pagamentos) {
             System.println(c.getTime())
             if (!p.dataPrevistoDePagamento.equals(null)) {
                 c.setTime(p.dataPrevistoDePagamento as Date)
                 c.add(Calendar.DATE, 30)
                 Date date = new Date()
                 if (date < c.getTime()) {
                     String descricao = p.descricao
                     BigDecimal valorDaPrestcao = p.valorDaPrestacao
                     p.setDescricao(descricao + "+1J")
                     p.setDataPrevistoDePagamento(c.getTime() as Date)
                     p.valorDaPrestacao += p.valorDaPrestacao * p.credito.percentualDejuros / 100
                     p.credito.estado = "Pendente"
                     p.credito.save()
                     p.save flush: true


                 }

             }


         }
     }*/



    BigDecimal pmt(double r, int nper, BigDecimal pv, BigDecimal fv, int type) {
        BigDecimal pmt = r / (Math.pow(1 + r, nper) - 1) * -(pv * Math.pow(1 + r, nper) + fv)

        return pmt
    }

    double taxafixa(double r, int nper, BigDecimal pv, BigDecimal fv, int type) {
        BigDecimal juros = pv * r
        BigDecimal total = pv + juros
        double taxafixa = total / nper
        return taxafixa * (-1)
    }

    List simuladorDeCredito(Credito creditoInstance) {
        def pagamentos = new ArrayList<Pagamento>()
        Integer dias = 0
        Calendar c = Calendar.getInstance()
        BigDecimal valorDaPrestacao = 0.0
        int periodo = 0
        if (creditoInstance.periodicidade == "mensal") {
            periodo = 30
        }
        if (creditoInstance.periodicidade == "quinzenal") {
            periodo = 15
        }
        if (creditoInstance.periodicidade == "semanal") {
            periodo = 7
        }
        if (creditoInstance.periodicidade == "diario") {
            periodo = 1
        }
        if (creditoInstance.periodicidade == "doisdias") {
            periodo = 2
        }

        def r = creditoInstance.percentualDejuros / 100
        if (creditoInstance.formaDeCalculo == "pmt") {
            // double pmt(double r, int nper, BigDecimal pv, BigDecimal fv, int type)
            valorDaPrestacao = pmt(r, creditoInstance.numeroDePrestacoes, creditoInstance.valorCreditado, 0.0, 0)
        } else if (creditoInstance.formaDeCalculo == "taxafixa") {
            valorDaPrestacao = taxafixa(r, creditoInstance.numeroDePrestacoes, creditoInstance.valorCreditado, 0.0, 0)
        }


        1.upto(creditoInstance.numeroDePrestacoes) {
            def pagamento = new Pagamento()
            def num = creditoInstance.numeroDePrestacoes
            pagamento.setCredito(creditoInstance)
            pagamento.setValorDaPrestacao(valorDaPrestacao)
            pagamentos.add(pagamento)
        }
        if (creditoInstance.formaDeCalculo == "pmt") {
            ArrayList its = simuladorService.gerarExtrato(creditoInstance, valorDaPrestacao)

            for (int x = 1; x < its.size(); x++) {

                pagamentos[x - 1].saldoDevedor = its[x].saldoDevedor.toBigDecimal()
                pagamentos[x - 1].valorDeJuros = its[x].juros.toBigDecimal()
                pagamentos[x - 1].valorDeAmortizacao = its[x].amortizacao.toBigDecimal()


            }
        } else if (creditoInstance.formaDeCalculo == "taxafixa") {
            BigDecimal amortizacao = creditoInstance.valorCreditado / creditoInstance.numeroDePrestacoes
            BigDecimal valorDeJuros = (creditoInstance.valorCreditado * creditoInstance.percentualDejuros / 100) / creditoInstance.numeroDePrestacoes
            BigDecimal base = creditoInstance.valorCreditado
            BigDecimal v_amortizacao = 0

            for (Pagamento pagamento in pagamentos) {
                v_amortizacao += amortizacao
                //  System.println(base)
                pagamento.valorDeAmortizacao = amortizacao
                pagamento.valorDeJuros = valorDeJuros
                pagamento.saldoDevedor = base - v_amortizacao

            }

        }
        // System.println(pagamentos)
        return pagamentos
    }

    def merge(Pagamento pagamento) {
        try {
            pagamento.merge(flush: true)
            return true
        } catch (SQLDataException e) {
            System.println(e.toString())
            return false
        }
    }

    List<Pagamento> getByCredito(Credito credito1) {
        return Pagamento.findAllByCredito(credito1)
    }

    List<Pagamento> findAllByDataPrevistoDePagamento(Date dia) {
        List<Pagamento> pgmts
        if (pgmts == null) {
            pgmts = new ListModelList<>()
        }
        def pagamentos = findAllByDate(dia)
        for (Iterator<Pagamento> i = pagamentos.iterator(); i.hasNext();) {
            Pagamento p = i.next()
            if (Objects.equals(p.dataPrevistoDePagamento.format("dd/MM/yyyy"), dia.format("dd/MM/yyyy"))) {
                if (p.valorDaPrestacao < 0 && p.totalEmDivida < 0) {
                    pgmts.add(p)
                }


            }
        }
        return pgmts
    }

    List<Pagamento> findAll() {
        return Pagamento.all
    }

    List<Pagamento> findAllByDate(Date date) {
        Calendar c = Calendar.getInstance()
        c.setTime(date)
        c.add(Calendar.DAY_OF_MONTH, -1)
        def startDate = c.getTime()
        c.add(Calendar.DAY_OF_MONTH, 2)
        def endDate = c.getTime()

        def pgmts = Pagamento.findAllByDataPrevistoDePagamentoBetween(startDate, endDate)
        return pgmts
    }

    def udateDatas(Credito creditoInstance) {
        int x = 0
        def pagamentos = Pagamento.findAllByCredito(creditoInstance)
        pagamentos.sort { it.id }
        Calendar c = Calendar.getInstance()
        c.setTime(creditoInstance.dateConcecao)
        def definicaoDeCredito = DefinicaoDeCredito.findAllByAtivoAndPeriodicidadeAndFormaDeCalculo(true, creditoInstance.periodicidade, creditoInstance.formaDeCalculo)
        1.upto(creditoInstance.numeroDePrestacoes) {


            if (creditoInstance.periodicidade == "mensal") {
                c.add(Calendar.MONTH, 1)
            }
            if (creditoInstance.periodicidade == "quinzenal") {
                c.add(Calendar.DATE, 15)
            }
            if (creditoInstance.periodicidade == "semanal") {
                c.add(Calendar.DATE, 7)
            }
            if (creditoInstance.periodicidade == "diario") {
                c.add(Calendar.DATE, 1)
            }
            if (creditoInstance.periodicidade == "doisdias") {
                c.add(Calendar.DATE, 2)
            }
            if (creditoInstance.periodicidade == "variavel") {
                int variavel = definicaoDeCredito.periodoVariavel as int
                creditoInstance.periodoVariavel = variavel as Integer
                c.add(Calendar.DATE, variavel)
            }
            //   System.println('depois do incremento' + c.getTime())

            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek == 7) {
                if (definicaoDeCredito.excluirSabados) {
                    c.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            int dayOfWee = c.get(Calendar.DAY_OF_WEEK)
            if (dayOfWee == 1) {
                if (definicaoDeCredito.excluirDomingos) {
                    c.add(Calendar.DAY_OF_MONTH, 1)
                }


            }
            def feriados = Feriado.all
            for (Iterator<Feriado> i = feriados.iterator(); i.hasNext();) {
                Feriado feriado = i.next()
                if (Objects.equals(c.getTime().format("dd/MM/yyyy"), feriado.data.format("dd/MM/yyyy"))) {
                    c.add(Calendar.DAY_OF_MONTH, 1)
                    int sabado = c.get(Calendar.DAY_OF_WEEK)
                    if (definicaoDeCredito.excluirDiaDePagNoSabado && sabado == 7) {
                        c.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    int domingo = c.get(Calendar.DAY_OF_WEEK)
                    if (definicaoDeCredito.excluirDiaDePagNoSabado && domingo == 1) {
                        c.add(Calendar.DAY_OF_MONTH, 1)
                    }

                }
            }
            int dayOf = c.get(Calendar.DAY_OF_WEEK)
            if (dayOf == 7) {
                if (definicaoDeCredito.excluirSabados) {
                    c.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            int dayO = c.get(Calendar.DAY_OF_WEEK)
            if (dayO == 1) {
                if (definicaoDeCredito.excluirDomingos) {
                    c.add(Calendar.DAY_OF_MONTH, 1)
                }


            }

            pagamentos[x].setDataPrevistoDePagamento(c.getTime())
            pagamentos[x].merge(flush: true)
            x++

        }

    }
}
