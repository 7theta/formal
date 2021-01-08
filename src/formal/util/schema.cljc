(ns formal.util.schema
  (:require [malli.core :as m]
            [malli.error :as me]
            [malli.util :as mu]
            [utilis.map :refer [compact map-vals]]))

(defn symbol->keyword
  [symbol]
  (get {'string? :string
        'boolean? :boolean
        'integer? :integer
        'number? :number} symbol))

(defn type->input
  [type]
  (cond
    (keyword? type) type
    (or (fn? type) (symbol? type)) (symbol->keyword type)
    :else (throw (ex-info "Unhandled type" {:type type}))))

(defn schema-walker
  [schema properties children options]
  (let [type (m/type schema)]
    (compact
     {:type type
      :input (type->input type)
      :schema schema
      :validate (m/validator schema)
      :properties (m/properties schema)
      :children children
      :options (m/options schema)
      :explain #(when-let [error (m/explain schema %)]
                  (with-meta error {:human (me/humanize error)}))})))

(defn walked
  [schema]
  (m/walk schema schema-walker))
