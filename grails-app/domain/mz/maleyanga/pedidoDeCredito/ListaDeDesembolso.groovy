package mz.maleyanga.pedidoDeCredito

import mz.maleyanga.security.Utilizador

/**
 * ListaDeDesembolso
 * A domain class describes the data object and it's mapping to the database
 */
class ListaDeDesembolso {
    private static final long serialVersionUID = 1
    /* Default (injected) attributes of GORM */
//	Long	id
//	Long	version
    String descricao
    String balcao
    Utilizador caixa
    Utilizador gerente
    /* Automatic timestamping of GORM */
    Date dateCreated
    Date lastUpdated
    Date dataDeDesembolso
    String numeroDaLista

//	static	belongsTo	= []	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static	hasOne		= []	// tells GORM to associate another domain object as an owner in a 1-1 mapping
    static hasMany = [pedidosDeCredito: PedidoDeCredito]
    // tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static	mappedBy	= []	// specifies which property should be used in a mapping

    static mapping = {
        batchSize(10)
        pedidosDeCredito lazy: false
    }

    static constraints = {
        balcao nullable: true
        caixa nullable: true
        numeroDaLista nullable: true

    }

    /*
     * Methods of the Domain Class
     */
//	@Override	// Override toString for a nicer / more descriptive UI
    public String toString() {
        return "${descricao + "-" + dataDeDesembolso.format("dd-MM-yy")}"
    }
}
