(ns formal.html
  (:require [formal.ui :as fui]
            [reagent.core :as r]))

;;; Forms

(defn form
  [{:keys [schema] :as props}]
  [fui/form (assoc props :namespace :html)])

(defn form-item
  []
  (let [this (r/current-component)
        {:keys [error namespace]} (r/props this)]
    [:div
     (doall (r/children this))
     (when error
       [:div {:style {:color "red"}}
        (str error)])]))

;;; Inputs

(fui/definput :html/string
  [{:keys [value default-value on-change error]}]
  [form-item {:error error}
   [:input ]])
