(ns formal.html
  (:require [formal.core :as f]
            [formal.util.component :as c]
            [utilis.js :as j]
            [utilis.types.number :refer [string->double string->long]]
            [reagent.core :as r]))

;;; Forms

(defn form
  [{:keys [schema] :as props}]
  [f/form (assoc props :namespace :html)])

(defn form-item
  []
  (r/create-class
   {:render (fn [this]
              (let [{:keys [error label namespace]} (r/props this)
                    {:keys [input-id]} (r/state this)]
                [:div
                 (when label [:label {:for input-id} label])
                 (when-let [input (first (r/children this))]
                   (c/assoc-prop input :id input-id))
                 (when error
                   [:div {:style {:color "red"}}
                    (str (or (-> error meta :human)
                             error))])]))
    :get-initial-state (fn [this]
                         {:input-id (str "html-input-" (gensym))})}))

;;; Inputs

(f/reg-input
 :html/string
 (fn [{:keys [on-change] :as props}]
   [form-item props
    [:input (-> props
                (select-keys [:default-value :placeholder])
                (assoc :type :text
                       :on-input #(on-change (j/get-in % [:currentTarget :value]))))]]))

(f/reg-input
 :html/boolean
 (fn [{:keys [on-change default-value] :as props}]
   (let [this (r/current-component)
         {:keys [checked] :or {checked default-value}} (r/state this)]
     [form-item props
      [:input {:checked (boolean checked)
               :type :checkbox
               :on-change #(let [checked (not checked)]
                             (on-change checked)
                             (r/set-state this {:checked checked}))}]])))

(f/reg-input
 :html/integer
 (fn [{:keys [on-change] :as props}]
   [form-item props
    [:input (-> props
                (select-keys [:default-value :placeholder])
                (assoc :type :number
                       :on-input #(on-change
                                   (string->long
                                    (j/get-in % [:currentTarget :value])))))]]))

(f/reg-input
 :html/number
 (fn [{:keys [on-change] :as props}]
   [form-item props
    [:input (-> props
                (select-keys [:default-value :placeholder])
                (assoc :type :number
                       :on-input #(on-change
                                   (string->double
                                    (j/get-in % [:currentTarget :value])))))]]))

(f/reg-input
 :html/map
 (fn [{:keys [error inputs id]}]
   [:div
    [:div (str id)]
    (into [:div (when id {:style {:padding-left 16}})]
          (concat (->> inputs
                       (sort-by first)
                       (map second))
                  [[:div {:style {:color "red"}}
                    (str (-> error meta :human))]]))]))

(f/reg-input
 :html/sequential
 (fn [{:keys [inputs on-change value error] :as props}]
   (into [:div]
         (concat inputs
                 [[:button {:on-click #(on-change (conj value nil))} "Add"]
                  [:div {:style {:color "red"}}
                   (str (-> error meta :human))]]))))
