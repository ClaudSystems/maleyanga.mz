package mz.maleyanga.pagamento
import mz.maleyanga.credito.Credito
import mz.maleyanga.documento.Nota

import java.math.RoundingMode

/*
O Pagamento aqui representa a prestacao por ser pago, so se efetiva o pagamento quanto se paga realmente.
* */

class Pagamento implements Serializable {
    private static final long serialVersionUID = 2
    // no UI � apresentado como Prestao

    String descricao
    String numeroDePagamento
    BigDecimal valorDaPrestacao = 0.0
    BigDecimal valorDeJuros = 0.0
    BigDecimal valorPagoJuros = 0.0
    BigDecimal valorDeAmortizacao = 0.0
    BigDecimal valorPagoAmortizacao = 0.0
    BigDecimal saldoDevedor = 0.0
    BigDecimal valorPago = 0.0      // ref aos totais parciais
    BigDecimal totalPago = 0.0   // referente o valor total pago das prestacoes

    BigDecimal totalPagoNoPrazo = 0.0
    BigDecimal totalEmDivida = 0.0
    BigDecimal totalEmDividaSemMoras = 0.0
    BigDecimal valorDeJurosDeDemora = 0.00
    BigDecimal valorPagoDemora = 0.0
    BigDecimal valorDaRemissao = 0.0
    int diasDeMora = 0
    int recorenciaDeMoras = 0
    boolean pago
    Boolean valoresAlocados = false
    Date dateCreated
    Date lastUpdated
    Date dataDePagamento
    Date dataPrevistoDePagamento
    Date dataDaCriacao

    static mapping = {
        id generator: 'increment'
        //  credito lazy: false
        batchSize(20)
    }


    BigDecimal getValorDaRemissao() {
        valorDaRemissao = 0.0
        if (remissoes != null)
            for (Remissao r in remissoes) {
                valorDaRemissao += r.valorDaRemissao
            }
        return valorDaRemissao
    }

    BigDecimal getValorPagoAmortizacao() {
        def valorPago = getValorPago()
        if (valorPago >= valorDeAmortizacao) {
            return valorDeAmortizacao
        } else {
            return valorPago
        }
    }

    BigDecimal getValorPagoDemora() {
        def valorPago = getValorPago()
        def valoramo_valorjuros = valorDeAmortizacao + valorDeJuros
        def valoramo_valorjuros_valo_mora = valorDeAmortizacao + valorDeJuros + -(valorDeJurosDeDemora)

        if (valorPago >= valorDeAmortizacao) {
            if (valorPago >= valoramo_valorjuros) {
                if (valorPago >= valoramo_valorjuros_valo_mora) {
                    return -(valorDeJurosDeDemora)
                } else return valorPago - valoramo_valorjuros
            } else return 0.0
        } else return 0.0

    }

    BigDecimal getValorPagoJuros() {
        def valorPago = getValorPago()
        def valorAmoJuros = valorDeAmortizacao + valorDeJuros
        if (valorPago <= valorDeAmortizacao) {
            return 0.0
        } else if (valorPago >= valorAmoJuros) {
            return valorDeJuros
        } else {

            return valorPago - valorDeAmortizacao
        }


    }

    BigDecimal getValorPago() {
        valorPago = 0.0
        if (parcelas != null) {
            for (Parcela p in parcelas) {
                valorPago += p.valorParcial
            }
        }
        return valorPago
    }


    BigDecimal getTotalEmDivida() {
        totalEmDivida = this.valorDaPrestacao + getValorDeJurosDeDemora() + getValorPago() + getValorDaRemissao()
        if (totalEmDivida > 0) {
            return 0.0
        }
        return totalEmDivida.setScale(2, RoundingMode.DOWN)
    }


    BigDecimal getTotalEmDividaSemMoras() {
        return (totalEmDivida + valorDeJurosDeDemora * (-1)).setScale(2, RoundingMode.DOWN)
    }

    boolean getPago() {
        return getTotalEmDivida() * (-1) <= 0.0
    }
    static hasOne = [credito: Credito]
    static hasMany = [parcelas: Parcela, remissoes: Remissao]

    static constraints = {
        totalPagoNoPrazo nullable: true
        totalEmDividaSemMoras nullable: true
        numeroDePagamento nullable: true
        valorDaPrestacao scale: 2
        valorDeJurosDeDemora nullable: true
        valorPago nullable: true
        dataPrevistoDePagamento nullable: true
        dataDePagamento nullable: true
        parcelas nullable: true
        totalEmDivida nullable: true
        lastUpdated nullable: true
        diasDeMora(validator: {
            return it >= 0
        })
        valorDaRemissao nullable: true
        valorPagoJuros nullable: true
        valorPagoAmortizacao nullable: true
        valorPagoDemora nullable: true
        valoresAlocados nullable: true
        // notas nullable: true
        dataDaCriacao nullable: true
        //  moras nullable: true
        totalPago nullable: true


    }


    String toString() {
        return "${"id:" + id + "|" + descricao}"
    }

    BigDecimal getTotalPago() {
        totalPago = 0.0
        if (parcelas != null) {
            for (Parcela p in parcelas) {
                totalPago += p.valorPago
            }
        }
        return totalPago
    }

    BigDecimal getTotalPagoNoPrazo() {
        totalPagoNoPrazo = 0.0
        if (parcelas != null) {
            for (Parcela p in parcelas) {

                if (p.dataDePagamento.before(dataPrevistoDePagamento + 1)) {
                    totalPagoNoPrazo += p.valorParcial
                }

            }
        }
        return totalPagoNoPrazo
    }

    BigDecimal getValorDeJurosDeDemora() {
        def vdp
        if (this.descricao == "CAPITALIZACAO") {
            return 0.0
        }

        if (diasDeMora == 0) {
            return 0.0
        } else if (parcelas != null) {
            def pdjdd = this.credito.percentualJurosDeDemora

            if (this.credito.ignorarValorPagoNoPrazo) {
                vdp = this.valorDaPrestacao
            } else {
                vdp = this.valorDaPrestacao + getTotalPagoNoPrazo()
            }

            if (vdp > 0) {
                return 0.0
            } else {
                def valorDeMoras = vdp * pdjdd / 100 * this.diasDeMora
                BigDecimal vdjdd = valorDeMoras
                return vdjdd.setScale(2, RoundingMode.DOWN)
            }

        } else {
            def pdjdd = this.credito.percentualJurosDeDemora
            vdp = this.valorDaPrestacao
            def valorDeMoras = vdp * pdjdd / 100
            BigDecimal vdjdd = valorDeMoras * this.diasDeMora
            return vdjdd.setScale(2, RoundingMode.DOWN)
        }

    }

    /*BigDecimal getValorDeJurosDeDemora() {
        valorDeJurosDeDemora = 0.0
        for (Mora mora in moras) {
            valorDeJurosDeDemora += mora.vdjdm
        }
        return valorDeJurosDeDemora
    }*/

    Integer getRecorenciaDeMoras() {
        if (this.credito == null) {
            return 1
        }
        if (this.credito.recorenciaDeMoras == null) {
            return 1
        } else
            return this.credito.recorenciaDeMoras
    }

    int getDiasDeMora() {
        Date date = new Date()
        def dias = date - dataPrevistoDePagamento
        Calendar c = Calendar.getInstance()
        c.setTime(date)
        c.add(Calendar.DAY_OF_MONTH, -1)
        if (dataPrevistoDePagamento.before(c.getTime())) {
            if (pago) {
                if (valorDaPrestacao * (-1) > totalPagoNoPrazo) {
                    if (dias > 1) {
                        if (dias >= recorenciaDeMoras) {
                            return recorenciaDeMoras
                        } else {
                            return dias
                        }
                    }
                    return 1
                } else {
                    return 0
                }

            } else {
                if (dias > 1) {
                    if (dias >= recorenciaDeMoras) {
                        return recorenciaDeMoras
                    } else {
                        return dias
                    }
                }
                return 1
            }

        } else {
            return 0
        }


    }
}
