(ns org.soulspace.overarch.adapter.render.plantuml.wbs
  "Functions to render PlantUML diagrams for WBS/Organigram views."
  (:require [clojure.string :as str]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.view :as view]
            [org.soulspace.overarch.application.render :as render]
            [org.soulspace.overarch.adapter.render.plantuml :as puml]))


#_(defn render-name
  "Renders the name of the element `e`. If `e` has a :link entry, a link is rendered."
  [e]
  (if-let [link (:link e)]
    (if (keyword? link)
      (do
        (println "Element")
        (println e)
        (str "[[" (link e) " " (el/element-name e) "]]"))
      (str "[[" link " " (el/element-name e) "]]"))
    (el/element-name e)))

#_(defmulti render-wbs-element
  "Renders a UML element in PlantUML.
   
   Multifunction dispatching on the value of the :el key of the element `e`."
  (fn [_ _ _ e] (:el e))
  :hierarchy #'el/element-hierarchy)

#_(defmethod render-wbs-element :org-unit
  [model view indent e]
  )

#_(defn render-wbs-layout
  [view]
  )

#_(defmethod puml/render-plantuml-view :wbs-view
  [model options view]
  (let [elements (view/view-elements model view)
        nodes (view/root-elements model (filter el/model-node? elements))
        relations (filter el/model-relation? elements)
        rendered (view/elements-to-render model view (concat nodes relations))]
    (flatten [(str "@startwbs " (name (:id view)))
            (render-wbs-layout view)
            (puml/render-title view)
            (puml/render-skinparams view)
            (map #(puml/render-wbs-element model view 0 %) rendered)
            "@endwbs"])))
