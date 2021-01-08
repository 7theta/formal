(ns formal.core
  (:require [malli.core :as m]
            [malli.util :as mu]
            [malli.generator :as mg]
            [utilis.map :refer [compact map-vals]]))

;; Responsibilities
;; - provide layout templates / capabilities
;; - provide extensible API to substitute any component library in
;; - provide an "easy" namespace that combines everything

;; TODO
;; - handle all collection schemas by default

;; - handle nested [:maybe ... ] schemas

;; - add 'required' prop to inputs
;; - enable / disable inputs (or should this be up to the individual ui libraries?)

;; - custom error message function
;; - add custom input for a particular schema (default to :string)

;; - provide options as props for individual schemas without inlining them in the schema itself
