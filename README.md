Overarch Architecture Description
=================================
A data driven description of a software architecture based on the C4 model.

Describe your model as data and specify/generate representations (e.g. diagrams) for your model.

Rationale
---------

C4 models are great to vizualize an architecture on different levels of detail with the various diagrams types. The value lies in an expressive description and visualization of an architecture with different views.

But but the models used for diagram generation with the existing tools are not models in the sense of generality. Especially if you describe your model in PlantUML files, these descriptions are mere textfiles.

The textfiles don't compose and you can't do anything else with these descriptions other than render them with PlantUML. The parsing process is opaque and you don't have access to the data of the model.

Also the model is complected with the diagrams, as layout and rendering information is part of the model description and vice versa.

If the model is described as plain *data* in an open format, it can be transformed into a graphical representation, e.g. into PlantUML textfiles.

The model data should be separated from information about these representations. They can be composed with these information and with other models. By doing so, the model may also be used in other ways, e.g. the generation of documentation, code or infrastructure.

Even if the model is specified as data, the format should be a text file (EDN, JSON) to be easily edited with text editors by the whole team and to be committed to version control, instead of being in some propriatary binary format.

The native format should be Extensible Data Notation (EDN) with representations in other formats like JSON. EDN is a textual format for data, which is human readable. It is directly readable into data structures in clojure code.

The data format should be open for extension. E.g. it should cope with additional attributes in the data structures.

The model should describe the architecture (the structure) of your system(s). The elements are based on the C4 model and are a hierarchical composition of the elements of the architecture.

Model references should be used to refer to model elements from representations (e.g. diagrams). To allow references to relations, the definition of a relation must have an id.

Model references may be enhanced with additional attributes that are specific to the usage context (e.g. a sprite attribute in the context of a diagram to be rendered by PlantUML)

### Related Projects

* [C4 PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML)
  * C4 standard library for PlantUML
  * just diagram oriented
  * no separation between model and presentation
  * pure textual representation
  * needs a parser implementation

* [structurizr](https://structurizr.org/)
  * C4 modelling tools from Simon Brown, the inventor of C4 models
  * diagram oriented
  * separates model from views, but in the same files
  * unwieldy tooling (IMHO)

* [fc4-framework](https://github.com/FundingCircle/fc4-framework)
  * 
  * just diagram oriented
  * no separation between model and presentation
  * tied to structurizr for modelling

* [archinsight](https://github.com/lonely-lockley/archinsight)
  * Insight language is a DSL (Domain Specific Language)
  * to translate C4 Model description into DOT language,
  * that can be rendered by Graphviz.
  * Insight supports only the first two levels of C4.
  * pure textual representation
  * needs a parser implementation


Examples
--------

Example of a model definition

```
[{:el :system
  :id :hexagonal/system
  :name "Hexagonal Architecture"
  :desc "describing structure and dependencies of a system of bounded domain context."
  :ct [{:el :container
        :id :hexagonal/container
        :name "Container"
        :desc "can be e.g. an application or a service"
        :ct [{:el :component
              :id :hexagonal/domain-core
              :name "Domain Core"
              :desc "contains the functional core of the domain"}
             {:el :component
              :id :hexagonal/domain-application
              :name "Domain Application"
              :desc "contains the processes and use-case orchestration of the domain. E.g. a controller."}
             {:el :component
              :id :hexagonal/repository-component
              :name "Repository"
              :desc "Adapter to the persistent data of the domain"}
             {:el :component
              :id :hexagonal/provided-interface-component
              :name "Provided Interface Adapter"
              :desc "API/Adapter for consumers of the domain"}
             {:el :component
              :id :hexagonal/cosumed-interface-component
              :name "Consumed Interface Adapter"
              :desc "Adapter for a service consumed for the the domain"}
             ]}]}
 {:el :rel
  :id :hexagonal/domain-application-uses-domain-core
  :from :hexagonal/domain-application
  :to :hexagonal/domain-core
  :name "uses"}
 {:el :rel
  :id :hexagonal/domain-application-uses-repository-component
  :from :hexagonal/domain-application
  :to :hexagonal/repository-component
  :name "uses"}
 {:el :rel
  :id :hexagonal/provided-interface-component-uses-domain-application
  :from :hexagonal/provided-interface-component
  :to :hexagonal/domain-application
  :name "uses"}
 ]
 ```

Example of a diagrams specification

```
[{:el :context-diagram
  :id :hexagonal/system-context-view
  :title "System Context View of a Hexagonal Architecture"
  :spec {:legend true}
  :ct [{:ref :hexagonal/system}]}
 
 {:el :container-diagram
  :id :hexagonal/container-view
  :title "Container View of a Hexagonal Arcitecture"
  :spec {:legend true}
  :ct [{:ref :hexagonal/system}]}
 
 {:el :component-diagram
  :id :hexagonal/component-view
  :title "Component View of a Hexagonal Arcitecture"
  :spec {:legend true}
  :ct [{:ref :hexagonal/container}
       
       {:ref :hexagonal/domain-application-uses-domain-core}
       {:ref :hexagonal/domain-application-uses-repository-component}
       {:ref :hexagonal/provided-interface-component-uses-domain-application}]}

 ]
 ```

Usage
-----

Use a folder for all the data (e.g. models, diagram specifications).
Add EDN files for the model and the diagram specifications and other representations. All the EDN files in the folder will be loaded.

...


Copyright
---------
© 2023 Ludger Solbach

License
-------
Eclipse Public License 1.0

