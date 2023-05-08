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

Model definitions

```
[{:el :system :id :astronomy/pulsar-system :name "Pulsar" :desc "Astronomy Application"
  :ct [{:el :container :id :astro/pulsar-ui-container :name "Pulsar UI" :desc "User Interface"
        :ct [{:el :component :id :astronomy/pulsar-catalog-component :name "Catalog"
              :desc "Providing celestial objects from catalogs"}
             {:el :component :id :astronomy/pulsar-control-component :name "Instrument Control"
              :desc "Controlling astronomical instruments (e.g. telescopes, cameras)"}
             {:el :component :id :astronomy/pulsar-star-map-component :name "Star Map"
              :desc "Rendering the celestial elements in a map"}]}]}
 {:el :person :id :astronomy/astronomer :name "Astronomer"}
 {:el :system-ext :id :astronomy/instruments :name "Astronomical Instruments"}]
```

Diagram specification

```
[{:el :context-diagram :id :astronomy/system-context
  :style {:legend true :linetype :orthogonal}
  :ct [{:ref :astronomy/pulsar-app}
       {:ref :astronomy/astronomer}
       {:ref :astronomy/instruments}

       {:el :rel :from :astronomy/astronomer :to :astronomy/pulsar-app :name "uses"}
       {:el :rel :from :astronomy/hypernova-app :to :astronomy/instruments :name "controls"}]}]
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

