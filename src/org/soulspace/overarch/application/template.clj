;;;;
;;;; Functions to generate artifacts from an overarch model via templates
;;;;
(ns org.soulspace.overarch.application.template
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [org.soulspace.clj.namespace :as ns]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.application.model-repository :as repo]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            ; register multimethods
            [org.soulspace.overarch.adapter.repository.file-model-repository :as fmr]
            [clojure.spec.alpha :as s]))

; TODOs

;;;
;;; Generation config spec
;;;
(s/def :overarch.template/selection :overarch/selection-criteria)
(s/def :overarch.template/template string?)
(s/def :overarch.template/engine keyword?)
(s/def :overarch.template/encoding string?)
(s/def :overarch.template/per-element boolean?)
(s/def :overarch.template/path string?)
(s/def :overarch.template/subdir string?)
(s/def :overarch.template/namespace-prefix string?)
(s/def :overarch.template/base-namespace string?)
(s/def :overarch.template/namespace-suffix string?)
(s/def :overarch.template/prefix string?)
(s/def :overarch.template/base-name string?)
(s/def :overarch.template/file-name string?)
(s/def :overarch.template/suffix string?)
(s/def :overarch.template/extension string?)
(s/def :overarch.template/name-as-namespace boolean?)
(s/def :overarch.template/protected-area boolean?)

(s/def :overarch.template/generation-context
  (s/keys :req-un [:overarch.template/template]
          :opt-un [:overarch.template/selection
                   :overarch.template/engine
                   :overarch.template/encoding
                   :overarch.template/per-element
                   :overarch.template/path
                   :overarch.template/subdir
                   :overarch.template/namespace-prefix
                   :overarch.template/base-namespace
                   :overarch.template/namespace-suffix
                   :overarch.template/file-name
                   :overarch.template/prefix
                   :overarch.template/base-name
                   :overarch.template/suffix
                   :overarch.template/extension
                   :overarch.template/name-as-namespace
                   :overarch.template/protected-area]))

(s/def :overarch.template/generation-config
  (s/coll-of :overarch.template/generation-context))

;;;
;;; Template engine functions
;;;
(defn repo-type
  "Returns the repository type."
  ([rtype]
   rtype)
  ([rtype & r]
   rtype))

(defn engine-type
  "Returns the template engine type."
  ([ttype]
   ttype)
  ([ttype & r]
   ttype))

; TODO really needed?
(defmulti read-template
  "Reads and parses the `template`."
  repo-type)

(defmulti apply-template
  "Applies the `template` to the `data` and returns the output."
  engine-type)

;;;
;;; Protected area handling
;;;
(defn begin-pattern
  "Returns the regex pattern for the begin of a protected area based on the area marker."
  [area-marker]
  (re-pattern (str "^.*" area-marker "-BEGIN\\((.*)\\).*$")))

(defn end-pattern
  "Returns the regex pattern for the end of a protected area based on the area marker and area id."
  [area-marker area-id]
  (re-pattern (str "^.*" area-marker "-END\\(" area-id "\\).*$")))

(defn read-lines
  "Reads the file given and returns a non lazy sequence a of its lines."
  ([file]
   (with-open [rdr (io/reader file)]
     (doall (line-seq rdr)))))

(defn parse-protected-areas
  "Parse the lines into a protected area map."
  [area-marker lines]
  (println "Read PAs from" lines)
  (let [begin-re (begin-pattern area-marker)]
    (loop [remaining-lines lines area-id nil area-content "" area-map {}]
      (if (seq remaining-lines)
        (if (nil? area-id)
          (if-let [begin-matches (re-seq begin-re (first remaining-lines))]
            (recur (rest remaining-lines) (nth (first begin-matches) 1) "" area-map) ; line starting a protected area
            (recur (rest remaining-lines) nil "" area-map)) ; line outside any protected areas
          (if-let [end-match (re-matches (end-pattern area-marker area-id) (first remaining-lines))]
            (recur (rest remaining-lines) nil "" (assoc area-map (keyword area-id) area-content)) ; line ending a protected area
            (recur (rest remaining-lines) area-id (str area-content (first remaining-lines) "\n") area-map))) ; line inside a protected area
        area-map)))) ; no more lines, return area map

(defn read-protected-areas
  "Reads the given path and returns the proected areas as a map."
  [ctx path]
  (if (and (:protected-area ctx)
           (file/exists? (io/as-file path)))
    (let [area-marker (:protected-area ctx)]
      (parse-protected-areas area-marker (read-lines path)))
    {}))

;;;
;;; Artifact handling
;;;
(defn artifact-filename
  "Returns the filename of the artifact given the generation context `ctx`
   and optionally a model element `el`."
  ([ctx]
    (str
     (:prefix ctx)
     (:base-name ctx)
     (:suffix ctx)
     "." (:extension ctx)))
  ([ctx el]
   (str
    (:prefix ctx)
    (if-let [base-name (:base-name ctx)]
      base-name
      (:name el))
    (:suffix ctx)
    "." (:extension ctx))))

(defn artifact-path
  "Returns the path for the artifact given the generation context `ctx`
   and optionally a model element `el`."
  ([ctx]
   (str
    (when (:subdir ctx)
      (str (:subdir ctx) "/"))
    (when (:namespace-prefix ctx)
      (str (ns/ns-to-path (:namespace-prefix ctx)) "/"))
    (when (:base-namespace ctx)
      (str (ns/ns-to-path (:base-namespace ctx)) "/"))
    (when (:namespace-suffix ctx)
      (str (ns/ns-to-path (:namespace-suffix ctx)) "/"))))
  ([ctx el]
   (str
    (when (:subdir ctx)
      (str (:subdir ctx) "/"))
    (when (:namespace-prefix ctx)
      (str (ns/ns-to-path (:namespace-prefix ctx)) "/"))
    
    (if (:id-as-namespace ctx)
      (str (ns/ns-to-path (name (:id el))) "/")
      (if (:base-namespace ctx)
        (str (ns/ns-to-path (:base-namespace ctx)) "/")
        (str (ns/ns-to-path (el/element-namespace el)) "/")))
    
    (when (:namespace-suffix ctx)
      (str (ns/ns-to-path (:namespace-suffix ctx)) "/")))))

(defn create-path
  "Creates the path by creating all neccessary directories."
  [pathname]
  (let [file (io/as-file pathname)]
    (when-not (file/exists? file)
      (.mkdirs file))))

(defn write-artifact
  "Write the generated artifact to file."
  [pathname result]
  (println "Writing" pathname)
  (let [file (io/as-file pathname)
        parent (file/parent-dir file)]
    ; TODO check suppress-write
    (when-not (file/exists? parent)
      (file/create-dir parent))
    ; TODO add encoding from generator
    ; (with-open ((writer pathname)))
    (spit pathname result)))

;;;
;;; Generation process
;;; 

(def ctx-defaults
  {:engine :comb
   :per-element false
;   :protected-area "PA"
   :encoding "UTF-8"
   :id-as-namespace false})

(defn read-config
  "Reads the generator configuration."
  [options]
  (if-let [generator-config (:generator-config options)]
    (map (partial merge ctx-defaults)
         (edn/read-string (slurp generator-config)))
    []))

; TODO generation-dir missing from path, comes from options
(defn generate-artifact
  "Generates an artifact"
  [template ctx model e]
;  (println "Element" e)
  (let [path (str (artifact-path ctx e) (artifact-filename ctx e))
        protected-areas (read-protected-areas ctx path)
;        _ (println "Protected Areas" protected-areas)
        result (apply-template (:engine ctx) template {:ctx ctx :e e :model model :protected-areas protected-areas})
        _ (print "Result" result)
        ]
    ; write artifact for result
    (write-artifact path result)))

(defn generate
  "Generates artifacts for the generation specification `spec`."
  [model options]
    (doseq [ctx (read-config options)]
      (println "Context" ctx)
      (let [template (io/as-file (str (:template-dir options) "/" (:template ctx)))]
        (when-let [selection (into #{} (model/filter-xf model (:selection ctx)) (repo/model-elements))] 
;          (println "Selection" selection)
          (if (:per-element ctx)
            (doseq [e selection]
              (generate-artifact template ctx model e))
            (generate-artifact template ctx model selection))))))

(comment
  (repo/read-models :file "models")
  (apply-template :comb (io/as-file "templates/clojure/gitignore.cmb") {})
  (into #{} (model/filter-xf (repo/model) {:el :container}) (repo/model-elements))
  )
