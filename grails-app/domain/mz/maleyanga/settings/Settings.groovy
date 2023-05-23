package mz.maleyanga.settings

/**
 * Settings
 * A domain class describes the data object and it's mapping to the database
 */
class Settings implements Serializable{
    private static final long serialVersionUID = 1
    String nome
    boolean domingo
    boolean segunda
    boolean terca
    boolean quarta
    boolean quinta
    boolean sexta
    boolean sabado
    Boolean taxaManual
    Boolean calcularAutomatico
    Boolean atualizarDadosDoCliente
    Boolean permitirDesembolsoComDivida
    Boolean excluirSabados
    Boolean excluirDomingos
    String conta1
    String conta2
    String conta3
    Boolean pagamentosEmOrdem
    String pdjda
    String rodaPePlanoDePagamento
    String nbPlanoDePagamento
    Boolean gd_prestacoes
    Boolean ignorarValorPagoNoPrazo
    Boolean pagarEmSequencia
    Boolean altearDataDePagamento
    /* Default (injected) attributes of GORM */
//	Long	id
//	Long	version

    /* Automatic timestamping of GORM */
//	Date	dateCreated
//	Date	lastUpdated

//	static	belongsTo	= []	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static	hasOne		= []	// tells GORM to associate another domain object as an owner in a 1-1 mapping
//	static	hasMany		= []	// tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static	mappedBy	= []	// specifies which property should be used in a mapping 
	
    static	mapping = {
    }
    
	static	constraints = {
        nome unique: true
        segunda unique: true
        domingo unique: true
        terca unique: true
        quarta unique: true
        quinta unique: true
        sexta unique: true
        sabado unique: true
        calcularAutomatico nullable: true
        atualizarDadosDoCliente nullable: true
        permitirDesembolsoComDivida nullable: true
        excluirDomingos nullable: true
        excluirSabados nullable: true
        conta1 nullable: true
        conta2 nullable: true
        conta3 nullable: true
        pagamentosEmOrdem nullable: true
        taxaManual nullable: true
        pdjda nullable: true, inList: ['pdj', 'pdjdm']
        rodaPePlanoDePagamento nullable: true
        nbPlanoDePagamento nullable: true
        gd_prestacoes nullable: true
        ignorarValorPagoNoPrazo nullable: true
        pagarEmSequencia nullable: true
        altearDataDePagamento nullable: true

    }
	
	/*
	 * Methods of the Domain Class
	 */
//	@Override	// Override toString for a nicer / more descriptive UI 
//	public String toString() {
//		return "${name}";
//	}
}
