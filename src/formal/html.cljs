(ns formal.html
  (:require [formal.ui :as fui]
            [utilis.js :as j]
            [utilis.types.number :refer [string->double
                                         string->long]]
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
     (into [:div] (r/children this))
     (when error
       [:div {:style {:color "red"}}
        (str (or (-> error meta :human)
                 error))])]))

;;; Inputs

(fui/reg-input
 :html/string
 (fn [{:keys [on-change error] :as props}]
   [form-item {:error error}
    [:input (-> props
                (select-keys [:default-value :placeholder])
                (assoc :type :text
                       :on-input #(on-change (j/get-in % [:currentTarget :value]))))]]))

(fui/reg-input
 :html/boolean
 (fn [{:keys [on-change error checked] :as props}]
   (let [this (r/current-component)
         {:keys [checked] :or {checked checked}} (r/state this)]
     [form-item {:error error}
      [:label (:label props)]
      [:input {:checked (boolean checked)
               :type :checkbox
               :on-change #(let [checked (not checked)]
                             (on-change checked)
                             (r/set-state this {:checked checked}))}]])))

(fui/reg-input
 :html/integer
 (fn [{:keys [on-change error] :as props}]
   [form-item {:error error}
    [:input (-> props
                (select-keys [:default-value :placeholder])
                (assoc :type :number
                       :on-input #(on-change
                                   (string->long
                                    (j/get-in % [:currentTarget :value])))))]]))

(fui/reg-input
 :html/number
 (fn [{:keys [on-change error] :as props}]
   [form-item {:error error}
    [:input (-> props
                (select-keys [:default-value :placeholder])
                (assoc :type :number
                       :on-input #(on-change
                                   (string->double
                                    (j/get-in % [:currentTarget :value])))))]]))
