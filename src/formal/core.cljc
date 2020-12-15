(ns formal.core
  (:require [malli.core :as m]
            [malli.util :as mu]
            [malli.generator :as mg]
            [utilis.map :refer [compact map-vals]]))

;; Responsibilities
;; - provide layout templates / capabilities
;; - provide extensible API to substitute any component library in
;; - provide an "easy" namespace that combines everything
