(ns formal.coll
  (:refer-clojure :exclude [map vector set])
  (:require [utilis.fn :refer [fsafe]]
            [reagent.core :as r]))

;;; Declarations

(declare replace-nth)

;;; API

(defn map
  []
  (r/create-class
   {:render (fn [this]
              (let [{:keys [component value namespace input children default-value components render-input] :as props} (r/props this)]
                [(component components namespace input)
                 (assoc props :inputs
                        (->> children
                             (clojure.core/map-indexed
                              (fn [index [id options schema]]
                                (let [props (merge (when (and (map? default-value)
                                                              (contains? default-value id))
                                                     {:default-value (get default-value id)})
                                                   schema options
                                                   {:id id
                                                    :index index
                                                    :components components
                                                    :namespace namespace
                                                    :key (str id "-" index)
                                                    :on-change (fn [input-value]
                                                                 (let [{:keys [change-handler]} (r/state this)]
                                                                   (change-handler id input-value)))})]
                                  [id {:render render-input :props props}])))
                             (into {})))]))
    :get-initial-state (fn [this]
                         {:change-handler (fn [id input-value]
                                            (let [{:keys [on-update]} (r/props this)]
                                              ((fsafe on-update) #(assoc % id input-value))))})}))

(defn sequential
  [props]
  (r/create-class
   {:render (fn [this]
              (let [{:keys [component namespace input children value render-input components] :as props} (r/props this)
                    [child-schema & _] children]
                [(component components namespace input)
                 (assoc props
                        :inputs (->> value
                                     (clojure.core/map-indexed
                                      (fn [index value]
                                        {:render render-input
                                         :props (merge child-schema
                                                       {:components components
                                                        :namespace namespace
                                                        :index index
                                                        :key (str "sequential-child-" index)
                                                        :on-change (fn [input-value]
                                                                     (let [{:keys [change-handler]} (r/state this)]
                                                                       (change-handler index input-value)))})}))
                                     (doall)))]))
    :get-initial-state (fn [this]
                         {:change-handler (fn [index input-value]
                                            (let [{:keys [on-change value]} (r/props this)]
                                              ((fsafe on-change) (replace-nth value index input-value))))})}))

(def vector sequential)

;;; Implementation

(defn- replace-nth
  [sq index value]
  (if (vector? sq)
    (assoc sq index value)
    (->> sq
         (map-indexed (fn [index* value*]
                        (if (= index index*)
                          value
                          value*)))
         (into (empty sq)))))
