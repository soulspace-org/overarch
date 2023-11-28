(ns org.soulspace.overarch.application.model-repository
  )

(defn repo-type
  "Returns the repository type."
  ([r-type]
   r-type)
  ([r-type location]
   r-type))

(defmulti read-model 
  "Reads the model with the repository of type `r-type` from the given `location`."
  repo-type
  )
