(ns lister.events
  (:require
   [re-frame.core :as re]
   ))

;; Placeholder data
(def default-db
  {:products [{:id 1 :name "Orange Juice" :category 1}
              {:id 2 :name "Rice" :category 1}
              {:id 3 :name "Banana" :category 1}
              {:id 4 :name "Tooth Brush" :category 2}
              {:id 5 :name "Apple" :category 1}]
   :categories [{:id 1 :name "Groceries"}
                {:id 2 :name "Cosmetics"}]
   :lists [{:id 1 :name "Shopping list 1" :pinned false :products [{:product 1 :amount 3 :checked false}
                                                                  {:product 2 :amount 1 :checked true}
                                                                  {:product 5 :amount 2 :checked false}]}
           {:id 2 :name "Shopping list 2" :pinned true :products [{:product 3 :amount 1 :checked false}
                                                                   {:product 1 :amount 5 :checked false}
                                                                   {:product 4 :amount 4 :checked false}]}
           {:id 3 :name "Shopping list 3" :pinned false :products [{:product 3 :amount 1 :checked false}
                                                                   {:product 1 :amount 5 :checked false}
                                                                   {:product 4 :amount 4 :checked false}]}
           {:id 4 :name "Shopping list 4" :pinned false :products [{:product 3 :amount 1 :checked false}
                                                                   {:product 1 :amount 5 :checked false}
                                                                   {:product 4 :amount 4 :checked false}]}]
   :edit-mode false
   :search nil
   :new-product nil
   :active-list nil})

(re/reg-event-db ::set-active-list
  (fn [db [_ list-id]]
    (assoc db :active-list list-id)))

(re/reg-event-db ::set-edit-mode
  (fn [db [_ edit-mode]]
    (assoc db :edit-mode edit-mode)))

(re/reg-event-db
 ::initialize-db
 (fn [_ _]
   default-db))
