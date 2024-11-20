Changelog
=========

Version (NEXT)
--------------
* **breaking** * removed view type :class-view
  * use :code-view in your views
* added `:required-for`, `:input-of` and `:output-of` relations for process model
* added process model predicates
* added negated selection criteria
* added order vectors for model elements and views
* **breaking** link/image link generation for markdown templates
  * renamed view-link/relative-view-link functions to diagram-link/relative-diagram-link in markdown api
  * added new view-link/relative-view-link functions to render normal links to docs for the views in markdown api
* added file and image link functions in markdown api
* fixed relative link functions in markdown api for empty namespace elements
* enhanced doc templates

Version 0.32.0
--------------
* added model navigation functions for state machines
* added generic model navigation functions
* added organization model predicates
* added `:capability`, `:process`, `:artifact`, `requirement` and `decision` nodes
* added `:role-in` and `:implements` relations
* fixed relative link functions in markdown api
* fixed organization structure views
* enhanced doc templates
* added best practices to usage docs
* added links to example model repositories

Version 0.31.0
--------------
* added an option to set a namespace prefix for the internal `scope` 
  * lets you combine models from different sources/projects
  * overrides the `:external` key in the model and views
* views with `:external` set to `true` are not rendered
* fixed model-api calls for organization navigation
* added relative link functions to markdown api
* enhanced documentation templates
* added a deps.edn file
* updated dependencies
* added best practices to doc topics

Version 0.30.0
--------------
* added a organization model with
  * nodes `:organization` and `:org-unit`
  * relations `:responsible-for` and `:collaborates-with`
* added relation type `:step` to be used in dynamic views instead of plain `:rel`
* added a `:model-view` that contains all selected elements
* added graphviz template for `:model-view` as an example for customizable rendering
* added `:system-structure-view`, `:deployment-structure-view` and `organization-structure-view` for organigrams
* added plantuml templates for `:system-structure-view`, `:deployment-structure-view` and `organization-structure-view`
* enhanced rendering of abstract methods and static fields/methods in `:code-view` for PlantUML
* refactored the handling of contained element hierarchies
  * synthetic `:id`s are generated for children missing an id (e.g. fields or methods in classes)
  * syntethic `:contained-in` relations are created to represent hierarches graph (replaces `:contains` relations)
  * `:contained-in` relations can also be used in models to replace `:ct`
  * the children function is used to navigate down the hierarchy in the model graph instead of the :ct key
* added criteria for sets of namespace prefixes on elements and relations
* enhanced model navigation functions to handle ids and refs as input
* added fallback on name or id when rendering view titles
* added template-api, graphviz-api and  markdown-api to use in templates
* enhanced model-api and view-api
* moved single-line function from model-api to template-api
* added analytics functions
* added and enhanced documentation templates
* refactored template handling
* refactored model loading, check spec on file level
* enhanced model warnings
* fixed some docstrings on architecture relation navigation functions
* fixed typos in example models
* simplified logical data model
* updated data model and spec
* updated documentation

Version 0.29.1
--------------
* fixed rendering of `:index` which triggered an NPE in PlantUML

Version 0.29.0
--------------
* added link generation for PlantUML UML diagrams
* added `:index` attribute for relations/refs in dynamic views
* rendered `:index` attribute in PlantUML C4 dynamic views
* added context to error reporting for view generation errors
* added single-line function to model-api
* enhanced model api for deployment models
* enhanced documentation templates
* fixed model predicates in model-api
* updated spec
* updated documentation
* added dynamic-view for testing

Version 0.28.0
--------------
* added `:link` attribute to be used on refs in views
  * value can be a URL or a keyword as key to a property containing a URL 
  * see [discussion](https://github.com/soulspace-org/overarch/discussions/27)
* added link generation for PlantUML C4 diagrams
* fixed direction of requests navigation functions
* fixed direction of pub/sub navigation functions
* fixed relation type for sends navigation functions
* print parsed template on error when `:debug true` is set in gen config for template
* updated documentation

Version 0.27.0
--------------
* added architecture model navigation functions
* added set of supported directions to spec
* enhanced documentation templates and generation config
* updated use case model
* updated documentation

Version 0.26.0
--------------
* *attention* renamed `:class-view` to `:code-view` for generalization
  * `:class-view` is still supported but will be removed in the future
* renamed class model to code model for generalization
* added a `--select-view-references CRITERIA` command line argument
* added `:per-namespace` artifact generation
* added `:synthetic?` selection criterium
* added `:maturities` selection criterium
* changed default direction for `:include` relations in plantuml rendering of use case views
* changed keys in model-info result map to more meaningful names
* updated overarch architecture model and diagrams

Version 0.25.0
--------------
* refactored and enhanced analytics functions
* added selection criteria for `:doc` and `:maturity`
* added a `--select-views CRITERIA` command line argument
* added `:name`, `:desc` and `:doc` as optional view attributes
* updated overarch data model and spec
* updated usage docs and data model diagrams

Version 0.24.0
--------------
* added template based artifact generation for views
* added a view API functions
* optimized template parsing
* added and enhanced documentation templates
* added `:doc` and `:maturity` attributes for nodes and relations 

Version 0.23.0
--------------
* enhanced model api for architecture models
* fixed :to-namespace-prefix selection criterium

Version 0.22.0
--------------
* added more model navigation functions for architecture, use case and class models
* enhanced model API, added resolve-view and use-case navigation
* enhanced architecture documentation template
* expose `clojure.set` with alias `set` for `:combsci` templates
* expose `clojure.string` with alias `str` for `:combsci` templates
* updated `:combsci` engine exception handling, print errors but don't fail

Version 0.21.0
--------------
* added default directions for relations in plantuml use case rendering
* added model navigation functions
* enhanced model API
* added an architecture documentation example template
* updated overarch use case model
* restructured and enhanced usage docs

Version 0.20.0
--------------
* render multi line names and descriptions as single line for plantuml and graphviz
* added added :id-as-name option in template generation context
* added option to include diagrams in generated markdown view (the diagram should reside in the same directory)
* added model API functions

Version 0.19.0
--------------
* commandline updates
  * *attention* changed short option for template dir from `-t` to `-T` for consistency with other arguments
  * added the possibility to explicitly set boolean options to false, which default to true
  * added an option to disable render format specific subdirs
* updated templating, added SCI as a generation environment for comb templates
  * using :combsci as the default :engine
* added model API for use in templates (work in progress, not stable yet)
* added generic support for plantuml skinparams
* added selection criterium :desc, support regular expressions for :desc and :name
* enhanced model navigation functions 
* updated and enhanced example views
* added a new, more complex example model named big-bank
* updated usage docs

Version 0.18.0
--------------
* enhanced dependent-node functions to handle model references
* added default directions for some architecture relations in plantuml c4 diagrams
* added selection criteria
* fixed id->parent mapping for refs
* removed leiningen dev profile

Version 0.17.0
--------------
* enhanced content handling for views
  * criteria based content selection via :selection key in view spec 
  * :include spec should work for all views, include relations or related nodes automatically
  * directly referenced elements have precedence over selection/inclusion for adding/overriding of keys in references
* added deployed-to relation to deployment model to decouple architecture and deployment models
  * no need to reference containers as content of deployment nodes (that is deprecated now)
  * see [Banking Deployment Model](/models/banking/model.edn)
* added selection criteria
* added and enhanced domain model functions
* added and enhanced clojure project templates
* added overarch specific templates
* updated overarch data-model and documentation
* updated dependencies and added antq for dependency checks
* fixed typo in element hierarchy affecting deployment models

Version 0.16.0
--------------
* *attention* changed export folder structure for views 
* changed id generation of plantuml views to make the plantuml output
  location more reasonable
* added rendering of cardinalities for fields in UML class diagrams
* enhanced enum modelling and rendering in UML class diagrams
* enhanced method modelling, added parameters
* extracted criteria predicates to named functions
* added selection criteria
* updated overarch data-model and documentation

Version 0.15.1
--------------
* fixed handling of generation-config option
* removed debug output

Version 0.15.0
--------------
* added generation of artifacts via templates
  * integrated comb template engine
* added selection criteria
* added specs for criteria
* added analytics functions
* added accessors in model-repository
* added hierarchy related functions for ancestor/descendant nodes
* enhanced model-info output
* refactored view rendering rules
* updated tests
* updated documentation

Version 0.14.0
--------------
* added selection of model elements by criteria
* added options for element/reference selection in CLI
* added :tags key to model nodes and relations
  * can be used in element selection
* added check for unmatched relation namespaces
* updated graphviz rendering to use style="filled" on nodes
* updated tests
* updated documentation

Version 0.13.0
--------------
* added semantic relation types in concept model (:is-a, :has)
* added containers from architecture model to allowed actors in use case model
* added rendering of element subtype in markdown (e.g Database Container)
* added an explicit example of a concept model and view
* fixed rendering of :tech attribute for systems in C4 diagrams
* restructured overarch models
* updated documentation

Version 0.12.0
--------------
* enhanched dispatch hierarchy for views
  * fixes missing dispatch values in renderers

Version 0.11.0
--------------
* added include specifications to graphviz and markdown renderers
* updated overarch and test models
* updated documentation
* fixed rendering of references in markdown
* fixed referrer-id->relations lookup creation for nodes

Version 0.10.0
--------------
* added support for new semantic relation types in architecture and deployment models
  * additional architecture relations
    * :request, :response, :publish, :subscribe, :send, :dataflow
  * additional deployment relation
    * :link
* added reporting for unresolvable references in relations
* added support for references in markdown rendering
  * configurable per view via spec
* added support for themes to make consistent styling easier
* removed rendering of ids as transition names in statemachines
* separated domain model from external data representation
  * domain model can be navigated relationally and hierarchically
* separated rendering from file output
* added/refactored model accessor functions
* added/refactored element predicates and type sets
* refactored and enhanced model spec and checks
  * use expound for spec error reporting (not ideal)
* refactored and enhanced tests
* refactored and enhanced example models
* updated documentation

Version 0.9.0
-------------
* added model warnings for inconsistencies
* added reporting for unresolvable references in views
* added support for skinparams nodesep and ranksep for plantuml
* enhanced model analytics functions
* enhanced model information
* refactored reference resolution
* refactored and enhanced hierarchical model traversal
* fixed arity exception in structurizr export

Version 0.8.0
-------------
* support for loading models from multiple locations by specifying a model path
* (WIP) adding include specification for views
* refactored codebase to clean architecture for better maintainablity/extensibility
* added a namespace for each kind of view for view specific logic
* added separate namespaces for plantuml UML and C4 rendering
* added tests for model and view logic
* added analytics namespace for model analytics
* extracted spec namespace
* added compilation tests for each namespace

Version 0.7.0
-------------
* make file watches work on Macs by using beholder instead of hawk 
* added direction rendering to relations in class and state machine views
* enhanced example models

Version 0.6.0
-------------
* refactored exports, distinguish between
  * exports of model data (to JSON, structurizr)
  * rendering of views (e.g. to PlantUML)
* changed command line options to reflect the refactoring
* added render-format 'all' to generate all formats in one go
* updated usage and design documentation and diagrams

Version 0.5.0
-------------
* fixed and enhanced class view rendering
* enhanced example models

Version 0.4.0
-------------
* added concept view for concept maps
* added graphviz export for concept view
* updated logical data model to incorporate enhancements
* enhanced example models
* updated and enhanced usage documentation
* updated design document

Version 0.3.0
-------------
* added first markdown export
* added concept model elements
* added glossary view (textual view)
* added logical data model for overarch
* enhanced documentation and examples

Version 0.2.0
-------------
* added sprite includes for :sprite entries
* added support for UML use case, state and class views/diagrams
* enhanced documentation

Version 0.1.0
-------------
* initial import
* data format specification
* data loading
* support for views
  * system landscape
  * system context
  * container
  * component
  * deployment
  * dynamic
* command line interface
  * exports to json, plantuml and structurizr
  * file system watch for exports on changes
  * print sprite mappings
  * infos about the loaded model
* json export
  * based on the individual EDN files 
* plantuml export
  * styling support
  * sprite support
* structurizr export *experimental*
  * export structurizr workspace with model and views
