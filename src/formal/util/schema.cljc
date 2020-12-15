(ns formal.util.schema
  (:require [malli.core :as m]
            [malli.util :as mu]
            [utilis.map :refer [compact map-vals]]))

(defn symbol->keyword
  [symbol]
  (get {'string? :string
        'boolean? :boolean
        'integer? :integer
        'number? :number} symbol))

(defn type->component
  [type]
  (let [type (cond
               (keyword? type) type
               (or (fn? type) (symbol? type)) (symbol->keyword type)
               :else (throw (ex-info "Unhandled type" {:type type})))]
    (condp = type
      :map :form
      type)))

(defn schema-walker
  [schema properties children options]
  (let [type (m/-type schema)]
    (compact
     {:type type
      :component (type->component type)
      :schema schema
      :validate (m/validator schema)
      :properties (m/-properties schema)
      :children children
      :options (m/-options schema)})))

(defn walked
  [schema]
  (m/walk schema schema-walker))
