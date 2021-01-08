(ns formal.example.views
  (:require [formal.html :as fhtml]))

(def sample-schema
  [:map
   [:name {:placeholder "Name"
           :default-value "tom3"
           :optional true}
    [:string {:min 0 :max 100}]]
   [:description {:placeholder "Description"}
    string?]
   [:enabled {:default-value true
              :label "Enabled"}
    boolean?]
   [:age {:placeholder "Age"} integer?]
   #_[:fruit [:sequential string?]]
   [:config {:default-value {:foo "foo"}}
    [:map
     [:foo :string]
     [:bar :string]]]])

(defn main-panel
  []
  [fhtml/form
   {:schema sample-schema
    :on-change #(cljs.pprint/pprint {:sample %})}])
