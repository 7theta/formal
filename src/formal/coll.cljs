(ns formal.coll
  (:refer-clojure :exclude [map vector set])
  (:require [utilis.fn :refer [fsafe]]
            [reagent.core :as r]))

(defn map
  []
  (r/create-class
   {:render (fn [this]
              (let [{:keys [component namespace input children default-value render-input] :as props} (r/props this)]
                [(component namespace input)
                 (assoc props :inputs
                        (->> children
                             (clojure.core/map-indexed
                              (fn [index [id options schema]]
                                [id [render-input
                                     (merge {:default-value (get default-value id)} schema options
                                            {:namespace namespace
                                             :key (str id "-" index)
                                             :on-change (fn [input-value]
                                                          (let [{:keys [change-handler]} (r/state this)]
                                                            (change-handler id input-value)))})]]))
                             (into {})))]))
    :get-initial-state (fn [this]
                         {:change-handler (fn [id input-value]
                                            (let [{:keys [on-change value]} (r/props this)]
                                              ((fsafe on-change) (assoc value id input-value))))})}))


(defn set
  []

  )

(defn tuple
  []


  )

(defn sequential
  []


  )

(def vector sequential)
