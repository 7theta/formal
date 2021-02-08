(ns formal.core
  (:require [formal.coll :as coll]
            [formal.util.schema :as fus]
            [reagent.core :as r]
            [utilis.fn :refer [fsafe]]
            [cljs.pprint :as pprint]))

;;; Declarations

(declare input* ensure-map-schema)

(defonce inputs (atom {}))

;;; API

(defn input
  "An input collects a single piece of data from the user."
  [{:keys [namespace input] :as props}]
  [input* props])

(defn form
  "A form is a map of input elements."
  [{:keys [namespace schema on-change]
    :or {namespace :html}
    :as props}]
  [input
   (merge props (ensure-map-schema (fus/walked schema))
          {:input :map
           :namespace namespace})])

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
  ([namespace input] (component nil namespace input))
  ([components namespace input]
   (let [components (merge @inputs components)]
     (or (get components (keyword namespace input))
         (get-in components [namespace input :component])
         (get-in components [:default :component])))))

;;; Defaults

(reg-input
 :default
 (fn [{:keys [namespace input] :as props}]
   (js/console.warn
    (str "[formal] No component found for input "
         (if (or namespace input)
           (keyword namespace input)
           props)))
   nil))

;;; Implementation

(defn- ensure-map-schema
  [schema]
  (when (not= (:type schema) :map)
    (throw (ex-info "The root entry in a form schema must be a :map."
                    {:schema schema})))
  schema)

(defn- maybe
  [{:keys [children] :as props}]
  (let [[schema & _] children]
    [input* (merge props (select-keys schema [:type :input]))]))

(defn input*
  []
  (r/create-class
   {:render (fn [this]
              (let [{:keys [value error on-change on-update]} (r/state this)
                    {:keys [namespace input optional components] :as props} (assoc (r/props this)
                                                                                   :on-update on-update
                                                                                   :on-change on-change
                                                                                   :error error
                                                                                   :value value)
                    props (assoc props
                                 :required (not optional)
                                 :render-input input*
                                 :component component)]
                [(condp = input
                   :map coll/map
                   :sequential coll/sequential
                   :vector coll/vector
                   :maybe maybe
                   (component components namespace input)) props]))
    :get-initial-state (fn [this]
                         (let [{:keys [default-value validate explain]} (r/props this)
                               change-handler (fn [value]
                                                (let [{:keys [on-change validate explain]} (r/props this)
                                                      valid? (validate value)]
                                                  (r/set-state this {:value value
                                                                     :error (when (not valid?)
                                                                              (explain value))})
                                                  (when valid? ((fsafe on-change) value))))]
                           {:value default-value
                            :error (when (and default-value (not (validate default-value)))
                                     (explain default-value))
                            :on-update (fn [f & args]
                                         (let [{:keys [value]} (r/state this)]
                                           (change-handler (apply f value args))))
                            :on-change change-handler}))}))
