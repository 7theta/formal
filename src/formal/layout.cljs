(ns formal.layout
  (:require [reagent.core :as r]))

(def div :div)

(defn default
  []
  (into [div] (r/children (r/current-component))))
