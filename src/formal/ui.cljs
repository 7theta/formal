(ns formal.ui
  (:require [formal.core :as f]
            [formal.layout :as layout]
            [formal.util.schema :as fus]
            [reagent.core :as r]
            [utilis.fn :refer [fsafe]]
            [cljs.pprint :as pprint]))

;;; Declarations

(declare ensure-map-schema component)

(defonce inputs (atom {}))

;;; API

(defn input
  []
  (r/create-class
   {:render (fn [this]
              (let [{:keys [value error on-change]} (r/state this)
                    {:keys [namespace input] :as props} (assoc (r/props this)
                                                               :on-change on-change
                                                               :error error
                                                               :value value)]
                [(component namespace input) props]))
    :get-initial-state (fn [this]
                         (let [{:keys [default-value validate explain]} (r/props this)]
                           {:value default-value
                            :error (when (and default-value (not (validate default-value)))
                                     (explain default-value))
                            :on-change (fn [value]
                                         (let [{:keys [on-change validate explain]} (r/props this)]
                                           (if (validate value)
                                             (do (r/set-state this {:value value :error nil})
                                                 ((fsafe on-change) value))
                                             (r/set-state this {:error (explain value)}))))}))}))

(defn form
  "A form is a map of input elements."
  [{:keys [schema namespace layout on-change]
    :or {namespace :html
         layout layout/default}}]
  (let [this (r/current-component)
        {:keys [children type] :as schema} (ensure-map-schema (fus/walked schema))]
    (into [layout {:schema schema}]
          (->> children
               (map (fn [[id options props]]
                      [input (-> props
                                 (merge options)
                                 (assoc :namespace namespace
                                        :id id
                                        :on-change (fn [value]
                                                     (let [state (assoc (r/state this) id value)]
                                                       ((fsafe on-change) state)
                                                       (r/set-state this state)))))]))
               (doall)))))

(defn reg-input
  "An input is an element used to collect a single piece of data."
  [id component]
  (let [ks (conj (if-let [namespace (namespace id)]
                   [(keyword namespace)
                    (keyword (name id))]
                   [id])
                 :component)]
    (swap! inputs assoc-in ks component)))

(defn component
  [namespace input]
  (or (get-in @inputs [namespace input :component])
      (get-in @inputs [:default :component])))

;;; Defaults

(reg-input
 :default
 (fn [props]
   (let [message (str "[formal] No component found for props - "
                      (pr-str (select-keys props [:namespace :input])))]
     (js/console.warn message)
     nil)))

;;; Implementation

(defn- ensure-map-schema
  [schema]
  (when (not= (:type schema) :map)
    (throw (ex-info "The root entry in a form schema must be a :map."
                    {:schema schema})))
  schema)
