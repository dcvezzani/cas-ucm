import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class CasUcmGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Cas Ucm Plugin" // Headline display name of the plugin
    def author = "David Vezzani"
    def authorEmail = "dvezzani@ucmerced.edu"
    def description = '''\
Due to problems with incorporating the standard plugins for CAS:
  - compile ':spring-security-core:2.0-RC4'
  - compile ':spring-security-cas:2.0-RC1'

I need to come up with another solution until the issues with the standard approach have been resolved.

Basically, this plugin directly writes to the web.xml template.  Actual modifications depend on the current environment.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/cas-ucm"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    //def environments = ['test', 'staging', 'production']
    // def environments = [excludes: ['development']]
    // def scopes = [excludes:'war']

    def doWithWebDescriptor = { webXml ->

      if("development" != grails.util.Environment.current.name){

        def mappingElement = webXml.'servlet-mapping'

        def lastMapping = mappingElement[mappingElement.size() - 1] 

        lastMapping + {

          /* CAS SINGLE SIGN OUT */
          /*
           * I'm not sure why the 'CAS Single Sign Out Filter' doesn't seem to be working.
           * I'm simply going to use session.invalidate() in a logout action
           * and redirect to the CAS logout page for right now
           */

          'filter' {
            'filter-name'("CAS Single Sign Out Filter") 
            'filter-class'("org.jasig.cas.client.session.SingleSignOutFilter")
          } 

          'filter-mapping' {
            'filter-name'("CAS Single Sign Out Filter") 
            'url-pattern'("/*")
            'dispatcher'("REQUEST")
            'dispatcher'("FORWARD")
          } 

          'listener' {
            'listener-class'("org.jasig.cas.client.session.SingleSignOutHttpSessionListener") 
          } 


          'filter' {
            'filter-name'("CAS Authentication Filter") 
            'filter-class'("org.jasig.cas.client.authentication.AuthenticationFilter")
            'init-param'{
              'param-name'("casServerLoginUrl")
              'param-value'(CH.config.grails.plugin.casucm.server.login)
            }
            'init-param'{
              'param-name'("serverName")
              'param-value'(CH.config.grails.plugin.casucm.app.domain)
            }
          } 

          'filter' {
            'filter-name'("CAS Validation Filter") 
            'filter-class'("org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter")
            'init-param'{
              'param-name'("casServerUrlPrefix")
              'param-value'(CH.config.grails.plugin.casucm.server.prefix)
            }
            'init-param'{
              'param-name'("serverName")
              'param-value'(CH.config.grails.plugin.casucm.app.domain)
            }
          } 

          'filter' {
            'filter-name'("CAS HttpServletRequest Wrapper Filter") 
            'filter-class'("org.jasig.cas.client.util.HttpServletRequestWrapperFilter")
          } 

          'filter-mapping' {
            'filter-name'("CAS Authentication Filter") 
            'url-pattern'("/*")
          } 

          'filter-mapping' {
            'filter-name'("CAS Validation Filter") 
            'url-pattern'("/*")
          } 

          'filter-mapping' {
            'filter-name'("CAS HttpServletRequest Wrapper Filter") 
            'url-pattern'("/*")
          } 
        }    
      } //end: if
    }


    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
