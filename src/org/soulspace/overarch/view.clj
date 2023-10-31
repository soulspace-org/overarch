(ns org.soulspace.overarch.view
  "Functions for the handling of diagrams."
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [org.soulspace.overarch.core :as core]))


(defn include-criteria?
  "Returns true, if the `view` should include elements selected by criteria."
  [view]
  (map? (get-in view [:spec :include])))

(defn include-relations?
  "Returns true, if the `view` should include the relations to the shown elements."
  [view]
  (= :relations (get-in view [:spec :include])))

;(defn include-related?
;  "Returns true, if the `view` should include the elements for the shown relations."
;  [view]
;  (= :related (get-in view [:spec :include])))

;(defn include-transitive?
;  "Returns true, if the `view` should include the transitve (convex) hull of the shown elements."
;  [view]
;  (= :transitive (get-in view [:spec :include])))

;;;
;;; Rendering functions
;;;
(defn element-to-render
  "Returns the model element to be rendered for element `e` for the `view-type`.
   Maps some elements to other elements (e.g. boundaries), depending on the type of view."
  [view-type e]
  (let [boundary (core/as-boundary? view-type e)]
    (if boundary
      ; e has a boundary type and has children, render as boundary
      (assoc e :el (keyword (str (name (:el e)) "-boundary")))
      ; render e as normal model element
      e)))

(defn render-relation?
  "Returns true if the relation should be rendered in the context of the view."
  [rel pred]
  (let [rendered? pred
        from (core/resolve-ref (:from rel))
        to   (core/resolve-ref (:to rel))]
    (when (and (rendered? rel) (rendered? from) (rendered? to))
      rel)))

(defn relation-to-render
  "Returns the relation to be rendered in the context of the view."
  [view rel]
  (let [view-type (:el view)
        rendered? (core/render-predicate view-type)
        from (core/resolve-ref (:from rel))
        to   (core/resolve-ref (:to rel))]))
  ; TODO promote relations to higher levels?
  
(defn elements-to-render
  "Returns the list of elements to render from the view
   or the given collection of elements, depending on the type
   of the view."
  ([view]
   (elements-to-render view (:ct view)))
  ([view coll]
   (let [view-type (:el view)]
     (->> coll
          (map core/resolve-ref)
          (filter (core/render-predicate view-type))
          (map #(element-to-render view-type %))))))

(defn elements-in-view
  "Returns the elements rendered in the view."
  ([view]
   (elements-in-view #{} view (elements-to-render view (:ct view))))
  ([elements view coll]
   (if (seq coll)
     (let [e (first coll)]
       (recur (elements-in-view (conj elements e)
                                view
                                (elements-to-render view (:ct e)))
              view
              (rest coll)))
     elements)))

; TODO reimplement with elements-in-view 
(defn collect-technologies
  "Returns the set of technologies for the elements of the coll."
  ([coll]
   (collect-technologies #{} coll))
  ([techs coll]
   (if (seq coll)
     (let [e (first coll)]
       (if (:tech e)
         (recur (collect-technologies (set/union techs #{(:tech e)}) (:ct e))
                (rest coll))
         (recur (collect-technologies techs (:ct e)) (rest coll))))
     techs)))

(defn technologies-in-view
  [view]
  (->> view
       (elements-in-view)
       (map :tech)
       (remove nil?)
       (into #{})))
       
(defn relations-for-view
  [view]
  (let [view-elements (elements-in-view view)
        relations (core/get-model-elements)]
    ; TODO
    ))


;; TODO Replace functions above with the functions below.
(defn referenced-elements
  "Returns the collection of referenced elements in the `view`."
  [view]
  (let [view-type (:el view)
        coll (:ct view)]
    (->> coll
         (map core/resolve-ref)
         (filter (core/render-predicate view-type))
         (map #(element-to-render view-type %)))))

(defn specified-elements
  "Returns the collection of model elements (without relations) specified in this `view`. 
 Also includes model elements, which are specified by include selectors in the view.
 When the view is rendered hierachically, additional, not directly specified elements may be rendered."
  [view]
  (let [referenced (referenced-elements view)
        referenced-model-elements (remove core/relational-element?
                                          referenced)
        referenced-relations (filter core/relational-element?
                                  referenced)
        ]
    )
  )

(defn specified-relations
  "Returns the collection of relations specified in this `view `."
  ([view]
   (specified-relations view (specified-elements view)))
  ([view elements]
   (let [element-ids (core/id-set elements)]
     )
   ))
   
(defn all-specified
  "Returns the collection of model elements and relations specified in this `view `."
  [view]
  (let [elements (specified-elements view)
        relations (specified-relations view elements)]
    {:specified-elements elements
     :specified-relations relations}))

(defn rendered-elements
  "Returns the collection of model elements (without relations) rendered in this `view`.
When the view is rendered hierachically, additional, not directly specified elements may be rendered."
  [view]
  )

(defn rendered-relations
  "Returns the collection of relations rendered in this `view `."
  ([view])
  ([view elements]
   (let [element-ids (core/id-set elements)]
     )
   ))

(defn all-rendered
  "Returns the collection of model elements and relations rendered in this `view `."
  [view]
  (let [elements (rendered-elements view)
        relations (rendered-relations view elements)]
    {:rendered-elements elements
     :rendered-relations relations}))


; general
(defn render-indent
  "Renders an indent of n space chars."
  [n]
  (str/join (repeat n " ")))

(defn element-name
  "Returns the name of the element."
  [e]
  (if (:name e)
    (:name e)
    (->> (name (:id e))
         (#(str/split % #"-"))
         (map str/capitalize)
         (str/join " "))))

; general?
(def element-hierarchy
  "Hierarchy for rendering methods."
  (-> (make-hierarchy)
      (derive :enterprise-boundary :boundary)
      (derive :system-boundary     :boundary)
      (derive :container-boundary  :boundary)
      (derive :context-boundary    :boundary)))

(comment
  ;(collect-technologies (:elements @core/state))
  (elements-in-view (core/get-view @core/state :banking/container-view))
  (technologies-in-view (core/get-view @core/state :banking/container-view)))
