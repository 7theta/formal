(ns formal.core
  (:require [malli.core :as m]
            [malli.util :as mu]
            [malli.generator :as mg]
            [utilis.map :refer [compact map-vals]]))

;; TODO
;; - handle all collection schemas by default

;; - handle nested [:maybe ... ] schemas

;; - add 'required' prop to inputs

;; - custom error message function
;; - add custom input for a particular schema (default to :string)

;; - provide options as props for individual schemas without inlining them in the schema itself
