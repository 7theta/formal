(ns formal.example.views
  (:require [formal.html :as fhtml]))

(def sample-schema
  [:map
   [:name [:string {:min 0 :max 100}]]
   [:description string?]])

(defn main-panel
  []
  [fhtml/form
   {:schema sample-schema}])
