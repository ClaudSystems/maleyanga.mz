
class UrlMappings {

	static mappings = {

		/*
		 * Pages without controller
		 */
		"/"				(view:"/index")
		"/about"		(view:"/siteinfo/about")
		"/blog"			(view:"/siteinfo/blog")
		"/systeminfo"	(view:"/siteinfo/systeminfo")
		"/contact"		(view:"/siteinfo/contact")
		"/terms"		(view:"/siteinfo/terms")
		"/imprint"		(view:"/siteinfo/imprint")
		"/nextSteps"	(view:"/home/nextSteps")

		/*
		 * Pages with controller
		 * WARN: No domain/controller should be named "api" or "mobile" or "web"!
		 */
        "/"	{

			controller	= 'home'
			action		= { 'index' }
            view		= { 'index' }
        }
		"/pedidoDeCredito/index"	{

			controller	= 'pedidoDeCredito'
			action		= { 'pedidoDeCredito' }
			view		= { 'pedidoDeCredito' }
		}
		"/diario/index?"(controller: "diario",action: "diario")
		"/cliente/*.*"	{

			controller	= 'cliente'
			action		= { 'gestorDeClientes' }
			view		= { 'gestorDeClientes' }
		}

		"/cliente/create"	{

			controller	= 'cliente'
			action		= { 'create' }
			view		= { 'create' }
		}

		"/credito/"	{

			controller	= 'credito'
			action		= { 'createCredito' }
			view		= { 'createCredito' }
		}
		"/$controller/$action?/$id?"{
			constraints {
				controller(matches:/^((?!(api|mobile|web)).*)$/)
		  	}
		}
		"/pagamento/index"(controller: "pagamento",action: "pagamentos")
		/*
		 * System Pages without controller
		 */
		"403"	(view:'/_errors/403')
		"404"	(view:'/_errors/404')
		"500"	(view:'/_errors/error')
		"503"	(view:'/_errors/503')
	}
}
