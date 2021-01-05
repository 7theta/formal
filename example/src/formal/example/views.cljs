(ns formal.example.views
  (:require [formal.html :as fhtml]))

(def sample-schema
  [:map
   [:name {:placeholder "Name"
           :default-value "tom"}
    [:string {:min 0 :max 100}]]
   [:description {:placeholder "Description"}
    string?]
   [:enabled {:checked true
              :label "Enabled"}
    [:boolean]]
   [:age {:placeholder "Age"} number?]])

(defn main-panel
  []
  [fhtml/form
   {:schema sample-schema
    :on-change #(println %)}])
