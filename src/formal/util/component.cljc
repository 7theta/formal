(ns formal.util.component
  (:refer-clojure :exclude [destructure])
  (:require [utilis.map :refer [map-keys compact]]))

(defn destructure
  [component]
  (when (vector? component)
    (let [[element & args] component
          props (first args)
          props? (map? props)
          props (when props? props)
          children (if props? (rest args) args)]
      {:element element
       :props props
       :children children})))

(defn assoc-prop
  [component k v]
  (let [{:keys [element props children]} (destructure component)]
    (into [element (assoc props k v)] children)))

(defn flatten-children
  [children]
  (cond
    (not (coll? children)) nil
    (not (coll? (first children))) children
    (not children) nil

    (and (vector? (first children))
         (not (coll? (ffirst children))))
    children

    :else (recur (first children))))
