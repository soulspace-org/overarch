;;;;
;;;; Functions to generate artifacts from an overarch model via templates
;;;;
(ns org.soulspace.overarch.application.template
  (:require [clojure.java.io :as io]
            [org.soulspace.clj.namespace :as ns]
            [org.soulspace.clj.java.file :as file]
            [org.soulspace.overarch.application.model-repository :as repo]
            [org.soulspace.overarch.domain.element :as el]
            [org.soulspace.overarch.domain.model :as model]
            ; register multimethods
            [org.soulspace.overarch.adapter.repository.file-model-repository :as fmr]))

; TODOs
; template context
; * which template with which model elements
; * output path (directory, filename, suffix)
; * protected region content

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
  (let [begin-re (begin-pattern area-marker)]
    (loop [remaining-lines lines area-id nil area-content "" area-map {}]
      (if (seq remaining-lines)
        (if (nil? area-id)
          (if-let [begin-matches (re-seq begin-re (first remaining-lines))]
            (recur (rest remaining-lines) (nth (first begin-matches) 1) "" area-map) ; line starting a protected area
            (recur (rest remaining-lines) nil "" area-map)) ; line outside any protected areas
          (if-let [end-match (re-matches (end-pattern area-marker area-id) (first remaining-lines))]
            (recur (rest remaining-lines) nil "" (assoc area-map area-id area-content)) ; line ending a protected area
            (recur (rest remaining-lines) area-id (str area-content (first remaining-lines) "\n") area-map))) ; line inside a protected area
        area-map)))) ; no more lines, return area map

(defn read-protected-areas
  "Reads the given path and returns the proected areas as a map."
  [gen path]
  (when-let [area-marker (:protected-area gen)]
    (parse-protected-areas area-marker (read-lines path))))

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
    (if (:base-namespace ctx)
      (str (ns/ns-to-path (:base-namespace ctx)) "/")
      (str (ns/ns-to-path (el/element-namespace el))))
    
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
  (let [file (io/as-file pathname)
        parent (file/parent-dir file)]
    ; TODO check suppress-write
    (when-not (file/exists? parent)
      (file/create-dir parent))
    ; TODO add encoding from generator
    ; (with-open ((writer pathname)))
    (spit pathname result)))

;;;
;;; Generation spec
;;;


;;;
;;; Generation process
;;; 

(defn generate-artifact
  "Generates an artifact"
  [ctx e]
  ; (apply-template (:template ctx) e)
  )

(defn generate
  "Generates artifacts for the generation specification `spec`."
  [model options]
  (doseq [ctx (:generation-spec options)]
    (let [selection ((into #{} (model/filter-xf model (:selection ctx)) (repo/elements)))]
      (if (:per-element ctx)
        (doseq [e selection]
          ; apply template on e
          ; write artifact for result
          )
        (do
          ; apply template on selection
          ; write artifact for result
          )))))


(comment
  (repo/read-models :file "../../overarch/models")
  )
