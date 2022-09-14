package mz.maleyanga

import mz.maleyanga.cliente.Assinante
import mz.maleyanga.cliente.Cliente
import mz.maleyanga.conta.Conta
import mz.maleyanga.entidade.Entidade
import mz.maleyanga.security.Utilizador
import org.springframework.transaction.annotation.Transactional
import org.zkoss.zul.ListModelList


/**
 * ClienteService
 * A service class encapsulates the core business logic of a Grails application
 */
@Transactional
class ClienteService {
    EncryptionService encryptionService
    List<Cliente> clientes

    List<Cliente> findAllByEmDivida() {
        def result = new ArrayList<Cliente>()
        def clientes = Cliente.all
        for (Cliente cliente in clientes) {
            if (cliente.emDivida && cliente.creditos != null) {
                result.add(cliente)
            }
        }
        return result
    }


    List<Cliente> findAllByName(String nome) {
        def c = Cliente.createCriteria()
        def results = c.list {
            like("nome", "%" + nome + "%")

            maxResults(4)
            order("nome", "desc")
        }
        return results
    }

    List<Cliente> getClientes() {
        if (clientes == null) {
            clientes = new ListModelList<Cliente>(Cliente.all)
        }
        return clientes
    }

    List<Cliente> getClientesAtivos(Utilizador gestor) {
        def clientesDb = Cliente.findAllByUtilizadorAndEmDivida(gestor, true)
        return clientes
    }

    List<Cliente> getClientesInativos(Utilizador gestor) {
        def clientesDb = Cliente.findAllByUtilizadorAndEmDivida(gestor, false)
        return clientes
    }

    List<Cliente> getClientes(Utilizador utilizador) {
        if (clientes == null) {
            clientes = new ListModelList<Cliente>(Cliente.findAllByUtilizador(utilizador))
        }
        return clientes
    }

    boolean saveCliente(Cliente clienteInstance) {
        Entidade entidade = Entidade.all.first()
        clienteInstance.entidade = entidade
        System.println("entidade" + entidade)
        try {
            clienteInstance.save flush: true
            def clienteDb = Cliente.findById(clienteInstance.id)
            if (clienteDb != null) {
                System.println("clienteDb=" + clienteDb)
                Conta conta = new Conta()
                conta.numeroDaConta = clienteDb.id
                Integer cod = clienteDb.id.toInteger()
                String str = String.format("%04d", cod)
                def contaDb = Conta.findByFinalidadeAndDesignacaoDaConta("conta_integradora", "CLIENTES")
                if (contaDb == null) {
                    return false
                } else {
                    conta.codigo = contaDb.codigo + "." + str
                    conta.designacaoDaConta = "conta_cliente" + '_' + conta.codigo
                    conta.finalidade = 'conta_cliente'
                    conta.conta = contaDb
                    conta.ativo = contaDb.ativo
                    conta.cliente = clienteInstance
                    conta.save(flush: true)
                    if (Conta.findById(conta.id) != null) {
                        System.println("conta==" + conta)
                        return true
                    }
                }

            }


        } catch (Exception e) {
            System.println(e.toString())
            return false

        }


    }

    boolean saveAssinate(Assinante assinante) {

        try {
            if (assinante == null) {

                return false
            }
            if (assinante.hasErrors()) {

                return false
            }

            assinante.save flush: true
            Conta conta = new Conta()
            conta.conta = Conta.findById(ci.id)
            conta.numeroDaConta = cliente.id
            Integer cod = cliente.id.toInteger()
            String str = String.format("%04d", cod)
            conta.codigo = ci.codigo + "." + str
            conta.designacaoDaConta = "conta_cliente" + '_' + conta.codigo
            conta.finalidade = 'conta_movimento'
            conta.save(flush: true)
        } catch (Exception e) {
            System.println(e.toString())
        }

        return true

    }

    boolean mergeCliente(Cliente cliente) {
        try {
            cliente.merge(flush: true)
            return true
        } catch (Exception e) {
            System.println(e.toString())
            return false
        }
    }

    boolean mergeAssinante(Assinante assinante) {
        try {
            assinante.merge(flush: true)
            return true
        } catch (Exception e) {
            System.println(e.toString())
            return false
        }
    }

    boolean deleteliente(Cliente cliente) {
        try {
            def contaDb = Conta.findByCliente(cliente)
            contaDb.cliente = null
            contaDb.merge(flush: true)
            cliente.delete(flush: true)

            return true
        } catch (Exception e) {
            System.println(e.toString())
            return false
        }
    }

    boolean deleteAssinante(Assinante assinante) {
        try {
            assinante.delete()
            return true
        } catch (Exception e) {
            System.println(e.toString())
            return false
        }
    }

    def all() {
        return Cliente.all
    }

    def findAllByCreditosIsNotEmpty() {
        def clientes = Cliente.all
        List clientes_com_credito = new ArrayList<Cliente>()
        for (Cliente cliente1 in clientes) {
            if (!cliente1.creditos.empty) {
                clientes_com_credito.add(cliente1)
            }
        }
        return clientes_com_credito
    }

    def findAllByAtivo() {
        return Cliente.findAllByAtivo(true)
    }


}
