(ns formal.util.schema
  (:require [malli.core :as m]
            [malli.error :as me]
            [malli.util :as mu]
            [utilis.map :refer [compact map-vals]]))

(def symbol->keyword
  {'any? :any
   'some? :some
   'number? :number
   'integer? :integer
   'int? :int
   'pos-int? :pos-int
   'neg-int? :neg-int
   'nat-int? :nat-int
   'float? :float
   'double? :double
   'boolean? :boolean
   'string? :string
   'ident? :ident
   'simple-ident? :simple-ident
   'qualified-ident? :qualified-ident
   'keyword? :keyword
   'simple-keyword? :simple-keyword
   'qualified-keyword? :qualified-keyword
   'symbol? :symbol
   'simple-symbol? :simple-symbol
   'qualified-symbol? :qualified-symbol
   'uuid? :uuid
   'uri? :uri
   'decimal? :decimal
   'inst? :inst
   'seqable? :seqable
   'indexed? :indexed
   'map? :map
   'vector? :vector
   'list? :list
   'seq? :seq
   'char? :char
   'set? :set
   'nil? :nil
   'false? :false
   'true? :true
   'zero? :zero
   'rational? :rational
   'coll? :coll
   'empty? :empty
   'associative? :associative
   'sequential? :sequential
   'ratio? :ratio
   'bytes? :bytes})

(defn type->input
  [type]
  (cond
    (keyword? type) type
    (or (fn? type) (symbol? type)) (get symbol->keyword type)
    :else (throw (ex-info "Unhandled type" {:type type}))))

(defn schema-walker
  [schema properties children options]
  (let [type (m/type schema)
        input (type->input type)]
    (compact
     {:type type
      :input input
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
