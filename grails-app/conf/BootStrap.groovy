import mz.maleyanga.GenerateRandomNumber
import mz.maleyanga.Taxa.Taxa
import mz.maleyanga.cliente.Cliente
import mz.maleyanga.conta.PlanoDeContas
import mz.maleyanga.entidade.Entidade
import mz.maleyanga.saidas.Saida
import mz.maleyanga.security.Role
import mz.maleyanga.security.RoleGroup
import mz.maleyanga.security.Utilizador
import mz.maleyanga.security.UtilizadorRole
import mz.maleyanga.security.UtilizadorRoleGroup
import mz.maleyanga.security.RoleGroupRole
import mz.maleyanga.settings.Settings
import mz.maleyanga.conta.Conta
import  mz.maleyanga.EncryptionService

class BootStrap {
    EncryptionService encryptionService

    def init = { servletContext ->
    /*    Feriado feriado0 = new Feriado(designacao:  'Dia da Família',data: new Date(2019,12,25)).save(flush: true)
        Feriado feriado1 = new Feriado(data: new Date(2019,12,25),designacao: 'Ano Novo').save(flush: true)
        Feriado feriado2 = new Feriado(data: new Date(2019,1,1),designacao: 'Ano Novo').save(flush: true)
        Feriado feriado3 = new Feriado(data: new Date(2019,2,3),designacao: 'Dia dos Heróis').save(flush: true)
        Feriado feriado5 = new Feriado(data: new Date(2019,4,7),designacao: 'Dia da Mulher').save(flush: true)
        Feriado feriado6 = new Feriado(data: new Date(2019,5,1),designacao: 'Dia dos Trabalhadores').save(flush: true)
        Feriado feriado7 = new Feriado(data: new Date(2019,6,25),designacao: 'Dia da Independência').save(flush: true)
        Feriado feriado8 = new Feriado(data: new Date(2019,9,7),designacao: 'Dia da Vitória').save(flush: true)
        Feriado feriado9 = new Feriado(data: new Date(2019,9,25),designacao: 'Dia das Forças Armadas').save(flush: true)
        Feriado feriado10 = new Feriado(data: new Date(2019,10,4),designacao: 'Dia da Paz e Reconciliação').save(flush: true)*/
        if(!Role.findByAuthority('DIARIO_EDIT')){ def role1 = new Role(authority:"DIARIO_EDIT").save (flush: true) }
        if(!Role.findByAuthority('DIARIO_CREATE')){ def role1 = new Role(authority:"DIARIO_CREATE").save (flush: true) }
        if(!Role.findByAuthority('DIARIO_FECHAR')){ def role1 = new Role(authority:"DIARIO_FECHAR").save (flush: true) }
        if(!Role.findByAuthority('DIARIO_ABRIR')){ def role1 = new Role(authority:"DIARIO_ABRIR").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_INDEX')){ def role1 = new Role(authority:"CLIENTE_INDEX").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_GESTOR')){ def role1 = new Role(authority:"CLIENTE_GESTOR").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_LIST')){ def role1 = new Role(authority:"CLIENTE_LIST").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_SHOW')){ def role1 = new Role(authority:"CLIENTE_SHOW").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_CREATE')){ def role1 = new Role(authority:"CLIENTE_CREATE").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_SAVE')){ def role1 = new Role(authority:"CLIENTE_SAVE").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_REMOTE_SAVE')){ def role1 = new Role(authority:"CLIENTE_REMOTE_SAVE").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_EDIT')){ def role1 = new Role(authority:"CLIENTE_EDIT").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_UPDATE')){ def role1 = new Role(authority:"CLIENTE_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_BLACKLIST')){ def role1 = new Role(authority:"CLIENTE_BLACKLIST").save (flush: true) }
        if(!Role.findByAuthority('CLIENTE_DELETE')){ def role1 = new Role(authority:"CLIENTE_DELETE").save (flush: true) }
        if(!Role.findByAuthority('BANCO_INDEX')){ def role1 = new Role(authority:"BANCO_INDEX").save (flush: true) }
        if(!Role.findByAuthority('BANCO_LIST')){ def role1 = new Role(authority:"BANCO_LIST").save (flush: true) }
        if(!Role.findByAuthority('BANCO_SHOW')){ def role1 = new Role(authority:"BANCO_SHOW").save (flush: true) }
        if(!Role.findByAuthority('BANCO_CREATE')){ def role1 = new Role(authority:"BANCO_CREATE").save (flush: true) }
        if(!Role.findByAuthority('BANCO_SAVE')){ def role1 = new Role(authority:"BANCO_SAVE").save (flush: true) }
        if(!Role.findByAuthority('BANCO_EDIT')){ def role1 = new Role(authority:"BANCO_EDIT").save (flush: true) }
        if(!Role.findByAuthority('BANCO_UPDATE')){ def role1 = new Role(authority:"BANCO_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('BANCO_DELETE')){ def role1 = new Role(authority:"BANCO_DELETE").save (flush: true) }
        if(!Role.findByAuthority('CONTA_INDEX')){ def role1 = new Role(authority:"CONTA_INDEX").save (flush: true) }
        if(!Role.findByAuthority('CONTA_LIST')){ def role1 = new Role(authority:"CONTA_LIST").save (flush: true) }
        if(!Role.findByAuthority('CONTA_SHOW')){ def role1 = new Role(authority:"CONTA_SHOW").save (flush: true) }
        if(!Role.findByAuthority('CONTA_CREATE')){ def role1 = new Role(authority:"CONTA_CREATE").save (flush: true) }
        if(!Role.findByAuthority('CONTA_CONTAS')){ def role1 = new Role(authority:"CONTA_CONTAS").save (flush: true) }
        if(!Role.findByAuthority('CONTA_CREATE_FROM_ENTIDADE')){ def role1 = new Role(authority:"CONTA_CREATE_FROM_ENTIDADE").save (flush: true) }
        if(!Role.findByAuthority('CONTA_SAVE')){ def role1 = new Role(authority:"CONTA_SAVE").save (flush: true) }
        if(!Role.findByAuthority('CONTA_EDIT')){ def role1 = new Role(authority:"CONTA_EDIT").save (flush: true) }
        if(!Role.findByAuthority('CONTA_UPDATE')){ def role1 = new Role(authority:"CONTA_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('CONTA_DELETE')){ def role1 = new Role(authority:"CONTA_DELETE").save (flush: true) }
        if(!Role.findByAuthority('CONTACT_INDEX')){ def role1 = new Role(authority:"CONTACT_INDEX").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_INDEX')){ def role1 = new Role(authority:"CREDITO_INDEX").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_LIST_ALL')){ def role1 = new Role(authority:"CREDITO_LIST_ALL").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_LIST_ABERTOS')){ def role1 = new Role(authority:"CREDITO_LIST_ABERTOS").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_LIST_FECHADOS')){ def role1 = new Role(authority:"CREDITO_LIST_FECHADOS").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_LIST_FECHADOS')){ def role1 = new Role(authority:"CREDITO_LIST_FECHADOS").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_LIST_PENDENTES')){ def role1 = new Role(authority:"CREDITO_LIST_PENDENTES").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_LIST_EM_PROGRESSO')){ def role1 = new Role(authority:"CREDITO_LIST_EM_PROGRESSO").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_LIST')){ def role1 = new Role(authority:"CREDITO_LIST").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_SHOW')){ def role1 = new Role(authority:"CREDITO_SHOW").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_CREATE')){ def role1 = new Role(authority:"CREDITO_CREATE").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_CREATE_SIMULADOR')){ def role1 = new Role(authority:"CREDITO_CREATE_SIMULADOR").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_SAVE')){ def role1 = new Role(authority:"CREDITO_SAVE").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_EDIT')){ def role1 = new Role(authority:"CREDITO_EDIT").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_UPDATE')){ def role1 = new Role(authority:"CREDITO_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_DELETE')){ def role1 = new Role(authority:"CREDITO_DELETE").save (flush: true) }
        if(!Role.findByAuthority('ANEXO_INDEX')){ def role1 = new Role(authority:"ANEXO_INDEX").save (flush: true) }
        if(!Role.findByAuthority('ANEXO_LIST')){ def role1 = new Role(authority:"ANEXO_LIST").save (flush: true) }
        if(!Role.findByAuthority('ANEXO_SHOW')){ def role1 = new Role(authority:"ANEXO_SHOW").save (flush: true) }
        if(!Role.findByAuthority('ANEXO_CREATE')){ def role1 = new Role(authority:"ANEXO_CREATE").save (flush: true) }
        if(!Role.findByAuthority('ANEXO_SAVE')){ def role1 = new Role(authority:"ANEXO_SAVE").save (flush: true) }
        if(!Role.findByAuthority('ANEXO_EDIT')){ def role1 = new Role(authority:"ANEXO_EDIT").save (flush: true) }
        if(!Role.findByAuthority('ANEXO_UPDATE')){ def role1 = new Role(authority:"ANEXO_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('ANEXO_DELETE')){ def role1 = new Role(authority:"ANEXO_DELETE").save (flush: true) }
        if(!Role.findByAuthority('NOTA_INDEX')){ def role1 = new Role(authority:"NOTA_INDEX").save (flush: true) }
        if(!Role.findByAuthority('NOTA_LIST')){ def role1 = new Role(authority:"NOTA_LIST").save (flush: true) }
        if(!Role.findByAuthority('NOTA_SHOW')){ def role1 = new Role(authority:"NOTA_SHOW").save (flush: true) }
        if(!Role.findByAuthority('NOTA_CREATE')){ def role1 = new Role(authority:"NOTA_CREATE").save (flush: true) }
        if(!Role.findByAuthority('NOTA_SAVE')){ def role1 = new Role(authority:"NOTA_SAVE").save (flush: true) }
        if(!Role.findByAuthority('NOTA_EDIT')){ def role1 = new Role(authority:"NOTA_EDIT").save (flush: true) }
        if(!Role.findByAuthority('NOTA_UPDATE')){ def role1 = new Role(authority:"NOTA_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('NOTA_DELETE')){ def role1 = new Role(authority:"NOTA_DELETE").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_INDEX')){ def role1 = new Role(authority:"ENTIDADE_INDEX").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_SELECT')){ def role1 = new Role(authority:"ENTIDADE_SELECT").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_GO_TO_HOME')){ def role1 = new Role(authority:"ENTIDADE_GO_TO_HOME").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_LIST')){ def role1 = new Role(authority:"ENTIDADE_LIST").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_SHOW')){ def role1 = new Role(authority:"ENTIDADE_SHOW").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_CREATE')){ def role1 = new Role(authority:"ENTIDADE_CREATE").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_SAVE')){ def role1 = new Role(authority:"ENTIDADE_SAVE").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_EDIT')){ def role1 = new Role(authority:"ENTIDADE_EDIT").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_UPDATE')){ def role1 = new Role(authority:"ENTIDADE_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('ENTIDADE_DELETE')){ def role1 = new Role(authority:"ENTIDADE_DELETE").save (flush: true) }
        if(!Role.findByAuthority('FERIADO_FERIADO_CRUD')){ def role1 = new Role(authority:"FERIADO_FERIADO_CRUD").save (flush: true) }
        if(!Role.findByAuthority('FERIADO_INDEX')){ def role1 = new Role(authority:"FERIADO_INDEX").save (flush: true) }
        if(!Role.findByAuthority('FERIADO_CREATE')){ def role1 = new Role(authority:"FERIADO_CREATE").save (flush: true) }
        if(!Role.findByAuthority('LOGS_INDEX')){ def role1 = new Role(authority:"LOGS_INDEX").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_INDEX')){ def role1 = new Role(authority:"PAGAMENTO_INDEX").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_LIST')){ def role1 = new Role(authority:"PAGAMENTO_LIST").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_LIST_DEVEDORES')){ def role1 = new Role(authority:"PAGAMENTO_LIST_DEVEDORES").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_SHOW')){ def role1 = new Role(authority:"PAGAMENTO_SHOW").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_CREATE')){ def role1 = new Role(authority:"PAGAMENTO_CREATE").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_SAVE')){ def role1 = new Role(authority:"PAGAMENTO_SAVE").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_GERAR_NOVA_PRESTACAO')){ def role1 = new Role(authority:"PAGAMENTO_GERAR_NOVA_PRESTACAO").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_EDIT')){ def role1 = new Role(authority:"PAGAMENTO_EDIT").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_UPDATE')){ def role1 = new Role(authority:"PAGAMENTO_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_DELETE')){ def role1 = new Role(authority:"PAGAMENTO_DELETE").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_PAGAMENTOS_EFECTIVADOS')){ def role1 = new Role(authority:"PAGAMENTO_PAGAMENTOS_EFECTIVADOS").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_PAGAMENTOS_DE_PAGAMENTO')){ def role1 = new Role(authority:"PAGAMENTO_PAGAMENTOS_DE_PAGAMENTO").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_PAGAMENTOS_PERFEITOS')){ def role1 = new Role(authority:"PAGAMENTO_PAGAMENTOS_PERFEITOS").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_PAGAMENTOS_POR_EFECTIVAR')){ def role1 = new Role(authority:"PAGAMENTO_PAGAMENTOS_POR_EFECTIVAR").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_SHOW_BACKUP')){ def role1 = new Role(authority:"PAGAMENTO_SHOW_BACKUP").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_PAGAMENTOS')){ def role1 = new Role(authority:"PAGAMENTO_PAGAMENTOS").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTOS_PERFEITOS')){ def role1 = new Role(authority:"PAGAMENTOS_PERFEITOS").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTOS_POR_EFECTIVAR')){ def role1 = new Role(authority:"PAGAMENTOS_POR_EFECTIVAR").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_PRINT_RECEBIDO')){ def role1 = new Role(authority:"PAGAMENTO_PRINT_RECEBIDO").save (flush: true) }
        if(!Role.findByAuthority('PAGAMENTO_JUROS_DE_DEMORA')){ def role1 = new Role(authority:"PAGAMENTO_JUROS_DE_DEMORA").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_INDEX')){ def role1 = new Role(authority:"PARCELA_INDEX").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_LIST')){ def role1 = new Role(authority:"PARCELA_LIST").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_SHOW')){ def role1 = new Role(authority:"PARCELA_SHOW").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_CREATE')){ def role1 = new Role(authority:"PARCELA_CREATE").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_CREATE_AUTOMATICO')){ def role1 = new Role(authority:"PARCELA_CREATE_AUTOMATICO").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_SAVE')){ def role1 = new Role(authority:"PARCELA_SAVE").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_EDIT')){ def role1 = new Role(authority:"PARCELA_EDIT").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_UPDATE')){ def role1 = new Role(authority:"PARCELA_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_DELETE')){ def role1 = new Role(authority:"PARCELA_DELETE").save (flush: true) }
        if(!Role.findByAuthority('PARCELA_SHOW_ATTACHMENT')){ def role1 = new Role(authority:"PARCELA_SHOW_ATTACHMENT").save (flush: true) }
        if(!Role.findByAuthority('REMISSAO_INDEX')){ def role1 = new Role(authority:"REMISSAO_INDEX").save (flush: true) }
        if(!Role.findByAuthority('REMISSAO_LIST')){ def role1 = new Role(authority:"REMISSAO_LIST").save (flush: true) }
        if(!Role.findByAuthority('REMISSAO_SHOW')){ def role1 = new Role(authority:"REMISSAO_SHOW").save (flush: true) }
        if(!Role.findByAuthority('REMISSAO_CREATE')){ def role1 = new Role(authority:"REMISSAO_CREATE").save (flush: true) }
        if(!Role.findByAuthority('REMISSAO_CREATE_OUT')){ def role1 = new Role(authority:"REMISSAO_CREATE_OUT").save (flush: true) }
        if(!Role.findByAuthority('REMISSAO_SAVE')){ def role1 = new Role(authority:"REMISSAO_SAVE").save (flush: true) }
        if(!Role.findByAuthority('REMISSAO_EDIT')){ def role1 = new Role(authority:"REMISSAO_EDIT").save (flush: true) }
        if(!Role.findByAuthority('REMISSAO_UPDATE')){ def role1 = new Role(authority:"REMISSAO_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('REMISSAO_DELETE')){ def role1 = new Role(authority:"REMISSAO_DELETE").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_LIST_APROVADOS')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_LIST_APROVADOS").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_ADD_CREDITO')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_ADD_CREDITO").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_LIST_FECHADOS')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_LIST_FECHADOS").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_LIST_PENDENTES')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_LIST_PENDENTES").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_LIST_ABERTOS')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_LIST_ABERTOS").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_LIST_INDEX')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_LIST_INDEX").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_LIST_ALL')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_LIST_ALL").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_SIMULADOR_DE_CREDITO')){ def role1 = new Role(authority:"CREDITO_SIMULADOR_DE_CREDITO").save (flush: true) }
        if(!Role.findByAuthority('CREDITO_SHOW_SIMULADOR')){ def role1 = new Role(authority:"CREDITO_SHOW_SIMULADOR").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_LIST')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_LIST").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_SHOW')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_SHOW").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_CREATE')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_CREATE").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_SAVE')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_SAVE").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_EDIT')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_EDIT").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_UPDATE')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('PEDIDO_DE_CREDITO_DELETE')){ def role1 = new Role(authority:"PEDIDO_DE_CREDITO_DELETE").save (flush: true) }
        if(!Role.findByAuthority('PERIODO_INDEX')){ def role1 = new Role(authority:"PERIODO_INDEX").save (flush: true) }
        if(!Role.findByAuthority('PERIODO_LIST')){ def role1 = new Role(authority:"PERIODO_LIST").save (flush: true) }
        if(!Role.findByAuthority('PERIODO_SHOW')){ def role1 = new Role(authority:"PERIODO_SHOW").save (flush: true) }
        if(!Role.findByAuthority('PERIODO_CREATE')){ def role1 = new Role(authority:"PERIODO_CREATE").save (flush: true) }
        if(!Role.findByAuthority('PERIODO_SAVE')){ def role1 = new Role(authority:"PERIODO_SAVE").save (flush: true) }
        if(!Role.findByAuthority('PERIODO_EDIT')){ def role1 = new Role(authority:"PERIODO_EDIT").save (flush: true) }
        if(!Role.findByAuthority('PERIODO_UPDATE')){ def role1 = new Role(authority:"PERIODO_UPDATE").save (flush: true) }
        if(!Role.findByAuthority('PERIODO_DELETE')){ def role1 = new Role(authority:"PERIODO_DELETE").save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_INDEX')){ def role1 = new Role(authority:"RELATORIOS_INDEX").save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_RECEITAS')){ def role1 = new Role(authority:"RELATORIOS_RECEITAS").save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_LOGS')){ def role1 = new Role(authority:"RELATORIOS_LOGS").save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_ACTION_LOGGING')){ def role1 = new Role(authority:"RELATORIOS_ACTION_LOGGING").save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_CREDITOS')){ def role1 = new Role(authority:"RELATORIOS_CREDITOS").save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_EXTRATO_DE_CREDITO')){ def role1 = new Role(authority:'RELATORIOS_EXTRATO_DE_CREDITO').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_EXTRATO_PRESTACOES_EM_DIVIDA')){ def role1 = new Role(authority:'RELATORIOS_EXTRATO_PRESTACOES_EM_DIVIDA').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_RECEITAS')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_RECEITAS').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_BALANCO')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_BALANCO').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_BALANCO_GERAL')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_BALANCO_GERAL').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_BALANCO_GERAL_DE_RECEBIMENTOS_DE_CREDITOS_FECHADOS')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_BALANCO_GERAL_DE_RECEBIMENTOS_DE_CREDITOS_FECHADOS').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_ACTION_LOGGING')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_ACTION_LOGGING').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_CREDITOS')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_CREDITOS').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_EXTRATO_DE_CREDITO')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_EXTRATO_DE_CREDITO').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_COMTRATO')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_COMTRATO').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_PRESTACAO')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_PRESTACAO').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_PAGAMENTOS_POR_RECEBER')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_PAGAMENTOS_POR_RECEBER').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_LOGS_REPORT')){ def role1 = new Role(authority:'RELATORIOS_LOGS_REPORT').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_ACTION_LOG_REPORT')){ def role1 = new Role(authority:'RELATORIOS_ACTION_LOG_REPORT').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_EXTRATO_DE_PAGAMENTO')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_EXTRATO_DE_PAGAMENTO').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_EXTRATO_DE_CREDITO')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_EXTRATO_DE_CREDITO').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_EXTRATO_DO_PEDIDO_DE_CREDITO')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_EXTRATO_DO_PEDIDO_DE_CREDITO').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_EXTRATO_DE_CLIENTE')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_EXTRATO_DE_CLIENTE').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_PRESTACOES_ATRASADAS')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_PRESTACOES_ATRASADAS').save (flush: true) }
        if(!Role.findByAuthority('RELATORIOS_IMPRIMIR_PRESTACOES_DO_DIA')){ def role1 = new Role(authority:'RELATORIOS_IMPRIMIR_PRESTACOES_DO_DIA').save (flush: true) }
        if(!Role.findByAuthority('ROLE_DELETE')){ def role1 = new Role(authority:'ROLE_DELETE').save (flush: true) }
        if(!Role.findByAuthority('ROLE_UPDATE')){ def role1 = new Role(authority:'ROLE_UPDATE').save (flush: true) }
        if(!Role.findByAuthority('ROLE_SAVE')){ def role1 = new Role(authority:'ROLE_SAVE').save (flush: true) }
        if(!Role.findByAuthority('ROLE_CREATE')){ def role1 = new Role(authority:'ROLE_CREATE').save (flush: true) }
        if(!Role.findByAuthority('ROLE_SHOW')){ def role1 = new Role(authority:'ROLE_SHOW').save (flush: true) }
        if(!Role.findByAuthority('ROLE_LIST')){ def role1 = new Role(authority:'ROLE_LIST').save (flush: true) }
        if(!Role.findByAuthority('ROLE_INDEX')){ def role1 = new Role(authority:'ROLE_INDEX').save (flush: true) }

        if(!Role.findByAuthority('SAIDA_DELETE')){ def role1 = new Role(authority:'SAIDA_DELETE').save (flush: true) }
        if(!Role.findByAuthority('SAIDA_UPDATE')){ def role1 = new Role(authority:'SAIDA_UPDATE').save (flush: true) }
        if(!Role.findByAuthority('SAIDA_SAVE')){ def role1 = new Role(authority:'SAIDA_SAVE').save (flush: true) }
        if(!Role.findByAuthority('SAIDA_CREATE')){ def role1 = new Role(authority:'SAIDA_CREATE').save (flush: true) }
        if(!Role.findByAuthority('SAIDA_SHOW')){ def role1 = new Role(authority:'SAIDA_SHOW').save (flush: true) }
        if(!Role.findByAuthority('SAIDA_LIST')){ def role1 = new Role(authority:'SAIDA_LIST').save (flush: true) }
        if(!Role.findByAuthority('SAIDA_INDEX')){ def role1 = new Role(authority:'SAIDA_INDEX').save (flush: true) }

        if(!Role.findByAuthority('UTILIZADOR_DELETE')){ def role1 = new Role(authority:'UTILIZADOR_DELETE').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_UPDATE')){ def role1 = new Role(authority:'').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_EDIT')){ def role1 = new Role(authority:'UTILIZADOR_EDIT').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_SAVE')){ def role1 = new Role(authority:'UTILIZADOR_SAVE').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_CREATE')){ def role1 = new Role(authority:'UTILIZADOR_CREATE').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_SHOW')){ def role1 = new Role(authority:'UTILIZADOR_SHOW').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_LIST')){ def role1 = new Role(authority:'UTILIZADOR_LIST').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_LOGS')){ def role1 = new Role(authority:'UTILIZADOR_LOGS').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_INDEX')){ def role1 = new Role(authority:'UTILIZADOR_INDEX').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_ROLE_DELETE')){ def role1 = new Role(authority:'UTILIZADOR_ROLE_DELETE').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_ROLE_UPDATE')){ def role1 = new Role(authority:'UTILIZADOR_ROLE_UPDATE').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_ROLE_EDIT')){ def role1 = new Role(authority:'UTILIZADOR_ROLE_EDIT').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_ROLE_SAVE')){ def role1 = new Role(authority:'UTILIZADOR_ROLE_SAVE').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_ROLE_CREATE')){ def role1 = new Role(authority:'UTILIZADOR_ROLE_CREATE').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_ROLE_SHOW')){ def role1 = new Role(authority:'UTILIZADOR_ROLE_SHOW').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_ROLE_LIST')){ def role1 = new Role(authority:'UTILIZADOR_ROLE_LIST').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_ROLE_INDEX')){ def role1 = new Role(authority:'UTILIZADOR_ROLE_INDEX').save (flush: true) }
        if(!Role.findByAuthority('SETTINGS_DELETE')){ def role1 = new Role(authority:'SETTINGS_DELETE').save (flush: true) }
        if(!Role.findByAuthority('SETTINGS_UPDATE')){ def role1 = new Role(authority:'SETTINGS_UPDATE').save (flush: true) }
        if(!Role.findByAuthority('SETTINGS_EDIT')){ def role1 = new Role(authority:'SETTINGS_EDIT').save (flush: true) }
        if(!Role.findByAuthority('SETTINGS_SAVE')){ def role1 = new Role(authority:'SETTINGS_SAVE').save (flush: true) }
        if(!Role.findByAuthority('SETTINGS_CREATE')){ def role1 = new Role(authority:'SETTINGS_CREATE').save (flush: true) }
        if(!Role.findByAuthority('SETTINGS_SHOW')){ def role1 = new Role(authority:'SETTINGS_SHOW').save (flush: true) }
        if(!Role.findByAuthority('SETTINGS_LIST')){ def role1 = new Role(authority:'SETTINGS_LIST').save (flush: true) }
        if(!Role.findByAuthority('SETTINGS_INDEX')){ def role1 = new Role(authority:'SETTINGS_INDEX').save (flush: true) }
        if(!Role.findByAuthority('SETTINGS_DIAS_UTEIS')){ def role1 = new Role(authority:'SETTINGS_DIAS_UTEIS').save (flush: true) }
        if(!Role.findByAuthority('SIMULADOR_DELETE')){ def role1 = new Role(authority:'SIMULADOR_DELETE').save (flush: true) }
        if(!Role.findByAuthority('SIMULADOR_UPDATE')){ def role1 = new Role(authority:'SIMULADOR_UPDATE').save (flush: true) }
        if(!Role.findByAuthority('SIMULADOR_EDIT')){ def role1 = new Role(authority:'SIMULADOR_EDIT').save (flush: true) }
        if(!Role.findByAuthority('SIMULADOR_SAVE')){ def role1 = new Role(authority:'SIMULADOR_SAVE').save (flush: true) }
        if(!Role.findByAuthority('SIMULADOR_CREATE')){ def role1 = new Role(authority:'SIMULADOR_CREATE').save (flush: true) }
        if(!Role.findByAuthority('SIMULADOR_SHOW')){ def role1 = new Role(authority:'SIMULADOR_SHOW').save (flush: true) }
        if(!Role.findByAuthority('SIMULADOR_LIST')){ def role1 = new Role(authority:'SIMULADOR_LIST').save (flush: true) }
        if(!Role.findByAuthority('SIMULADOR_INDEX')){ def role1 = new Role(authority:'SIMULADOR_INDEX').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEIN_DELETE')){ def role1 = new Role(authority:'OZELIMESSAGEIN_DELETE').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEIN_UPDATE')){ def role1 = new Role(authority:'OZELIMESSAGEIN_UPDATE').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEIN_EDIT')){ def role1 = new Role(authority:'OZELIMESSAGEIN_EDIT').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEIN_SAVE')){ def role1 = new Role(authority:'OZELIMESSAGEIN_SAVE').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEIN_SHOW')){ def role1 = new Role(authority:'OZELIMESSAGEIN_SHOW').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEIN_CREATE')){ def role1 = new Role(authority:'OZELIMESSAGEIN_CREATE').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEIN_LIST')){ def role1 = new Role(authority:'OZELIMESSAGEIN_LIST').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEIN_INDEX')){ def role1 = new Role(authority:'OZELIMESSAGEIN_INDEX').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEOUT_DELETE')){ def role1 = new Role(authority:'OZELIMESSAGEOUT_DELETE').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEOUT_UPDATE')){ def role1 = new Role(authority:'OZELIMESSAGEOUT_UPDATE').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEOUT_EDIT')){ def role1 = new Role(authority:'OZELIMESSAGEOUT_EDIT').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEOUT_SAVE')){ def role1 = new Role(authority:'OZELIMESSAGEOUT_SAVE').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEOUT_CREATE')){ def role1 = new Role(authority:'OZELIMESSAGEOUT_CREATE').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEOUT_SHOW')){ def role1 = new Role(authority:'OZELIMESSAGEOUT_SHOW').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEOUT_LIST')){ def role1 = new Role(authority:'OZELIMESSAGEOUT_LIST').save (flush: true) }
        if(!Role.findByAuthority('OZELIMESSAGEOUT_INDEX')){ def role1 = new Role(authority:'OZELIMESSAGEOUT_INDEX').save (flush: true) }
        if(!Role.findByAuthority('SMS_LOG_DELETE')){ def role1 = new Role(authority:'SMS_LOG_DELETE').save (flush: true) }
        if(!Role.findByAuthority('SMS_LOG_UPDATE')){ def role1 = new Role(authority:'SMS_LOG_UPDATE').save (flush: true) }
        if(!Role.findByAuthority('SMS_LOG_EDIT')){ def role1 = new Role(authority:'SMS_LOG_EDIT').save (flush: true) }
        if(!Role.findByAuthority('SMS_LOG_SAVE')){ def role1 = new Role(authority:'SMS_LOG_SAVE').save (flush: true) }
        if(!Role.findByAuthority('SMS_LOG_CREATE')){ def role1 = new Role(authority:'SMS_LOG_CREATE').save (flush: true) }
        if(!Role.findByAuthority('SMS_LOG_SHOW')){ def role1 = new Role(authority:'SMS_LOG_SHOW').save (flush: true) }
        if(!Role.findByAuthority('SMS_LOG_SHOW')){ def role1 = new Role(authority:'SMS_LOG_SHOW').save (flush: true) }
        if(!Role.findByAuthority('SMS_LOG_INDEX')){ def role1 = new Role(authority:'SMS_LOG_INDEX').save (flush: true) }
        if(!Role.findByAuthority('TAXA_DELETE')){ def role1 = new Role(authority:'TAXA_DELETE').save (flush: true) }
        if(!Role.findByAuthority('TAXA_UPDATE')){ def role1 = new Role(authority:'TAXA_UPDATE').save (flush: true) }
        if(!Role.findByAuthority('TAXA_SAVE')){ def role1 = new Role(authority:'TAXA_SAVE').save (flush: true) }
        if(!Role.findByAuthority('TAXA_CREATE')){ def role1 = new Role(authority:'TAXA_CREATE').save (flush: true) }
        if(!Role.findByAuthority('TAXA_SHOW')){ def role1 = new Role(authority:'TAXA_SHOW').save (flush: true) }
        if(!Role.findByAuthority('TAXA_LIST')){ def role1 = new Role(authority:'TAXA_LIST').save (flush: true) }
        if(!Role.findByAuthority('TAXA_INDEX')){ def role1 = new Role(authority:'TAXA_INDEX').save (flush: true) }
        if(!Role.findByAuthority('UTILITARIOS_SMS')){ def role1 = new Role(authority:'UTILITARIOS_SMS').save (flush: true) }
        if(!Role.findByAuthority('UTILITARIOS_SEND_HTTP')){ def role1 = new Role(authority:'UTILITARIOS_SEND_HTTP').save (flush: true) }
        if(!Role.findByAuthority('UTILITARIOS_LIST')){ def role1 = new Role(authority:'UTILITARIOS_LIST').save (flush: true) }
        if(!Role.findByAuthority('UTILITARIOS_DO_UPLAOD')){ def role1 = new Role(authority:'UTILITARIOS_DO_UPLAOD').save (flush: true) }
        if(!Role.findByAuthority('UTILITARIOS_INDEX')){ def role1 = new Role(authority:'UTILITARIOS_INDEX').save (flush: true) }
        if(!Role.findByAuthority('UTILIZADOR_UTILIZADOR_CRUD')){ def role1 = new Role(authority:'UTILIZADOR_UTILIZADOR_CRUD').save (flush: true) }
        if(!Role.findByAuthority('ROLE_GROUP')){ def role1 = new Role(authority:'ROLE_GROUP').save (flush: true) }
        if(!Role.findByAuthority('HOME_INDEX')){ def role1 = new Role(authority:'HOME_INDEX').save (flush: true) }
      //  if(!Role.findByAuthority('')){ def role1 = new Role(authority:'').save (flush: true) }


        def adminRole = new Role (authority: 'ROLE_ADMIN').save(flush: true)
        def assisAdmin = new Role (authority: 'ROLE_ASSIS_ADMIN').save(flush: true)
        def gestorCredito = new Role (authority: 'ROLE_GESTOR_CREDITO').save(flush: true)

        def admin = new  Utilizador(username: 'admin',password: 'Admin2021***',email: 'cnhantumbo@vbc.co.mz')
        admin.save(flash:true)



     /*   def entidade1 = new Entidade(nome: 'PMC',
                nuit: '10000001',residencia: 'Boquico',email: 'macuvele@gmail.com', telefone: '210009',proprietario: 'Macuvele',
                formaDeCalculo: 'm', descricaoDaFormulaDeCalculo:'Juros de mora (jm) = j*30%*d/30= 1800*30%*d/30, onde d=dias que o cliente levou at� pagar os juros.'
                 )
        def entidade2 = new Entidade(nome: 'TXUNA',
                nuit: '10000002',residencia: 'Boqui�o',email: 'macuvele@gmail.com', telefone: '210010',proprietario: 'Atalvino',
                formaDeCalculo: 'v', descricaoDaFormulaDeCalculo:'Juros de mora (jm) = j*d= 1800*30%*d, onde d=dias que o cliente levou at� pagar os juros., j=juros de mora'
        )
        def entidade3 = new Entidade(nome: 'SAC',
                nuit: '10000003',residencia: 'Bairro: Inhagoya',email: 'fanisso@gmail.com', telefone: '2100102',proprietario: 'Claudino',
                formaDeCalculo: 's', descricaoDaFormulaDeCalculo:'Juros de mora (jm) = j*d= 1800*2%*d, onde d=dias que o cliente levou ate pagar os juros., j=juros de mora'
        )*/
        def admiDb = Utilizador.findByUsername('admin')
        if(!RoleGroup.findByName("RG_FULL")){
            RoleGroup roleGroup = new RoleGroup(name: "RG_FULL")
            roleGroup.save(flush: true)
            def rolesDB = Role.all
            def grDB = RoleGroup.findByName("RG_FULL")
            for(Role role in rolesDB){
                    UtilizadorRole.create admiDb,role,true
                    RoleGroupRole.create grDB, role, true
            }
        }

        def rgFull = RoleGroup.findByName("RG_FULL")

        UtilizadorRoleGroup.create admiDb, rgFull, true
        def adminRoleDb = Role.findByAuthority('ROLE_ADMIN')
        UtilizadorRole.create admiDb, adminRoleDb, true
        def chave = "maleyanga"
        byte[] chav = encryptionService.encrypt(chave)
        def entidade4 = new Entidade(nome: 'MALEYANGA',chave: chav,
                nuit: "10000001",residencia: 'Rua de Kassuende Nº 39, r/c',email: 'maleyanga@gmail.com', telefone: '82',proprietario: 'Claudino Nhantumbo',
                formaDeCalculo: 'f', descricaoDaFormulaDeCalculo:'excel PMT'
        ).save(flush: true)


        /*if(!Entidade.findByNome('MALEYANGA')){
            entidade4.save(flush: true)
        }*/
        Settings settings = new Settings(nome: 'settings', domingo: true,segunda: true,terca: true,quarta: true,quinta: true,sexta: true,sabado: true,atualizarDadosDoCliente: false).save flush: true


        /* criação de plano de contas inicial*/

        if(!PlanoDeContas.findByDescricao("PLANO_DE_CONTAS_INICIAL")){
            def selectedPlanoDeContas = new PlanoDeContas()
            selectedPlanoDeContas.descricao="PLANO_DE_CONTAS_INICIAL"
            selectedPlanoDeContas.ativo = true
            selectedPlanoDeContas.fechado = false

            def pdcs= PlanoDeContas.all
            if(pdcs!=null){
                if(selectedPlanoDeContas.ativo){
                    pdcs.each {
                        it.ativo=false
                        it.merge()
                    }
                }
            }
            selectedPlanoDeContas.save(flush: true)
        }
        def planoDeContas = PlanoDeContas.findByAtivo(true)

        if(!Conta.findByDesignacaoDaConta("ACTIVOS")){
            def selectedClasseActivo = new Conta()
            selectedClasseActivo.designacaoDaConta= "ACTIVOS"
            selectedClasseActivo.ativo = true
            selectedClasseActivo.codigo="1"
            selectedClasseActivo.planoDeContas = planoDeContas
            selectedClasseActivo.finalidade = "conta_classe"
            selectedClasseActivo.save(flush: true)
        }
        if(!Conta.findByDesignacaoDaConta("PASSIVOS")){
            def selectedClasseActivo = new Conta()
            selectedClasseActivo.designacaoDaConta= "PASSIVOS"
            selectedClasseActivo.ativo = false
            selectedClasseActivo.codigo="2"
            selectedClasseActivo.planoDeContas = planoDeContas
            selectedClasseActivo.finalidade = "conta_classe"
            selectedClasseActivo.save(flush: true)
        }
        if(!Conta.findByDesignacaoDaConta("Investimentos de capital")){
            def selectedClasseActivo = new Conta()
            selectedClasseActivo.designacaoDaConta= "Investimentos de capital"
            selectedClasseActivo.ativo = true
            selectedClasseActivo.codigo="3"
            selectedClasseActivo.planoDeContas = planoDeContas
            selectedClasseActivo.finalidade = "conta_classe"
            selectedClasseActivo.save(flush: true)
        }

        if(!Conta.findByDesignacaoDaConta("CAIXA")){
            def conta_classe = Conta.findByDesignacaoDaConta("ACTIVOS")
            def selectedClasseActivo = new Conta()
            selectedClasseActivo.designacaoDaConta= "CAIXAS"
            selectedClasseActivo.ativo = true
            selectedClasseActivo.codigo=conta_classe.codigo+"."+"1"
            selectedClasseActivo.planoDeContas = planoDeContas
            selectedClasseActivo.finalidade = "conta_integradora"
            selectedClasseActivo.conta = conta_classe
            selectedClasseActivo.save(flush: true)
        }
        if(!Conta.findByDesignacaoDaConta("BANCOS")){
            def conta_classe = Conta.findByDesignacaoDaConta("ACTIVOS")
            def selectedClasseActivo = new Conta()
            selectedClasseActivo.designacaoDaConta= "BANCOS"
            selectedClasseActivo.ativo = true
            selectedClasseActivo.codigo=conta_classe.codigo+"."+"2"
            selectedClasseActivo.planoDeContas = planoDeContas
            selectedClasseActivo.finalidade = "conta_integradora"
            selectedClasseActivo.conta = conta_classe
            selectedClasseActivo.save(flush: true)
        }


        if(!Conta.findByDesignacaoDaConta("CLIENTES")){
            def ci = new Conta()
            def cci = Conta.findByDesignacaoDaConta("PASSIVOS")
            ci.designacaoDaConta = "CLIENTES"
            ci.ativo = cci.ativo
            ci.codigo = cci.codigo+"."+"1"
            ci.conta = cci
            ci.finalidade="conta_integradora"
            ci.save flush: true
        }

        if(!Conta.findByDesignacaoDaConta("PERDAO_DA_DIVIDA")){
            def ci = new Conta()
            def cci = Conta.findByDesignacaoDaConta("PASSIVOS")
            ci.designacaoDaConta = "PERDAO_DA_DIVIDA"
            ci.ativo = cci.ativo
            ci.codigo = cci.codigo+"."+"2"
            ci.conta = cci
            ci.finalidade="conta_integradora"
            ci.save flush: true
        }

        if(!Taxa.findByNome("taxa_base")){
            Taxa taxaBase = new Taxa()
            taxaBase.nome = "taxa_base"
            taxaBase.valor = 0
            taxaBase.percentagem=0
            taxaBase.activo = true
            taxaBase.entidade = Entidade.first()
            taxaBase.recorencia= "0000"
            taxaBase.utilizador = Utilizador.first()
            taxaBase.save(flush: true)

        }

        def clientes = Cliente.all
        for(Cliente c in clientes){
            if(c.codigo==null){
                c.codigo = GenerateRandomNumber.randomBigNumberString
                c.merge(flush: true)
            }
        }
        return Cliente.all

    }
    def destroy = {
    }
}
