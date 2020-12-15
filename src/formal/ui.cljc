(ns formal.ui
  #?(:cljs (:require-macros [formal.ui]))
  (:require [formal.core :as f]
            [formal.util.schema :as fus]
            [reagent.core :as r]
            [utilis.fn :refer [fsafe]]
            #?(:cljs [cljs.pprint :as pprint])
            #?(:clj [clojure.pprint :as pprint])))

;;; Declarations

(declare validate-walked-schema default-component)

(def input-registry (atom {}))

;;; API

(defn input
  []
  (r/create-class
   {:render (fn [this]
              (let [{:keys [namespace default-value value component]} (r/props this)
                    {:keys [value on-change error]
                     :or {value default-value}} (r/state this)]
                [(-> @input-registry
                     (get-in [namespace component])
                     (or default-component))
                 {:on-change on-change
                  :error error
                  :value value
                  :default-value default-value}]))
    :get-initial-state (fn [this]
                         {:on-change (fn [value]
                                       (let [{:keys [on-change validate explain]} (r/props this)]
                                         (if (validate value)
                                           (do (r/set-state this {:value value :error nil})
                                               ((fsafe on-change) value))
                                           (r/set-state this {:error (explain value)}))))})}))

(defn form
  [{:keys [schema namespace] :or {namespace :html}}]
  (let [schema (validate-walked-schema (fus/walked schema))]
    [input (merge {:namespace namespace}
                  (select-keys schema [:validate :input]))]))

#?(:clj
   (defmacro definput
     [name props & body]



     ))

;;; Implementation

(defn- validate-walked-schema
  [schema]
  (when (not= (:type schema) :map)
    (throw (ex-info "The root entry in a form schema must be a :map."
                    {:schema schema}))))

(defn- default-component
  [props]
  (let [message (str "Unable to render input:\n"
                     (with-out-str (pprint/pprint props)))]
    #?(:clj (println message)
       :cljs (do (js/console.warn message) nil))))
