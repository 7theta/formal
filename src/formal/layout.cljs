(ns formal.layout
  (:require [reagent.core :as r]))

(def div :div)

(defn default
  [{:keys [error]}]
  (into [div]
        (concat (r/children (r/current-component))
                [[div {:style {:color "red"}}
                  (-> error meta :human str)]])))
