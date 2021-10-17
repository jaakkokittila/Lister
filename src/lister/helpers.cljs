(ns lister.helpers
  (:require
   [re-frame.core :as re]
   [lister.subs :as subs]))

(defn new-product-id []
  (let [products @(re/subscribe [::subs/products])
        last-product-id (:id (last (vals products)))]
    (inc last-product-id)))

(defn new-list-id []
  (let [lists @(re/subscribe [::subs/lists])
        last-list-id (:id (last (vals lists)))]
    (inc last-list-id)))

(defn new-product-placeholder []
  (let [new-product-id (new-product-id)]
    {:id new-product-id :name "" :category 1}))

(defn new-list-placeholder []
  (let [new-list-id (new-list-id)]
    {:id new-list-id :name (str "Shopping list " new-list-id) :pinned false :products {}}))