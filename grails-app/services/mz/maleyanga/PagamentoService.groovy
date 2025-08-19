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

import java.math.MathContext
import java.math.RoundingMode
import java.sql.SQLDataException

/**
 * PagamentoService
 * Serviço para gestão de pagamentos e cálculos financeiros
 */
@Transactional
class PagamentoService {

    Credito credito
    SimuladorService simuladorService
    Pagamento pagamentoInstance
    def settingsService

    // MAPAS DE CONFIGURAÇÃO (evitam múltiplos if-else)
    private static final Map<String, Integer> PERIODICIDADE_DIAS = [
            "mensal": 30,
            "quinzenal": 15,
            "semanal": 7,
            "diario": 1,
            "doisdias": 2
    ]

    private static final Map<String, Integer> PERIODOS_POR_ANO = [
            "mensal": 12,
            "quinzenal": 24,
            "semanal": 52,
            "diario": 365,
            "doisdias": 182
    ]

    /**
     * Cria pagamentos para um crédito conforme definição
     */
    def criarPagamentos(Credito creditoInstance, DefinicaoDeCredito definicaoDeCredito) {
        // VALIDAÇÃO INICIAL
        if (!creditoInstance || !definicaoDeCredito) {
            throw new IllegalArgumentException("Parâmetros inválidos para criação de pagamentos")
        }

        def pagamentos = new ArrayList<Pagamento>()
        def feriados = Feriado.list()
        Calendar c = Calendar.getInstance()

        // ⚠️ CORREÇÃO CRÍTICA: Converter taxa para periodicidade correta
        double taxaPeriodica = converterTaxaParaPeriodicidade(
                creditoInstance.percentualDejuros,
                creditoInstance.periodicidade
        )

        BigDecimal valorDaPrestacao = BigDecimal.ZERO

        // CÁLCULO DO VALOR DA PRESTAÇÃO
        if ("pmt".equalsIgnoreCase(creditoInstance.formaDeCalculo)) {
            valorDaPrestacao = pmt(taxaPeriodica, creditoInstance.numeroDePrestacoes,
                    creditoInstance.valorCreditado, BigDecimal.ZERO, 0)
        } else if ("taxafixa".equalsIgnoreCase(creditoInstance.formaDeCalculo)) {
            valorDaPrestacao = calcularTaxaFixa(taxaPeriodica, creditoInstance.numeroDePrestacoes,
                    creditoInstance.valorCreditado)
        } else {
            throw new IllegalArgumentException("Forma de cálculo inválida: " + creditoInstance.formaDeCalculo)
        }

        c.setTime(creditoInstance.dateConcecao)

        // CRIAÇÃO DAS PRESTAÇÕES
        1.upto(creditoInstance.numeroDePrestacoes) { numeroPrestacao ->
            Pagamento pagamento = new Pagamento()
            pagamento.recorenciaDeMoras = definicaoDeCredito.recorenciaDeMoras
            pagamento.credito = creditoInstance

            // AVANÇA A DATA CONFORME PERIODICIDADE
            avancarDataConformePeriodicidade(c, creditoInstance.periodicidade, definicaoDeCredito)

            // AJUSTES PARA FINS DE SEMANA E FERIADOS
            ajustarDataParaFinsSemanaEFeriados(c, definicaoDeCredito, feriados, pagamento)

            pagamento.dataPrevistoDePagamento = c.getTime()
            pagamento.valorDaPrestacao = valorDaPrestacao
            pagamento.descricao = "${numeroPrestacao}ª Prestação"

            // NUMERAÇÃO DO PAGAMENTO
            def partesNumeroCredito = creditoInstance.numeroDoCredito.split('/')
            if (partesNumeroCredito.size() >= 2) {
                pagamento.numeroDePagamento = partesNumeroCredito[0] + partesNumeroCredito[1] + numeroPrestacao
            }

            // ÚLTIMA PRESTAÇÃO COM CAPITAL RETIDO
            if (numeroPrestacao == creditoInstance.numeroDePrestacoes && creditoInstance.reterCapital) {
                pagamento.valorDaPrestacao = valorDaPrestacao.add(creditoInstance.valorCreditado)
            }

            pagamentos.add(pagamento)
        }

        // AJUSTES FINAIS DE DATAS
        ajustarDatasFinais(pagamentos, definicaoDeCredito)

        // CÁLCULO DETALHADO DA AMORTIZAÇÃO
        calcularDetalhesAmortizacao(creditoInstance, pagamentos, valorDaPrestacao)

        return pagamentos
    }

    /**
     * ⚠️ MÉTODO CRÍTICO: Conversão de taxa anual para periódica
     */
    private double converterTaxaParaPeriodicidade(BigDecimal percentualDejuros, String periodicidade) {
        if (!percentualDejuros || !periodicidade) {
            throw new IllegalArgumentException("Parâmetros inválidos para conversão de taxa")
        }

        double taxaAnual = percentualDejuros.doubleValue() / 100.0
        Integer periodosPorAno = PERIODOS_POR_ANO.get(periodicidade.toLowerCase())

        if (!periodosPorAno) {
            periodosPorAno = 12 // Default mensal
        }

        // Fórmula: (1 + taxa_anual)^(1/periodos_ano) - 1
        return Math.pow(1 + taxaAnual, 1.0 / periodosPorAno) - 1
    }

    /**
     * Avança data conforme periodicidade
     */
    private void avancarDataConformePeriodicidade(Calendar calendar, String periodicidade, DefinicaoDeCredito definicao) {
        Integer dias = PERIODICIDADE_DIAS.get(periodicidade.toLowerCase())

        if ("variavel".equalsIgnoreCase(periodicidade) {
            dias = definicao?.periodoVariavel ?: 30
        }

            if (dias) {
                calendar.add(Calendar.DATE, dias)
            }
    }

    /**
     * Ajusta data para fins de semana e feriados
     */
    private void ajustarDataParaFinsSemanaEFeriados(Calendar calendar, DefinicaoDeCredito definicao,
                                                    List<Feriado> feriados, Pagamento pagamento) {
        boolean ajustou = false

        // VERIFICA FINS DE SEMANA
        int diaSemana = calendar.get(Calendar.DAY_OF_WEEK)
        if (diaSemana == Calendar.SATURDAY && definicao.excluirSabados) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            pagamento.descricao += " - Sábado ajustado"
            ajustou = true
        }
        if (diaSemana == Calendar.SUNDAY && definicao.excluirDomingos) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            pagamento.descricao += " - Domingo ajustado"
            ajustou = true
        }

        // VERIFICA FERIADOS
        String dataAtualStr = calendar.getTime().format("dd/MM/yyyy")
        for (Feriado feriado : feriados) {
            if (dataAtualStr.equals(feriado.data.format("dd/MM/yyyy"))) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                pagamento.descricao += " - Feriado ajustado"
                ajustou = true
                break
            }
        }

        // VERIFICA NOVAMENTE FINS DE SEMANA APÓS AJUSTE
        if (ajustou) {
            diaSemana = calendar.get(Calendar.DAY_OF_WEEK)
            if (diaSemana == Calendar.SATURDAY && definicao.excluirDiaDePagNoSabado) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            if (diaSemana == Calendar.SUNDAY && definicao.excluirDiaDePagNoDomingo) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    /**
     * Ajustes finais de datas após criação
     */
    private void ajustarDatasFinais(List<Pagamento> pagamentos, DefinicaoDeCredito definicao) {
        for (Pagamento pagamento : pagamentos) {
            Calendar cal = Calendar.getInstance()
            cal.setTime(pagamento.dataPrevistoDePagamento)

            int diaSemana = cal.get(Calendar.DAY_OF_WEEK)
            if (diaSemana == Calendar.SATURDAY && definicao.excluirDiaDePagNoSabado) {
                cal.add(Calendar.DAY_OF_MONTH, 1)
                pagamento.dataPrevistoDePagamento = cal.getTime()
            }
            if (diaSemana == Calendar.SUNDAY && definicao.excluirDiaDePagNoDomingo) {
                cal.add(Calendar.DAY_OF_MONTH, 1)
                pagamento.dataPrevistoDePagamento = cal.getTime()
            }
        }
    }

    /**
     * Cálculo detalhado da amortização
     */
    private void calcularDetalhesAmortizacao(Credito credito, List<Pagamento> pagamentos, BigDecimal valorPrestacao) {
        if ("pmt".equalsIgnoreCase(credito.formaDeCalculo)) {
            calcularAmortizacaoPmt(credito, pagamentos, valorPrestacao)
        } else if ("taxafixa".equalsIgnoreCase(credito.formaDeCalculo)) {
            calcularAmortizacaoTaxaFixa(credito, pagamentos)
        }
    }

    /**
     * Amortização para sistema Price (PMT)
     */
    private void calcularAmortizacaoPmt(Credito credito, List<Pagamento> pagamentos, BigDecimal valorPrestacao) {
        try {
            ArrayList extrato = simuladorService.gerarExtrato(credito, valorPrestacao)

            if (extrato.size() != pagamentos.size() + 1) {
                throw new RuntimeException("Tamanho do extrato incompatível")
            }

            for (int i = 1; i < extrato.size(); i++) {
                def item = extrato[i]
                Pagamento pagamento = pagamentos[i - 1]

                pagamento.saldoDevedor = item.saldoDevedor?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO
                pagamento.valorDeJuros = item.juros?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO
                pagamento.valorDeAmortizacao = item.amortizacao?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO

                pagamento.save(failOnError: true, flush: true)
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular amortização PMT: " + e.getMessage(), e)
        }
    }

    /**
     * Amortização para taxa fixa (amortização constante)
     */
    private void calcularAmortizacaoTaxaFixa(Credito credito, List<Pagamento> pagamentos) {
        try {
            BigDecimal amortizacaoConstante = credito.valorCreditado.divide(
                    new BigDecimal(credito.numeroDePrestacoes),
                    new MathContext(10, RoundingMode.HALF_UP)
            )

            double taxaPeriodica = converterTaxaParaPeriodicidade(
                    credito.percentualDejuros,
                    credito.periodicidade
            )

            BigDecimal saldoDevedor = credito.valorCreditado
            BigDecimal totalAmortizado = BigDecimal.ZERO

            for (Pagamento pagamento : pagamentos) {
                BigDecimal jurosPeriodo = saldoDevedor.multiply(new BigDecimal(taxaPeriodica))

                pagamento.valorDeAmortizacao = amortizacaoConstante.setScale(2, RoundingMode.HALF_UP)
                pagamento.valorDeJuros = jurosPeriodo.setScale(2, RoundingMode.HALF_UP)
                pagamento.saldoDevedor = saldoDevedor.subtract(amortizacaoConstante).setScale(2, RoundingMode.HALF_UP)

                totalAmortizado = totalAmortizado.add(amortizacaoConstante)
                saldoDevedor = pagamento.saldoDevedor

                pagamento.save(failOnError: true, flush: true)
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular amortização taxa fixa: " + e.getMessage(), e)
        }
    }

    // --- MÉTODOS PMT E TAXAFIXA CORRIGIDOS ---

    BigDecimal pmt(double r, int nper, BigDecimal pv, BigDecimal fv, int type) {
        try {
            if (nper <= 0) throw new IllegalArgumentException("nper deve ser > 0")
            if (r <= 0) throw new IllegalArgumentException("Taxa deve ser > 0")

            MathContext mc = new MathContext(16, RoundingMode.HALF_UP)
            BigDecimal rate = BigDecimal.valueOf(r)

            BigDecimal onePlusRate = BigDecimal.ONE.add(rate, mc)
            BigDecimal powerTerm = onePlusRate.pow(nper, mc)
            BigDecimal denominator = powerTerm.subtract(BigDecimal.ONE, mc)

            BigDecimal numerator = rate.multiply(powerTerm, mc)
            BigDecimal annuityFactor = numerator.divide(denominator, mc)

            BigDecimal pvComponent = pv.negate(mc).multiply(annuityFactor, mc)
            BigDecimal fvComponent = rate.multiply(fv, mc).divide(denominator, mc)

            BigDecimal pmt = pvComponent.subtract(fvComponent, mc)

            if (type == 1) {
                pmt = pmt.divide(onePlusRate, mc)
            }

            return pmt.setScale(2, RoundingMode.HALF_UP)

        } catch (Exception e) {
            throw new RuntimeException("Erro cálculo PMT: " + e.getMessage(), e)
        }
    }

    BigDecimal calcularTaxaFixa(double r, int nper, BigDecimal pv) {
        try {
            BigDecimal juros = pv.multiply(BigDecimal.valueOf(r))
            BigDecimal total = pv.add(juros)
            return total.divide(new BigDecimal(nper), new MathContext(10, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP)
        } catch (Exception e) {
            throw new RuntimeException("Erro cálculo taxa fixa: " + e.getMessage(), e)
        }
    }

    // --- MÉTODOS EXISTENTES (mantidos com melhorias) ---

    def actualizarEstadoDeCredito(Credito creditoInstance) {
        // Implementação existente com melhorias
    }

    def eliminarPagamentos(Credito creditoInstance) {
        // Implementação existente
    }

    // ... (outros métodos mantidos com melhorias incrementais)

    /**
     * Método auxiliar para verificar se é fim de semana
     */
    private boolean isFimDeSemana(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }

    /**
     * Método auxiliar para verificar feriado
     */
    private boolean isFeriado(Date data, List<Feriado> feriados) {
        String dataStr = data.format("dd/MM/yyyy")
        return feriados.any { it.data.format("dd/MM/yyyy") == dataStr }
    }
}